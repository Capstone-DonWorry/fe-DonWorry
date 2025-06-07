package com.example.capstone_donworry.fragment.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone_donworry.DBHelper;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentChartBinding;
import com.example.capstone_donworry.fragment.calendar.AmountItem;
import com.example.capstone_donworry.fragment.calendar.ViewModelCalendar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentChart extends Fragment {
    private MaterialCalendarView calendarView;
    private PieChart pieChart;
    private CalendarDay selectedDate;
    private DBHelper dbHelper;
    private String userID;

    private FragmentChartBinding binding;
    private ViewModelCalendar shareViewModel;
    private ViewModelChart viewModelChart;
    private DayViewDecorator textDecorator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewModelChart = new ViewModelProvider(this).get(ViewModelChart.class);

        binding = FragmentChartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 초기화
        calendarView = binding.calendarView;
        pieChart = binding.pieChart;
        dbHelper = new DBHelper(getContext());

        // 사용자 정보
        shareViewModel = new ViewModelProvider(requireActivity()).get(ViewModelCalendar.class);
        shareViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String user) {
                userID = user;
                updatePieChartData(selectedDate);
            }
        });

        // 데코 설정
        textDecorator = ChartCalendarDeco.textDecorator();
        calendarView.addDecorators(textDecorator);

        // 현재 날짜 기본 선택
        selectedDate = CalendarDay.today();

        calendarView.setOnMonthChangedListener(((widget, date) -> {
            selectedDate = date;
            updatePieChartData(selectedDate);
        }));

        // 처음 데이터를 설정 (현재 월)
        updatePieChartData(selectedDate);

        return root;
    }

    private void updatePieChartData(CalendarDay selectedDate) {
        if (userID == null) {
            // userID가 아직 설정되지 않았으면 차트를 업데이트하지 않음
            return;
        }

        String strMon = String.format("%02d", selectedDate.getMonth());
        String dateKey = selectedDate.getYear() +"-"+ strMon;

        List<AmountItem> items = dbHelper.getMonthItems(userID, dateKey);

        // 카테고리별 금액
        HashMap<String, Long> categorySums = new HashMap<>();

        // 합계 계산
        for (AmountItem item : items) {
            String category = item.getCategory();
            long amount = item.getAmount();
            categorySums.put(category, categorySums.getOrDefault(category, 0L) + amount);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (String category : categorySums.keySet()) {
            entries.add(new PieEntry(categorySums.get(category), category));
        }

        // 차트 색상
        int [] categoryColors = {
                ContextCompat.getColor(getContext(), R.color.chart1),
                ContextCompat.getColor(getContext(), R.color.chart2),
                ContextCompat.getColor(getContext(), R.color.chart3),
                ContextCompat.getColor(getContext(), R.color.chart4),
                ContextCompat.getColor(getContext(), R.color.chart5),
                ContextCompat.getColor(getContext(), R.color.chart6),
                ContextCompat.getColor(getContext(), R.color.chart7),
        };
        PieDataSet dataSet = new PieDataSet(entries, selectedDate.getYear() + "년 " + (selectedDate.getMonth()) + "월 지출");
        dataSet.setColors(categoryColors);

        // 차트에 데이터 넣기
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh the chart
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}