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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.SetAttendanceAdapter;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListAttendanceFragment extends Fragment {
    private static final String TAG = "ListAttendanceFragment";
    private Unbinder mUnbinder;
    private View mRootView;

    private final DatabaseReference mRootReference;
    private DatabaseReference mAttendanceReference;

    //region BindView
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    //endregion

    private SetAttendanceAdapter mAdapter;
    private String mKeyGroup;
    private String mDate;
    private int mNumberPair;

    public ListAttendanceFragment(String keyGroup, String mDate, int numberPair) {
        this.mKeyGroup = keyGroup;
        this.mDate = mDate;
        this.mNumberPair = numberPair;
        this.mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.attendance_list_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);

        mAttendanceReference = mRootReference.child(App.KEY_MISSINGS)
                .child(mKeyGroup)
                .child(mDate)
                .child(String.valueOf(mNumberPair));
        mAttendanceReference.addValueEventListener(checkIsExistData);

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

    private void writeStudentsInCurrentCouple(List<Student> listStudent, List<String> listStudentId) {
        Map<String, Object> childUpdates = new HashMap<>();
        for (int i = 0; i < listStudentId.size(); i++) {
            Missing missing = new Missing(mDate, listStudent.get(i), "null", null, mNumberPair);
            childUpdates.put(listStudentId.get(i), missing);
        }
        mAttendanceReference.updateChildren(childUpdates);
    }

    private ValueEventListener checkIsExistData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                mRootReference
                        .child(App.KEY_GROUP_STUDENTS)
                        .child(mKeyGroup)
                        .addValueEventListener(getGroupStudents);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener getGroupStudents = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Student> listStudent = new ArrayList<>();
            List<String> listStudentId = new ArrayList<>();

            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Student student = childSnapshot.getValue(Student.class);
                listStudent.add(student);
                listStudentId.add(childSnapshot.getKey());
            }
            writeStudentsInCurrentCouple(listStudent, listStudentId);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
