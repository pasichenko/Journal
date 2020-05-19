package com.makspasich.journal;

import android.app.Application;

import com.makspasich.journal.data.model.Group;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class App extends Application {
    public static final String URL_CREATE_GROUP = "http://google.com";

    public static final String KEY_GROUPS = "groups";
    public static final String KEY_USER_GROUP = "user_group";
    public static final String KEY_GROUP_DAY_COUPLE_MISSINGS = "group_day_couple_missings";

    //for admin set reason for missing
    public static final String KEY_GROUP_DAY_STUDENT_MISSINGS = "group_day_student_missings";

    public static final String KEY_GROUP_STUDENT_DAY_MISSINGS = "group_student_day_missings";
    public static final String KEY_STUDENTS = "students";
    public static final String KEY_GROUP_STUDENTS = "group_students";
    public static final String KEY_GROUP_TYPES_MISSING = "group_types_missing";
    public static final String KEY_TYPES_MISSING = "types_missing";
    public static final String KEY_USERS = "users";

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private String sessionKeyGroup = null;
    private Group sessionGroup = null;
    private String keyStudent = null;
    private Student student = null;
    private String keyUser = null;
    private User user = null;
    private Date selectedDate = null;


    @Override
    public void onCreate() {
        super.onCreate();
        this.selectedDate = Calendar.getInstance().getTime();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = this;
    }

    public String getKeyGroup() {
        return sessionKeyGroup;
    }

    public void setKeyGroup(String keyGroup) {
        this.sessionKeyGroup = keyGroup;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat(App.DATE_FORMAT);
        return formatter.format(selectedDate);
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Group getGroup() {
        return sessionGroup;
    }

    public void setGroup(Group group) {
        this.sessionGroup = group;
    }

    public String getKeyStudent() {
        return keyStudent;
    }

    public void setKeyStudent(String keyStudent) {
        this.keyStudent = keyStudent;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getKeyUser() {
        return keyUser;
    }

    public void setKeyUser(String keyUser) {
        this.keyUser = keyUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private static App instance;

    public static App getInstance() {
        return instance;
    }
}
