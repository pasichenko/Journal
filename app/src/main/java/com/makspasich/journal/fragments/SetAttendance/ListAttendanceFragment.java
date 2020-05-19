package com.makspasich.journal.fragments.SetAttendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.SetAttendanceAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListAttendanceFragment extends Fragment {
    private static final String TAG = "ListAttendanceFragment";
    private Unbinder mUnbinder;
    private View mRootView;

    private DatabaseReference mAttendanceReference;

    //region BindView
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    //endregion

    private SetAttendanceAdapter mAdapter;

    public static ListAttendanceFragment newInstance(int numberPair) {
        return new ListAttendanceFragment(numberPair);
    }

    private ListAttendanceFragment(int numberPair) {
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
        mRootView = inflater.inflate(R.layout.attendance_list_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new SetAttendanceAdapter(getContext(), mAttendanceReference);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
