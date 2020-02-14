package com.makspasich.journal.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.adapters.SettingTypesMissingAdapter;
import com.makspasich.journal.dialogs.AddTypeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingTypesMissingActivity extends AppCompatActivity {
    private static final String TAG = "SettingStudentsActivity";
    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    //region BindView
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.add_type_fab)
    protected FloatingActionButton mAddTypeAB;
    //endregion

    private SettingTypesMissingAdapter mAdapter;
    private String mKeyGroup;

    public SettingTypesMissingActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_types_missing);
        mUnbinder = ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mKeyGroup = getIntent().getStringExtra(SignInActivity.KEY_GROUP);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddTypeAB.setOnClickListener(v -> {
            AddTypeDialog custom = new AddTypeDialog(this);
            Bundle arg = new Bundle();
            arg.putString(SignInActivity.KEY_GROUP, mKeyGroup);
            custom.setArguments(arg);
            custom.show(getSupportFragmentManager(), "");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query typesQuery = mRootReference.child(App.KEY_GROUP_TYPES_MISSING).child(mKeyGroup);
        mAdapter = new SettingTypesMissingAdapter(this, typesQuery);
        mRecyclerView.setAdapter(mAdapter);
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
        mUnbinder.unbind();
    }
}
