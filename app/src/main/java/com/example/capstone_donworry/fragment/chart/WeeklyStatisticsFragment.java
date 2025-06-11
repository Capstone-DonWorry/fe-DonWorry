package com.example.capstone_donworry.fragment.chart;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.capstone_donworry.JsonAuthorizedRequest;
import com.example.capstone_donworry.MyApplication;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentWeeklyBinding;
import com.example.capstone_donworry.model.statistics.DailyExpense;
import com.example.capstone_donworry.model.statistics.PaymentExpense;
import com.example.capstone_donworry.model.statistics.WeeklyDetail;
import com.example.capstone_donworry.model.statistics.WeeklyStatistic;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyStatisticsFragment extends Fragment {

    private RecyclerView recyclerViewWeeks;
    private WeeklySummaryAdapter weeklySummaryAdapter;
    private FragmentWeeklyBinding binding;

    private LineChart chartDailyExpenses;
    // 그래프용 뷰 (MPAndroidChart 등 사용)
    private TextView tvCardAmount;
    private TextView tvCashAmount;
    private TextView tv; // 총 지출

    private TextView weekRangeText;

    private int currentYear;
    private int currentMonth;

    private RequestQueue queue;
    private String token;
    private boolean isLoading = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeeklyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewWeeks = root.findViewById(R.id.weeklyRecyclerView);
        recyclerViewWeeks.setLayoutManager(new LinearLayoutManager(getContext()));
        weeklySummaryAdapter = new WeeklySummaryAdapter(new ArrayList<>(), this::onWeekSelected);
        recyclerViewWeeks.setAdapter(weeklySummaryAdapter);

        chartDailyExpenses = root.findViewById(R.id.weeklyLineChart);

        tvCardAmount = root.findViewById(R.id.cardAmountText);
        tvCashAmount = root.findViewById(R.id.cashAmountText);
        tv = root.findViewById(R.id.totalExpenseText);
        weekRangeText = root.findViewById(R.id.weekRangeText);


        queue = MyApplication.getInstance().getRequestQueue();
        token = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("token", null);

        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;

        CalendarDay today = CalendarDay.from(currentYear, currentMonth, cal.get(Calendar.DAY_OF_MONTH));
        updateMonthText(today);

        fetchWeeklySummaryList(currentYear, currentMonth);

        // 이전/다음 월 버튼 처리 예)
        binding.btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            fetchWeeklySummaryList(currentYear, currentMonth);
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            fetchWeeklySummaryList(currentYear, currentMonth);
        });

        return root;
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
        calendar.set(date.getYear(), date.getMonth() - 1, date.getDay()); // 월은 0부터 시작하니까 -1

        // 날짜 포맷 지정
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(calendar.getTime());

        binding.tvCurrentMonth.setText(formattedDate);
    }

    private void fetchWeeklySummaryList(int year, int month) {
        Log.d("WeeklyFragment", "fetchWeeklySummaryList 호출 - year: " + year + ", month: " + month);
        binding.tvCurrentMonth.setText(String.valueOf(month));

        if (isLoading) return;
        isLoading = true;

        String url = String.format("http://10.0.2.2:8080/api/statistics/weekly?year=%d&month=%d", year, month);


        JsonAuthorizedRequest request = new JsonAuthorizedRequest(Request.Method.GET, url, null, requireContext(),
                response -> {
                    try {
                        clearWeeklyDetailViews();
                        JSONObject json = new JSONObject(response.toString());
                        JSONArray data = json.getJSONArray("data");

                        Type listType = new TypeToken<List<WeeklyStatistic>>() {}.getType();
                        List<WeeklyStatistic> summaries = new Gson().fromJson(data.toString(), listType);

                        weeklySummaryAdapter.updateData(summaries);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "주간 통계 파싱 실패", Toast.LENGTH_SHORT).show();
                    } finally {
                        isLoading = false;
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
        );
        queue.add(request);
    }

    // 주 선택 시 상세 데이터 호출
    private void onWeekSelected(WeeklyStatistic selectedWeek) {
        clearWeeklyDetailViews();

        long totalExpense = selectedWeek.getTotalExpense();
        tv.setText(totalExpense + "원");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat outputFormat = new SimpleDateFormat("M월 d일", Locale.KOREA);

        try {
            Date startDate = inputFormat.parse(selectedWeek.getStartDate());
            Date endDate = inputFormat.parse(selectedWeek.getEndDate());

            String weekRange = outputFormat.format(startDate) + " - " + outputFormat.format(endDate);
            weekRangeText.setText(weekRange);
        } catch (ParseException e) {
            e.printStackTrace();
            weekRangeText.setText("날짜 오류");
        }

        fetchWeeklyDetail(selectedWeek.getStartDate(), selectedWeek.getEndDate());

    }

    private void fetchWeeklyDetail(String startDate, String endDate) {
        Log.d("API Request", "startdate: " + startDate + ", enddate: " + endDate);

        String url = String.format("http://10.0.2.2:8080/api/statistics/weekly/detail?startDate=%s&endDate=%s", startDate, endDate);

        JsonAuthorizedRequest request = new JsonAuthorizedRequest(Request.Method.GET, url, null, requireContext(),
                response -> {
                    try {
                        Log.d("서버응답", response.toString());

                        JSONObject json = new JSONObject(response.toString());
                        JSONObject data = json.getJSONObject("data");

                        Gson gson = new Gson();
                        WeeklyDetail detail = gson.fromJson(data.toString(), WeeklyDetail.class);

                        // ✅ 디버깅용 로그 추가
                        Log.d("주간상세", "파싱된 dailyAmounts 크기: " + (detail.dailyAmounts != null ? detail.dailyAmounts.size() : -1));
                        Log.d("주간상세", "파싱된 paymentAmounts 크기: " + (detail.paymentAmounts != null ? detail.paymentAmounts.size() : -1));

                        List<DailyExpense> paddedDailyExpenses = getPaddedDailyExpenses(detail.dailyAmounts, startDate, endDate);
                        showDailyExpensesGraph(paddedDailyExpenses);


                        if (detail.paymentAmounts == null || detail.paymentAmounts.isEmpty()) {
                            Log.d("주간상세", "paymentExpenses가 null이거나 비어있음");
                        } else {
                            showPaymentExpenses(detail.paymentAmounts);
                        }

//                        showPaymentExpenses(detail.paymentAmounts);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "상세 통계 불러오기 실패", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }

    private void clearWeeklyDetailViews() {
        if (chartDailyExpenses != null) {
            chartDailyExpenses.clear();  // 차트 비우기
        }

        tvCardAmount.setText("0원");
        tvCashAmount.setText("0원");

    }


    // 그래프 그리기 함수
    private void showDailyExpensesGraph(List<DailyExpense> dailyExpenses) {
        if (dailyExpenses == null || dailyExpenses.isEmpty()) {
            Log.d("주간상세", "dailyExpenses가 null이거나 비어있음");
            // 그래프를 초기화하거나, '데이터 없음' 표시 등
            return;
        }
        List<Entry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        for (int i = 0; i < dailyExpenses.size(); i++) {
            DailyExpense item = dailyExpenses.get(i);
            entries.add(new Entry(i, (float) item.getAmount()));
            xLabels.add(item.getDate().substring(5)); // MM-DD만 표시
        }

        LineDataSet dataSet = new LineDataSet(entries, "일별 지출");
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setCircleColor(Color.parseColor("#3F51B5"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#C5CAE9"));

        LineData lineData = new LineData(dataSet);
        chartDailyExpenses.setData(lineData);

        chartDailyExpenses.getDescription().setEnabled(false);
        chartDailyExpenses.getLegend().setEnabled(false);


        // X축 설정
        XAxis xAxis = chartDailyExpenses.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < xLabels.size()) {
                    return xLabels.get(index);
                } else {
                    return "";
                }
            }
        });

        // Y축 설정
        YAxis leftAxis = chartDailyExpenses.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = chartDailyExpenses.getAxisRight();
        rightAxis.setEnabled(false);

        // 애니메이션 및 갱신
        chartDailyExpenses.animateY(500);
        chartDailyExpenses.invalidate();
    }

    private List<DailyExpense> getPaddedDailyExpenses(List<DailyExpense> original, String startDate, String endDate) {
        Map<String, Long> map = new HashMap<>();
        if (original != null) {
            for (DailyExpense d : original) {
                map.put(d.getDate(), d.getAmount());
            }
        }

        List<DailyExpense> result = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(startDate));
            Date end = sdf.parse(endDate);

            while (!cal.getTime().after(end)) {
                String date = sdf.format(cal.getTime());
                long amount = map.getOrDefault(date, 0L);
                result.add(new DailyExpense(date, amount));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }



    // 지불방법별 총액 표시
    private void showPaymentExpenses(List<PaymentExpense> paymentExpenses) {
        for (PaymentExpense p : paymentExpenses) {
            String method = p.getPaymentMethod();
            Long amount = p.getTotalAmount();

            if ("CARD".equals(method)) {
                tvCardAmount.setText(amount + "원");
            } else if ("CASH".equals(method)) {
                tvCashAmount.setText(amount + "원");
            }
        }

    }
}


