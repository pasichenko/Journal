package com.makspasich.journal;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class RVAdapterForCheckCouplesAttendance extends RecyclerView.Adapter<RVAdapterForCheckCouplesAttendance.PersonViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public RVAdapterForCheckCouplesAttendance(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }


    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personName;
        SwitchCompat switch1;
        Spinner spinner1;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_check);
            personName = itemView.findViewById(R.id.person_name);
            switch1 = itemView.findViewById(R.id.switch1);
            spinner1 = itemView.findViewById(R.id.spinner1);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_for_check_co_att, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVAdapterForCheckCouplesAttendance.PersonViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        //final ContentValues contentValues = new ContentValues();

        String name = mCursor.getString(mCursor.getColumnIndex("TS." + SQL.SQLColumn.STUDENT_COLUMN_STUDENT));
        long id = mCursor.getLong(mCursor.getColumnIndex("TS." + SQL.SQLColumn.STUDENT_COLUMN_ID));
        int status = mCursor.getInt(mCursor.getColumnIndex("Al."+ SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG));
        int reason = mCursor.getInt(mCursor.getColumnIndex("Al."+ SQL.SQLColumn.ALLDATA_COLUMN_REASON_MISSING));
        Log.d("myLog", "id zapisi = " + id);



        holder.personName.setText(name);
        switch (status){
            case 0: holder.switch1.setChecked(true);break;
            case 1: holder.switch1.setChecked(false);break;
            case 2: holder.switch1.setChecked(false);break;
            default:holder.switch1.setChecked(false);break;
        }
        switch (reason){
            case 0: holder.spinner1.setSelection(0);break;
            case 1: holder.spinner1.setSelection(1);break;
            case 2: holder.spinner1.setSelection(2);break;
            case 3: holder.spinner1.setSelection(3);break;
            case 4: holder.spinner1.setSelection(4);break;
            case 5: holder.spinner1.setSelection(5);break;
            case 6: holder.spinner1.setSelection(6);break;
            default:holder.switch1.setChecked(false);break;
        }
        holder.itemView.setTag(id);


//        holder.btnButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SQL.SQLCommand.setStatusMissing(mContext, 0, position + 1);
//
//            }
//        });
//
//        holder.btnButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SQL.SQLCommand.setStatusMissing(mContext, 1, position + 1);
////                SQLiteDatabase mDatabase;
////                DBHelper dbHelper = new DBHelper(mContext);
////                mDatabase = dbHelper.getWritableDatabase();
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_DATE_MISSING, (MainActivity.selectedYear+"-"+MainActivity.selectedMonth+"-"+MainActivity.selectedDay));
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG, 0);
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT,position+1);
////                mDatabase.update(SQL.SQLColumn.TABLE_NAME_ALLDATA, contentValues, SQL.SQLColumn.ALLDATA_COLUMN_ID + "= ?", new String[] {Integer.toString(position)});
////                Log.d("myLog", "Press button 1, posid"+position);
//////                swapCursor(mDatabase.query(table, columns, null, null, null, null, null));
////                contentValues.clear();
////                mDatabase.close();
//            }
//        });
//
//        holder.btnButton3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SQL.SQLCommand.setStatusMissing(mContext, 2, position + 1);
////                SQLiteDatabase mDatabase;
////                DBHelper dbHelper = new DBHelper(mContext);
////                mDatabase = dbHelper.getWritableDatabase();
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_DATE_MISSING, (MainActivity.selectedYear+"-"+MainActivity.selectedMonth+"-"+MainActivity.selectedDay));
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_STATUS_MISSISNG, 0);
////                contentValues.put(SQL.SQLColumn.ALLDATA_COLUMN_ID_STUDENT,position+1);
////                mDatabase.update(SQL.SQLColumn.TABLE_NAME_ALLDATA, contentValues, SQL.SQLColumn.ALLDATA_COLUMN_ID + "= ?", new String[] {Integer.toString(position)});
////                Log.d("myLog", "Press button 1, posid"+position);
//////                swapCursor(mDatabase.query(table, columns, null, null, null, null, null));
////                contentValues.clear();
////                mDatabase.close();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
