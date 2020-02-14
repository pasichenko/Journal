package com.makspasich.journal.adapters;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.makspasich.journal.R;
import com.makspasich.journal.activities.SettingTypesMissingActivity;
import com.makspasich.journal.data.model.TypeMissing;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingTypesMissingAdapter extends RecyclerView.Adapter<SettingTypesMissingAdapter.RVHolder> {
    private static final String TAG = SettingTypesMissingActivity.class.getSimpleName();
    private Context mContext;
    private Query mQuery;
    private ChildEventListener mChildEventListener;

    private List<String> mTypeIds = new ArrayList<>();
    private List<TypeMissing> mTypes = new ArrayList<>();

    public SettingTypesMissingAdapter(final Context context, Query query) {
        mContext = context;
        mQuery = query;


        // Create child event listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new typeMissing has been added, add it to the displayed list
                TypeMissing typeMissing = dataSnapshot.getValue(TypeMissing.class);


                // Update RecyclerView
                mTypeIds.add(dataSnapshot.getKey());
                mTypes.add(typeMissing);
                notifyItemInserted(mTypes.size() - 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                TypeMissing typeMissing = dataSnapshot.getValue(TypeMissing.class);
                String typeKey = dataSnapshot.getKey();

                int typeIndex = mTypeIds.indexOf(typeKey);
                if (typeIndex > -1) {
                    // Replace with the new data
                    mTypes.set(typeIndex, typeMissing);

                    // Update the RecyclerView
                    notifyItemChanged(typeIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + typeKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String typeKey = dataSnapshot.getKey();

                int typeIndex = mTypeIds.indexOf(typeKey);
                if (typeIndex > -1) {
                    // Remove data from the list
                    mTypeIds.remove(typeIndex);
                    mTypes.remove(typeIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(typeIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + typeKey);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                TypeMissing movedStudent = dataSnapshot.getValue(TypeMissing.class);
                String missingKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postStudents:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load types.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mQuery.addChildEventListener(childEventListener);

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public SettingTypesMissingAdapter.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_types, parent, false);
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingTypesMissingAdapter.RVHolder holder, int position) {
        holder.bind(mTypes.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mTypes.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mQuery.removeEventListener(mChildEventListener);
        }
    }

    public class RVHolder extends RecyclerView.ViewHolder {
        //region BindView
        @BindView(R.id.container_card_view)
        MaterialCardView containerCardView;
        @BindView(R.id.type_name_text_view)
        TextView typeNameTextView;
        @BindView(R.id.type_short_name_text_view)
        TextView typeShortTextView;
        @BindView(R.id.edit_type_button)
        Button editTypeButton;
        @BindView(R.id.header_view)
        ConstraintLayout headerView;
        @BindView(R.id.expandable_view)
        ConstraintLayout expandableView;

        @BindView(R.id.full_name_til)
        TextInputLayout fullNameTextInputLayout;
        @BindView(R.id.full_name_et)
        TextInputEditText fullNameEditText;
        @BindView(R.id.short_name_til)
        TextInputLayout shortNameTextInputLayout;
        @BindView(R.id.short_name_et)
        TextInputEditText shortNameEditText;
        @BindView(R.id.cancel_action_button)
        MaterialButton cancelActionButton;
        @BindView(R.id.save_action_button)
        MaterialButton saveActionButton;
        //endregion

        RVHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editTypeButton.setBackgroundResource(R.drawable.ic_edit);
            editTypeButton.setOnClickListener(v -> {
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
            saveActionButton.setOnClickListener(view ->
                    //TODO: implementing change name type
                    Toast.makeText(mContext, "This functionality in progress", Toast.LENGTH_SHORT).show());
        }

        void bind(TypeMissing typeMissing) {
            typeNameTextView.setText(typeMissing.name_type);
            typeShortTextView.setText(typeMissing.short_name_type);
            fullNameEditText.setText(typeMissing.name_type);
            shortNameEditText.setText(typeMissing.short_name_type);
        }

        void hideExpandableView() {
            TransitionManager.beginDelayedTransition(containerCardView, new AutoTransition());
            expandableView.setVisibility(View.GONE);
            editTypeButton.setBackgroundResource(R.drawable.ic_edit);
        }

        void showExpandableView() {
            TransitionManager.beginDelayedTransition(containerCardView, new AutoTransition());
            expandableView.setVisibility(View.VISIBLE);
            editTypeButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        }
    }
}
