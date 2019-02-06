package ua.in.korneiko.hiberlite;

import android.content.ContentValues;
import android.database.Cursor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Date;

public class SimpleTypesDefinition {

    public static <T> boolean isSimpleType(Class<T> forClass) {

        return !toSQLDataType(forClass).equals("");
    }

    public static boolean isSimpleType(Field forField) {

        return !toSQLDataType(forField.getType()).equals("");
    }

    public static <T> String toSQLDataType(@NotNull Class<T> forClass) {

        String result = "";

        switch (forClass.getSimpleName()) {
            case "int":
                result = "integer";
                break;
            case "Integer":
                result = "integer";
                break;
            case "long":
                result = "long";
                break;
            case "Long":
                result = "long";
                break;
            case "boolean":
                result = "integer";
                break;
            case "Boolean":
                result = "integer";
                break;
            case "String":
                result = "text";
                break;
            case "double":
                result = "real";
                break;
            case "Double":
                result = "real";
                break;
            case "Date":
                result = "long";
                break;
        }

        return result;
    }

    static <T> T invokeCursorGetMethod(Cursor cursor, int columnIndex, @NotNull Class<T> type) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        T instance = null;

        if (type.equals(Integer.class) || type.equals(int.class)) {
            int intValue = cursor.getInt(columnIndex);
            Constructor<Integer> constructor = Integer.class.getConstructor(int.class);
            instance = (T) constructor.newInstance(intValue);
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            Constructor<Double> constructor = Double.class.getConstructor(double.class);
            instance = (T) constructor.newInstance(cursor.getDouble(columnIndex));
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            Constructor<Long> constructor = Long.class.getConstructor(Long.class);
            instance = (T) constructor.newInstance(cursor.getLong(columnIndex));
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            Constructor<Boolean> constructor = Boolean.class.getConstructor(Boolean.class);
            instance = (T) constructor.newInstance(cursor.getInt(columnIndex) > 0);
        }
        if (type.equals(String.class)) {
            Constructor<String> constructor = String.class.getConstructor(String.class);
            instance = (T) constructor.newInstance(cursor.getString(columnIndex));
        }
        if (type.equals(Date.class)) {
            Constructor<Date> constructor = Date.class.getConstructor(Date.class);
            instance = (T) constructor.newInstance(new java.sql.Date(cursor.getLong(columnIndex)));
        }

        return instance;
    }

    static <T> void invokeContentValuesPutMethod(ContentValues contentValues, String columnName, @NotNull T item) {

        if (item instanceof Integer) {
            contentValues.put(columnName, (Integer) item);
        }
        if (item instanceof Double) {
            contentValues.put(columnName, (Double) item);
        }
        if (item instanceof Long) {
            contentValues.put(columnName, (Long) item);
        }
        if (item instanceof Boolean) {
            contentValues.put(columnName, (Boolean) item);
        }
        if (item instanceof String) {
            contentValues.put(columnName, (String) item);
        }
        if (item instanceof Date) {
            contentValues.put(columnName, ((Date) item).getTime());
        }

    }

    static <T> Object fieldGetValue(Field field, T object) throws InvocationTargetException, IllegalAccessException {
        Object invoke = null;
        String fieldName = field.getName();
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().toLowerCase().startsWith("get") || method.getName().toLowerCase().startsWith("is")) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    if (method.getName().length() == (fieldName.length() + 3) || method.getName().length() == (fieldName.length() + 2)) {
                        invoke = method.invoke(object);
                    }
                }
            }
        }
        return invoke;
    }

    @Nullable
    static Class<?> getClassOfGenericFromField(@NotNull Field field) {

        Class<?> genericClass = null;

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            String actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0].toString();
            String classForName = actualTypeArgument.split(" ")[1];
            try {
                genericClass = Class.forName(classForName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return genericClass;
    }
}
