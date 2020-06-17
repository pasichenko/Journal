package com.makspasich.journal.activities;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applandeo.materialcalendarview.EventDay;
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
import com.makspasich.journal.data.utils.FirebaseDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final DatabaseReference rootReference;
    private MutableLiveData<List<EventDay>> eventsLiveData;
    private MutableLiveData<Boolean> isHeadOfGroupLiveData;

    public MainViewModel() {
        auth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        eventsLiveData = new MutableLiveData<>();
        isHeadOfGroupLiveData = new MutableLiveData<>();
    }

    public LiveData<Boolean> requireIsHeadOfGroup() {
        rootReference.child(App.KEY_GROUPS)
                .child(App.getInstance().getKeyGroup()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    if (auth.getCurrentUser().getUid().equals(group.starosta.user_reference.uid)) {
                        isHeadOfGroupLiveData.setValue(true);
                        FirebaseDB.getDays(getDays);
                    } else {
                        isHeadOfGroupLiveData.setValue(false);
                        FirebaseDB.getDaysByCurrentStudentId(App.getInstance().getKeyStudent(), getDaysByStudent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return isHeadOfGroupLiveData;
    }

    public LiveData<Boolean> isHeadOfGroup() {
        return isHeadOfGroupLiveData;
    }

    private ValueEventListener getDays = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<EventDay> events = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String stringDate = dateSnapshot.getKey();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parseStringToDate(stringDate));
                    events.add(new EventDay(calendar, R.drawable.ic_check_24dp));
                }
            }
            eventsLiveData.setValue(events);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener getDaysByStudent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<EventDay> events = new ArrayList<>();
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
            eventsLiveData.setValue(events);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private Date parseStringToDate(String date) {
        Date dte = new Date();
        int year = Integer.parseInt(date.split("-")[0]) - 1900;
        int month = Integer.parseInt(date.split("-")[1]) - 1;
        dte.setYear(year);
        dte.setMonth(month);
        dte.setDate(Integer.parseInt(date.split("-")[2]));
        return dte;
    }

    public LiveData<List<EventDay>> getEventDays() {
        return eventsLiveData;
    }
}
