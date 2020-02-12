package com.makspasich.journal.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.R;
import com.makspasich.journal.fragments.SetAttendance.SetAttendanceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private Unbinder mUnbinder;

    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    //region BindView
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @BindView(R.id.fab)
    protected FloatingActionButton fab;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    private HeaderViewHolder mHeaderView;
    //endregion

    private String mKeyGroup;

    public MainActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        View header = mNavigationView.getHeaderView(0);
        mHeaderView = new HeaderViewHolder(header);

        mKeyGroup = getIntent().getStringExtra(SignInActivity.KEY_GROUP);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        bindViews();
        if (savedInstanceState == null) {
            replaceFragment(new SetAttendanceFragment(mKeyGroup), R.id.set_attendance);
        }
    }

    private void bindViews() {
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        mNavigationView.setNavigationItemSelectedListener(this);

        mHeaderView.displayName.setText(mAuth.getCurrentUser().getDisplayName());
        mHeaderView.email.setText(mAuth.getCurrentUser().getEmail());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemSelectedId = item.getItemId();
        if (itemSelectedId == R.id.set_attendance) {
            replaceFragment(new SetAttendanceFragment(mKeyGroup), R.id.set_attendance);
        } else if (itemSelectedId == R.id.check_couples_attendance) {

        } else if (itemSelectedId == R.id.reason_for_missing_couples) {

        } else if (itemSelectedId == R.id.report_couples_attendance) {

        } else if (itemSelectedId == R.id.setting_database) {

        } else if (itemSelectedId == R.id.nav_send) {
            Toast.makeText(this, R.string.about, Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment, int itemId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        mNavigationView.setCheckedItem(itemId);
    }

    protected static class HeaderViewHolder {
        @BindView(R.id.display_name)
        protected TextView displayName;
        @BindView(R.id.email)
        protected TextView email;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
