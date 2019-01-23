package ua.in.korneiko.hiberlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase {

    private final SQLiteDatabase database;

    public DataBase(Context context, String databaseName, int databaseVersion) {
        database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
