package com.makspasich.journal.data.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makspasich.journal.App;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.StatusMissing;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.TypeMissing;
import com.makspasich.journal.data.model.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {

    public static final DatabaseReference coupleMissingReference = FirebaseDatabase.getInstance().getReference()
            .child(App.KEY_GROUP_DAY_COUPLE_MISSINGS)
            .child(App.getInstance().getKeyGroup());

    public static final DatabaseReference studentMissingReference = FirebaseDatabase.getInstance().getReference()
            .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
            .child(App.getInstance().getKeyGroup());

    public static final DatabaseReference dayMissingReference = FirebaseDatabase.getInstance().getReference()
            .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
            .child(App.getInstance().getKeyGroup());

    public static final DatabaseReference groupStudentsReference = FirebaseDatabase.getInstance().getReference()
            .child(App.KEY_GROUP_STUDENTS)
            .child(App.getInstance().getKeyGroup());
    private static final int COUNT_COUPLES = 6;

    public static void checkIfExistsMissing() {
        ValueEventListener getGroupStudents = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Student student = childSnapshot.getValue(Student.class);
                    FirebaseDB.createMissingInDB(App.getInstance().getSelectedDateString(), student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        coupleMissingReference.child(App.getInstance().getSelectedDateString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            groupStudentsReference
                                    .addListenerForSingleValueEvent(getGroupStudents);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private static void createMissingInDB(String date, Student student) {
        studentMissingReference.child(student.id_student).child(date)
                .child("student").setValue(student);
        dayMissingReference.child(date).child(student.id_student)
                .child("student").setValue(student);
        for (int numberPair = 1; numberPair <= COUNT_COUPLES; numberPair++) {
            String keyMissing = FirebaseDatabase.getInstance().getReference().push().getKey();
            Missing missing = new Missing(date, null, StatusMissing.NULL, null, numberPair);
            Missing missingWithStudent = new Missing(date, student, StatusMissing.NULL, null, numberPair);
            coupleMissingReference.child(date).child(String.valueOf(numberPair))
                    .child(keyMissing)
                    .setValue(missingWithStudent);
            studentMissingReference.child(student.id_student).child(date)
                    .child("missings").child(keyMissing).setValue(missing);
            dayMissingReference.child(date).child(student.id_student)
                    .child("missings").child(keyMissing).setValue(missing);
        }
    }

    public static void updateStatusMissingInDB(int numberPair, String keyMissing, String keyStudent, StatusMissing status) {
        Map<String, Object> data = new HashMap<>();
        data.put("is_missing", status);
        if (status == StatusMissing.PRESENT) {
            data.put("type_missing", null);
        }
        coupleMissingReference.child(App.getInstance().getSelectedDateString()).child(String.valueOf(numberPair))
                .child(keyMissing).updateChildren(data);
        studentMissingReference.child(keyStudent).child(App.getInstance().getSelectedDateString())
                .child("missings").child(keyMissing).updateChildren(data);
        dayMissingReference.child(App.getInstance().getSelectedDateString()).child(keyStudent)
                .child("missings").child(keyMissing).updateChildren(data);
    }

    public static void updateTypeMissingInDB(int numberPair, String keyMissing, String keyStudent, TypeMissing typeMissing) {
        Map<String, Object> data = new HashMap<>();
        data.put("type_missing", typeMissing);
        coupleMissingReference.child(App.getInstance().getSelectedDateString()).child(String.valueOf(numberPair))
                .child(keyMissing).updateChildren(data);
        studentMissingReference.child(keyStudent).child(App.getInstance().getSelectedDateString())
                .child("missings").child(keyMissing).updateChildren(data);
        dayMissingReference.child(App.getInstance().getSelectedDateString()).child(keyStudent)
                .child("missings").child(keyMissing).updateChildren(data);
    }

    public static void linkedUserInDB(String keyUser, String keyStudent) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_USERS)
                .child(keyUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            updateStudent(user, keyStudent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void writeNewStudentInDB(Student student) {
        FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_STUDENTS)
                .child(student.id_student)
                .setValue(student);

        groupStudentsReference
                .child(student.id_student)
                .setValue(student);

        coupleMissingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot date : dataSnapshot.getChildren()) {
                    createMissingInDB(date.getKey(), student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //region linked user to group
    private static void linkedUserWithGroup(String keyUser, String keyStudent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(App.KEY_USER_GROUP).child(keyUser);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> userGroup = new HashMap<>();
                userGroup.put("key_group", App.getInstance().getKeyGroup());
                userGroup.put("key_student", keyStudent);
                reference.updateChildren(userGroup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void updateStudent(User user, String keyStudent) {
        linkedUserWithGroup(user.uid, keyStudent);

        groupStudentsReference
                .child(keyStudent)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if (student != null) {
                            student.user_reference = user;
                            addStudentInGroupStudentsReference(student);
                            addStudentInGroupCoupleReference(student);
                            addStudentInStudentMissingReference(student);
                            addStudentInDayMissingReference(student);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private static void addStudentInGroupStudentsReference(Student student) {
        groupStudentsReference
                .child(student.id_student)
                .setValue(student);
    }

    private static void addStudentInGroupCoupleReference(Student student) {
        coupleMissingReference
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
                                        coupleMissingReference
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

    private static void addStudentInStudentMissingReference(Student student) {
        DatabaseReference groupStudentReference = studentMissingReference.child(student.id_student);
        groupStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    groupStudentReference.child(dateSnapshot.getKey())
                            .child("student").setValue(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void addStudentInDayMissingReference(Student student) {
        dayMissingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    dayMissingReference.child(dateSnapshot.getKey()).child(student.id_student)
                            .child("student").setValue(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //endregion
}
