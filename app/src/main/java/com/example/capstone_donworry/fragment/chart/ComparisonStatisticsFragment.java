package com.example.capstone_donworry.fragment.chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.model.statistics.CategoryExpense;
import com.example.capstone_donworry.model.statistics.ComparisonStatistics;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparisonStatisticsFragment extends Fragment {

    private TextView tvGoalComparisonTitle;
    private TextView tvAgeComparisonTitle;
    private TextView tvCurrentMonth;
    private BarChart barChartGoal;
    private BarChart barChartAge;
    private View includeGoalTable;
    private View includeAgeTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparison_statistics, container, false);

        tvGoalComparisonTitle = view.findViewById(R.id.tv_goalComparisonTitle);
        tvAgeComparisonTitle = view.findViewById(R.id.tv_ageComparisonTitle);
        tvCurrentMonth = view.findViewById(R.id.tv_currentMonth);
        barChartGoal = view.findViewById(R.id.barChart_goal);
        barChartAge = view.findViewById(R.id.barChart_age);
        includeGoalTable = view.findViewById(R.id.include_goalTable);
        includeAgeTable = view.findViewById(R.id.include_ageTable);

        int year = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            year = LocalDate.now().getYear();
        }
        int month = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = LocalDate.now().getMonthValue();
        }

        fetchComparisonStatistics(requireContext(), year, month);

        return view;
    }

    private void fetchComparisonStatistics(Context context, int year, int month) {
        String url = "http://10.0.2.2:8080/api/statistics/monthly/comparison?year=" + year + "&month=" + month;
        SharedPreferences prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        Gson gson = new Gson();
                        ComparisonStatistics stats = gson.fromJson(data.toString(), ComparisonStatistics.class);
                        logComparisonStats(stats);
                        updateUI(stats);
                    } catch (Exception e) {
                        Toast.makeText(context, "파싱 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ComparisonStats", "파싱 실패", e);
                    }
                },
                error -> {
                    Toast.makeText(context, "네트워크 오류: " + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("ComparisonStats", "요청 실패", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void updateUI(ComparisonStatistics stats) {
        int currentMonth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentMonth = LocalDate.now().getMonthValue();
        }
        tvCurrentMonth.setText(currentMonth + "월");

        long myExpense = stats.getMyTotalExpense() != null ? stats.getMyTotalExpense() : 0L;
        long myGoal = stats.getMyMonthExpenseGoal() != null ? stats.getMyMonthExpenseGoal() : 0L;
        String ageGroup = stats.getAgeGroup() != null ? stats.getAgeGroup() : "미지정";

        String goalText = String.format("목표 지출 금액 %,d원 사용자 비교", myGoal);
        tvGoalComparisonTitle.setText(goalText);
        String ageText = String.format("총 지출 금액 %s대 사용자 비교", ageGroup);
        tvAgeComparisonTitle.setText(ageText);

        long goalAvg = stats.getByGoalAmount() != null && stats.getByGoalAmount().getAvgExpense() != null
                ? stats.getByGoalAmount().getAvgExpense() : 0L;
        long ageAvg = stats.getByAgeGroup() != null && stats.getByAgeGroup().getAvgExpense() != null
                ? stats.getByAgeGroup().getAvgExpense() : 0L;

        setBarChartData(barChartGoal, "나", myExpense, "평균", goalAvg);
        setBarChartData(barChartAge, "나", myExpense, ageGroup, ageAvg);

        List<CategoryExpense> myTop = stats.getMyTopCategories() != null ? stats.getMyTopCategories() : new ArrayList<>();
        List<CategoryExpense> goalTop = stats.getByGoalAmount() != null ? stats.getByGoalAmount().getTopCategories() : new ArrayList<>();
        List<CategoryExpense> ageTop = stats.getByAgeGroup() != null ? stats.getByAgeGroup().getTopCategories() : new ArrayList<>();

        populateCategoryTable(includeGoalTable, myTop, goalTop);
        populateCategoryTable(includeAgeTable, myTop, ageTop);
    }

    private void setBarChartData(BarChart chart, String label1, long value1, String label2, long value2) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, value1));
        entries.add(new BarEntry(1, value2));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#4A90E2"), Color.parseColor("#BDC3C7"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(13f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 값이 0이면 아무 것도 표시하지 않음
                if (value == 0f) return "";
                return String.format("%,d", (int) value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chart.setData(barData);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0f) return label1;
                else if (value == 1f) return label2;
                else return "";
            }
        });
        chart.setFitBars(true);
        chart.invalidate();
    }

    private void populateCategoryTable(View tableLayoutRoot,
                                       List<CategoryExpense> myTop,
                                       List<CategoryExpense> avgTop) {
        if (tableLayoutRoot == null) return;

        TextView[] myCells = {
                tableLayoutRoot.findViewById(R.id.tv_myCat1),
                tableLayoutRoot.findViewById(R.id.tv_myCat2),
                tableLayoutRoot.findViewById(R.id.tv_myCat3)
        };
        TextView[] avgCells = {
                tableLayoutRoot.findViewById(R.id.tv_avgCat1),
                tableLayoutRoot.findViewById(R.id.tv_avgCat2),
                tableLayoutRoot.findViewById(R.id.tv_avgCat3)
        };

        for (int i = 0; i < 3; i++) {
            String myText = i < myTop.size() ? mapCategory(myTop.get(i).getCategory()) : "-";
            String avgText = i < avgTop.size() ? mapCategory(avgTop.get(i).getCategory()) : "-";
            myCells[i].setText(myText);
            avgCells[i].setText(avgText);
        }
    }

    private String mapCategory(String categoryEnum) {
        if (categoryEnum == null) return "-";
        switch (categoryEnum) {
            case "FOOD": return "식비";
            case "SHOPPING": return "쇼핑";
            case "LEISURE": return "여가";
            case "HEALTHCARE": return "의료/건강";
            case "LIVING": return "생활비";
            case "TRANSPORTATION": return "교통비";
            case "CULTURE": return "문화생활";
            default: return "기타";
        }
    }

    private void logComparisonStats(ComparisonStatistics stats) {
        Log.d("ComparisonStats", "📊 내 총 지출: " + stats.getMyTotalExpense());
        Log.d("ComparisonStats", "📊 내 목표 금액: " + stats.getMyMonthExpenseGoal());
        Log.d("ComparisonStats", "📊 비교 기준(연령대): " + stats.getAgeGroup());

        if (stats.getMyTopCategories() != null) {
            for (int i = 0; i < stats.getMyTopCategories().size(); i++) {
                Log.d("ComparisonStats", "내 TOP" + (i + 1) + ": " +
                        stats.getMyTopCategories().get(i).getCategory() + " - " +
                        stats.getMyTopCategories().get(i).getTotalAmount());
            }
        }
    }
}

