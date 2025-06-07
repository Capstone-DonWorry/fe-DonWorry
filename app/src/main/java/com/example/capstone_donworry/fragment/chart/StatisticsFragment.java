package com.example.capstone_donworry.fragment.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.capstone_donworry.databinding.FragmentStatisticsBinding;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);

        ViewPager2 viewPager = binding.viewPager;
        StatisticsPagerAdapter adapter = new StatisticsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        return binding.getRoot();
    }
}


