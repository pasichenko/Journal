package com.makspasich.journal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    public static final String URL_CREATE_GROUP = "http://google.com";
    public static final String KEY_USER_GROUP = "user_group";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
