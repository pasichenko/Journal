package com.makspasich.journal.ui.settingGroupActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.activities.SettingStudentsActivity;
import com.makspasich.journal.activities.SettingTypesMissingActivity;
import com.makspasich.journal.data.model.Group;
import com.makspasich.journal.databinding.ActivitySettingGroupBinding;

public class SettingGroupActivity extends AppCompatActivity {
    private static final String TAG = "SettingGroupActivity";

    private ActivitySettingGroupBinding binding;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private GroupNameViewHolder mGroupNameViewHolder;
    private StudentsViewHolder mStudentsViewHolder;
    private TypeMissingViewHolder mTypeMissingViewHolder;

    public SettingGroupActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGroupNameViewHolder = new GroupNameViewHolder(binding.containerViewGroupName);
        mStudentsViewHolder = new StudentsViewHolder(binding.containerViewListStudent);
        mTypeMissingViewHolder = new TypeMissingViewHolder(binding.containerViewListTypesMissing);
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

    class GroupNameViewHolder {
        ViewGroup container;

        GroupNameViewHolder(ViewGroup container) {
            this.container = container;
            binding.editNameButton.setBackgroundResource(R.drawable.ic_edit);
            binding.editNameButton.setOnClickListener(v -> {
                if (binding.expandableView.getVisibility() == View.GONE) {
                    showExpandableView();
                } else {
                    hideExpandableView();
                }
            });
            binding.cancelActionButton.setOnClickListener(v -> {
                if (binding.expandableView.getVisibility() == View.VISIBLE) {
                    hideExpandableView();
                }
            });
            mRootReference.child(App.KEY_GROUPS).child(App.getInstance().getKeyGroup()).addValueEventListener(getNameGroup);
            binding.saveActionButton.setOnClickListener(view ->
                    //TODO: implementing change last first name student
                    Toast.makeText(getBaseContext(), "This functionality in progress", Toast.LENGTH_SHORT).show());
        }

        void hideExpandableView() {
            TransitionManager.beginDelayedTransition(container, new AutoTransition());
            binding.expandableView.setVisibility(View.GONE);
            binding.editNameButton.setBackgroundResource(R.drawable.ic_edit);
        }

        void showExpandableView() {
            TransitionManager.beginDelayedTransition(container, new AutoTransition());
            binding.expandableView.setVisibility(View.VISIBLE);
            binding.editNameButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        }

        ValueEventListener getNameGroup = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);

                binding.groupNameTextView.setText("Group: " + group.number_group);
                binding.editText.setText(group.number_group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    class StudentsViewHolder {
        ViewGroup container;

        StudentsViewHolder(ViewGroup container) {
            this.container = container;
            binding.checkStudentListButton.setBackgroundResource(R.drawable.ic_eye);
            binding.checkStudentListButton.setOnClickListener(v -> {
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

        TypeMissingViewHolder(ViewGroup container) {
            this.container = container;
            binding.checkTypesMissingButton.setBackgroundResource(R.drawable.ic_eye);
            binding.checkTypesMissingButton.setOnClickListener(v -> {
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
