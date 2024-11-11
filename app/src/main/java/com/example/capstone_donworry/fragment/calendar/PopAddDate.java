package com.example.capstone_donworry.fragment.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class PopAddDate extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String strMon = String.format("%02d", month+1);
        String strDay = String.format("%02d", day);
        String msg = year + "-" + strMon + "-" + strDay;

        PopAddItem targetFragment = (PopAddItem) getTargetFragment();
        if(targetFragment != null) {
            targetFragment.updateDate(msg);
        }
    }
}