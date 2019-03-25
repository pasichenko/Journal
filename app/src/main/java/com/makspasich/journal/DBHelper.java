package com.makspasich.journal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.makspasich.journal.DataContract.*;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "grocerylist.db";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GROCERYLIST_TABLE = "CREATE TABLE " +
                DataEntry.TABLE_NAME + " (" +
                DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataEntry.COLUMN_STUDENT + " TEXT NOT NULL " +
                //DataEntry.COLUMN_AMOUNT + " INTEGER NOT NULL, " +
                //DataEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_GROCERYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
