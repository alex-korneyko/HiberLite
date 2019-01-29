package ua.in.korneiko.hiberlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DataBase {

    private final SQLiteDatabase database;

    public DataBase(Context context, String databaseName) {
        database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
