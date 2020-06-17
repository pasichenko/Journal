package com.makspasich.journal.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.makspasich.journal.App;
import com.makspasich.journal.adapters.SettingStudentAdapter;
import com.makspasich.journal.databinding.ActivitySettingStudentsBinding;
import com.makspasich.journal.dialogs.AddStudentDialog;

public class SettingStudentsActivity extends AppCompatActivity {
    private static final String TAG = "SettingStudentsActivity";
    private ActivitySettingStudentsBinding binding;
    private final DatabaseReference mRootReference;

    private SettingStudentAdapter mAdapter;

    public SettingStudentsActivity() {
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.addStudentFab.setOnClickListener(v -> {
            AddStudentDialog custom = new AddStudentDialog();
            custom.show(getSupportFragmentManager(), "");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query attendanceQuery = mRootReference.child(App.KEY_GROUP_STUDENTS).child(App.getInstance().getKeyGroup()).orderByChild("last_name");
        mAdapter = new SettingStudentAdapter(this, attendanceQuery);
        binding.recyclerView.setAdapter(mAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
