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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentCalendarBinding;
import com.example.capstone_donworry.model.DailySummary;
import com.example.capstone_donworry.model.ExpectedExpense;
import com.example.capstone_donworry.model.Expense;
import com.example.capstone_donworry.model.MonthlySummary;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentCalendar extends Fragment implements PopAddExpectedItem.ItemAddListener, PopAddItem.ItemAddListener, PopShowDaylist.OnDialogCancelListener, PopDetailItem.OnDialogCancelListener {
    private FragmentCalendarBinding binding;
    // 클래스에 추가
    private final Map<CalendarDay, CustomDayDecorator> decoratorMap = new HashMap<>();
    private RecyclerView recyclerView;
    private MixedAmountAdapter adapter;

    private MaterialCalendarView calendarView;
    private TextView ableAmount, targetAmount, totalExpenseTV;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private ViewModelCalendar viewModelCalendar;
    private String userID;
    private Map<String, CalendarTextDeco> dots = new HashMap<>();
    private CalendarDay selectDay = null;

    private CheckBox checkBoxCard, checkBoxCash;

    private MonthlySummary monthlySummary;

    private PopShowDaylist popShowDaylist;

    private boolean shouldShowPopup = true;

    private boolean isReturningFromEdit = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.RecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MixedAmountAdapter(getContext());
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
            Item item = adapter.getItem(position);
            if (item instanceof AmountItem) {
                AmountItem amountItem = (AmountItem) item;
                PopDetailItem popDetail = PopDetailItem.newInstance(amountItem);
                popDetail.setTargetFragment(FragmentCalendar.this, 0);
                popDetail.setOnUpdateListener(FragmentCalendar.this::updateExpense);
                popDetail.show(getParentFragmentManager(), "세부내역");
            } else if (item instanceof AmountExpectedItem) {
                AmountExpectedItem expectedItem = (AmountExpectedItem) item;
                PopDetailExpectedItem popExpected = PopDetailExpectedItem.newInstance(expectedItem);
                popExpected.setTargetFragment(FragmentCalendar.this, 0);
                popExpected.setOnUpdateListener(FragmentCalendar.this::updateExpectedExpense);
                popExpected.show(getParentFragmentManager(), "예상세부내역");
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Item deleteItem = adapter.getItem(position);

                //어뎁터에서 삭제
                adapter.removeItem(position);

                if (deleteItem instanceof AmountItem) {
                    long expenseId = ((AmountItem) deleteItem).getUid();
                    String url = "http://10.0.2.2:8080/api/expense/" + expenseId;
                    JsonObjectRequest deleteRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url,
                            null,
                            response -> Log.d("DELETE", "삭제 성공"),
                            error -> Log.e("DELETE", "삭제 실패: " + error.toString())
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token = prefs.getString("token", "");
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };
                    Volley.newRequestQueue(requireContext()).add(deleteRequest);
                } else if (deleteItem instanceof AmountExpectedItem) {
                    long expectedId = ((AmountExpectedItem) deleteItem).getId();
                    String url = "http://10.0.2.2:8080/api/expectedExpenditure/" + expectedId;

                    JsonObjectRequest deleteRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url,
                            null,
                            response -> Log.d("DELETE", "예상 삭제 성공"),
                            error -> Log.e("DELETE", "예상 삭제 실패: " + error.toString())
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            String token = prefs.getString("token", "");
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };
                    Volley.newRequestQueue(requireContext()).add(deleteRequest);
                }

                Snackbar.make(recyclerView, "삭제했습니다.", Snackbar.LENGTH_LONG)
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

        //binding.b.setOnClickListener(v -> showAddItem());

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
        TextView btnExpense = view.findViewById(R.id.btn_add_expense);
        TextView btnExpected = view.findViewById(R.id.btn_add_expected);
        FrameLayout menuContainer = view.findViewById(R.id.menuContainer);
        TextView btnMenu = view.findViewById(R.id.btnShowAddMenu);

        btnMenu.setOnClickListener(v -> {
            menuContainer.setVisibility(menuContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        btnExpense.setOnClickListener(e -> {
            showAddItem();
            menuContainer.setVisibility(View.GONE);
        });

        btnExpected.setOnClickListener(e -> {
            showAddExpectedItem();
            menuContainer.setVisibility(View.GONE);
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
        Log.i("temp" , "temp");
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

                long totalRealExpense = summary.getCardExpenses() + summary.getCashExpenses();
                totalExpenseTV.setText(decimalFormat.format(totalRealExpense));

                JSONObject daysObj = data.getJSONObject("days");
                Map<String, DailySummary> daysMap = new HashMap<>();
                List<Item> totalList = new ArrayList<>();

                Iterator<String> keys = daysObj.keys();
                while (keys.hasNext()) {
                    String dateKey = keys.next();
                    JSONObject dayJson = daysObj.getJSONObject(dateKey);

                    DailySummary daily = new DailySummary();
                    daily.setDailyGoal(dayJson.getLong("dailyGoal"));
                    daily.setDailyTotalExpense(dayJson.getLong("dailyTotalExpense"));
                    daily.setDailyTotalExpectedExpense(dayJson.getLong("dailyTotalExpectedExpense"));
                    daily.setDailyLevel(dayJson.getInt("dailyLevel"));

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

                        AmountExpectedItem expectedItem = new AmountExpectedItem();
                        expectedItem.setId(expected.getId());
                        expectedItem.setContent(expected.getDetails());
                        expectedItem.setAmount(expected.getAmount());
                        expectedItem.setDate(expected.getDate().toString());
                        totalList.add(expectedItem);
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

                decoratorMap.clear();
                for (Map.Entry<String, DailySummary> entry : daysMap.entrySet()) {
                    CalendarDay day = stringToCalendarDay(entry.getKey());
                    int level = entry.getValue().getDailyLevel();
                    calendarView.addDecorator(new CustomDayDecorator(day, level));
                }
                Log.i("fetchList", "totalList" + totalList.size());
                adapter.updateItems(new ArrayList<>(totalList));

                onComplete.run();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("fetchMonthlySummary", "❌ error: " + error.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void showDateAmount(CalendarDay date) {
        if (date == null || monthlySummary == null) return;

        selectDay = date;  // 현재 선택된 날짜 저장

        String key = date.getYear() + "-" +
                String.format("%02d", date.getMonth()) + "-" +
                String.format("%02d", date.getDay());

        DailySummary daySummary = monthlySummary.getDays().get(key);

        if (daySummary == null) {
            Log.w("POPUP", "해당 날짜에 대한 summary가 없음: " + selectDay);
            return;
        }

        // amountList, expectedList 분리
        ArrayList<AmountItem> amountList = new ArrayList<>();
        ArrayList<AmountExpectedItem> expectedList = new ArrayList<>();

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

        for (ExpectedExpense expected : daySummary.getDailyExpectedList()) {
            AmountExpectedItem item = new AmountExpectedItem();
            item.setId(expected.getId());
            item.setContent(expected.getDetails());
            item.setAmount(expected.getAmount());
            item.setDate(expected.getDate().toString());
            expectedList.add(item);
        }

        // showDateAmount 내부 일부
        popShowDaylist = PopShowDaylist.newInstance(
                amountList,
                expectedList,
                key,
                daySummary.getDailyGoal(),
                daySummary.getDailyTotalExpense(),
                daySummary.getDailyTotalExpectedExpense()
        );
        popShowDaylist.setTargetFragment(FragmentCalendar.this, 0);
        popShowDaylist.show(getParentFragmentManager(), "DayListPopup");

        selectDay = null;
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
                        Log.d("UPDATE_EXPECTED", "수정 성공!");
                        Log.d("UPDATE_EXPECTED", "day!" + selectDay);
                        int year = stringToCalendarDay(updatedItem.getDate()).getYear();
                        int month = stringToCalendarDay(updatedItem.getDate()).getMonth();
                        if (selectDay == null) {
                            fetchMonthlySummary(year, month, () -> {
                                updateItemInRecycler(updatedItem);
                            });
                        } else {
                            fetchMonthlySummary(year, month, () -> {
                                showDateAmount(selectDay);
                                updateItemInRecycler(updatedItem);
                            });
                        }
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


    void updateExpectedExpense(AmountExpectedItem updatedItem) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", updatedItem.getId());
            body.put("details", updatedItem.getContent());
            body.put("amount", updatedItem.getAmount());
            body.put("date", updatedItem.getDate());

            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = prefs.getString("token", "");

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    "http://10.0.2.2:8080/api/expectedExpenditure/" + updatedItem.getId(),
                    body,
                    response -> {
                        Log.d("UPDATE_EXPECTED", "수정 성공!");
                        Log.d("UPDATE_EXPECTED", "id" + updatedItem.getId());
                        Log.d("UPDATE_EXPECTED", "details" + updatedItem.getContent());
                        Log.d("UPDATE_EXPECTED", "amount" + updatedItem.getAmount());
                        Log.d("UPDATE_EXPECTED", "date" + updatedItem.getDate());

                        int year = stringToCalendarDay(updatedItem.getDate()).getYear();
                        int month = stringToCalendarDay(updatedItem.getDate()).getMonth();
                        if (selectDay == null) {
                            fetchMonthlySummary(year, month, () -> {
                                updateExpectedItemInRecycler(updatedItem);
                            });
                        } else {
                            fetchMonthlySummary(year, month, () -> {
                                showDateAmount(selectDay);
                                updateExpectedItemInRecycler(updatedItem);
                            });
                        }
                    },
                    error -> Log.e("UpdateExpectedExpense", "수정 실패: " + error.toString())
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


    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    private void showAddExpectedItem() {
        PopAddExpectedItem popAddExpectedItem = new PopAddExpectedItem();
        popAddExpectedItem.setItemAddListener(this);
        popAddExpectedItem.setTargetFragment(this, 0);
        popAddExpectedItem.show(getParentFragmentManager(), "내용추가");
    }

    private CalendarDay stringToCalendarDay(String dateString) {
        String[] parts = dateString.split("-");
        return CalendarDay.from(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    private long sumTotalExpense() {
        long total = 0;
        boolean isCardChecked = binding.CheckBoxCard.isChecked();
        boolean isCashChecked = binding.CheckBoxCash.isChecked();

        for (Item item : adapter.getItems()) {
            if (item instanceof AmountItem) {
                AmountItem amountItem = (AmountItem) item;

                if ((isCardChecked && "CARD".equals(amountItem.getCard())) ||
                        (isCashChecked && "CASH".equals(amountItem.getCard()))) {
                    total += amountItem.getAmount();
                }
            }
        }

        totalExpenseTV.setText(decimalFormat.format(total));
        return total;
    }


    private void updateRecycler(Item item) {
        List<Item> currentItems = new ArrayList<>(adapter.getItems()); // 복사본을 만들어!
        currentItems.add(item);
        Collections.sort(currentItems, (a, b) -> b.getDate().compareTo(a.getDate()));
        adapter.updateItems(currentItems); // 완전 새 리스트로 전달

        if (item instanceof AmountItem) {
            sumTotalExpense();
        }
    }

    private void updateItemInRecycler(AmountItem updatedItem) {
        List<Item> allItems = adapter.getItems();
        List<Item> updatedList = new ArrayList<>();

        // 기존 리스트에서 같은 uid의 AmountItem을 찾아 교체
        for (Item item : allItems) {
            if (item instanceof AmountItem) {
                AmountItem current = (AmountItem) item;
                if (current.getUid() == updatedItem.getUid()) {
                    updatedList.add(updatedItem);
                } else {
                    updatedList.add(current);
                }
            } else if (!(item instanceof DateItem)) {
                updatedList.add(item); // 예상지출 등도 유지
            }
        }

        // 날짜 기준 정렬
        updatedList.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // 어댑터에 반영
        adapter.updateItems(updatedList);

        totalExpenseTV.setText(decimalFormat.format(monthlySummary.getCardExpenses() + monthlySummary.getCashExpenses()));
        ableAmount.setText(decimalFormat.format(monthlySummary.getRemaining()));
    }

    private void updateExpectedItemInRecycler(AmountExpectedItem updatedItem) {
        List<Item> originalItems = adapter.getItems();
        Log.i("size", "size " + originalItems.size());
        List<Item> flatList = new ArrayList<>();

        for (Item item : originalItems) {
            if (item instanceof AmountExpectedItem) {
                AmountExpectedItem current = (AmountExpectedItem) item;
                if (current.getId() == updatedItem.getId()) {
                    flatList.add(updatedItem);
                } else {
                    flatList.add(current);
                }
            } else if (item instanceof AmountItem) {
                flatList.add(item);
            }
        }

        flatList.sort(Comparator.comparing(Item::getDate));

        adapter.updateItems(flatList);

        totalExpenseTV.setText(decimalFormat.format(monthlySummary.getCardExpenses() + monthlySummary.getCashExpenses()));
        ableAmount.setText(decimalFormat.format(monthlySummary.getRemaining()));
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
            Log.d("TOKEN_CHECK", "[fetchMonthlySummary] token: " + token);
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

            //프론트에서 업데이트
            updateRecycler(item);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExpectedItemAdded(AmountExpectedItem item) {
        try {
            JSONObject body = new JSONObject();
            body.put("details", item.getContent());
            body.put("amount", item.getAmount());
            body.put("date", item.getDate());

            SharedPreferences prefs = requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = prefs.getString("token", "");
            Log.d("TOKEN_CHECK", "[fetchMonthlySummary] token: " + token);
            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2:8080/api/expectedExpenditure",
                    body,
                    response -> {
                        // 성공 시 최신 데이터 다시 로딩
                        CalendarDay today = calendarView.getCurrentDate();
                        fetchMonthlySummary(today.getYear(), today.getMonth(), () -> {});
                    },
                    error -> Log.e("AddExpectedExpense", "추가 실패: " + error.toString())
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
        if (selectDay != null && !isReturningFromEdit) { // 🔥 조건 추가
            fetchMonthlySummary(selectDay.getYear(), selectDay.getMonth(), () -> {});
        }
        isReturningFromEdit = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}