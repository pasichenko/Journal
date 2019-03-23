package com.makspasich.journal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentSetCouplesAttendance extends Fragment {

    private List<Person> persons;
    private RecyclerView rv;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_couples_attendance, container, false);

        rv = view.findViewById(R.id.rv_set);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
        initializeAdapter();
        return view;
    }

    int totalSize = 30;

    private void initializeData() {
        persons = new ArrayList<>();

        for (int i = 0; i < totalSize; i++) {
            persons.add(new Person(i + ". FragmentSetCouplesAttendance"/*, i+"", R.drawable.ic_launcher_background*/));
        }
    }

    private void initializeAdapter() {
        RVAdapterForSetCouplesAttendance adapter = new RVAdapterForSetCouplesAttendance(persons);
        rv.setAdapter(adapter);
    }
}