package com.makspasich.journal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.utils.FirebaseDB;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetAttendanceAdapter extends RecyclerView.Adapter<SetAttendanceAdapter.RVHolder> {

    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private DatabaseReference mMissingCoupleReference;
    private ChildEventListener mChildEventListener;

    private List<String> mMissingIds = new ArrayList<>();
    private List<Missing> mMissings = new ArrayList<>();

    private final String mKeyGroup;

    public SetAttendanceAdapter(final Context context, DatabaseReference ref, String keyGroup) {
        mContext = context;
        mMissingCoupleReference = ref;
        mKeyGroup = keyGroup;
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_attendance_binding, parent, false);
        return new RVHolder(view);
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

        //region BindView
        @BindView(R.id.container_card_view)
        MaterialCardView container;
        @BindView(R.id.person_name)
        TextView personName;
        @BindView(R.id.manage_attendance)
        Group manageAttendance;
        @BindView(R.id.true_button)
        Button trueButton;
        @BindView(R.id.false_button)
        Button falseButton;
        @BindView(R.id.cancel_button)
        Button cancelButton;
        //endregion

        String keyMissing;
        private Missing missing;

        RVHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            trueButton.setBackgroundResource(R.drawable.ic_check_24dp);
            falseButton.setBackgroundResource(R.drawable.ic_close_24dp);
            cancelButton.setBackgroundResource(R.drawable.ic_cancel);
            trueButton.setOnClickListener(view -> updateMissing("present"));
            falseButton.setOnClickListener(view -> updateMissing("absent"));
            cancelButton.setOnClickListener(view -> updateMissing("null"));
        }

        void bind(String keyMissing, Missing missing) {
            this.keyMissing = keyMissing;
            this.missing = missing;
            String fio = missing.student.last_name + " " + missing.student.first_name;
            personName.setText(fio);
            if (missing.is_missing.equals("null")) {
                manageAttendance.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
            } else {
                manageAttendance.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
            }
            if (missing.is_missing.equals("present")) {
                container.setBackgroundResource(R.color.present_student);
            } else if (missing.is_missing.equals("absent")) {
                container.setBackgroundResource(R.color.absent_student);
            }
        }

        void updateMissing(String status) {
            FirebaseDB.updateStatusMissingInDB(mKeyGroup,missing.date,missing.number_pair,keyMissing,missing.student.id_student,status);
        }
    }
}
