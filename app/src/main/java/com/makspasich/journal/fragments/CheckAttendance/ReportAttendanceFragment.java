package com.makspasich.journal.fragments.CheckAttendance;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.ReportAttendanceAdapter;
import com.makspasich.journal.data.model.TypeMissing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReportAttendanceFragment extends Fragment {
    private static final String TAG = "SetReasonMissingFragment";
    private View mRootView;
    private Unbinder mUnbinder;

    private final DatabaseReference mRootReference;
    private DatabaseReference mReasonReference;

    //region BindView
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    //endregion

    private ReportAttendanceAdapter mAdapter;
    private String mKeyGroup;
    private String mDate;

    public ReportAttendanceFragment(String mKeyGroup) {
        this.mKeyGroup = mKeyGroup;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        mDate = formatter.format(currentTime);
        this.mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.check_attendance_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        setHasOptionsMenu(true);

        mReasonReference = mRootReference.child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(mKeyGroup)
                .child(mDate);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRootReference.child(App.KEY_GROUP_TYPES_MISSING).child(mKeyGroup).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TypeMissing> types = new ArrayList<>();
                for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                    TypeMissing type = typeSnapshot.getValue(TypeMissing.class);
                    types.add(type);
                }
                mAdapter = new ReportAttendanceAdapter(getContext(), mReasonReference, types, mKeyGroup);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        super.onDestroyView();
        mUnbinder.unbind();
    }
}