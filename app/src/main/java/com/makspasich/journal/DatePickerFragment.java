package com.makspasich.journal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final String TAG = "DataPickerFragment";
    Button dob;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        try {
            dob = getActivity().findViewById(R.id.dateView);
        } catch (NullPointerException e) {
            Log.e(TAG, "getAktivity() - NullPointerException");
        }

//        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
//        Date date = new Date(yy, mm, dd - 1);

        GregorianCalendar gregorianCalendar = new GregorianCalendar(yy, mm, dd - 1);

        String dayOfWeek = "";
        switch (gregorianCalendar.get(gregorianCalendar.DAY_OF_WEEK)) {
            case 1:
                dayOfWeek = "Mon";
                break;
            case 2:
                dayOfWeek = "Tue";
                break;
            case 3:
                dayOfWeek = "Wed";
                break;
            case 4:
                dayOfWeek = "Thu";
                break;
            case 5:
                dayOfWeek = "Fri";
                break;
            case 6:
                dayOfWeek = "Sat";
                break;
            case 7:
                dayOfWeek = "Sun";
                break;
        }
        MainActivity.selectedDay = dd;
        MainActivity.selectedMonth = mm;
        MainActivity.selectedYear = yy;
        dob.setText(dayOfWeek + ", " + ((dd + 1 < 10) ? "0" + (dd + 1) : (dd + 1)) + "." + ((mm + 1 < 10) ? "0" + (mm + 1) : (mm + 1)) + "." + yy);
        MainActivity.selectedDate = yy + "-" + ((mm + 1 < 10) ? "0" + (mm + 1) : (mm + 1)) + "-" + ((dd + 1 < 10) ? "0" + (dd + 1) : (dd + 1));


    }

}
