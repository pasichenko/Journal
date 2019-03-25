package com.makspasich.journal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    Button button;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

           button = findViewById(R.id.dateView);
           button.setText(initializeDate());
           button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   DialogFragment datePicker = new DatePickerFragment();
                   datePicker.show(getSupportFragmentManager(), "data picker");
               }
           });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentSetCouplesAttendance()).commit();
            navigationView.setCheckedItem(R.id.set_couples_attendance);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_for_set_co_att clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.set_couples_attendance:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentSetCouplesAttendance()).commit();
                break;
            case R.id.reason_for_missing_couples:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentReasonForMissingCouples()).commit();
                break;
            case R.id.check_couples_attendance:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentCheckCouplesAttendance()).commit();
                break;
            case R.id.report_couples_attendance:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentReportCouplesAttendance()).commit();
                break;

            case R.id.setting_database:
                Toast.makeText(this, R.string.setting_database, Toast.LENGTH_SHORT).show();
                break;
             case R.id.nav_send:
                Toast.makeText(this, R.string.about, Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String initializeDate(){
        Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);



        String dayOfWeek;
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case 2:
                dayOfWeek = "Mon";
                break;
            case 3:
                dayOfWeek = "Tue";
                break;
            case 4:
                dayOfWeek = "Wed";
                break;
            case 5:
                dayOfWeek = "Thu";
                break;
            case 6:
                dayOfWeek = "Fri";
                break;
            case 7:
                dayOfWeek = "Sat";
                break;
            case 1:
                dayOfWeek = "Sun";
                break;
                default:dayOfWeek="";
        }
        return dayOfWeek + ", " + dd +"." + ((mm+1<10)?"0" + (mm+1):(mm+1)) + "."  + yy;
    }


}
