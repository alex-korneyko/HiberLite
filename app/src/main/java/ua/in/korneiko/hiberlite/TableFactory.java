package ua.in.korneiko.hiberlite;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ua.in.korneiko.hiberlite.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static ua.in.korneiko.testHiberLite.MainActivity.LOG_TAG;

public class TableFactory {

    //TODO Need to implements! TableUpdate (if the class-entity was changed)
    //TODO Need to implements! JOIN-columns
    //TODO Need to implements! Collections in tables (Create table - OK, Add - OK, Select - NG)

    private static Map<String, List<Table>> joinTables;
    private SQLiteDatabase database;

    public TableFactory(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
        joinTables = new HashMap<>();
    }

    public <T> Table<T> createTable(Class<T> entityClass) {

        return this.createTable(entityClass, entityClass.getSimpleName());
    }

    private <T> Table<T> createTable(Class<T> entityClass, String determinedTableName) {

        //Строка-запрос
        StringBuilder query = new StringBuilder();

        int classUnicNumber = getClassUniqueIdentifier(entityClass);

        //добавление в строку комманды на создание таблицы
        appendCreateTable(query, entityClass, determinedTableName);

        //Список полей класса
        List<Field> fields = new ArrayList<>();

        if (!SimpleTypesDefinition.isSimpleType(entityClass)) {
            //Пробегаем по классу и его суперклассам. Собираем все поля
            for (Class cls = entityClass; cls != null; cls = cls.getSuperclass()) {
                fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            }
        }

        //Если было отдельно определено имя таблицы, заканчивающееся на "JoinTable",
        //значит необходима вспомогательная таблица (для полей List, Map, etc.)
        //Добавляем в таблицу колонку для идентификации мастер-таблицы
        if (determinedTableName.endsWith("JoinTable")) {
            appendColumnNameType(query, "ownerId", Integer.class);
            query.append(", ");
        }

        if (!fields.isEmpty()) {
            //Пробегаем по всем полям, создаём колонки для них
            for (Field field : fields) {
                if (!(field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class))) continue;

                //Если метод добавления колонки вернул false, то колонка не добавлена, значит метод наткнулся
                //на поле типа List, Map, etc. Для такого поля колонка не нужна, пропускаем оставшуюся часть
                if (!appendColumnNameType(query, field, entityClass, determinedTableName)) {
                    continue;
                }

                //Добавляем ключь PRIMARY KEY, если поле проаннотировано @Id
                appendColumnPKey(query, field);
                query.append(",");
            }
        } else {
            if (SimpleTypesDefinition.isSimpleType(entityClass)) {
                appendColumnNameType(query, entityClass.getSimpleName(), entityClass);
                query.append(",");
            }
        }

        //По всем полям пробежались, в конце лишняя запятая, удаляем
        query.deleteCharAt(query.length() - 1);
        //Закрываем скобку
        query.append(")");

        //Выполнение комманды на создание таблицы
        try {
            database.execSQL(query.toString());
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        //Создание оэкземпляра Table
        Table<T> table = new Table<>(database, entityClass,
                joinTables.containsKey(determinedTableName) ? joinTables.get(determinedTableName) : new ArrayList<Table>());

        table.setRawQueryCreateTable(query.toString());
        table.setTableName(determinedTableName);

        return table;
    }

    public boolean deleteTable(Table table) {

        Class entityClass = table.getEntityClass();

        if (!entityClass.isAnnotationPresent(Entity.class)) {
            return false;
        }

        String entityClassSimpleName = entityClass.getSimpleName();

        try {
            database.execSQL("drop table " + entityClassSimpleName);
        } catch (SQLException e) {
            Log.d(LOG_TAG, e.getMessage());
            return false;
        }

        table = null;

        return true;
    }

    private int getClassUniqueIdentifier(Class entityClass) {

        Field[] declaredFields = entityClass.getDeclaredFields();
        int hash = 1;

        for (Field declaredField : declaredFields) {
            if (!declaredField.isAnnotationPresent(Column.class)) {
                continue;
            }

            char[] chars = declaredField.getName().toCharArray();
            for (char aChar : chars) {
                hash = (hash * aChar) % (Integer.MAX_VALUE / 2);
            }
        }

        return hash;
    }

    private void appendCreateTable(StringBuilder query, Class entityClass, String determinedTableName) {

        String tableName;

        if (!determinedTableName.equals("")) {
            tableName = determinedTableName;
        } else {
            tableName = entityClass.getSimpleName();
        }

        query.append("create table ").append(tableName).append(" (");
    }

    private void appendColumnPKey(StringBuilder query, Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            query.append(" primary key ");
            if (field.isAnnotationPresent(Autoincrement.class)) {
                query.append(" autoincrement");
            }
        }
    }

    private boolean appendColumnNameType(StringBuilder query, Field field, Class ownerTable, String ownerTableName) {

        if (field.getType().getSimpleName().equals("List")) {
            if (field.isAnnotationPresent(JoinColumn.class)) {

            }

            if (field.isAnnotationPresent(Column.class)) {
                createJoinTable(field, ownerTable, ownerTableName);
                return false;
            }
        }

        if (field.isAnnotationPresent(JoinColumn.class)) {
            appendColumnNameType(query, field.getName(), Integer.class);
            return true;
        }

        appendColumnNameType(query, field.getName(), field.getType());

        if (field.isAnnotationPresent(NotNull.class)) {
            query.append(" not null ");
        }

        return true;
    }

    private void appendColumnNameType(StringBuilder query, String columnName, Class columnType) {

        query.append(" ").append(columnName).append(" ");

        query.append(" ").append(SimpleTypesDefinition.toSQLDataType(columnType)).append(" ");
    }

    private void createJoinTable(Field field, Class owner, String ownerTableName) {

        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("List not parameterized");
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;

        try {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            String actualTypeArgument = actualTypeArguments[0].toString();
            String classForName = actualTypeArgument.split(" ")[1];
            Class<?> genericClass = Class.forName(classForName);
            if (genericClass.isAnnotationPresent(Entity.class) || SimpleTypesDefinition.isSimpleType(genericClass)) {
                final Table<?> table = createTable(genericClass,
                        field.getName() + "_" + owner.getSimpleName() + "JoinTable");
                if (joinTables.containsKey(ownerTableName)) {
                    joinTables.get(ownerTableName).add(table);
                } else {
                    joinTables.put(ownerTableName, new ArrayList<Table>() {{
                        add(table);
                    }});
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

