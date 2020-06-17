package com.makspasich.journal.dialogs;


import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.databinding.AddStudentDialogBinding;

import java.util.Objects;

public class AddStudentDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";

    private final DatabaseReference mRootReference;

    private AddStudentDialogBinding binding;
    private AlertDialog dialog;

    public AddStudentDialog() {
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View mRootView = inflater.inflate(R.layout.add_student_dialog, null);
        binding = AddStudentDialogBinding.bind(mRootView);
        builder.setTitle(R.string.add_student);
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> addStudent());
        builder.setNegativeButton(R.string.cancel, null);

        binding.lastNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    binding.lastNameTil.setError(getString(R.string.enter_last_name));
                } else {
                    binding.lastNameTil.setError(null);
                }
            }
        });

        binding.firstNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    binding.firstNameTil.setError(getString(R.string.enter_first_name));
                } else {
                    binding.firstNameTil.setError(null);
                }
            }
        });
        binding.firstNameEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addStudent();
            }
            return false;
        });
        dialog = builder.create();
        return dialog;
    }

    private boolean validateLastNameInput() {
        String loginInput = Objects.requireNonNull(binding.lastNameTil.getEditText()).getText().toString().trim();

        if (loginInput.isEmpty()) {
            binding.lastNameTil.setError(getString(R.string.enter_last_name));
            return false;
        } else {
            binding.lastNameTil.setError(null);
            return true;
        }

    }

    private boolean validateFirstNameInput() {
        String passwordInput = Objects.requireNonNull(binding.firstNameTil.getEditText()).getText().toString().trim();

        if (passwordInput.isEmpty()) {
            binding.firstNameTil.setError(getString(R.string.enter_first_name));
            return false;
        } else {
            binding.firstNameTil.setError(null);
            return true;
        }

    }

    private void addStudent() {
        if (validateLastNameInput() & validateFirstNameInput()) {
            String keyStudent = mRootReference.child(App.KEY_STUDENTS).push().getKey();
            String lastName = binding.lastNameEt.getText().toString();
            String firstName = binding.firstNameEt.getText().toString();
            Student student = new Student(keyStudent, lastName, firstName, null);
            FirebaseDB.writeNewStudentInDB(student);
            Toast.makeText(requireContext(), R.string.student_added, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
