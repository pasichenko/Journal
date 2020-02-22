package com.makspasich.journal.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
        builder.setTitle(R.string.add_student);
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> addStudent());
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
        firstNameEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addStudent();
            }
            return false;
        });
        dialog = builder.create();
        return dialog;
    }

    private boolean validateLastNameInput() {
        String loginInput = Objects.requireNonNull(lastNameTextInputLayout.getEditText()).getText().toString().trim();

        if (loginInput.isEmpty()) {
            lastNameTextInputLayout.setError(getString(R.string.enter_last_name));
            return false;
        } else {
            lastNameTextInputLayout.setError(null);
            return true;
        }

    }

    private boolean validateFirstNameInput() {
        String passwordInput = Objects.requireNonNull(firstNameTextInputLayout.getEditText()).getText().toString().trim();

        if (passwordInput.isEmpty()) {
            firstNameTextInputLayout.setError(getString(R.string.enter_first_name));
            return false;
        } else {
            firstNameTextInputLayout.setError(null);
            return true;
        }

    }

    private void addStudent() {
        if (validateLastNameInput() & validateFirstNameInput()) {
            Student student = createStudent();
            writeInStudentsReference(student);
            writeInMissingsReference(student);
            Toast.makeText(mContext, R.string.student_added, Toast.LENGTH_SHORT).show();
            dismiss();
        }
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

        mRootReference
                .child(App.KEY_GROUP_STUDENTS)
                .child(mKeyGroup)
                .child(student.id_student)
                .setValue(student);
    }

    private void writeInMissingsReference(Student student) {
        DatabaseReference missingReference = mRootReference.child(App.KEY_GROUP_DAY_COUPLE_MISSINGS).child(mKeyGroup);
        missingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot date : dataSnapshot.getChildren()) {
                    mRootReference
                            .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                            .child(mKeyGroup)
                            .child(student.id_student)
                            .child(date.getKey())
                            .child("student")
                            .setValue(student);
                    mRootReference
                            .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                            .child(mKeyGroup)
                            .child(date.getKey())
                            .child(student.id_student)
                            .child("student")
                            .setValue(student);

                    for (DataSnapshot couple : date.getChildren()) {
                        String keyMissing = missingReference.child(date.getKey()).child(couple.getKey()).push().getKey();
                        Missing missing = new Missing(date.getKey(), student, "null", null, Integer.valueOf(couple.getKey()));
                        missingReference
                                .child(date.getKey())
                                .child(couple.getKey())
                                .child(keyMissing)
                                .setValue(missing);

                        mRootReference
                                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                                .child(mKeyGroup)
                                .child(student.id_student)
                                .child(date.getKey())
                                .child("missings")
                                .child(keyMissing)
                                .setValue(missing);
                        mRootReference
                                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                                .child(mKeyGroup)
                                .child(date.getKey())
                                .child(student.id_student)
                                .child("missings")
                                .child(keyMissing)
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
