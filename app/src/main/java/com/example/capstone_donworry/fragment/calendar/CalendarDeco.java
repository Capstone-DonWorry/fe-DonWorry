package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.capstone_donworry.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CalendarDeco {

    private static final HashMap<CalendarDay, String> dateTextMap = new HashMap<>();
    public static void addText(CalendarDay day, String text) {
        dateTextMap.put(day, text);
    }
    // 현재 날짜
    public static DayViewDecorator todayViewDecorator (Context context) {
        return new DayViewDecorator() {
            private final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector);
            private final CalendarDay today = CalendarDay.today();

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(today);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(drawable);
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)));
            }
        };
    }

    // 일요일 강조
    public static DayViewDecorator sundayDecorator () {
        return new DayViewDecorator() {

            private CalendarDay selectedDay;
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(day.getYear(), day.getMonth() -1, day.getDay());
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(Color.RED));
            }
        };
    }

    // 토요일 강조
    public static DayViewDecorator saturdayDecorator () {
        return new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(day.getYear(), day.getMonth() -1, day.getDay());
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(Color.BLUE));
            }
        };
    }

    // 데코 삭제
    public static DayViewDecorator removeDecorator() {
        return new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {

            }
        };
    }
}
