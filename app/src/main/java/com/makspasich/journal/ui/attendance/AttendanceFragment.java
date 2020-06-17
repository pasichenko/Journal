package com.makspasich.journal.ui.attendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.adapters.SetAttendanceAdapter;
import com.makspasich.journal.databinding.AttendanceListFragmentBinding;

public class AttendanceFragment extends Fragment {
    private static final String TAG = "ListAttendanceFragment";
    private AttendanceListFragmentBinding binding;

    private DatabaseReference mAttendanceReference;

    private SetAttendanceAdapter mAdapter;

    public static AttendanceFragment newInstance(int numberPair) {
        return new AttendanceFragment(numberPair);
    }

    private AttendanceFragment(int numberPair) {
        mAttendanceReference = FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                .child(App.getInstance().getKeyGroup())
                .child(App.getInstance().getSelectedDateString())
                .child(String.valueOf(numberPair));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = AttendanceListFragmentBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new SetAttendanceAdapter(getContext(), mAttendanceReference);
        binding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
