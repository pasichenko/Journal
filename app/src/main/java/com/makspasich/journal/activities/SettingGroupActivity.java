package com.makspasich.journal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
import com.makspasich.journal.data.model.Group;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingGroupActivity extends AppCompatActivity {
    private static final String TAG = "SettingGroupActivity";

    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;
    //region BindView
    @BindView(R.id.container_view_group_name)
    MaterialCardView containerViewGroupName;
    @BindView(R.id.container_view_list_student)
    MaterialCardView containerViewListStudent;
    @BindView(R.id.container_view_list_types_missing)
    MaterialCardView containerViewTypesMissing;

    private GroupNameViewHolder mGroupNameViewHolder;
    private StudentsViewHolder mStudentsViewHolder;
    private TypeMissingViewHolder mTypeMissingViewHolder;
    //endregion

    public SettingGroupActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_group);
        mUnbinder = ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGroupNameViewHolder = new GroupNameViewHolder(containerViewGroupName);
        mStudentsViewHolder = new StudentsViewHolder(containerViewListStudent);
        mTypeMissingViewHolder = new TypeMissingViewHolder(containerViewTypesMissing);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    class GroupNameViewHolder {
        ViewGroup container;

        //region BindView
        @BindView(R.id.group_name_text_view)
        TextView groupNameTextView;
        @BindView(R.id.edit_name_button)
        Button editNameButton;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;

        @BindView(R.id.text_input_layout)
        TextInputLayout textInputLayout;
        @BindView(R.id.edit_text)
        TextInputEditText textInputEditText;
        @BindView(R.id.cancel_action_button)
        MaterialButton cancelActionButton;
        @BindView(R.id.save_action_button)
        MaterialButton saveActionButton;
        //endregion

        GroupNameViewHolder(ViewGroup container) {
            ButterKnife.bind(this, container);
            this.container = container;
            editNameButton.setBackgroundResource(R.drawable.ic_edit);
            editNameButton.setOnClickListener(v -> {
                if (expandableView.getVisibility() == View.GONE) {
                    showExpandableView();
                } else {
                    hideExpandableView();
                }
            });
            cancelActionButton.setOnClickListener(v -> {
                if (expandableView.getVisibility() == View.VISIBLE) {
                    hideExpandableView();
                }
            });
            mRootReference.child(App.KEY_GROUPS).child(App.getInstance().getKeyGroup()).addValueEventListener(getNameGroup);
            saveActionButton.setOnClickListener(view ->
                    //TODO: implementing change last first name student
                    Toast.makeText(getBaseContext(), "This functionality in progress", Toast.LENGTH_SHORT).show());
        }

        void hideExpandableView() {
            TransitionManager.beginDelayedTransition(container, new AutoTransition());
            expandableView.setVisibility(View.GONE);
            editNameButton.setBackgroundResource(R.drawable.ic_edit);
        }

        void showExpandableView() {
            TransitionManager.beginDelayedTransition(container, new AutoTransition());
            expandableView.setVisibility(View.VISIBLE);
            editNameButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        }

        ValueEventListener getNameGroup = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);

                mGroupNameViewHolder.groupNameTextView.setText("Group: " + group.number_group);
                mGroupNameViewHolder.textInputEditText.setText(group.number_group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    class StudentsViewHolder {
        ViewGroup container;

        //region BindView
        @BindView(R.id.student_list_text_view)
        TextView studentListTextView;
        @BindView(R.id.check_student_list_button)
        Button checkStudentListButton;
        //endregion

        StudentsViewHolder(ViewGroup container) {
            ButterKnife.bind(this, container);
            this.container = container;
            checkStudentListButton.setBackgroundResource(R.drawable.ic_eye);
            checkStudentListButton.setOnClickListener(v -> {
                Intent intent = new Intent(SettingGroupActivity.this, SettingStudentsActivity.class);
                startActivity(intent);
            });
            this.container.setOnClickListener(v -> {
                Intent intent = new Intent(SettingGroupActivity.this, SettingStudentsActivity.class);
                startActivity(intent);
            });
        }
    }

    class TypeMissingViewHolder {
        ViewGroup container;

        //region BindView
        @BindView(R.id.types_missing_text_view)
        TextView studentListTextView;
        @BindView(R.id.check_types_missing_button)
        Button checkStudentListButton;
        //endregion

        TypeMissingViewHolder(ViewGroup container) {
            ButterKnife.bind(this, container);
            this.container = container;
            checkStudentListButton.setBackgroundResource(R.drawable.ic_eye);
            checkStudentListButton.setOnClickListener(v -> {
                Intent intent = new Intent(SettingGroupActivity.this, SettingTypesMissingActivity.class);
                startActivity(intent);
            });
            this.container.setOnClickListener(v -> {
                Intent intent = new Intent(SettingGroupActivity.this, SettingTypesMissingActivity.class);
                startActivity(intent);
            });
        }
    }
}
