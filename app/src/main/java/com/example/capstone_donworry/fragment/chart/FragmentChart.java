package com.example.capstone_donworry.fragment.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone_donworry.databinding.FragmentChartBinding;

public class FragmentChart extends Fragment {

    private FragmentChartBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewModelChart viewModelChart =
                new ViewModelProvider(this).get(ViewModelChart.class);

        binding = FragmentChartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textCalendar;
//        viewModelChart.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}