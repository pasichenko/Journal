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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.TypeMissing;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddTypeDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";
    private Context mContext;
    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private View mRootView;
    private AlertDialog dialog;

    //region BindView
    @BindView(R.id.full_name_til)
    TextInputLayout fullNameTextInputLayout;
    @BindView(R.id.full_name_et)
    TextInputEditText fullNameEditText;
    @BindView(R.id.short_name_til)
    TextInputLayout shortNameTextInputLayout;
    @BindView(R.id.short_name_et)
    TextInputEditText shortNameEditText;
    //endregion

    public AddTypeDialog(Context context) {
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
        mRootView = inflater.inflate(R.layout.add_type_dialog, null);
        mUnbinder = ButterKnife.bind(this, mRootView);
        builder.setTitle(R.string.add_type);
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            addType();
            Toast.makeText(mContext, R.string.type_added, Toast.LENGTH_SHORT).show();
            dismiss();
        });
        builder.setNegativeButton(R.string.cancel, null);

        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    fullNameTextInputLayout.setError(getString(R.string.enter_full_name));
                } else {
                    fullNameTextInputLayout.setError(null);
                }
            }
        });

        shortNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    shortNameTextInputLayout.setError(getString(R.string.enter_short_name));
                } else {
                    shortNameTextInputLayout.setError(null);
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
        String fullName = fullNameEditText.getText().toString();
        String shortName = shortNameEditText.getText().toString();
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
