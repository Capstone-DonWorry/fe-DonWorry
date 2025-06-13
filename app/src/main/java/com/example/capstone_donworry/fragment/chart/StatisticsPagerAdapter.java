package com.example.capstone_donworry.fragment.chart;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatisticsPagerAdapter extends FragmentStateAdapter {
    public StatisticsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WeeklyStatisticsFragment();
            case 1:
                return new CategoryStatisticsFragment();
            case 2:
                return new ComparisonStatisticsFragment(); // 사용자 간 비교 페이지
            default:
                return new CategoryStatisticsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
