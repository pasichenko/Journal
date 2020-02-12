package com.makspasich.journal.data.model;

public class Missing {
    public String date;
    public Student student;
    public String is_missing;
    public TypeMissing type_missing;
    public int number_pair;

    public Missing() {

    }

    public Missing(String date, Student student, String is_missing, TypeMissing type_missing, int number_pair) {
        this.date = date;
        this.student = student;
        this.is_missing = is_missing;
        this.type_missing = type_missing;
        this.number_pair = number_pair;
    }
}
