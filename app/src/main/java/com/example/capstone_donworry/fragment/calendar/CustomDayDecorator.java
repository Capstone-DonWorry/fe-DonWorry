package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class CustomDayDecorator implements DayViewDecorator {

    private final CalendarDay targetDate;
    private final int spent;
    private final int goal;

    private final int dailyLevel;

    public CustomDayDecorator(CalendarDay date, int spent, int goal, int dailyLevel) {
        this.targetDate = date;
        this.spent = spent;
        this.goal = goal;
        this.dailyLevel = dailyLevel;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(targetDate); // 지정한 날짜에만 적용
    }

    @Override
    public void decorate(DayViewFacade view) {
        int color = spent > goal ? Color.RED : Color.BLUE;
        view.addSpan(new DotSpan(8, color)); // 점 크기 8
    }
}


