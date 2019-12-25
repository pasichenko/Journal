package com.makspasich.journal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, MainActivity.DATABASE_NAME, null, MainActivity.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQL.SQLCommand.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SQL.SQLColumn.TABLE_NAME_STUDENT);
        onCreate(db);
    }

    public static void logCursor(Cursor cursor) {
        Log.d("myLog", "StartLogCursor");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : cursor.getColumnNames()) {
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("myLog", str);
                } while (cursor.moveToNext());
            }
        } else Log.d("myLog", "Cursor is null");
        Log.d("myLog", "ENDLogCursor");
    }
}
