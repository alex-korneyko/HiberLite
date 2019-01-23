package ua.in.korneiko.hiberlite;

import android.content.ContentValues;
import android.database.Cursor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
            instance = type.getConstructor(Integer.class).newInstance(cursor.getInt(columnIndex));
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            instance = type.getConstructor(Double.class).newInstance(cursor.getDouble(columnIndex));
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            instance = type.getConstructor(Long.class).newInstance(cursor.getLong(columnIndex));
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            instance = type.getConstructor(Boolean.class).newInstance(cursor.getInt(columnIndex) > 0);
        }
        if (type.equals(String.class)) {
            instance = type.getConstructor(String.class).newInstance(cursor.getString(columnIndex));
        }
        if (type.equals(Date.class)) {
            instance = type.getConstructor(Date.class).newInstance(new java.sql.Date(cursor.getLong(columnIndex)));
        }

        return instance;
    }

    static <T> void invokeContentValuesPutMethod(ContentValues contentValues, @NotNull T item) {

        String columnName = item.getClass().getSimpleName();
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
}
