package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

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
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{getLevelColor(dailyLevel), getLevelColor(dailyLevel)}  // 시작, 끝 같은 색이면 단색 느낌
        );
        background.setShape(GradientDrawable.OVAL);  // 원형 배경
        background.setSize(80, 80); // 크기 조정 (적절히 조절)

        view.setBackgroundDrawable(background); // 핵심
    }

    private int getLevelColor(int level) {
        switch (level) {
            case 1: return Color.parseColor("#FFE5E5"); // 아주 옅은 빨강
            case 2: return Color.parseColor("#FFCCCC");
            case 3: return Color.parseColor("#FF9999");
            case 4: return Color.parseColor("#FF6666");
            case 5: return Color.parseColor("#FF4D4D");
            case 6: return Color.parseColor("#FF1A1A"); // 진한 파스텔 레드
            default: return Color.TRANSPARENT;
        }
    }
}


