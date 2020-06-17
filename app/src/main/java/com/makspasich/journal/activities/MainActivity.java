package com.makspasich.journal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Group;
import com.makspasich.journal.data.model.StatusMissing;
import com.makspasich.journal.data.utils.CircularTransformation;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.databinding.ActivityMainBinding;
import com.makspasich.journal.databinding.NavHeaderMainBinding;
import com.makspasich.journal.dialogs.DatePickerDialog;
import com.makspasich.journal.fragments.CheckAttendance.ReportAttendanceFragment;
import com.makspasich.journal.fragments.SetAttendance.SetAttendanceFragment;
import com.makspasich.journal.fragments.SetReason.SetReasonMissingFragment;
import com.makspasich.journal.fragments.StudentReportFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private NavHeaderMainBinding navBinding;
    private final FirebaseAuth mAuth;
    private final DatabaseReference mRootReference;

    private boolean isHeadOfGroup = false;

    public MainActivity() {
        mAuth = FirebaseAuth.getInstance();
        mRootReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        navBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarLayout.toolbar);

        binding.appBarLayout.fab.setText(App.getInstance().getSelectedDateString());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        bindViews();

        mRootReference.child(App.KEY_GROUPS)
                .child(App.getInstance().getKeyGroup()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    if (mAuth.getCurrentUser().getUid().equals(group.starosta.user_reference.uid)) {
                        binding.navView.getMenu().findItem(R.id.set_attendance).setVisible(true);
                        binding.navView.getMenu().findItem(R.id.set_reason_for_missing).setVisible(true);
                        binding.navView.getMenu().findItem(R.id.setting_group).setVisible(true);
                        if (savedInstanceState == null) {
                            replaceFragment(new SetAttendanceFragment(), R.id.set_attendance);
                        }
                        isHeadOfGroup = true;
                        FirebaseDB.checkIfExistsMissing();
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
    }

    private void bindViews() {
        binding.appBarLayout.fab.setOnClickListener(view -> {

            if (isHeadOfGroup) {
                FirebaseDB.getDays(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<EventDay> events = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                                String stringDate = dateSnapshot.getKey();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(parseStringToDate(stringDate));
                                events.add(new EventDay(calendar, R.drawable.ic_check_24dp));
                            }
                        }
                        if (events.isEmpty()) {
                            Toast.makeText(MainActivity.this, "You don't have missing", Toast.LENGTH_SHORT).show();
                        } else {
                            DatePickerDialog datePickerDialog;
                            datePickerDialog = new DatePickerDialog(onDateSetListener, events, false);
                            datePickerDialog.show(getSupportFragmentManager(), "tag");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                FirebaseDB.getDaysByCurrentStudentId(App.getInstance().getKeyStudent(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<EventDay> events = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot statusMissing : dateSnapshot.child("missings").getChildren()) {
                                    for (DataSnapshot variableSnapshot : statusMissing.getChildren()) {
                                        if (variableSnapshot.getKey().equals("is_missing")) {
                                            String stringDate = dateSnapshot.getKey();
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(parseStringToDate(stringDate));
                                            if (variableSnapshot.getValue(StatusMissing.class) == StatusMissing.PRESENT) {
                                                events.add(new EventDay(calendar, R.drawable.ic_check_24dp));
                                            } else if (variableSnapshot.getValue(StatusMissing.class) == StatusMissing.ABSENT) {
                                                events.add(new EventDay(calendar, R.drawable.ic_close_24dp));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (events.isEmpty()) {
                            Toast.makeText(MainActivity.this, "You don't have missing", Toast.LENGTH_SHORT).show();
                        } else {
                            DatePickerDialog datePickerDialog;
                            datePickerDialog = new DatePickerDialog(onDateSetListener, events, true);
                            datePickerDialog.show(getSupportFragmentManager(), "tag");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        binding.navView.setNavigationItemSelectedListener(this);

        navBinding.displayName.setText(mAuth.getCurrentUser().getDisplayName());
        navBinding.email.setText(mAuth.getCurrentUser().getEmail());
        Uri photoUri = mAuth.getCurrentUser().getPhotoUrl();
        Picasso.get()
                .load(photoUri)
                .placeholder(R.drawable.account_circle_outline)
                .error(R.drawable.ic_warning)
                .transform(new CircularTransformation(0))
                .into(navBinding.avatarImage);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment, int itemId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        binding.navView.setCheckedItem(itemId);
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = selectedDate -> {
        selectDay(selectedDate.getTime());
    };

    private void selectDay(Date date) {
        App.getInstance().setSelectedDate(date);
        //TODO: rewrite this restart activity
        startActivity(getIntent());
        finish();
    }

    private Date parseStringToDate(String date) {
        Date dte = new Date();
        int year = Integer.parseInt(date.split("-")[0]) - 1900;
        int month = Integer.parseInt(date.split("-")[1]) - 1;
        dte.setYear(year);
        dte.setMonth(month);
        dte.setDate(Integer.parseInt(date.split("-")[2]));
        return dte;
    }
}
