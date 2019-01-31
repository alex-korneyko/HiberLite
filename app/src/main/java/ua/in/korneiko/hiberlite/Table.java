package ua.in.korneiko.hiberlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Id;
import ua.in.korneiko.hiberlite.annotations.JoinColumn;
import ua.in.korneiko.hiberlite.annotations.SearchKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static ua.in.korneiko.hiberlite.GlobalConstants.AUX_TAB_SUFFIX;

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
    private TableFactory tableFactory;

    public Table(SQLiteDatabase database, Class<T> entity, List<Table> joinTables) {
        this.database = database;
        this.entityClass = entity;
        this.joinTables.addAll(joinTables);
    }

    public String getRawQueryCreateTableString() {
        return rawQueryCreateTableString;
    }

    public void setRawQueryCreateTableString(String createTableRawQuery) {
        this.rawQueryCreateTableString = createTableRawQuery;
    }

    Class<T> getEntityClass() {
        return entityClass;
    }

    void setEntityClass(Class<T> entityClass) {
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

        List<Field> fields = findFieldsByAnnotation(SearchKey.class, item);
        StringBuilder selectionStringConstruct = new StringBuilder();
        String[] selectionArgs = new String[fields.size()];
        List<T> data = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            selectionStringConstruct.append(fields.get(i).getName()).append("=?");
            if (i < (fields.size() - 1)) {
                selectionStringConstruct.append(" and ");
            }
            try {
                Field field = fields.get(i);
                Object value = SimpleTypesDefinition.fieldGetValue(field, item);
                selectionArgs[i] = value.toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        String selectionString = selectionStringConstruct.toString();

        Cursor cursor = database.query(tableName,
                null,
                selectionString,
                selectionArgs,
                null,
                null,
                null);

        try {
            data = getDataFromCursor(cursor);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public List<T> filter(T item) {
        return null;
    }


    private List<T> filterByOwnerId(int ownerId) {

        List<T> data = new ArrayList<>();

        Cursor cursor = database.query(tableName,
                null,
                "ownerId=?",
                new String[]{String.valueOf(ownerId)},
                null,
                null,
                null
        );

        try {
            data = getDataFromCursor(cursor);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return data;
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
                    values.put("_idAI", ownerIdAfterInsert);

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
                                contentValuesForRow.put("ownerId", values.getAsInteger("_idAI"));
                            }
                        }
                    }
                }
            }

            for (String tableName : contentValuesList.keySet()) {
                for (ContentValues values : contentValuesList.get(tableName)) {
                    Integer idAfterInsert = values.getAsInteger("_idAI");
                    values.remove("_idAI");
                    edit(tableName, idAfterInsert, values);
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
    public boolean edit(int id, T item) {

        ContentValues contentValues = null;
        int updateId = 0;

        try {
            contentValues = generateContentValues(item);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        if (contentValues != null) {
            updateId = database.update(getTableName(), contentValues, "id=?", new String[]{String.valueOf(id)});
        }

        return updateId > 0;
    }

    private boolean edit(String tableName, int id, ContentValues values) {

        int update = 0;
        Cursor cursor;

        cursor = database.query(
                "sqlite_master",
                new String[]{"sql"},
                "tbl_name=?",
                new String[]{tableName},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            String sql = cursor.getString(cursor.getColumnIndex("sql"));
            if (sql.contains(" id ")) {
                update = database.update(tableName, values, "id=?", new String[]{String.valueOf(id)});
            }
        }

        cursor.close();


        return update > 0;
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
        Object invokedResultGetMethod = null;
        List<Field> fields = new ArrayList<>();
        tablesFinished.put(getTableName(), false);
        long randomKey = Math.round(Math.random() * 1000000);

        if (SimpleTypesDefinition.isSimpleType(item.getClass())) {
            SimpleTypesDefinition.invokeContentValuesPutMethod(contentValues, item.getClass().getSimpleName(), item);
        } else {
            for (Class cls = item.getClass(); cls != null; cls = cls.getSuperclass()) {
                fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            }

            Class<?> aClass = item.getClass();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                if (field.isAnnotationPresent(Id.class)) continue;

                for (Method method : aClass.getMethods()) {
                    if (method.getName().toLowerCase().startsWith("get") || method.getName().toLowerCase().startsWith("is")) {
                        if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                            if (method.getName().length() == (fieldName.length() + 3) || method.getName().length() == (fieldName.length() + 2)) {
                                try {
                                    //Получение значения поля
                                    invokedResultGetMethod = method.invoke(item);
                                    if (invokedResultGetMethod == null) continue;
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                if (field.isAnnotationPresent(Column.class)) {
                                    if (SimpleTypesDefinition.isSimpleType(fieldType)) {
                                        SimpleTypesDefinition.invokeContentValuesPutMethod(contentValues, fieldName, invokedResultGetMethod);
                                    } else {
                                        if (!fieldType.equals(List.class) && !fieldType.equals(Map.class)) {
                                            throw new IllegalArgumentException("Not simple classes must be annotated as @JoinColumn" +
                                                    field.getName() + " - is not simple!");
                                        }
                                    }

                                    if (fieldType.equals(List.class)) {
                                        Type genericType = field.getGenericType();
                                        if (genericType instanceof ParameterizedType) {
                                            for (Table joinTable : joinTables) {
                                                if (joinTable.getTableName().equals(fieldName + "_" + entityClass.getSimpleName() + AUX_TAB_SUFFIX)) {
                                                    assert invokedResultGetMethod != null;
                                                    for (Object o : ((List<?>) invokedResultGetMethod)) {
                                                        contentValues.put("owner", randomKey);
                                                        joinTable.add(o, randomKey);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (field.isAnnotationPresent(JoinColumn.class)) {
                                    //Получение таблицы из общего списка таблиц
                                    Table table = tableFactory.getTable(fieldName);
                                    //Поиск записи в join-таблице соответствующее значению поля в главной таблице
                                    List<Entity> joinedTableObjects = table.find(invokedResultGetMethod);
                                    Entity entity = !joinedTableObjects.isEmpty() ? joinedTableObjects.get(0) : null;
                                    assert entity != null;
                                    //Получение ID записи
                                    int entityId = entity.getId();
                                    //Добавление ID в главную таблицу, указывающего на запись в join-таблице
                                    contentValues.put(fieldName, entityId);
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
        int entityId = 0;

        if (cursor.moveToFirst()) {
            do {
                T instance;
                if (SimpleTypesDefinition.isSimpleType(entityClass)) {
                    instance = SimpleTypesDefinition.invokeCursorGetMethod(
                            cursor, cursor.getColumnIndex(entityClass.getSimpleName()), entityClass);
                } else {

                    instance = entityClass.newInstance();

                    List<Field> fields = new ArrayList<>();
                    for (Class cls = entityClass; cls != null; cls = cls.getSuperclass()) {
                        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
                    }

                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
                            int columnIndex = cursor.getColumnIndex(field.getName());
                            entityId = ((Integer) SimpleTypesDefinition.invokeCursorGetMethod(cursor, columnIndex, field.getType()));
                        }
                    }

                    for (Field field : fields) {
                        String fieldName = field.getName();
                        Class<?> fieldType = field.getType();
                        String methodName = generateMethodName(fieldName);
                        int columnIndex = cursor.getColumnIndex(fieldName);


                        if (field.isAnnotationPresent(Column.class)) {
                            Method method = entityClass.getMethod(methodName, fieldType);
                            if (fieldType.equals(List.class)) {
                                Table joinedTable = tableFactory.getTable(
                                        fieldName + "_" + entityClass.getSimpleName() + GlobalConstants.AUX_TAB_SUFFIX);
                                List objectsForOwner = joinedTable.filterByOwnerId(entityId);
                                method.invoke(instance, objectsForOwner);
                            } else {
                                Object dataFromCursor = SimpleTypesDefinition.invokeCursorGetMethod(cursor, columnIndex, fieldType);
                                method.invoke(instance, dataFromCursor);
                            }
                        }

                        if (field.isAnnotationPresent(JoinColumn.class)) {
                            Method method = entityClass.getMethod(methodName, fieldType);
                            int idInJoinedTable = cursor.getInt(columnIndex);
                            Table joinedTable = tableFactory.getTable(fieldName);
                            Object joinedEntity = joinedTable.find(idInJoinedTable);
                            method.invoke(instance, joinedEntity);
                        }
                    }
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

    public TableFactory getTableFactory() {
        return this.tableFactory;
    }

    public void setTableFactory(TableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }

    private List<Field> findFieldsByAnnotation(Class<? extends Annotation> annotation, T item) {
        ArrayList<Field> fields = new ArrayList<>();

        for (Field field : item.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                fields.add(field);
            }
        }
        return fields;
    }
}
