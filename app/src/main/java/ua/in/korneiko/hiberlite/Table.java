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

public class Table<T> implements DbProvider<T> {

    //TODO Need to implements! Manipulations with date. Selections with before, after. between

    private String rawQueryCreateTableString;
    private Class<T> entityClass;
    private SQLiteDatabase database;
    private List<ContentValues> preparedContentValuesList = new ArrayList<>();
    private Map<String, Table> joinTables = new HashMap<>();
    private String tableName;
    private TableFactory tableFactory;

    public Table(SQLiteDatabase database, Class<T> entity, Map<String, Table> joinTables) {
        this.database = database;
        this.entityClass = entity;
        this.joinTables.putAll(joinTables);
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


    private List<T> filterByOwnerId(long ownerId) {

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

    @Override
    public long add(T item) {

        generateContentValues(item);
        List<Long> ids = this.savePreparedContentValuesToDB();

        for (String joinTableName : joinTables.keySet()) {
            Table joinTable = joinTables.get(joinTableName);
            joinTable.savePreparedContentValuesToDB(ids.get(0));
        }

        return ids.get(0);
    }

    private List<Long> savePreparedContentValuesToDB() {

        ArrayList<Long> ids = new ArrayList<>();

        for (ContentValues contentValues : preparedContentValuesList) {
            ids.add(database.insert(this.getTableName(), null, contentValues));
        }

        preparedContentValuesList.clear();

        return ids;
    }

    private List<Long> savePreparedContentValuesToDB(long ownerId) {

        for (ContentValues contentValues : preparedContentValuesList) {
            contentValues.put("ownerId", ownerId);
        }

        return this.savePreparedContentValuesToDB();
    }

    @Override
    public boolean edit(int id, T item) {

        return true;
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

    public void generateContentValues(T item) {

        ContentValues contentValues = new ContentValues();

        if (SimpleTypesDefinition.isSimpleType(item.getClass())) {
            SimpleTypesDefinition.invokeContentValuesPutMethod(contentValues, item);
            addContentValues(contentValues);
            return;
        }

        ArrayList<Field> fields = new ArrayList<>();

        for (Class c = item.getClass(); c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Column.class)) {
                if (!SimpleTypesDefinition.isSimpleType(field) && !(field.getType().equals(List.class) || field.getType().equals(Map.class))) {
                    throw new IllegalArgumentException("For not simple types needs use @JoinColumn annotations. " +
                            field.getType().getName() + " - is not simple type");
                }

                addValueToContentValuesFromColumnAnnotatedField(contentValues, item, field);
            }

            if (field.isAnnotationPresent(JoinColumn.class)  && !(field.getType().equals(List.class) || field.getType().equals(Map.class))) {
                if (SimpleTypesDefinition.isSimpleType(field)) {
                    throw new IllegalArgumentException("For simple types needs use @Column annotations. " +
                            field.getType().getName() + " - is simple type");
                }
                if (SimpleTypesDefinition.isSimpleGenericType(field)) {
                    throw new IllegalArgumentException("For simple generic types needs use @Column annotations. " +
                            SimpleTypesDefinition.getClassOfGenericFromField(field).getName() + " - is simple generic type");
                }

                addValueToContentValuesFromJoinColumnAnnotation(contentValues, item, field);
            }
        }

        addContentValues(contentValues);
    }

    private void addValueToContentValuesFromColumnAnnotatedField(ContentValues contentValues, T item, Field field) {

        String columnName = field.getAnnotation(Column.class).columnName().equals("") ?
                field.getName() :
                field.getAnnotation(Column.class).columnName();

        //Если поле простого типа, то просто добавление в contentValues
        if (SimpleTypesDefinition.isSimpleType(field.getType())) {
            try {
                SimpleTypesDefinition.invokeContentValuesPutMethod(contentValues, item, field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //Если поле типизированный список
        if (field.getType().equals(List.class)) {
            Class<?> genericClass = SimpleTypesDefinition.getClassOfGenericFromField(field);
            if (genericClass == null) {
                throw new IllegalArgumentException(List.class.getName() + " must be parameterized." +
                        " Field: " + field.getName() +
                        " Table: " + getTableName());
            }

            List fieldValues = new ArrayList();
            try {
                fieldValues.addAll((Collection) SimpleTypesDefinition.fieldGetValue(field, item));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if (SimpleTypesDefinition.isSimpleType(genericClass)) {
                String joinTableName = columnName + "_" + item.getClass().getSimpleName() + GlobalConstants.JOIN_TAB_SUFFIX;
                Table joinTable = joinTables.get(joinTableName);
                for (Object fieldValue : fieldValues) {
                    joinTable.generateContentValues(fieldValue);
                }
            } else {
                
            }
        }
    }

    private void addValueToContentValuesFromJoinColumnAnnotation(ContentValues contentValues, T item, Field field) {

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
                                        fieldName + "_" + entityClass.getSimpleName() + GlobalConstants.JOIN_TAB_SUFFIX);
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

    void setTableFactory(TableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }

    private List<Field> findFieldsByAnnotation(Class<? extends Annotation> annotation, T item) {
        ArrayList<Field> filteredFields = new ArrayList<>();

        for (Class cls = item.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (Field declaredField : cls.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(annotation)) {
                    filteredFields.add(declaredField);
                }
            }
        }

        return filteredFields;
    }

    void addContentValues(ContentValues values) {
        this.preparedContentValuesList.add(values);
    }

    public List<ContentValues> getPreparedContentValuesList() {
        return preparedContentValuesList;
    }

    public void setPreparedContentValuesList(List<ContentValues> preparedContentValuesList) {
        this.preparedContentValuesList = preparedContentValuesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table<?> table = (Table<?>) o;
        return Objects.equals(tableName, table.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
