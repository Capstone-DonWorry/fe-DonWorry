package com.example.capstone_donworry.fragment.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class CalendarTextDeco implements DayViewDecorator {
    private final int color;
    private final String dates;

    public CalendarTextDeco(int color, String dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        String date = day.getYear() +"-"+ day.getMonth() +"-"+ day.getDay();
        return dates.contains(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}
