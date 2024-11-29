package com.example.capstone_donworry.fragment.chart;

import android.text.style.ForegroundColorSpan;

import android.graphics.Color;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class ChartCalendarDeco {

    // 일요일 강조
    public static DayViewDecorator textDecorator () {
        return new DayViewDecorator() {

            private CalendarDay selectedDay;
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return true;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(Color.TRANSPARENT));
            }
        };
    }
}
