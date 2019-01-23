package ua.in.korneiko.hiberlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Id;

import java.lang.reflect.*;
import java.util.*;

public class Table<T> implements DbProvider<T> {

    //TODO Need to implements! Select from JOIN-tables
    //TODO Need to implements! Manipulations with date. Selections with before, after. between

    private static TreeMap<String, ArrayList<ContentValues>> contentValuesList = new TreeMap<>();
    private static Map<String, Boolean> tablesFinished = new HashMap<>();
    private String rawQueryCreateTableString;
    private Class<T> entityClass;
    private SQLiteDatabase database;
    private List<Table> joinTables = new ArrayList<>();
    private String tableName;

    public Table(SQLiteDatabase database, Class<T> entity, List<Table> joinTables) {
        this.database = database;
        this.entityClass = entity;
        this.joinTables.addAll(joinTables);
    }

    public String getRawQueryCreateTableString() {
        return rawQueryCreateTableString;
    }

    public void setRawQueryCreateTable(String createTableRawQuery) {
        this.rawQueryCreateTableString = createTableRawQuery;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {

        List<T> result = new ArrayList<>();

        Cursor query = database.query(
                entityClass.getSimpleName(),
                null,
                null,
                null,
                null,
                null,
                null);

        try {
            result = getDataFromCursor(query);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public T find(long id) {

        List<T> result = new ArrayList<>();
        String primaryKeyColumnName = null;

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                primaryKeyColumnName = field.getName();
                break;
            }
        }

        if (primaryKeyColumnName == null) return null;

        Cursor query = database.query(
                entityClass.getSimpleName(),
                null,
                primaryKeyColumnName + " =?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        try {
            result = getDataFromCursor(query);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return result.size() > 0 ? result.get(0) : null;
    }

    @Override
    public List<T> find(T item) {

        return null;
    }

    @Override
    public List<T> filter(T item) {
        return null;
    }


    private long add(T item, long ownerId) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        boolean allTablesFinished = true;

        final ContentValues contentValues = generateContentValues(item);
        if (ownerId != 0) {
            contentValues.put("ownerId", ownerId);
        }

        if (contentValuesList.containsKey(getTableName())) {
            contentValuesList.get(getTableName()).add(contentValues);
        } else {
            contentValuesList.put(getTableName(), new ArrayList<ContentValues>() {{
                add(contentValues);
            }});
        }

        for (String tableName : tablesFinished.keySet()) {
            allTablesFinished = allTablesFinished && tablesFinished.get(tableName);
        }

        Integer ownerIdRndGenerated;

        if (allTablesFinished) {
            System.out.println();
            //Пробегаем по наборам contentValues (CV) каждой таблицы
            for (String tableName : contentValuesList.keySet()) {
                //Получаем нбор CV для конкретной таблицы
                ArrayList<ContentValues> contentValuesForTable = contentValuesList.get(tableName);

                //Пробегаем по набору CV для конкретной таблицы (CV для каждой строки в базу)
                for (ContentValues values : contentValuesForTable) {
                    //Поучаем случайно-сгенеринный owner-id идентификатор для текущей табл.
                    ownerIdRndGenerated = values.getAsInteger("owner");

                    //Удаляем идентификатор. Для него нет колонки в таблице
                    values.remove("owner");

                    //Запись в базу строки и получение реального id для этой строки
                    long ownerIdAfterInsert = database.insert(tableName, null, values);

                    //Пробегаем по наборам contentValues (CV) каждой таблицы, чтобы найти CV с соответствующим случайно-сгенеринный owner-id
                    //соответствующим уже записаной строке
                    for (String tableNameForModifyOwnerId : contentValuesList.keySet()) {
                        //Получаем нбор CV для конкретной таблицы
                        ArrayList<ContentValues> contentValuesForModifyOwnerId = contentValuesList.get(tableNameForModifyOwnerId);

                        //Пробегаем по набору CV для конкретной таблицы (CV для каждой строки в базу)
                        for (ContentValues contentValuesForRow : contentValuesForModifyOwnerId) {
                            //Ищем старый соответствующий случайно-сгенерированный ownerId и заменяем на новый, полученный при записи в базу
                            if (contentValuesForRow.containsKey("ownerId") && contentValuesForRow.getAsInteger("ownerId").equals(ownerIdRndGenerated)) {

                                //Заменяем старый случайный ownerId на новй, из базы ownerId
                                contentValuesForRow.put("ownerId", ownerIdAfterInsert);
                            }
                        }
                    }
                }
            }
            contentValuesList.clear();
        }

        return 0;
    }

    @Override
    public long add(T item) {

        long resultId = 0L;

        try {
            resultId = add(item, 0);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return resultId;
    }

    @Override
    public boolean edit(T item) {

        return false;
    }

    @Override
    public boolean edit(long id, T item) {
        return false;
    }

    @Override
    public List<Long> addAll(Iterable<? extends T> items) {
        return null;
    }

    @Override
    public boolean remove(long id) {
        return false;
    }

    @Override
    public boolean remove(T value) {
        return false;
    }

    @Override
    public int clear() {

        return database.delete(entityClass.getSimpleName(), null, null);
    }

    @Override
    public int getCount() {
        return 0;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private ContentValues generateContentValues(T item) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ContentValues contentValues = new ContentValues();
        Object invoke = null;
        List<Field> fields = new ArrayList<>();
        tablesFinished.put(getTableName(), false);
        long randomKey = Math.round(Math.random() * 1000000);

        if (SimpleTypesDefinition.isSimpleType(item.getClass())) {
            SimpleTypesDefinition.invokeContentValuesPutMethod(contentValues, item);
        } else {
            for (Class cls = item.getClass(); cls != null; cls = cls.getSuperclass()) {
                fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            }

            Class<?> aClass = item.getClass();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                if (!field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) continue;
                for (Method method : aClass.getMethods()) {
                    if (method.getName().toLowerCase().startsWith("get") || method.getName().toLowerCase().startsWith("is")) {
                        if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                            if (method.getName().length() == (fieldName.length() + 3) || method.getName().length() == (fieldName.length() + 2)) {
                                try {
                                    invoke = method.invoke(item);
                                    if (invoke == null) continue;
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                if (fieldType.equals(Integer.class) || fieldType.equals(int.class))
                                    contentValues.put(fieldName, (Integer) invoke);
                                if (fieldType.equals(Double.class) || fieldType.equals(double.class))
                                    contentValues.put(fieldName, (Double) invoke);
                                if (fieldType.equals(Long.class) || fieldType.equals(long.class))
                                    contentValues.put(fieldName, (Long) invoke);
                                if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class))
                                    contentValues.put(fieldName, (Boolean) invoke);
                                if (fieldType.equals(String.class))
                                    contentValues.put(fieldName, (String) invoke);
                                if (fieldType.equals(Date.class))
                                    contentValues.put(fieldName, ((Date) invoke).getTime());
                                if (fieldType.equals(List.class)) {
                                    Type genericType = field.getGenericType();
                                    if (genericType instanceof ParameterizedType) {
                                        for (Table joinTable : joinTables) {
                                            if (joinTable.getTableName().equals(fieldName + "_" + entityClass.getSimpleName() + "JoinTable")) {
                                                assert invoke != null;
                                                for (Object o : ((List<?>) invoke)) {
                                                    contentValues.put("owner", randomKey);
                                                    joinTable.add(o, randomKey);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        tablesFinished.put(getTableName(), true);
        return contentValues;
    }

    private List<T> getDataFromCursor(Cursor cursor) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<T> result = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                T instance = entityClass.newInstance();

                List<Field> fields = new ArrayList<>();
                for (Class cls = entityClass; cls != null; cls = cls.getSuperclass()) {
                    fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                }

                for (Field field : fields) {
                    if (!field.isAnnotationPresent(Column.class)) continue;

                    String fieldName = field.getName();
                    Class<?> fieldType = field.getType();
                    int columnIndex = cursor.getColumnIndex(fieldName);
                    String methodName = generateMethodName(fieldName);
                    Method method = entityClass.getMethod(methodName, fieldType);

                    method.invoke(instance, SimpleTypesDefinition.invokeCursorGetMethod(cursor, columnIndex, fieldType));
                }
                result.add(instance);
            } while (cursor.moveToNext());
        }
        return result;
    }

    private String generateMethodName(String fieldName) {

        StringBuilder stringBuilder = new StringBuilder(fieldName);
        String firstLetter = stringBuilder.substring(0, 1);
        StringBuilder methodName = stringBuilder.replace(0, 1, "set" + firstLetter.toUpperCase());
        return methodName.toString();
    }
}
