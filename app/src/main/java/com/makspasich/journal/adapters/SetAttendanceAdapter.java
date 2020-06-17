package com.makspasich.journal.adapters;

import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.StatusMissing;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.databinding.ItemAttendanceBindingBinding;

import java.util.ArrayList;
import java.util.List;

public class SetAttendanceAdapter extends RecyclerView.Adapter<SetAttendanceAdapter.RVHolder> {

    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private DatabaseReference mMissingCoupleReference;
    private ChildEventListener mChildEventListener;

    private List<String> mMissingIds = new ArrayList<>();
    private List<Missing> mMissings = new ArrayList<>();

    public SetAttendanceAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mMissingCoupleReference = ref;
        Query attendanceQuery = mMissingCoupleReference.orderByChild("student/last_name");

        // Create child event listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new missing has been added, add it to the displayed list
                Missing missing = dataSnapshot.getValue(Missing.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mMissingIds.add(dataSnapshot.getKey());
                mMissings.add(missing);
                notifyItemInserted(mMissings.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A missing has changed, use the key to determine if we are displaying this
                // missing and if so displayed the changed missing.
                Missing newMissing = dataSnapshot.getValue(Missing.class);
                String missingKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int missingIndex = mMissingIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    // Replace with the new data
                    mMissings.set(missingIndex, newMissing);

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
                int missingIndex = mMissingIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    // Remove data from the list
                    mMissingIds.remove(missingIndex);
                    mMissings.remove(missingIndex);

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
                Missing movedMissing = dataSnapshot.getValue(Missing.class);
                String missingKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postMissings:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load missings.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        attendanceQuery.addChildEventListener(childEventListener);

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAttendanceBindingBinding binding = ItemAttendanceBindingBinding.inflate(inflater, parent, false);
        return new RVHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {
        holder.bind(mMissingIds.get(holder.getAdapterPosition()),
                mMissings.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mMissings.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mMissingCoupleReference.removeEventListener(mChildEventListener);
        }
    }

    class RVHolder extends RecyclerView.ViewHolder {
        private ItemAttendanceBindingBinding binding;

        String keyMissing;
        private Missing missing;

        RVHolder(@NonNull ItemAttendanceBindingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.trueButton.setBackgroundResource(R.drawable.ic_check_24dp);
            binding.falseButton.setBackgroundResource(R.drawable.ic_close_24dp);
            binding.cancelButton.setBackgroundResource(R.drawable.ic_cancel);
            binding.trueButton.setOnClickListener(view -> updateMissing(StatusMissing.PRESENT));
            binding.falseButton.setOnClickListener(view -> updateMissing(StatusMissing.ABSENT));
            binding.cancelButton.setOnClickListener(view -> updateMissing(StatusMissing.NULL));
        }

        void bind(String keyMissing, Missing missing) {
            this.keyMissing = keyMissing;
            this.missing = missing;
            String fio = missing.student.last_name + " " + missing.student.first_name;
            binding.personName.setText(fio);
            if (missing.is_missing == StatusMissing.NULL) {
                binding.manageAttendance.setVisibility(View.VISIBLE);
                binding.cancelButton.setVisibility(View.GONE);
            } else {
                binding.manageAttendance.setVisibility(View.GONE);
                binding.cancelButton.setVisibility(View.VISIBLE);
            }
            if (missing.is_missing == StatusMissing.PRESENT) {
                binding.containerCardView.setBackgroundResource(R.color.present_student);
            } else if (missing.is_missing == StatusMissing.ABSENT) {
                binding.containerCardView.setBackgroundResource(R.color.absent_student);
            }
        }

        void updateMissing(StatusMissing status) {
            FirebaseDB.updateStatusMissingInDB(missing.number_pair, keyMissing, missing.student.id_student, status);
        }
    }
}
