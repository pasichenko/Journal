package com.makspasich.journal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.StudentReportAdapter;
import com.makspasich.journal.databinding.CheckAttendanceFragmentBinding;

public class StudentReportFragment extends Fragment {
    private static final String TAG = "SetReasonMissingFragment";
    private CheckAttendanceFragmentBinding binding;

    private final DatabaseReference mRootReference;
    private DatabaseReference mReportReference;

    private StudentReportAdapter mAdapter;

    public StudentReportFragment() {
        this.mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        binding = CheckAttendanceFragmentBinding.inflate(inflater, container, false);

        mReportReference = mRootReference.child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(App.getInstance().getKeyGroup())
                .child(App.getInstance().getKeyStudent())
                .child(App.getInstance().getSelectedDateString())
                .child("missings");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter = new StudentReportAdapter(getContext(), mReportReference);
        binding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
