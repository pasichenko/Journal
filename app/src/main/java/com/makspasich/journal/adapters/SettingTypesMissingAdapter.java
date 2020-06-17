package com.makspasich.journal.adapters;

import android.content.Context;
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
import com.makspasich.journal.activities.SettingTypesMissingActivity;
import com.makspasich.journal.data.model.TypeMissing;
import com.makspasich.journal.databinding.ItemTypesBinding;

import java.util.ArrayList;
import java.util.List;

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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTypesBinding binding = ItemTypesBinding.inflate(inflater, parent, false);
        return new RVHolder(binding);
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
        private ItemTypesBinding binding;

        RVHolder(@NonNull ItemTypesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.editTypeButton.setBackgroundResource(R.drawable.ic_edit);
            binding.editTypeButton.setOnClickListener(v -> {
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
                    //TODO: implementing change name type
                    Toast.makeText(mContext, "This functionality in progress", Toast.LENGTH_SHORT).show());
        }

        void bind(TypeMissing typeMissing) {
            binding.typeNameTextView.setText(typeMissing.name_type);
            binding.typeShortNameTextView.setText(typeMissing.short_name_type);
            binding.fullNameEt.setText(typeMissing.name_type);
            binding.shortNameEt.setText(typeMissing.short_name_type);
        }

        void hideExpandableView() {
            TransitionManager.beginDelayedTransition(binding.containerCardView, new AutoTransition());
            binding.expandableView.setVisibility(View.GONE);
            binding.editTypeButton.setBackgroundResource(R.drawable.ic_edit);
        }

        void showExpandableView() {
            TransitionManager.beginDelayedTransition(binding.containerCardView, new AutoTransition());
            binding.expandableView.setVisibility(View.VISIBLE);
            binding.editTypeButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        }
    }
}
