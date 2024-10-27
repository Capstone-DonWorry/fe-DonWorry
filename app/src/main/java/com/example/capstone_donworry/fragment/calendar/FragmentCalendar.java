package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentCalendarBinding;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class FragmentCalendar extends Fragment implements PopAddItem.ItemAddListener{

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private MaterialCalendarView calendarView;
    private CheckBox checkBoxCard;
    private CheckBox checkBoxCash;
    private FragmentCalendarBinding binding;
    private HashMap<String, ArrayList<AmountItem>> amountMap;

    private DayViewDecorator todayViewDecorator;
    private DayViewDecorator sundayDecorator;
    private DayViewDecorator saturdayDecorator;

    private int currentYear;
    private int currentMonth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelCalendar viewModelCalendar = new ViewModelProvider(this).get(ViewModelCalendar.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // recyclerView 설정
        recyclerView = binding.RecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // adapter 설정
        adapter = new AmountAdapter(getActivity().getApplicationContext());

        calendarView = binding.calendarView;

        CalendarDay calendarDay = calendarView.getCurrentDate();
        currentYear = calendarDay.getYear();
        currentMonth = calendarDay.getMonth();

        // CheckBox
        checkBoxCard = binding.CheckBoxCard;
        checkBoxCash = binding.CheckBoxCash;

        checkBoxCard.setChecked(true);
        checkBoxCash.setChecked(true);

        checkBoxCard.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());
        checkBoxCash.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());

        // 아이템 이벤트 처리
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new AmountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AmountAdapter.ViewHolder holder, View view, int position) {
                AmountItem item = adapter.getItem(position);

                if (item != null) {
                    Log.d("FragmentCalendar", "Clicked item: " + item.getContent());
                } else {
                    Log.d("FragmentCalendar", "Item is null");
                }
                // 아이템 클릭시 팝업 창 띄우기
                PopDetailItem popDetail = PopDetailItem.newInstance(item);
                popDetail.show(getChildFragmentManager(), "세부내역");
            }
        });

        // 아이템 삭제
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        // 삭제할 아이템 담아두기
                        AmountItem deleteItem = adapter.getItem(position);

                        // 삭제
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);

                        // 복구
                        Snackbar.make(recyclerView, deleteItem.getContent()+"삭제 했습니다.", Snackbar.LENGTH_LONG).setAction("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                adapter.addItemPo(position, deleteItem);
                                adapter.notifyItemInserted(position);
                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.err_red))
                                .addSwipeLeftActionIcon(R.drawable.baseline_delete_forever_24)
                                        .addSwipeLeftLabel("삭제")
                                                .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                                                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        // 추가 버튼 이벤트 처리
        Button addBtn = binding.ButtonAdd;
        addBtn.setOnClickListener(view -> showAddItem());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 캘린더 뷰
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
//        initViewModel();
    }


    // 항목추가 다이얼로그 표시
    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        // FragmentCalendar를 타겟으로 설정
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    // 아이템 추가
    public void onItemAdded(String date, AmountItem item) {

        String[] datePart = date.split("-");
        int itemYear = Integer.parseInt(datePart[0]);
        int itemMonth = Integer.parseInt(datePart[1]);

        amountMap.putIfAbsent(date, new ArrayList<>());
        amountMap.get(date).add(item);

        if (itemYear == currentYear && itemMonth == currentMonth) {
            // recycler뷰 업데이트
            updateRecycler(date);
        }

    }

    // 리사이클러 뷰 업데이트
    private void updateRecycler(String date) {
        ArrayList<AmountItem> item = amountMap.get(date);

        if (item != null){
            ArrayList<AmountItem> existItems = adapter.getItems();

            for (AmountItem newItem : item) {
                int index = existItems.indexOf(newItem);
                if (index != -1) {
                    existItems.set(index, newItem);
                } else {
                    existItems.add(newItem);
                }
            }
            // 날짜 순서로 정렬
            Collections.sort(existItems, (item1, item2) ->
                    item1.getDate().compareTo(item2.getDate()));

            adapter.updateItems(existItems);
        }
        // 총 금액 업데이트
        sumTotalExpense();

    }

    private void sumTotalExpense() {
        int total = 0;
        boolean isCardChecked = checkBoxCard.isChecked();
        boolean isCashChecked = checkBoxCash.isChecked();

        for (AmountItem item : adapter.getItems()) {
            int amountValue = Integer.parseInt(item.getAmount().replace("원", "").trim());

            if ((isCardChecked && item.getCard().equals("카드")) ||
                    (isCashChecked && item.getCard().equals("현금")) ||
                    (isCardChecked && isCashChecked)) {
                total += amountValue;
            }
        }
        TextView totalExpenseTV = binding.TotalExpense;
        totalExpenseTV.setText(total + "원");
    }

    private void initView() {
        todayViewDecorator = CalendarDeco.todayViewDecorator(requireContext());
        sundayDecorator = CalendarDeco.sundayDecorator();
        saturdayDecorator = CalendarDeco.saturdayDecorator();

        amountMap = new HashMap<>();

        calendarView.addDecorators(todayViewDecorator, sundayDecorator, saturdayDecorator);
        Collection<String> dates = new ArrayList<>();

        // 예를 들어 2024년 10월 27일을 추가합니다.
        dates.add("2024-10-1");
        dates.add("2024-10-13");

        calendarView.addDecorator(new CalendarTextDeco(Color.RED, dates));

        calendarView.setOnMonthChangedListener(((widget, date) -> {
            currentYear = date.getYear();
            currentMonth = date.getMonth();
            showMonthAmount(date);
            sumTotalExpense(); // 월 별경 시 총 금액 변경
        }));

        // 날짜 변경 시 처리
        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            showDateAmount(date);
        }));
    }

    private void showDateAmount(CalendarDay date) {
        String dateKey = date.getYear() +"-"+ date.getMonth() +"-"+ date.getDay();
        ArrayList<AmountItem> amountList = amountMap.get(dateKey);

        if (amountList != null){
            Log.d("showDateAmount", "Amount List for"+dateKey+":"+amountList);
            PopShowDaylist popShowDaylist = PopShowDaylist.newInstance(amountList, dateKey);
            popShowDaylist.show(getChildFragmentManager(), "특정날짜");
        }
        else {
            Log.d("showDateAmount", "null amountList");
        }
        for (String key : amountMap.keySet()) {
            Log.d("AmountMap", "Key: " + key + ", Value: " + amountMap.get(key));
            Log.d("AmountMap", "dateKey: " + dateKey + ", Value: " + amountList);
        }
    }

    private void showMonthAmount(CalendarDay date) {
        int month = date.getMonth();
        int year = date.getYear();

        ArrayList<AmountItem> dateAmount = new ArrayList<>();
        for (String key : amountMap.keySet()) {
            String[] datePart = key.split("-");
            int itemYear = Integer.parseInt(datePart[0]);
            int itemMonth = Integer.parseInt(datePart[1]);

            if (itemYear == year && itemMonth == month) {
                dateAmount.addAll(amountMap.get(key));
            }
        }

        // 날짜 순서로 정렬
        Collections.sort(dateAmount, (item1, item2) ->
            item1.getDate().compareTo(item2.getDate()));

        adapter.clearItems();
        adapter.addItems(dateAmount);
    }

}