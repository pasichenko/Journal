package com.makspasich.journal.data.utils;

import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.TypeMissing;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {

    public static void createMissingInDB(String keyGroup, String date, int numberPair, String keyMissing, Missing missing) {
        String keyStudent = missing.student.id_student;
        Student student = missing.student;
        writeMissingInGroupCoupleMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing,student);
        writeMissingInGroupStudentMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing,student);
        writeMissingInGroupDayMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing,student);
    }

    public static void updateStatusMissingInDB(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("is_missing", status);
        updateMissingInGroupCoupleMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);
        updateMissingInGroupStudentMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);
        updateMissingInGroupDayMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);

    }

    public static void updateTypeMissingInDB(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, TypeMissing typeMissing) {
        Map<String, Object> data = new HashMap<>();
        data.put("type_missing", typeMissing);
        updateTypeMissingInGroupCoupleMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);
        updateTypeMissingInGroupStudentMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);
        updateTypeMissingInGroupDayMissing(keyGroup, date, numberPair, keyMissing, keyStudent, data);
    }

    //region create missing
    private static void writeMissingInGroupCoupleMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Missing missing, Student student) {
        FirebaseDatabase.getInstance().getReference().child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(String.valueOf(numberPair))
                .child(keyMissing)
                .setValue(missing);
    }

    private static void writeMissingInGroupStudentMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Missing missing, Student student) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(keyGroup)
                .child(keyStudent)
                .child(date)
                .child("student")
                .setValue(student);

        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(keyGroup)
                .child(keyStudent)
                .child(date)
                .child("missings")
                .child(keyMissing)
                .setValue(missing);
    }

    private static void writeMissingInGroupDayMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Missing missing,Student student) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(keyStudent)
                .child("student")
                .setValue(student);
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(keyStudent)
                .child("missings")
                .child(keyMissing)
                .setValue(missing);
    }
    //endregion

    //region update status missing
    private static void updateMissingInGroupCoupleMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> status) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(String.valueOf(numberPair))
                .child(keyMissing)
                .updateChildren(status);
    }

    private static void updateMissingInGroupStudentMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> status) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(keyGroup)
                .child(keyStudent)
                .child(date)
                .child("missings")
                .child(keyMissing)
                .updateChildren(status);
    }

    private static void updateMissingInGroupDayMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> status) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(keyStudent)
                .child("missings")
                .child(keyMissing)
                .updateChildren(status);
    }
    //endregion

    //region update type missing
    private static void updateTypeMissingInGroupCoupleMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> typeMissing) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(String.valueOf(numberPair))
                .child(keyMissing)
                .updateChildren(typeMissing);
    }

    private static void updateTypeMissingInGroupStudentMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> typeMissing) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(keyGroup)
                .child(keyStudent)
                .child(date)
                .child("missings")
                .child(keyMissing)
                .updateChildren(typeMissing);
    }

    private static void updateTypeMissingInGroupDayMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Map<String, Object> typeMissing) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(keyGroup)
                .child(date)
                .child(keyStudent)
                .child("missings")
                .child(keyMissing)
                .updateChildren(typeMissing);
    }
    //endregion
}
