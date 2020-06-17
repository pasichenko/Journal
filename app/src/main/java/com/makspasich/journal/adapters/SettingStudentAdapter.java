package com.makspasich.journal.adapters;

import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.makspasich.journal.R;
import com.makspasich.journal.activities.SettingStudentsActivity;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.User;
import com.makspasich.journal.databinding.ItemStudentsBinding;
import com.makspasich.journal.dialogs.LinkedStudentDialog;

import java.util.ArrayList;
import java.util.List;

public class SettingStudentAdapter extends RecyclerView.Adapter<SettingStudentAdapter.RVHolder> {
    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private Query mQuery;
    private ChildEventListener mChildEventListener;

    private List<String> mStudentIds = new ArrayList<>();
    private List<Student> mStudents = new ArrayList<>();


    public SettingStudentAdapter(final Context context, Query query) {
        mContext = context;
        mQuery = query;

        // Create child event listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new missing has been added, add it to the displayed list
                Student missing = dataSnapshot.getValue(Student.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mStudentIds.add(dataSnapshot.getKey());
                mStudents.add(missing);
                notifyItemInserted(mStudents.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A missing has changed, use the key to determine if we are displaying this
                // missing and if so displayed the changed missing.
                Student newStudent = dataSnapshot.getValue(Student.class);
                String missingKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int missingIndex = mStudentIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    // Replace with the new data
                    mStudents.set(missingIndex, newStudent);

                    // Update the RecyclerView
                    notifyItemChanged(missingIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + missingKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A missing has changed, use the key to determine if we are displaying this
                // missing and if so remove it.
                String missingKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int missingIndex = mStudentIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    // Remove data from the list
                    mStudentIds.remove(missingIndex);
                    mStudents.remove(missingIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(missingIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + missingKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A missing has changed position, use the key to determine if we are
                // displaying this missing and if so move it.
                Student movedStudent = dataSnapshot.getValue(Student.class);
                String missingKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postStudents:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load missings.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mQuery.addChildEventListener(childEventListener);

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public SettingStudentAdapter.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemStudentsBinding binding = ItemStudentsBinding.inflate(inflater, parent, false);
        return new RVHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingStudentAdapter.RVHolder holder, int position) {
        holder.bind(mStudents.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mQuery.removeEventListener(mChildEventListener);
        }
    }

    public class RVHolder extends RecyclerView.ViewHolder {
        private ItemStudentsBinding binding;

        RVHolder(@NonNull ItemStudentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.editStudentButton.setBackgroundResource(R.drawable.ic_edit);
            binding.linkUserButton.setBackgroundResource(R.drawable.ic_insert_link);
            binding.editStudentButton.setOnClickListener(v -> {
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
            binding.saveActionButton.setOnClickListener(view ->
                    //TODO: implementing change name group
                    Toast.makeText(mContext, "This functionality in progress", Toast.LENGTH_SHORT).show());
        }

        void bind(Student student) {
            String fio = student.last_name + " " + student.first_name;
            binding.personNameTextView.setText(fio);
            binding.linkUserButton.setOnClickListener(v -> {
                LinkedStudentDialog custom = new LinkedStudentDialog();
                Bundle arg = new Bundle();
                arg.putString("KEY_STUDENT", student.id_student);
                custom.setArguments(arg);
                custom.show(((SettingStudentsActivity) mContext).getSupportFragmentManager(), "");
            });

            User user = student.user_reference;
            if (user != null) {
                binding.uidTextView.setText(mContext.getString(R.string.user_id, user.uid));
                binding.usernameTextView.setText(mContext.getString(R.string.username, user.username));
            }
            binding.lastNameEt.setText(student.last_name);
            binding.firstNameEt.setText(student.first_name);
        }

        void hideExpandableView() {
            TransitionManager.beginDelayedTransition(binding.containerCardView, new AutoTransition());
            binding.expandableView.setVisibility(View.GONE);
            binding.editStudentButton.setBackgroundResource(R.drawable.ic_edit);
        }

        void showExpandableView() {
            TransitionManager.beginDelayedTransition(binding.containerCardView, new AutoTransition());
            binding.expandableView.setVisibility(View.VISIBLE);
            binding.editStudentButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        }
    }
}
