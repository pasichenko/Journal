package com.makspasich.journal.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String uid;
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

}
