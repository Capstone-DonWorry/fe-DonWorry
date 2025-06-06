package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class LabelDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final SpannableStringBuilder label;

    public LabelDecorator(CalendarDay date, String text, int color) {
        this.date = date;
        this.label = new SpannableStringBuilder(text);
        label.setSpan(new ForegroundColorSpan(color), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        label.setSpan(new RelativeSizeSpan(0.7f), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(label);
    }
}

