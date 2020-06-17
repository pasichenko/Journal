package com.makspasich.journal.dialogs;


import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.TypeMissing;
import com.makspasich.journal.databinding.AddTypeDialogBinding;

public class AddTypeDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";

    private final DatabaseReference mRootReference;
    private AddTypeDialogBinding binding;
    private AlertDialog dialog;

    public AddTypeDialog() {
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View mRootView = inflater.inflate(R.layout.add_type_dialog, null);
        binding = AddTypeDialogBinding.bind(mRootView);
        builder.setTitle(R.string.add_type);
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            addType();
            Toast.makeText(requireContext(), R.string.type_added, Toast.LENGTH_SHORT).show();
            dismiss();
        });
        builder.setNegativeButton(R.string.cancel, null);

        binding.fullNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    binding.fullNameTil.setError(getString(R.string.enter_full_name));
                } else {
                    binding.fullNameTil.setError(null);
                }
            }
        });

        binding.shortNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    binding.shortNameTil.setError(getString(R.string.enter_short_name));
                } else {
                    binding.shortNameTil.setError(null);
                }
            }
        });

        dialog = builder.create();
        return dialog;
    }

    private void addType() {
        TypeMissing typeMissing = createType();
        writeInTypesReference(typeMissing);
        writeInGroupTypesReference(typeMissing);
    }

    private TypeMissing createType() {
        String keyType = mRootReference.child(App.KEY_TYPES_MISSING).push().getKey();
        String fullName = binding.fullNameEt.getText().toString();
        String shortName = binding.shortNameEt.getText().toString();
        return new TypeMissing(keyType, fullName, shortName);
    }

    private void writeInTypesReference(TypeMissing typeMissing) {
        mRootReference
                .child(App.KEY_TYPES_MISSING)
                .child(typeMissing.id_type)
                .setValue(typeMissing);
    }

    private void writeInGroupTypesReference(TypeMissing typeMissing) {
        mRootReference
                .child(App.KEY_GROUP_TYPES_MISSING)
                .child(App.getInstance().getKeyGroup())
                .child(typeMissing.id_type)
                .setValue(typeMissing);
    }
}
