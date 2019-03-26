package com.makspasich.journal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SettingDBHelper extends SQLiteOpenHelper {

    public SettingDBHelper(Context context) {
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
}
