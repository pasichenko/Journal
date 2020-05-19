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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.utils.FirebaseDB;

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
            String keyStudent = mRootReference.child(App.KEY_STUDENTS).push().getKey();
            String lastName = lastNameEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString();
            Student student = new Student(keyStudent, lastName, firstName, null);
            FirebaseDB.writeNewStudentInDB(student);
            Toast.makeText(mContext, R.string.student_added, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
