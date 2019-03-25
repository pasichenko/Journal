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
import android.widget.Toast;

import java.util.List;

public class RVAdapterForSetCouplesAttendance extends RecyclerView.Adapter<RVAdapterForSetCouplesAttendance.PersonViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public RVAdapterForSetCouplesAttendance(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        //ublic TextView nameText;
        public ImageButton btnButton1;
        public ImageButton btnButton2;
        public Button btnButton3;


        CardView cv;
        TextView personName;

        PersonViewHolder(View itemView) {
            super(itemView);

            this.btnButton1= itemView.findViewById(R.id.btn1);
            this.btnButton2= itemView.findViewById(R.id.btn2);
            this.btnButton3= itemView.findViewById(R.id.btn3);

            cv = itemView.findViewById(R.id.cv_set);
            personName = itemView.findViewById(R.id.person_name);

        }
    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_for_set_co_att, viewGroup, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(DataContract.DataEntry.COLUMN_STUDENT));
        long id = mCursor.getLong(mCursor.getColumnIndex(DataContract.DataEntry._ID));

        holder.personName.setText(name);
        holder.itemView.setTag(id);


        holder.btnButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("myLog", "Press button 1, posid"+position);
            }
        });

        holder.btnButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("myLog", "Press button 2, posid"+position);
            }
        });

        holder.btnButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("myLog", "Press button 3, posid"+position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
