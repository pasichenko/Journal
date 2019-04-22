package com.makspasich.journal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class SQL {

    public SQL() {
    }

    public static final class SQLColumn implements BaseColumns {
        public static final String TABLE_NAME_ALLDATA = "ALLDATA";
        public static final String TABLE_NAME_STUDENT = "STUDENT";

        public static final String ALLDATA_COLUMN_ID = "ID_ALLDATA";
        public static final String ALLDATA_COLUMN_DATE_MISSING = "DATE_MISSING";
        public static final String ALLDATA_COLUMN_ID_STUDENT = "ID_STUDENT";
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

        public static void setStatusMissing(Context context, int status, int position) {
            ContentValues contentValues = new ContentValues();
            SQLiteDatabase mDatabase;
            DBHelper dbHelper = new DBHelper(context);
            mDatabase = dbHelper.getWritableDatabase();
            contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_DATE_MISSING, (MainActivity.selectedYear + "-" + MainActivity.selectedMonth + "-" + MainActivity.selectedDay));
            contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG, status);
            contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT, position);

            int i = mDatabase.update(SQL.SQLColumn.TABLE_NAME_ALLDATA, contentValues, SQL.SQLColumn.ALLDATA_COLUMN_ID + "= ?", new String[]{Integer.toString(position)});
            Log.d("myLog", "int i=" + i);
            if (i <= 0) {
                mDatabase.insert(SQLColumn.TABLE_NAME_ALLDATA, null, contentValues);
                Log.d("myLog", "event insert");
            }
            Log.d("myLog", "Press button " + status + " posid" + position);
//                swapCursor(mDatabase.query(table, columns, null, null, null, null, null));
            contentValues.clear();

            mDatabase.close();
        }
        public static void setReasonMissing(Context context, int status, int position){
            ContentValues contentValues = new ContentValues();
            SQLiteDatabase mDatabase;
            DBHelper dbHelper = new DBHelper(context);
            mDatabase = dbHelper.getWritableDatabase();
            contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_DATE_MISSING, (MainActivity.selectedYear + "-" + MainActivity.selectedMonth + "-" + MainActivity.selectedDay));
            contentValues.put(SQLColumn.ALLDATA_COLUMN_REASON_MISSING, status);
            contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT, position);

            int i = mDatabase.update(SQL.SQLColumn.TABLE_NAME_ALLDATA, contentValues, SQL.SQLColumn.ALLDATA_COLUMN_ID + "= ?", new String[]{Integer.toString(position)});
            Log.d("myLog", "int i=" + i);
            if (i <= 0) {
                mDatabase.insert(SQLColumn.TABLE_NAME_ALLDATA, null, contentValues);
                Log.d("myLog", "event insert");
            }
            Log.d("myLog", "Press button " + status + " posid" + position);
//                swapCursor(mDatabase.query(table, columns, null, null, null, null, null));
            contentValues.clear();

            mDatabase.close();
        }
    }
}

