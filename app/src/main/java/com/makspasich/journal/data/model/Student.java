package com.makspasich.journal.data.model;

public class Student {
    public String id_student;
    public String last_name;
    public String first_name;
    public User user_reference;

    public Student() {
    }

    public Student(String id_student, String last_name, String first_name, User user_reference) {
        this.id_student = id_student;
        this.last_name = last_name;
        this.first_name = first_name;
        this.user_reference = user_reference;
    }
}
