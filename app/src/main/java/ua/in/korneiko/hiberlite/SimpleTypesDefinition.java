package ua.in.korneiko.hiberlite;

import android.content.ContentValues;
import android.database.Cursor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.in.korneiko.hiberlite.annotations.Column;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            Constructor<Long> constructor = Long.class.getConstructor(long.class);
            instance = (T) constructor.newInstance(cursor.getLong(columnIndex));
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            Constructor<Boolean> constructor = Boolean.class.getConstructor(boolean.class);
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

    static void invokeContentValuesPutMethod(ContentValues contentValues, Object item, Field field) throws IllegalAccessException {

        String columnName = field.getAnnotation(Column.class).columnName().equals("") ?
                field.getName() :
                field.getAnnotation(Column.class).columnName();

        if (field.getType().equals(Integer.class)) {
            contentValues.put(columnName, (Integer) field.get(item));
        }
        if (field.getType().equals(Double.class)) {
            contentValues.put(columnName, (Double) field.get(item));
        }
        if (field.getType().equals(Long.class)) {
            contentValues.put(columnName, (Long) field.get(item));
        }
        if (field.getType().equals(Boolean.class)) {
            contentValues.put(columnName, (Boolean) field.get(item));
        }
        if (field.getType().equals(String.class)) {
            contentValues.put(columnName, (String) field.get(item));
        }
        if (field.getType().equals(Date.class)) {
            contentValues.put(columnName, ((Date) field.get(item)).getTime());
        }
    }

    public static void invokeContentValuesPutMethod(ContentValues contentValues, Object item) {

        if (item.getClass().equals(Integer.class)) {
            contentValues.put(item.getClass().getSimpleName(), (Integer) item);
        }
        if (item.getClass().equals(Double.class)) {
            contentValues.put(item.getClass().getSimpleName(), (Double) item);
        }
        if (item.getClass().equals(Long.class)) {
            contentValues.put(item.getClass().getSimpleName(), (Long) item);
        }
        if (item.getClass().equals(Boolean.class)) {
            contentValues.put(item.getClass().getSimpleName(), (Boolean) item);
        }
        if (item.getClass().equals(String.class)) {
            contentValues.put(item.getClass().getSimpleName(), (String) item);
        }
        if (item.getClass().equals(Date.class)) {
            contentValues.put(item.getClass().getSimpleName(), ((Date) item).getTime());
        }
    }

    static <T, F> F fieldGetValue(Field item, T object) throws InvocationTargetException, IllegalAccessException {
        Object invoke = null;
        String fieldName = item.getName();
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().toLowerCase().startsWith("get") || method.getName().toLowerCase().startsWith("is")) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    if (method.getName().length() == (fieldName.length() + 3) || method.getName().length() == (fieldName.length() + 2)) {
                        invoke = method.invoke(object);
                    }
                }
            }
        }
        return (F) invoke;
    }

    @Nullable
    static Class<?> getClassOfGenericFromField(@NotNull Field item) {

        Class<?> genericClass = null;

        Type genericType = item.getGenericType();
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

    static boolean isSimpleGenericType(Field item) {

        Class<?> genericClass = getClassOfGenericFromField(item);
        return isSimpleType(genericClass);
    }

    public static <T> List<T> getParameterizedList(Class<T> classParameter) {

        return new ArrayList<>();
    }
}
