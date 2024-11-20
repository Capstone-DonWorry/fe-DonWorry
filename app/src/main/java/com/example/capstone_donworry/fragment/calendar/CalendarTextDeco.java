package com.example.capstone_donworry.fragment.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class CalendarTextDeco implements DayViewDecorator {
    private final int color;
    private final String dates;

    public CalendarTextDeco(int color, String dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        String strMon = String.format("%02d", day.getMonth());
        String strDay = String.format("%02d", day.getDay());
        String date = day.getYear() +"-"+ strMon +"-"+ strDay;
        return dates.contains(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}
