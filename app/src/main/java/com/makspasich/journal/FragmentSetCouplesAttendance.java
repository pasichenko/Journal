package com.makspasich.journal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentSetCouplesAttendance extends Fragment {
    View view;

    private SQLiteDatabase mDatabase;
    private RVAdapterForSetCouplesAttendance mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_couples_attendance, container, false);

        DBHelper dbHelper = new DBHelper(getActivity());

        mDatabase = dbHelper.getWritableDatabase();

        RecyclerView recyclerView = view.findViewById(R.id.rv_set);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RVAdapterForSetCouplesAttendance(getActivity(), getAllItems());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private Cursor getAllItems() {

        String table = SQL.SQLColumn.TABLE_NAME_STUDENT + " as TS left join " + SQL.SQLColumn.TABLE_NAME_ALLDATA + " as AD on " + "TS." + SQL.SQLColumn.STUDENT_COLUMN_ID + " = AD." + SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT;
        Log.d("myLog", table);
        String selection = "AD." + SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG + " IS NULL ";
        Log.d("myLogselection", selection);
        String[] selectionArgs = {"0", "1", "2"};

        Cursor cursor = mDatabase.query(table, null, null, null, null, null, null);
//        Cursor cursor = mDatabase.rawQuery(table, null);
        Log.d("myLog!!!!!!!!!!!!", "before query with WHERE");
        DBHelper.logCursor(cursor);
        return cursor;

    }


}