package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.fragment.calendar.AmountAdapter;
import com.example.capstone_donworry.fragment.calendar.CalendarDeco;
import com.example.capstone_donworry.fragment.calendar.CalendarTextDeco;
import com.example.capstone_donworry.fragment.calendar.CustomDayDecorator;
import com.example.capstone_donworry.databinding.FragmentCalendarBinding;
import com.example.capstone_donworry.fragment.calendar.AmountItem;
import com.example.capstone_donworry.model.DailySummary;
import com.example.capstone_donworry.model.ExpectedExpense;
import com.example.capstone_donworry.model.Expense;
import com.example.capstone_donworry.model.MonthlySummary;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentCalendar extends Fragment implements PopAddItem.ItemAddListener, PopShowDaylist.OnDialogCancelListener, PopDetailItem.OnDialogCancelListener {
    private FragmentCalendarBinding binding;
    private RecyclerView recyclerView;
    private AmountAdapter adapter;
    private MaterialCalendarView calendarView;
    private TextView ableAmount, targetAmount, totalExpenseTV;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private ViewModelCalendar viewModelCalendar;
    private String userID;
    private Map<String, CalendarTextDeco> dots = new HashMap<>();
    private CalendarDay selectDay;

    private CheckBox checkBoxCard, checkBoxCash;

    private MonthlySummary monthlySummary;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.RecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AmountAdapter(getContext());
        recyclerView.setAdapter(adapter);

        calendarView = binding.calendarView;
        ableAmount = binding.AbleAmount;
        targetAmount = binding.TargetAmount;
        totalExpenseTV = binding.TotalExpense;

        // CheckBox
        checkBoxCard = binding.CheckBoxCard;
        checkBoxCash = binding.CheckBoxCash;

        checkBoxCard.setChecked(true);
        checkBoxCash.setChecked(true);

        binding.CheckBoxCard.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());
        binding.CheckBoxCash.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());

        decimalFormat = new DecimalFormat("#,###");
        dots = new HashMap<>();

        adapter.setOnClickListener((holder, view, position) -> {
            AmountItem item = adapter.getItem(position);
            PopDetailItem popDetail = PopDetailItem.newInstance(item);
            popDetail.setTargetFragment(FragmentCalendar.this, 0);
            popDetail.setOnUpdateListener(FragmentCalendar.this::updateExpense);
            popDetail.show(getParentFragmentManager(), "세부내역");
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                AmountItem deleteItem = adapter.getItem(position);

                Snackbar.make(recyclerView, deleteItem.getContent() + " 삭제했습니다.", Snackbar.LENGTH_LONG)
                        .setAction("취소", v -> updateRecycler(deleteItem))
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.err_red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_forever_24)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        binding.ButtonAdd.setOnClickListener(v -> showAddItem());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelCalendar = new ViewModelProvider(requireActivity()).get(ViewModelCalendar.class);
        viewModelCalendar.getUserId().observe(getViewLifecycleOwner(), user -> {
            userID = user;
            initView();
        });
    }

    private void initView() {
        calendarView.addDecorators(
                CalendarDeco.todayViewDecorator(requireContext()),
                CalendarDeco.sundayDecorator(),
                CalendarDeco.saturdayDecorator()
        );

        CalendarDay today = CalendarDay.today();
        fetchMonthlySummary(today.getYear(), today.getMonth(), () -> {});

        calendarView.setOnMonthChangedListener((widget, date) -> fetchMonthlySummary(date.getYear(), date.getMonth(), () -> {}));
        calendarView.setOnDateChangedListener((widget, date, selected) -> showDateAmount(date));
    }

    private void fetchMonthlySummary(int year, int month, Runnable onComplete) {
        adapter.clearItems();
        String url = "http://10.0.2.2:8080/api/calendar/monthly?year=" + year + "&month=" + month;
        SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        if (token.isEmpty()) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject data = response.getJSONObject("data");

                targetAmount.setText(decimalFormat.format(data.getLong("goalAmount")));
                ableAmount.setText(decimalFormat.format(data.getLong("remaining")));
                totalExpenseTV.setText(decimalFormat.format(data.getLong("totalExpenseAndExpectedExpense")));

                MonthlySummary summary = new MonthlySummary();
                summary.setGoalAmount(data.getLong("goalAmount"));
                summary.setRemaining(data.getLong("remaining"));
                summary.setTotalExpenseAndExpectedExpense(data.getLong("totalExpenseAndExpectedExpense"));
                summary.setCardExpenses(data.getLong("cardExpenses"));
                summary.setCashExpenses(data.getLong("cashExpenses"));

                JSONObject daysObj = data.getJSONObject("days");
                Map<String, DailySummary> daysMap = new HashMap<>();

                List<AmountItem> totalList = new ArrayList<>();

                Iterator<String> keys = daysObj.keys();
                while (keys.hasNext()) {
                    String dateKey = keys.next();
                    JSONObject dayJson = daysObj.getJSONObject(dateKey);

                    DailySummary daily = new DailySummary();
                    daily.setDailyGoal(dayJson.getLong("dailyGoal"));
                    daily.setDailyTotalExpense(dayJson.getLong("dailyTotalExpense"));
                    daily.setDailyTotalExpectedExpense(dayJson.getLong("dailyTotalExpectedExpense"));
                    daily.setDailyLevel(dayJson.getInt("dailyGoal"));

                    List<Expense> expenseList = new ArrayList<>();
                    JSONArray expenses = dayJson.getJSONArray("dailyExpenseList");
                    for (int i = 0; i < expenses.length(); i++) {
                        JSONObject e = expenses.getJSONObject(i);
                        Expense exp = new Expense();
                        exp.setId(e.getLong("id"));
                        exp.setTitle(e.getString("title"));
                        exp.setAmount(e.getLong("amount"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            exp.setExpenseDate(LocalDate.parse(e.getString("expenseDate")));
                        }
                        exp.setCategory(e.getString("category"));
                        exp.setPayment(e.getString("payment"));
                        exp.setBankName(e.optString("bankName", ""));
                        expenseList.add(exp);

                        // 리스트에 추가
                        AmountItem item = new AmountItem();
                        item.setUid(exp.getId());
                        item.setContent(exp.getTitle());
                        item.setAmount((int) exp.getAmount());
                        item.setDate(exp.getExpenseDate().toString());
                        item.setCard(exp.getPayment());
                        item.setCategory(exp.getCategory());
                        item.setBank(exp.getBankName());
                        totalList.add(item);
                    }
                    daily.setDailyExpenseList(expenseList);

                    List<ExpectedExpense> expectedList = new ArrayList<>();
                    JSONArray expectedArray = dayJson.getJSONArray("dailyExpectedList");
                    for (int i = 0; i < expectedArray.length(); i++) {
                        JSONObject ex = expectedArray.getJSONObject(i);
                        ExpectedExpense expected = new ExpectedExpense();
                        expected.setId(ex.getLong("id"));
                        expected.setDetails(ex.getString("details"));
                        expected.setAmount(ex.getLong("amount"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            expected.setDate(LocalDate.parse(ex.getString("date")));
                        }
                        expectedList.add(expected);
                    }
                    daily.setDailyExpectedList(expectedList);

                    daysMap.put(dateKey, daily);
                }

                summary.setDays(daysMap);
                monthlySummary = summary;

                calendarView.removeDecorators();
                calendarView.addDecorators(
                        CalendarDeco.todayViewDecorator(requireContext()),
                        CalendarDeco.sundayDecorator(),
                        CalendarDeco.saturdayDecorator()
                );

                for (String dateKey : daysMap.keySet()) {
                    DailySummary ds = daysMap.get(dateKey);
                    CalendarDay cd = stringToCalendarDay(dateKey);
                    calendarView.addDecorator(new CustomDayDecorator(cd, ds.getDailyTotalExpense().intValue(), ds.getDailyGoal().intValue(), ds.getDailyLevel()));
                }

                adapter.updateItems(totalList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("fetchMonthlySummary", "error: " + error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private CalendarDay stringToCalendarDay(String dateString) {
        String[] parts = dateString.split("-");
        return CalendarDay.from(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    private void updateRecycler(AmountItem item) {
        List<AmountItem> items = adapter.getItems();
        items.add(item);
        Collections.sort(items, (a, b) -> b.getDate().compareTo(a.getDate()));
        adapter.updateItems(items);
        sumTotalExpense();
    }


    private void showDateAmount(CalendarDay date) {
        selectDay = date; // 선택된 날짜 저장

        String strMon = String.format("%02d", date.getMonth());
        String strDay = String.format("%02d", date.getDay());
        String dateKey = date.getYear() + "-" + strMon + "-" + strDay;

        if (monthlySummary == null || monthlySummary.getDays() == null) {
            Log.w("showDateAmount", "월별 데이터가 아직 로딩되지 않음");
            return;
        }

        DailySummary daySummary = monthlySummary.getDays().get(dateKey);
        if (daySummary == null) {
            Log.w("showDateAmount", "선택한 날짜에 해당하는 데이터 없음: " + dateKey);
            return;
        }

        List<AmountItem> amountList = new ArrayList<>();
        for (Expense expense : daySummary.getDailyExpenseList()) {
            AmountItem item = new AmountItem();
            item.setUid(expense.getId());
            item.setContent(expense.getTitle());
            item.setAmount((int) expense.getAmount());
            item.setDate(expense.getExpenseDate().toString());
            item.setCard(expense.getPayment());
            item.setCategory(expense.getCategory());
            item.setBank(expense.getBankName());
            amountList.add(item);
        }

        long recomAmount = daySummary.getDailyGoal();
        long totalSpent = daySummary.getDailyTotalExpense();
        long expectedSpent = daySummary.getDailyTotalExpectedExpense();

        // 팝업 다이얼로그 일일 리스트
        PopShowDaylist popShowDaylist = PopShowDaylist.newInstance(
                (ArrayList<AmountItem>) amountList,
                dateKey,
                recomAmount,
                totalSpent,
                expectedSpent
        );
        popShowDaylist.setUserId(userID);
        popShowDaylist.setTargetFragment(this, 0);
        popShowDaylist.show(getParentFragmentManager(), "특정날짜");
    }



    private long sumTotalExpense() {
        long total = 0;
        boolean isCardChecked = binding.CheckBoxCard.isChecked();
        boolean isCashChecked = binding.CheckBoxCash.isChecked();

        for (AmountItem item : adapter.getItems()) {
            if ((isCardChecked && "CARD".equals(item.getCard())) ||
                    (isCashChecked && "CASH".equals(item.getCard()))) {
                total += item.getAmount();
            }
        }

        totalExpenseTV.setText(decimalFormat.format(total));
        return total;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemAdded(AmountItem item) {
        try {
            JSONObject body = new JSONObject();
            body.put("title", item.getContent());
            body.put("amount", item.getAmount());
            body.put("expenseDate", item.getDate());
            String payment = item.getCard();
            if ("카드".equals(payment)) {
                payment = "CARD";
            } else if ("현금".equals(payment)) {
                payment = "CASH";
            }
            body.put("payment", payment);
            String category = item.getCategory();
            switch (category) {
                case "식비": category = "FOOD"; break;
                case "쇼핑": category = "SHOPPING"; break;
                case "여가": category = "LEISURE"; break;
                case "병원": category = "HEALTHCARE"; break;
                case "생활비": category = "LIVING"; break;
                case "교통비": category = "TRANSPORTATION"; break;
                default: category = "OTHER"; break;
            }
            body.put("category", category);
            body.put("bankName", item.getBank());

            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = prefs.getString("token", "");

            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2:8080/api/expense",
                    body,
                    response -> {
                        // 성공 시 최신 데이터 다시 로딩
                        CalendarDay today = calendarView.getCurrentDate();
                        fetchMonthlySummary(today.getYear(), today.getMonth(), () -> {});
                    },
                    error -> Log.e("AddExpense", "추가 실패: " + error.toString())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(requireContext()).add(postRequest);

            // 프론트에서도 임시로 즉시 업데이트
            updateRecycler(item);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogCancel() {
        // 다이얼로그에서 아무것도 안 했을 때도, 그냥 서버에서 해당 월 데이터 재요청
        if (selectDay != null) {
            fetchMonthlySummary(selectDay.getYear(), selectDay.getMonth(), () -> {});
        }
    }

    @Override
    public void onDialogUpdate(String date) {
        CalendarDay targetDay = stringToCalendarDay(date);
        fetchMonthlySummary(targetDay.getYear(), targetDay.getMonth(), () -> {});
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectDay != null) {
            fetchMonthlySummary(selectDay.getYear(), selectDay.getMonth(), () -> {});
        }
    }

    void updateExpense(AmountItem updatedItem) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", updatedItem.getUid());
            body.put("title", updatedItem.getContent());
            body.put("amount", updatedItem.getAmount());
            body.put("expenseDate", updatedItem.getDate());

            String payment = updatedItem.getCard();
            if ("카드".equals(payment)) payment = "CARD";
            else if ("현금".equals(payment)) payment = "CASH";
            body.put("payment", payment);

            String category = updatedItem.getCategory();
            if (category != null && !category.matches("[A-Z_]+")) {
                switch (category) {
                    case "식비": category = "FOOD"; break;
                    case "쇼핑": category = "SHOPPING"; break;
                    case "여가": category = "LEISURE"; break;
                    case "병원": category = "HEALTHCARE"; break;
                    case "생활비": category = "LIVING"; break;
                    case "교통비": category = "TRANSPORTATION"; break;
                    default: category = "OTHER"; break;
                }
            }

            body.put("category", category);
            body.put("bankName", updatedItem.getBank());

            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = prefs.getString("token", "");

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    "http://10.0.2.2:8080/api/expense/" + updatedItem.getUid(),
                    body,
                    response -> {
                        if (selectDay != null) {
                            fetchMonthlySummary(selectDay.getYear(), selectDay.getMonth(), () -> {
                                showDateAmount(selectDay); // ✅ 반드시 fetch 후에 호출되어야 함!
                            });
                        }

                        updateItemInRecycler(updatedItem);
                    },
                    error -> Log.e("UpdateExpense", "수정 실패: " + error.toString())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(requireContext()).add(putRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateItemInRecycler(AmountItem updatedItem) {
        List<AmountItem> items = adapter.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUid() == (updatedItem.getUid())) {
                items.set(i, updatedItem);  // 수정된 아이템 반영
                break;
            }
        }

        // 정렬 다시 하고 반영
        Collections.sort(items, (a, b) -> a.getDate().compareTo(b.getDate()));

        adapter.updateItems(items);

        // 총 지출 및 잔액 업데이트
        long total = sumTotalExpense();
        long remaining = Long.parseLong(targetAmount.getText().toString().replace(",", "")) - total;
        ableAmount.setText(decimalFormat.format(remaining));
    }

}