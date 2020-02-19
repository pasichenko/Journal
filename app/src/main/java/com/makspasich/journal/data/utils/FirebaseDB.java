package com.makspasich.journal.data.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.TypeMissing;
import com.makspasich.journal.data.model.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {

    public static void createMissingInDB(String keyGroup, String date, int numberPair, String keyMissing, Missing missing) {
        String keyStudent = missing.student.id_student;
        Student student = missing.student;
        writeMissingInGroupCoupleMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing, student);
        writeMissingInGroupStudentMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing, student);
        writeMissingInGroupDayMissing(keyGroup, date, numberPair, keyMissing, keyStudent, missing, student);
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

    public static void linkedUserInDB(String keyGroup, String keyUser, String keyStudent) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_USERS)
                .child(keyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            updateStudent(keyGroup, user, keyStudent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

    private static void writeMissingInGroupDayMissing(String keyGroup, String date, int numberPair, String keyMissing, String keyStudent, Missing missing, Student student) {
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

    //region linked user to group
    private static void linkedUserWithGroup(String keyGroup, String keyUser, String keyStudent) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_USER_GROUP)
                .child(keyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> userGroup = new HashMap<>();
                        userGroup.put("key_group", keyGroup);
                        userGroup.put("key_student", keyStudent);
                        FirebaseDatabase.getInstance().getReference()
                                .child(App.KEY_USER_GROUP).child(keyUser).updateChildren(userGroup);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private static void updateStudent(String keyGroup, User user, String keyStudent) {
        linkedUserWithGroup(keyGroup, user.uid, keyStudent);

        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENTS)
                .child(keyGroup)
                .child(keyStudent)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if (student != null) {
                            student.user_reference = user;
                            addUserInGroupStudentsReference(keyGroup, student);
                            addUserInGroupCoupleReference(keyGroup, student);
                            addUserInGroupStudentReference(keyGroup, student);
                            addUserInGroupDayReference(keyGroup, student);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private static void addUserInGroupStudentsReference(String keyGroup, Student student) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENTS)
                .child(keyGroup)
                .child(student.id_student)
                .setValue(student);
    }

    private static void addUserInGroupCoupleReference(String keyGroup, Student student) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                .child(keyGroup)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot coupleSnapshot : dateSnapshot.getChildren()) {
                                for (DataSnapshot missingSnapshot : coupleSnapshot.getChildren()) {
                                    Map<String, Object> data = new HashMap<>();
                                    for (DataSnapshot variableSnapshot : missingSnapshot.getChildren()) {
                                        data.put(variableSnapshot.getKey(), variableSnapshot.getValue());
                                    }
                                    Student currentStudent = missingSnapshot.child("student").getValue(Student.class);
                                    if (currentStudent.id_student.equals(student.id_student)) {
                                        data.put("student", student);
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
                                                .child(keyGroup)
                                                .child(dateSnapshot.getKey())
                                                .child(coupleSnapshot.getKey())
                                                .child(missingSnapshot.getKey())
                                                .updateChildren(data);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private static void addUserInGroupStudentReference(String keyGroup, Student student) {
        DatabaseReference groupStudentReference = FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                .child(keyGroup)
                .child(student.id_student);
        groupStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    groupStudentReference.child(dateSnapshot.getKey()).child("student").setValue(student);
                    for (DataSnapshot missingSnapshot : dateSnapshot.child("missings").getChildren()) {
                        Map<String, Object> missingMap = new HashMap<>();
                        for (DataSnapshot variableSnaphot : missingSnapshot.getChildren()) {
                            missingMap.put(variableSnaphot.getKey(), variableSnaphot.getValue());
                        }
                        missingMap.put("student", student);
                        groupStudentReference
                                .child(dateSnapshot.getKey())
                                .child("missings")
                                .child(missingSnapshot.getKey())
                                .updateChildren(missingMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void addUserInGroupDayReference(String keyGroup, Student student) {
        DatabaseReference groupStudentReference = FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                .child(keyGroup);
//                .child(student.id_student);
        groupStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    groupStudentReference.child(dateSnapshot.getKey()).child(student.id_student).child("student").setValue(student);
                    for (DataSnapshot missingSnapshot : dateSnapshot.child(student.id_student).child("missings").getChildren()) {
                        Map<String, Object> missingMap = new HashMap<>();
                        for (DataSnapshot variableSnaphot : missingSnapshot.getChildren()) {
                            missingMap.put(variableSnaphot.getKey(), variableSnaphot.getValue());
                        }
                        missingMap.put("student", student);
                        groupStudentReference
                                .child(dateSnapshot.getKey())
                                .child(student.id_student)
                                .child("missings")
                                .child(missingSnapshot.getKey())
                                .updateChildren(missingMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //endregion
}
