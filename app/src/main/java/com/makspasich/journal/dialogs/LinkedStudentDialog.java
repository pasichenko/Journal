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
import com.makspasich.journal.R;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.databinding.LinkedStudentDialogBinding;

import java.util.Objects;

public class LinkedStudentDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";

    private LinkedStudentDialogBinding binding;
    private AlertDialog dialog;

    private String mKeyStudent;

    public LinkedStudentDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View mRootView = inflater.inflate(R.layout.linked_student_dialog, null);
        binding = LinkedStudentDialogBinding.bind(mRootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mKeyStudent = bundle.getString("KEY_STUDENT");
        } else {
            Toast.makeText(requireContext(), "Oops, no group", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        builder.setTitle("Add student");
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> linkedUser());
        builder.setNegativeButton(R.string.cancel, null);

        binding.userLinkEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    binding.userLinkTil.setError(getString(R.string.enter_last_name));
                } else {
                    binding.userLinkTil.setError(null);
                }
            }
        });

        binding.userLinkEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                linkedUser();
            }
            return false;
        });
        dialog = builder.create();
        return dialog;
    }

    private boolean validateLastNameInput() {
        String loginInput = Objects.requireNonNull(binding.userLinkTil.getEditText()).getText().toString().trim();

        if (loginInput.isEmpty()) {
            binding.userLinkTil.setError(getString(R.string.enter_last_name));
            return false;
        } else {
            binding.userLinkTil.setError(null);
            return true;
        }

    }

    private void linkedUser() {
        if (validateLastNameInput()) {
            String keyUser = binding.userLinkEt.getText().toString();
            FirebaseDB.linkedUserInDB(keyUser, mKeyStudent);
            Toast.makeText(requireContext(), "Student added", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
