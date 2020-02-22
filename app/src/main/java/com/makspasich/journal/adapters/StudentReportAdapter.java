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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Missing;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentReportAdapter extends RecyclerView.Adapter<StudentReportAdapter.RVHolder> {

    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private DatabaseReference mReasonReference;
    private ChildEventListener mChildEventListener;

    private List<String> mMissingIds = new ArrayList<>();
    private List<Missing> mMissings = new ArrayList<>();

    public StudentReportAdapter(final Context context, DatabaseReference ref) {
        mContext = context;
        mReasonReference = ref;

        // Create child event listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Missing missing = dataSnapshot.getValue(Missing.class);
                mMissingIds.add(dataSnapshot.getKey());
                mMissings.add(missing);
                notifyItemInserted(mMissingIds.size() - 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                Missing newMissing = dataSnapshot.getValue(Missing.class);
                String missingKey = dataSnapshot.getKey();

                int missingIndex = mMissingIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    mMissings.set(missingIndex, newMissing);
                    notifyItemChanged(missingIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + missingKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String missingKey = dataSnapshot.getKey();

                int missingIndex = mMissingIds.indexOf(missingKey);
                if (missingIndex > -1) {
                    mMissingIds.remove(missingIndex);
                    mMissings.remove(missingIndex);
                    // Update the RecyclerView
                    notifyItemRemoved(missingIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + missingKey);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postMissings:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load missings.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mReasonReference.addChildEventListener(childEventListener);

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_report_for_student, parent, false);
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
            mReasonReference.removeEventListener(mChildEventListener);
        }
    }

    class RVHolder extends RecyclerView.ViewHolder {

        //region BindView
        @BindView(R.id.container_card_view)
        MaterialCardView container;
        @BindView(R.id.missing_title)
        TextView missingTitle;
        @BindView(R.id.status_chip)
        Chip statusChip;
        //endregion

        RVHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void bind(Missing missing) {
            missingTitle.setText(String.valueOf(missing.number_pair));
            if (missing.is_missing.equals("present")) {
                container.setBackgroundResource(R.color.present_student);
            } else if (missing.is_missing.equals("absent")) {
                container.setBackgroundResource(R.color.absent_student);
                if (missing.type_missing != null) {
                    setVisibilityStatusChip(true);
                    statusChip.setText(missing.type_missing.short_name_type);
                } else {
                    setVisibilityStatusChip(true);
                    statusChip.setText(R.string.not_set);
                }
            }

        }

        private void setVisibilityStatusChip(boolean visibility) {
            if (visibility) {
                statusChip.setVisibility(View.VISIBLE);
            } else {
                statusChip.setVisibility(View.GONE);
            }
        }
    }
}
