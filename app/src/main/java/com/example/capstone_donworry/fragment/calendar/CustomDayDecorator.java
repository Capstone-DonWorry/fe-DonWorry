package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Map;
import java.util.Set;

public class CustomDayDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final int level;

    public CustomDayDecorator(CalendarDay date, int level) {
        this.date = date;
        this.level = level;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{getLevelColor(level), getLevelColor(level)}
        );
        background.setSize(80, 80);
        view.setBackgroundDrawable(background);
    }

    private int getLevelColor(int level) {
        switch (level) {
            case 1: return Color.TRANSPARENT;
            case 2: return Color.parseColor("#ffdbdb"); // 옅은 분홍
            case 3: return Color.parseColor("#ffb8b8"); // 연한 빨강
            case 4: return Color.parseColor("#fa9393"); // 보통 빨강
            case 5: return Color.parseColor("#ff8383"); // 진한 빨강
            case 6: return Color.parseColor("#ff5959"); // 아주 진한 빨강
            default: return Color.TRANSPARENT;         // level 1 이하는 완전 투명
        }
    }
}

