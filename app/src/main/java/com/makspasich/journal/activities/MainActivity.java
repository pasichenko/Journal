package com.makspasich.journal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Group;
import com.makspasich.journal.data.utils.CircularTransformation;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.fragments.CheckAttendance.ReportAttendanceFragment;
import com.makspasich.journal.fragments.SetAttendance.SetAttendanceFragment;
import com.makspasich.journal.fragments.SetReason.SetReasonMissingFragment;
import com.makspasich.journal.fragments.StudentReportFragment;
import com.squareup.picasso.Picasso;

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
    protected ExtendedFloatingActionButton fab;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    private HeaderViewHolder mHeaderView;
    //endregion

    private boolean isHeadOfGroup = false;

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

        setSupportActionBar(toolbar);

        fab.setText(App.getInstance().getSelectedDateString());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        bindViews();

        mRootReference.child(App.KEY_GROUPS)
                .child(App.getInstance().getKeyGroup()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    if (mAuth.getCurrentUser().getUid().equals(group.starosta.user_reference.uid)) {
                        mNavigationView.getMenu().findItem(R.id.set_attendance).setVisible(true);
                        mNavigationView.getMenu().findItem(R.id.set_reason_for_missing).setVisible(true);
                        mNavigationView.getMenu().findItem(R.id.setting_group).setVisible(true);
                        if (savedInstanceState == null) {
                            replaceFragment(new SetAttendanceFragment(), R.id.set_attendance);
                        }
                        isHeadOfGroup = true;
                    } else {
                        if (savedInstanceState == null) {
                            replaceFragment(new StudentReportFragment(), R.id.report_attendance);
                        }
                        isHeadOfGroup = false;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDB.checkIfExistsMissing();
    }

    private void bindViews() {
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDB.checkIfExistsMissing();
                    }
                }).show());

        mNavigationView.setNavigationItemSelectedListener(this);

        mHeaderView.displayName.setText(mAuth.getCurrentUser().getDisplayName());
        mHeaderView.email.setText(mAuth.getCurrentUser().getEmail());
        Uri photoUri = mAuth.getCurrentUser().getPhotoUrl();
        Picasso.get()
                .load(photoUri)
                .placeholder(R.drawable.account_circle_outline)
                .error(R.drawable.ic_warning)
                .transform(new CircularTransformation(0))
                .into(mHeaderView.avatarImage);


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
            replaceFragment(new SetAttendanceFragment(), itemSelectedId);
        } else if (itemSelectedId == R.id.set_reason_for_missing) {
            replaceFragment(new SetReasonMissingFragment(), itemSelectedId);
        } else if (itemSelectedId == R.id.report_attendance) {
            if (isHeadOfGroup) {
                replaceFragment(new ReportAttendanceFragment(), itemSelectedId);
            } else {
                replaceFragment(new StudentReportFragment(), itemSelectedId);
            }
        } else if (itemSelectedId == R.id.setting_group) {
            Intent intent = new Intent(MainActivity.this, SettingGroupActivity.class);
            startActivity(intent);
        } else if (itemSelectedId == R.id.nav_logout) {
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            });
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
        @BindView(R.id.avatar_image)
        protected ImageView avatarImage;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
