package com.makspasich.journal.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.makspasich.journal.App;
import com.makspasich.journal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment {
    private final String TAG = "DataPickerFragment";

    private OnDateSetListener onDateSetListener = null;
    private List<EventDay> events;
    private boolean limitDateRange = false;

    private SimpleDateFormat mYearFormat;
    private SimpleDateFormat mMonthDayFormat;

    public DatePickerDialog() {
        Locale locale = Locale.getDefault();
        mYearFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "y"), locale);
        mMonthDayFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(locale, "EMMMd"), locale);
    }

    public DatePickerDialog(OnDateSetListener onDateSetListener, boolean limitDateRange) {
        this();
        this.onDateSetListener = onDateSetListener;
        this.limitDateRange = limitDateRange;
    }

    public DatePickerDialog(OnDateSetListener onDateSetListener, List<EventDay> events, boolean limitDateRange) {
        this(onDateSetListener, limitDateRange);
        this.events = events;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View root = layoutInflater.inflate(R.layout.date_picker_layout, null);
        View headerView = layoutInflater.inflate(R.layout.date_picker_header_material, null);
        TextView year = headerView.findViewById(R.id.date_picker_header_year);
        TextView date = headerView.findViewById(R.id.date_picker_header_date);
        CalendarView calendarView = root.findViewById(R.id.calendarView);
        Calendar mCurrentDate = calendarView.getFirstSelectedDate();
        try {
            calendarView.setDate(App.getInstance().getSelectedDate());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        year.setText(mYearFormat.format(mCurrentDate.getTime()));
        date.setText(mMonthDayFormat.format(mCurrentDate.getTime()));
        calendarView.setOnDayClickListener(eventDay -> {
            year.setText(mYearFormat.format(eventDay.getCalendar().getTime()));
            date.setText(mMonthDayFormat.format(eventDay.getCalendar().getTime()));
        });

        if (events != null) {
            calendarView.setEvents(events);
            if (limitDateRange & events.size() >= 2) {
                calendarView.setMinimumDate(events.get(0).getCalendar());
                calendarView.setMaximumDate(events.get(events.size() - 1).getCalendar());
            }
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setCustomTitle(headerView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (onDateSetListener != null) {
                        onDateSetListener.onDateSet(calendarView.getFirstSelectedDate());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(root);
        return adb.create();
    }

    public interface OnDateSetListener {
        void onDateSet(Calendar selectedDate);
    }
}