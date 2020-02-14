package com.makspasich.journal.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.activities.SignInActivity;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.Student;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddStudentDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";
    private Context mContext;
    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private View mRootView;
    private AlertDialog dialog;

    //region BindView
    @BindView(R.id.last_name_til)
    TextInputLayout lastNameTextInputLayout;
    @BindView(R.id.last_name_et)
    TextInputEditText lastNameEditText;
    @BindView(R.id.first_name_til)
    TextInputLayout firstNameTextInputLayout;
    @BindView(R.id.first_name_et)
    TextInputEditText firstNameEditText;
    //endregion

    private String mKeyGroup;

    public AddStudentDialog(Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AlertDialogTheme);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        mRootView = inflater.inflate(R.layout.add_student_dialog, null);
        mUnbinder = ButterKnife.bind(this, mRootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mKeyGroup = bundle.getString(SignInActivity.KEY_GROUP);
        } else {
            Toast.makeText(mContext, "Oops, no group", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        builder.setTitle("Add student");
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            addStudent();
            Toast.makeText(mContext, "Student added", Toast.LENGTH_SHORT).show();
            dismiss();
        });
        builder.setNegativeButton(R.string.cancel, null);

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    lastNameTextInputLayout.setError(getString(R.string.enter_last_name));
                } else {
                    lastNameTextInputLayout.setError(null);
                }
            }
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    firstNameTextInputLayout.setError(getString(R.string.enter_first_name));
                } else {
                    firstNameTextInputLayout.setError(null);
                }
            }
        });

        dialog = builder.create();
        return dialog;
    }

    private void addStudent() {
        Student student = createStudent();
        writeInStudentsReference(student);
        writeInGroupStudentsReference(student);
        writeInMissingsReference(student);
    }

    private Student createStudent() {
        String keyStudent = mRootReference.child(App.KEY_STUDENTS).push().getKey();
        String lastName = lastNameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        return new Student(keyStudent, lastName, firstName, null);
    }

    private void writeInStudentsReference(Student student) {
        mRootReference
                .child(App.KEY_STUDENTS)
                .child(student.id_student)
                .setValue(student);
    }

    private void writeInGroupStudentsReference(Student student) {
        mRootReference
                .child(App.KEY_GROUP_STUDENTS)
                .child(mKeyGroup)
                .child(student.id_student)
                .setValue(student);
    }

    private void writeInMissingsReference(Student student) {
        DatabaseReference missingReference = mRootReference.child(App.KEY_MISSINGS).child(mKeyGroup);
        missingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot date : dataSnapshot.getChildren()) {
                    for (DataSnapshot couple : date.getChildren()) {
                        Missing missing = new Missing(date.getKey(), student, "null", null, Integer.valueOf(couple.getKey()));
                        missingReference
                                .child(date.getKey())
                                .child(couple.getKey())
                                .child(student.id_student)
                                .setValue(missing);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
