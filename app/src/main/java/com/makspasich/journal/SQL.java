package com.makspasich.journal;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class SQL {

    public SQL() {
    }

    public static final class SQLColumn implements BaseColumns {
        public static final String TABLE_NAME_ALLDATA = "ALLDATA";
        public static final String TABLE_NAME_STUDENT = "STUDENT";

        public static final String ALLDATA_COLUMN_ID = "ID_ALLDATA";
        public static final String ALLDATA_COLUMN_DATE_MISSING = "DATE_MISSING";
        public static final String ALLDATA_COLUMN_ID_STUDENT = "ID_STUDENT";
        public static final String ALLDATA_COLUMN_STUDENT = "NAME_STUDENT";
        public static final String ALLDATA_COLUMN_STATUS_MISSISNG = "STATUS_MISSING";
        public static final String ALLDATA_COLUMN_REASON_MISSING = "REASON_MISSING";

        public static final String STUDENT_COLUMN_ID = "ID_STUDENT";
        public static final String STUDENT_COLUMN_STUDENT = "STUDENT";
    }

    public static final class SQLCommand {
        public static final String SQL_CREATE_TABLE_ALLDATA = "CREATE TABLE " +
                SQLColumn.TABLE_NAME_ALLDATA + " (" +
                SQLColumn.ALLDATA_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SQLColumn.ALLDATA_COLUMN_DATE_MISSING + " TEXT NOT NULL, " +
                SQLColumn.ALLDATA_COLUMN_ID_STUDENT + " INTEGER, " +
                SQLColumn.ALLDATA_COLUMN_STUDENT + " TEXT, " +
                SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG + " INTEGER NOT NULL, " +
                SQLColumn.ALLDATA_COLUMN_REASON_MISSING + " INTEGER " +
                ");";
        public static final String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE " +
                SQLColumn.TABLE_NAME_STUDENT + " (" +
                SQLColumn.STUDENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SQLColumn.STUDENT_COLUMN_STUDENT + " TEXT NOT NULL " +
                ");";

        public static void createTable(SQLiteDatabase db) {
            db.execSQL(SQL.SQLCommand.SQL_CREATE_TABLE_ALLDATA);
            db.execSQL(SQL.SQLCommand.SQL_CREATE_STUDENT_TABLE);
        }
    }
}

