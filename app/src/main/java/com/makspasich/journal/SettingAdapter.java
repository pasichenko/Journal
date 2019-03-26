package com.makspasich.journal;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.StudentViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public SettingAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;

        public StudentViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_name_item);
        }
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.setting_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(SQL.SQLColumn.STUDENT_COLUMN_STUDENT));
        long id = mCursor.getLong(mCursor.getColumnIndex(SQL.SQLColumn.STUDENT_COLUMN_ID));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);
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
