package com.example.capstone_donworry.fragment.chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.capstone_donworry.JsonAuthorizedRequest;
import com.example.capstone_donworry.MyApplication;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentCategoryBinding;
import com.example.capstone_donworry.fragment.calendar.ViewModelCalendar;
import com.example.capstone_donworry.model.statistics.CategoryAmount;
import com.example.capstone_donworry.model.statistics.MonthlyCategoryStatistics;
import com.example.capstone_donworry.model.statistics.PaymentAmount;
import com.example.capstone_donworry.model.statistics.MonthlyCategoryStatisticsResponse;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CategoryStatisticsFragment extends Fragment {
    private PieChart pieChart;
    private CalendarDay selectedDate;
    private String userID;

    private FragmentCategoryBinding binding;
    private ViewModelCalendar shareViewModel;
    private RequestQueue queue;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        pieChart = binding.pieChart;
        categoryRecyclerView = binding.categoryRecyclerView;

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        categoryRecyclerView.setAdapter(categoryAdapter);

        queue = MyApplication.getInstance().getRequestQueue();

        shareViewModel = new ViewModelProvider(requireActivity()).get(ViewModelCalendar.class);
        shareViewModel.getUserId().observe(getViewLifecycleOwner(), user -> {
            if (user != null && !user.equals(userID)) {
                userID = user;
                // 초기 날짜 오늘로 세팅
                selectedDate = CalendarDay.today();
                updateMonthText(selectedDate);
                updatePieChartData(selectedDate);
            }
        });

        // 이전달 버튼 클릭
        binding.btnPrevMonth.setOnClickListener(v -> {
            selectedDate = getPreviousMonth(selectedDate);
            updateMonthText(selectedDate);
            updatePieChartData(selectedDate);
        });

        // 다음달 버튼 클릭
        binding.btnNextMonth.setOnClickListener(v -> {
            selectedDate = getNextMonth(selectedDate);
            updateMonthText(selectedDate);
            updatePieChartData(selectedDate);
        });

        return binding.getRoot();
    }

    // 이전 달 계산
    private CalendarDay getPreviousMonth(CalendarDay date) {
        int year = date.getYear();
        int month = date.getMonth();
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }

        return CalendarDay.from(year, month, 1);
    }

    // 다음 달 계산
    private CalendarDay getNextMonth(CalendarDay date) {
        int year = date.getYear();
        int month = date.getMonth();
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
        return CalendarDay.from(year, month, 1);
    }

    // 월 텍스트 업데이트
    private void updateMonthText(CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth() - 1, date.getDay()); // 월은 0부터 시작하니까 -1!

        // 날짜 포맷 지정
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(calendar.getTime());

        binding.tvCurrentMonth.setText(formattedDate);
    }


    private void updatePieChartData(CalendarDay selectedDate) {
        if (userID == null) {
            return;
        }

        int year = selectedDate.getYear();
        int month = selectedDate.getMonth();



        String url = "http://10.0.2.2:8080/api/statistics/monthly/category"
                + "?year=" + year
                + "&month=" + month;

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "로그인 토큰이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonAuthorizedRequest jsonRequest = new JsonAuthorizedRequest(Request.Method.GET, url, null, requireContext(),
                response -> {
                    try {
                        Gson gson = new Gson();
                        MonthlyCategoryStatisticsResponse responseObj = gson.fromJson(response.toString(), MonthlyCategoryStatisticsResponse.class);
                        MonthlyCategoryStatistics stats = responseObj.getData();


                        long totalExpense = stats.getTotalExpense();
                        long goalAmount = stats.getGoalAmount();

                        long cardAmount = 0;
                        long cashAmount = 0;

                        TextView totalExpenseText = binding.totalExpenseText;
                        TextView goalAmountText = binding.goalExpenseText;
                        TextView cardAmountText = binding.cardAmountText;
                        TextView cashAmountText = binding.cashAmountText;

                        List<PaymentAmount> paymentExpenses = stats.getPaymentMethodExpenses();
                        if (paymentExpenses != null) {
                            for (PaymentAmount p : paymentExpenses) {
                                if ("CARD".equals(p.getPaymentMethod())) {
                                    cardAmount = p.getTotalAmount();
                                } else if ("CASH".equals(p.getPaymentMethod())) {
                                    cashAmount = p.getTotalAmount();
                                }

                            }
                        }


                        binding.totalExpenseText.setText("지출: " + totalExpense + "원");
                        binding.goalExpenseText.setText("목표 지출 금액: " + goalAmount + "원");
                        binding.cardAmountText.setText(cardAmount + "원");
                        binding.cashAmountText.setText(cashAmount + "원");



                        List<CategoryAmount> categoryData = stats.getCategoryExpenses();
                        if (categoryData == null || categoryData.isEmpty()) {
                            pieChart.clear();
                            pieChart.invalidate();
                            categoryAdapter.updateData(new ArrayList<>());
                            categoryAdapter.notifyDataSetChanged();
//                            Toast.makeText(getContext(), year + "년 " + month + "월 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        for (CategoryAmount item : categoryData) {
                            entries.add(new PieEntry(item.getTotalAmount(), item.getCategory()));
                        }

                        int[] categoryColors = {
                                ContextCompat.getColor(requireContext(), R.color.chart1),
                                ContextCompat.getColor(requireContext(), R.color.chart2),
                                ContextCompat.getColor(requireContext(), R.color.chart3),
                                ContextCompat.getColor(requireContext(), R.color.chart4),
                                ContextCompat.getColor(requireContext(), R.color.chart5),
                                ContextCompat.getColor(requireContext(), R.color.chart6),
                                ContextCompat.getColor(requireContext(), R.color.chart7),
                        };

                        Log.d("파이차트", "서버 응답: " + response.toString());


                        PieDataSet dataSet = new PieDataSet(entries, year + "년 " + month + "월 지출");
                        dataSet.setColors(categoryColors);
                        dataSet.setSliceSpace(3f);
                        dataSet.setValueTextSize(12f);
                        dataSet.setValueTextColor(Color.BLACK);

                        PieData pieData = new PieData(dataSet);
                        pieData.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return String.format(Locale.getDefault(), "%.0f%%", value);
                            }
                        });

                        pieChart.setData(pieData);
                        pieChart.setUsePercentValues(true);
                        pieChart.setEntryLabelColor(Color.TRANSPARENT); // 내부 라벨 숨기기
                        pieChart.getLegend().setEnabled(false);         // 범례 숨기기
                        pieChart.getDescription().setEnabled(false);
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setHoleColor(Color.TRANSPARENT);
                        pieChart.animateY(800, Easing.EaseInOutQuad);
                        pieChart.invalidate();

                        // 리스트 뷰에 데이터 적용
                        categoryAdapter.updateData(categoryData);
//                        categoryAdapter.setCategoryList(categoryData);
                        categoryAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "데이터 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

