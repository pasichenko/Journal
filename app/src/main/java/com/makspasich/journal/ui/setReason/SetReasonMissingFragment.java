package com.makspasich.journal.ui.setReason;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.SetReasonMissingAdapter;
import com.makspasich.journal.data.model.TypeMissing;
import com.makspasich.journal.databinding.SetReasonFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class SetReasonMissingFragment extends Fragment {
    private static final String TAG = "SetReasonMissingFragment";
    private SetReasonFragmentBinding binding;

    private final DatabaseReference mRootReference;
    private DatabaseReference mReasonReference;

    private SetReasonMissingAdapter mAdapter;

    public SetReasonMissingFragment() {
        this.mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        binding = SetReasonFragmentBinding.inflate(inflater, container, false);

        mReasonReference = mRootReference.child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(App.getInstance().getKeyGroup())
                .child(App.getInstance().getSelectedDateString());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRootReference.child(App.KEY_GROUP_TYPES_MISSING)
                .child(App.getInstance().getKeyGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TypeMissing> types = new ArrayList<>();
                for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                    TypeMissing type = typeSnapshot.getValue(TypeMissing.class);
                    types.add(type);
                }
                mAdapter = new SetReasonMissingAdapter(getContext(), mReasonReference, types);
                binding.recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
