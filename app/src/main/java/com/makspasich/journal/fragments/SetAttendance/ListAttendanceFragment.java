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
    private final String mKeyGroup;
    private final String mDate;
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

        mAttendanceReference = mRootReference.child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
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
        mAdapter = new SetAttendanceAdapter(getContext(), mAttendanceReference, mKeyGroup);
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

    private void writeStudentsInMissingsReference(Student student) {
        String keyMissing = mAttendanceReference.push().getKey();
        Missing missing = new Missing(mDate, student, "null", null, mNumberPair);

        mAttendanceReference.child(keyMissing).setValue(missing);

        mRootReference
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(mKeyGroup)
                .child(student.id_student)
                .child(mDate)
                .child("student")
                .setValue(student);
        mRootReference
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(mKeyGroup)
                .child(student.id_student)
                .child(mDate)
                .child("missings")
                .child(keyMissing)
                .setValue(missing);
        mRootReference
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(mKeyGroup)
                .child(mDate)
                .child(student.id_student)
                .child("student")
                .setValue(student);
        mRootReference
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(mKeyGroup)
                .child(mDate)
                .child(student.id_student)
                .child("missings")
                .child(keyMissing)
                .setValue(missing);
    }

    private ValueEventListener checkIsExistData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                mRootReference
                        .child(App.KEY_GROUP_STUDENTS)
                        .child(mKeyGroup)
                        .addListenerForSingleValueEvent(getGroupStudents);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener getGroupStudents = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Student student = childSnapshot.getValue(Student.class);
                writeStudentsInMissingsReference(student);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
