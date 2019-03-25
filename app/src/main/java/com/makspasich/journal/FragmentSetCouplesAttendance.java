package com.makspasich.journal;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentSetCouplesAttendance extends Fragment {

    private List<Person> persons;
    private RecyclerView rv;
    View view;



    private SQLiteDatabase mDatabase;
    private RVAdapterForSetCouplesAttendance mAdapter;
    //private EditText mEditTextName;
    //private TextView mTextViewAmount;
    private int mAmount = 0;


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

        Button buttonAdd = view.findViewById(R.id.temp_add_stud);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        return view;
    }

    private void addItem() {


        String name = "Student";
        ContentValues cv = new ContentValues();
        cv.put(DataContract.DataEntry.COLUMN_STUDENT, name);

        mDatabase.insert(DataContract.DataEntry.TABLE_NAME, null, cv);
        mAdapter.swapCursor(getAllItems());

    }
    private Cursor getAllItems() {
        return mDatabase.query(
                DataContract.DataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}