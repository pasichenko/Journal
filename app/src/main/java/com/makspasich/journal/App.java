package com.makspasich.journal;

import android.app.Application;

public class App extends Application {
    public static final String URL_CREATE_GROUP = "http://google.com";

    public static final String KEY_GROUPS = "groups";
    public static final String KEY_USER_GROUP = "user_group";
    public static final String KEY_MISSINGS = "missings";
    public static final String KEY_STUDENTS = "students";
    public static final String KEY_GROUP_STUDENTS = "group_students";
    public static final String KEY_GROUP_TYPES_MISSING = "group_types_missing";
    public static final String KEY_TYPES_MISSING = "types_missing";

    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
