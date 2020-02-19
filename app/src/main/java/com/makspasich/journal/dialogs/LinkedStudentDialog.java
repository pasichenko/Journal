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
import com.makspasich.journal.R;
import com.makspasich.journal.activities.SignInActivity;
import com.makspasich.journal.data.utils.FirebaseDB;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LinkedStudentDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";
    private Context mContext;
    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private View mRootView;
    private AlertDialog dialog;

    //region BindView
    @BindView(R.id.user_link_til)
    TextInputLayout userLinkTextInputLayout;
    @BindView(R.id.user_link_et)
    TextInputEditText userLinkEditText;
    //endregion

    private String mKeyGroup;
    private String mKeyStudent;

    public LinkedStudentDialog(Context context) {
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
        mRootView = inflater.inflate(R.layout.linked_student_dialog, null);
        mUnbinder = ButterKnife.bind(this, mRootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mKeyGroup = bundle.getString(SignInActivity.KEY_GROUP);
            mKeyStudent = bundle.getString("KEY_STUDENT");
        } else {
            Toast.makeText(mContext, "Oops, no group", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        builder.setTitle("Add student");
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> linkedUser());
        builder.setNegativeButton(R.string.cancel, null);

        userLinkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    userLinkTextInputLayout.setError(getString(R.string.enter_last_name));
                } else {
                    userLinkTextInputLayout.setError(null);
                }
            }
        });

        userLinkEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                linkedUser();
            }
            return false;
        });
        dialog = builder.create();
        return dialog;
    }

    private boolean validateLastNameInput() {
        String loginInput = Objects.requireNonNull(userLinkTextInputLayout.getEditText()).getText().toString().trim();

        if (loginInput.isEmpty()) {
            userLinkTextInputLayout.setError(getString(R.string.enter_last_name));
            return false;
        } else {
            userLinkTextInputLayout.setError(null);
            return true;
        }

    }

    private void linkedUser() {
        if (validateLastNameInput()) {
            String keyUser = userLinkEditText.getText().toString();
            FirebaseDB.linkedUserInDB(mKeyGroup, keyUser, mKeyStudent);
            Toast.makeText(mContext, "Student added", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
