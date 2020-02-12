package com.makspasich.journal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Missing;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetAttendanceAdapter extends RecyclerView.Adapter<SetAttendanceAdapter.RVHolder> {

    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private Query mQuery;
    private ChildEventListener mChildEventListener;

    private List<String> mMissingIds = new ArrayList<>();
    private List<Missing> mMissings = new ArrayList<>();

    public SetAttendanceAdapter(final Context context, Query query) {
        mContext = context;
        mQuery = query;


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
        mQuery.addChildEventListener(childEventListener);

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
        holder.bind(mMissings.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mMissings.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mQuery.removeEventListener(mChildEventListener);
        }
    }

    class RVHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.person_name)
        TextView personName;

        RVHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Missing missing) {
            String fio = missing.student.last_name + " " + missing.student.first_name;
            personName.setText(fio);
        }
    }
}
