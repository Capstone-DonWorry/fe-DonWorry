package com.example.capstone_donworry.fragment.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class PopAddDate extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String initDate;

    // 날짜 넘겨받기
    public void setInitDate(String date){
        this.initDate = date;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        // initDate가 있으면 날짜 설정, 없으면 현재 날짜 사용
        if (initDate != null && !initDate.isEmpty()) {
            String[] dateParts = initDate.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int day = Integer.parseInt(dateParts[2]);

            calendar.set(year, month, day);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String strMon = String.format("%02d", month+1);
        String strDay = String.format("%02d", day);
        String selecDay = year + "-" + strMon + "-" + strDay;

        PopAddItem targetFragment = (PopAddItem) getTargetFragment();
        if(targetFragment != null) {
            targetFragment.updateDate(selecDay);
        }
    }
}