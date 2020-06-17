package com.makspasich.journal.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.makspasich.journal.App;
import com.makspasich.journal.adapters.SettingTypesMissingAdapter;
import com.makspasich.journal.databinding.ActivitySettingTypesMissingBinding;
import com.makspasich.journal.dialogs.AddTypeDialog;

public class SettingTypesMissingActivity extends AppCompatActivity {
    private static final String TAG = "SettingStudentsActivity";
    private ActivitySettingTypesMissingBinding binding;
    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private SettingTypesMissingAdapter mAdapter;

    public SettingTypesMissingActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingTypesMissingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.addTypeFab.setOnClickListener(v -> {
            AddTypeDialog custom = new AddTypeDialog();
            custom.show(getSupportFragmentManager(), "");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query typesQuery = mRootReference.child(App.KEY_GROUP_TYPES_MISSING).child(App.getInstance().getKeyGroup());
        mAdapter = new SettingTypesMissingAdapter(this, typesQuery);
       binding. recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
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
}
