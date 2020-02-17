package com.makspasich.journal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makspasich.journal.App;
import com.makspasich.journal.R;
import com.makspasich.journal.data.model.Missing;
import com.makspasich.journal.data.model.Student;
import com.makspasich.journal.data.model.TypeMissing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetReasonMissingAdapter extends RecyclerView.Adapter<SetReasonMissingAdapter.RVHolder> {

    private static final String TAG = "SetAttendanceAdapter";
    private Context mContext;
    private DatabaseReference mReasonReference;
    private ChildEventListener mChildEventListener;

    private List<String> mStudentIds = new ArrayList<>();
    private List<Student> mStudents = new ArrayList<>();
    private List<List<String>> mMissingIds = new ArrayList<>();
    private List<List<Missing>> mMissingsByStudent = new ArrayList<>();
    private List<TypeMissing> mTypes;
    private String mKeyGroup;

    public SetReasonMissingAdapter(final Context context, DatabaseReference ref, List<TypeMissing> types, String mKeyGroup) {
        mContext = context;
        mReasonReference = ref;
        this.mTypes = types;
        this.mKeyGroup = mKeyGroup;

        // Create child event listener
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new missing has been added, add it to the displayed list

                // [START_EXCLUDE]
                // Update RecyclerView
                mStudentIds.add(dataSnapshot.getKey());
                List<String> listMissingIds = new ArrayList<>();
                List<Missing> listMissing = new ArrayList<>();
                for (DataSnapshot missingSnapshot : dataSnapshot.child("missings").getChildren()) {
                    Missing missing = missingSnapshot.getValue(Missing.class);
                    listMissingIds.add(missingSnapshot.getKey());
                    listMissing.add(missing);
                }
                Student student = dataSnapshot.child("student").getValue(Student.class);
                mStudents.add(student);
                mMissingIds.add(listMissingIds);
                mMissingsByStudent.add(listMissing);
                notifyItemInserted(mStudentIds.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                String studentKey = dataSnapshot.getKey();

                int studentIndex = mStudentIds.indexOf(studentKey);
                if (studentIndex > -1) {
                    List<String> listMissingIds = new ArrayList<>();
                    List<Missing> listMissing = new ArrayList<>();
                    for (DataSnapshot missingSnapshot : dataSnapshot.child("missings").getChildren()) {
                        Missing missing = missingSnapshot.getValue(Missing.class);
                        listMissingIds.add(missingSnapshot.getKey());
                        listMissing.add(missing);
                    }
                    mMissingIds.add(listMissingIds);
                    mMissingsByStudent.set(studentIndex, listMissing);
                    Student student = dataSnapshot.child("student").getValue(Student.class);
                    mStudents.set(studentIndex, student);
                    notifyItemChanged(studentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + studentKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String studentKey = dataSnapshot.getKey();

                int studentIndex = mStudentIds.indexOf(studentKey);
                if (studentIndex > -1) {
                    // Remove data from the list
                    mStudentIds.remove(studentIndex);
                    mStudents.remove(studentIndex);
                    mMissingIds.remove(studentIndex);
                    mMissingsByStudent.remove(studentIndex);
                    // Update the RecyclerView
                    notifyItemRemoved(studentIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + studentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postMissings:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load missings.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mReasonReference.addChildEventListener(childEventListener);

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @NonNull
    @Override
    public RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_reason_binding, parent, false);
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolder holder, int position) {
        holder.bind(mStudents.get(holder.getAdapterPosition()),
                mMissingIds.get(holder.getAdapterPosition()),
                mMissingsByStudent.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mMissingsByStudent.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mReasonReference.removeEventListener(mChildEventListener);
        }
    }

    class RVHolder extends RecyclerView.ViewHolder {

        //region BindView
        @BindView(R.id.container_card_view)
        MaterialCardView container;
        @BindView(R.id.person_name)
        TextView personName;
        @BindView(R.id.first_couple)
        TextView firstCoupleTextView;
        @BindView(R.id.second_couple)
        TextView secondCoupleTextView;
        @BindView(R.id.three_couple)
        TextView threeCoupleTextView;
        @BindView(R.id.four_couple)
        TextView fourCoupleTextView;
        @BindView(R.id.five_couple)
        TextView fiveCoupleTextView;
        @BindView(R.id.six_couple)
        TextView sixCoupleTextView;
        @BindView(R.id.status_chip)
        Chip statusChip;

        @BindView(R.id.types_missing_chip_group)
        ChipGroup typesMissingChipGroup;
        //endregion
        private List<Missing> listMissing;
        private List<String> missingsIds;

        RVHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void bind(Student student, List<String> missingsIds, List<Missing> listMissing) {
            String fio = student.last_name + " " + student.first_name;
            personName.setText(fio);
            this.listMissing = listMissing;
            this.missingsIds = missingsIds;
            boolean isMissedAnyCouple = false;
            boolean isSetttedTypeMissing = false;
            TypeMissing typeSelected = null;
            for (Missing missing : listMissing) {
                if (missing.number_pair == 1) {
                    changeBackgroundMissing(firstCoupleTextView, missing);
                } else if (missing.number_pair == 2) {
                    changeBackgroundMissing(secondCoupleTextView, missing);
                } else if (missing.number_pair == 3) {
                    changeBackgroundMissing(threeCoupleTextView, missing);
                } else if (missing.number_pair == 4) {
                    changeBackgroundMissing(fourCoupleTextView, missing);
                } else if (missing.number_pair == 5) {
                    changeBackgroundMissing(fiveCoupleTextView, missing);
                } else if (missing.number_pair == 6) {
                    changeBackgroundMissing(sixCoupleTextView, missing);
                }
                if (missing.is_missing.equals("absent")) {
                    isMissedAnyCouple = true;
                }
                if (missing.type_missing != null) {
                    isSetttedTypeMissing = true;
                    typeSelected = missing.type_missing;
                }
            }
            if (isMissedAnyCouple) {
                if (!isSetttedTypeMissing) {
                    typesMissingChipGroup.setVisibility(View.VISIBLE);
                } else {
                    setVisibilityStatusChip(true);
                }
            } else {
                typesMissingChipGroup.setVisibility(View.GONE);
                setVisibilityStatusChip(false);
            }
            if (isSetttedTypeMissing) {
                statusChip.setText(typeSelected.short_name_type);
            }
            if (typesMissingChipGroup.getVisibility() == View.VISIBLE) {
                for (TypeMissing type : mTypes) {
                    Chip chip =
                            (Chip) LayoutInflater.from(mContext).inflate(R.layout.cat_chip_group_item_choice, typesMissingChipGroup, false);
                    chip.setText(type.short_name_type);
                    chip.setOnClickListener(v -> setTypeMissing(type));
                    typesMissingChipGroup.addView(chip);
                }
            }
        }

        private void setTypeMissing(TypeMissing type) {
            Map<String, Object> data = new HashMap<>();
            data.put("type_missing", type);
            for (Missing missing : listMissing) {
                if (missing.is_missing.equals("absent")) {
                    String keyMissing = missingsIds.get(listMissing.indexOf(missing));
                    FirebaseDatabase.getInstance().getReference()
                            .child(App.KEY_GROUP_STUDENT_DAY_MISSINGS)
                            .child(mKeyGroup)
                            .child(missing.student.id_student)
                            .child(missing.date)
                            .child("missings")
                            .child(keyMissing)
                            .updateChildren(data);

                    FirebaseDatabase.getInstance().getReference()
                            .child(App.KEY_GROUP_DAY_STUDENT_MISSINGS)
                            .child(mKeyGroup)
                            .child(missing.date)
                            .child(missing.student.id_student)
                            .child("missings")
                            .child(keyMissing)
                            .updateChildren(data);
                }
            }

            setVisibilityStatusChip(true);
            statusChip.setText(type.short_name_type);
            typesMissingChipGroup.setVisibility(View.GONE);
        }

        private void changeBackgroundMissing(TextView view, Missing missing) {
            view.setText(String.valueOf(missing.number_pair));

            if (missing.is_missing.equals("present")) {
                view.setBackgroundResource(R.drawable.border_present_student);
            } else if (missing.is_missing.equals("absent")) {
                view.setBackgroundResource(R.drawable.border_absent_student);
            } else if (missing.is_missing.equals("null")) {
                view.setBackgroundResource(R.drawable.border_null);
            }

        }

        private void setVisibilityStatusChip(boolean visibility) {
            if (visibility) {
                statusChip.setVisibility(View.VISIBLE);
            } else {
                statusChip.setVisibility(View.GONE);
            }
        }
    }
}
