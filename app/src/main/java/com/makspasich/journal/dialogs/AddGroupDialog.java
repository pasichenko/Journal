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
import com.makspasich.journal.data.model.Group;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.utils.FirebaseDB;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddGroupDialog extends DialogFragment {
    private static final String TAG = "AddStudentDialog";
    private Context mContext;
    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private View mRootView;
    private AlertDialog dialog;

    //region BindView
    @BindView(R.id.create_group_til)
    TextInputLayout groupNameTextInputLayout;
    @BindView(R.id.create_group_et)
    TextInputEditText groupNameLinkEditText;
    //endregion

    private String mKeyStudent;

    public AddGroupDialog(Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.AlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = inflater.inflate(R.layout.create_group_dialog, null);
        mUnbinder = ButterKnife.bind(this, mRootView);
        builder.setTitle("Create group");
        builder.setView(mRootView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> createGroup());
        builder.setNegativeButton(R.string.cancel, null);

        groupNameLinkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    groupNameTextInputLayout.setError(getString(R.string.enter_name_group));
                } else {
                    groupNameTextInputLayout.setError(null);
                }
            }
        });

        groupNameLinkEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                createGroup();
            }
            return false;
        });
        dialog = builder.create();
        return dialog;
    }

    private boolean validateLastNameInput() {
        String loginInput = Objects.requireNonNull(groupNameTextInputLayout.getEditText()).getText().toString().trim();

        if (loginInput.isEmpty()) {
            groupNameTextInputLayout.setError(getString(R.string.enter_name_group));
            return false;
        } else {
            groupNameTextInputLayout.setError(null);
            return true;
        }

    }

    private void createGroup() {
        if (validateLastNameInput()) {
            String nameGroup = groupNameLinkEditText.getText().toString();

            String keyGroup = FirebaseDatabase.getInstance().getReference().child(App.KEY_GROUPS).push().getKey();
            App.getInstance().setKeyGroup(keyGroup);
            String keyStarosta = FirebaseDatabase.getInstance().getReference().child(App.KEY_GROUP_STUDENTS).push().getKey();

            Student starosta = new Student(keyStarosta, "Староста", "", null);
            FirebaseDatabase.getInstance().getReference().child(App.KEY_GROUP_STUDENTS)
                    .child(keyGroup).child(keyStarosta).setValue(starosta);

            FirebaseDB.linkedUserInDB(App.getInstance().getKeyUser(), keyStarosta);
            starosta.user_reference = App.getInstance().getUser();
            Group group = new Group(keyGroup, nameGroup, starosta);
            FirebaseDatabase.getInstance().getReference().child(App.KEY_GROUPS).child(keyGroup).setValue(group);

            Toast.makeText(mContext, "Group added", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
