package com.makspasich.journal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.utils.CircularTransformation;
import com.makspasich.journal.data.utils.FirebaseDB;
import com.makspasich.journal.databinding.ActivityMainBinding;
import com.makspasich.journal.databinding.NavHeaderMainBinding;
import com.makspasich.journal.dialogs.DatePickerDialog;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private NavHeaderMainBinding navBinding;
    private DatePickerDialog datePickerDialog;

    public MainActivity() {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        navBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
        setContentView(binding.getRoot());
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setSupportActionBar(binding.appBarLayout.toolbar);

        binding.appBarLayout.fab.setText(App.getInstance().getSelectedDateString());
        bindViews();
        viewModel.requireIsHeadOfGroup().observe(this, isHeadOfGroup -> {
            if (isHeadOfGroup) {
                binding.navView.getMenu().findItem(R.id.nav_set_attendance).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_set_reason_for_missing).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_report_attendance).setVisible(true);
                binding.navView.getMenu().findItem(R.id.nav_setting_group).setVisible(true);
                FirebaseDB.checkIfExistsMissing();
            } else {
                binding.navView.getMenu().findItem(R.id.nav_student_report_attendance).setVisible(true);
            }
        });
        viewModel.getEventDays().observe(this, eventDays -> {
            if (eventDays.isEmpty()) {
                Toast.makeText(MainActivity.this, "You don't have missing", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (viewModel.isHeadOfGroup().getValue()) {
                        datePickerDialog = new DatePickerDialog(onDateSetListener, eventDays, false);
                    } else {
                        datePickerDialog = new DatePickerDialog(onDateSetListener, eventDays, true);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.navView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                // Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, task -> {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_set_attendance,
                R.id.nav_set_reason_for_missing,
                R.id.nav_report_attendance,
                R.id.nav_student_report_attendance,
                R.id.nav_setting_group)
                .setDrawerLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void bindViews() {
        binding.appBarLayout.fab.setOnClickListener(view -> {
            if (datePickerDialog == null) {
                Toast.makeText(this, "Please wait, data requiring", Toast.LENGTH_SHORT).show();
            } else {
                datePickerDialog.show(getSupportFragmentManager(), "tag");
            }
        });

        navBinding.displayName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        navBinding.email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Uri photoUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
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

    DatePickerDialog.OnDateSetListener onDateSetListener = selectedDate -> selectDay(selectedDate.getTime());

    private void selectDay(Date date) {
        App.getInstance().setSelectedDate(date);
        //TODO: rewrite this restart activity
        startActivity(getIntent());
        finish();
    }

}
