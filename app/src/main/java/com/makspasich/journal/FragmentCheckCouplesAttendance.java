package com.makspasich.journal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentCheckCouplesAttendance extends Fragment {
    View view;

    private SQLiteDatabase mDatabase;
    private RVAdapterForCheckCouplesAttendance mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_check_couples_attendance, container, false);

        DBHelper dbHelper = new DBHelper(getActivity());




        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = view.findViewById(R.id.rv_check);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RVAdapterForCheckCouplesAttendance(getActivity(), getAllItems());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private Cursor getAllItems() {

        String table = SQL.SQLColumn.TABLE_NAME_STUDENT+" as TS left join "+ SQL.SQLColumn.TABLE_NAME_ALLDATA +" as AD on "+"TS."+ SQL.SQLColumn.STUDENT_COLUMN_ID+" = AD."+ SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT;
        Log.d("myLog", table);
        String selection = "AD."+SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG+" IS NULL ";
        Log.d("myLogselection", selection);
        String[] selectionArgs = {"0","1","2"};

        Cursor cursor = mDatabase.query(table, null, null, null, null, null, null);
//        Cursor cursor = mDatabase.rawQuery(table, null);
        Log.d("myLog!!!!!!!!!!!!","before query with WHERE");
        logCursor(cursor);
        return cursor;

    }
    void logCursor(Cursor cursor) {
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