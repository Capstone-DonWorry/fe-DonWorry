package com.example.capstone_donworry.fragment.calendar;

import android.util.Log;

public class DateItem extends Item{

    public DateItem(String date) {
        super(Item.TYPE_DATE, date);
    }

    @Override
    public int getType() {
        return Item.TYPE_DATE;
    }

    public String getDate() {
        if (date == null || !date.contains("-")) {
            return date;  // 또는 "invalid"
        }

        String[] parts = date.split("-");
        if (parts.length < 3) return date;
        return parts[1] + "/" + parts[2];
    }

    public void setDate(String date) {
        this.date = date;
    }
}
