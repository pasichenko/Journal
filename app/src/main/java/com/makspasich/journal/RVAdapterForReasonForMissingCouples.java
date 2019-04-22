package com.makspasich.journal;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class RVAdapterForReasonForMissingCouples extends RecyclerView.Adapter<RVAdapterForReasonForMissingCouples.PersonViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    RVAdapterForReasonForMissingCouples(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {


        CardView cv;
        TextView personName;
        ImageButton btn0;
        Button btn1;
        Button btn2;
        Button btn3;
        Button btn4;
        Button btn5;
        Button btn6;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_reason);
            personName = itemView.findViewById(R.id.person_name);
            btn0 = itemView.findViewById(R.id.btn0);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            btn4 = itemView.findViewById(R.id.btn4);
            btn5 = itemView.findViewById(R.id.btn5);
            btn6 = itemView.findViewById(R.id.btn6);

        }
    }




    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_for_reason_co_att, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        //final ContentValues contentValues = new ContentValues();

        String name = mCursor.getString(mCursor.getColumnIndex("TS."+SQL.SQLColumn.STUDENT_COLUMN_STUDENT));
        long id = mCursor.getLong(mCursor.getColumnIndex("TS."+SQL.SQLColumn.STUDENT_COLUMN_ID));
        Log.d("myLog","id zapisi = "+id);

        holder.personName.setText(name);
        holder.itemView.setTag(id);


        holder.btn0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setStatusMissing(mContext,0,position+1);
            }
        });

        holder.btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,0,position+1);

            }
        });

        holder.btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,1,position+1);
            }
        });

        holder.btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,2,position+1);
            }
        });

        holder.btn4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,3,position+1);

            }
        });

        holder.btn5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,4,position+1);
            }
        });

        holder.btn6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SQL.SQLCommand.setReasonMissing(mContext,5,position+1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

}
