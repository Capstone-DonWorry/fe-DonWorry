package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
import android.graphics.Canvas;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.DBHelper;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.FragmentCalendarBinding;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class FragmentCalendar extends Fragment implements PopAddItem.ItemAddListener{

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private ViewModelCalendar viewModelCalendar;
    private DBHelper db;

    private MaterialCalendarView calendarView;
    private TextView targetAmount;
    private CheckBox checkBoxCard;
    private CheckBox checkBoxCash;
    private FragmentCalendarBinding binding;
//    private HashMap<String, ArrayList<AmountItem>> amountMap;

    private DayViewDecorator todayViewDecorator;
    private DayViewDecorator sundayDecorator;
    private DayViewDecorator saturdayDecorator;

    private int currentYear;
    private int currentMonth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity에서 ViewModel 가져오기 - 같은 인스턴스 공유
        viewModelCalendar = new ViewModelProvider(requireActivity()).get(ViewModelCalendar.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        // db 설정
        db = new DBHelper(getContext());

        // 목표 금액 설정
        targetAmount = binding.TargetAmount;
        viewModelCalendar.getExpenseGoal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String goal) {
                Log.d("expenseGoal", "El: " + goal);
                targetAmount.setText(goal); // TextView 업데이트
            }
        });

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

                        db.deleteItem(deleteItem);

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
        // db에 item 등록
        long uid = db.addItem(item);
        if (uid != -1) {
            item.setUid(uid);
        }

        // recycler뷰 업데이트
        updateRecycler(date);

        // 해당 날짜 점 표시
        calendarView.addDecorator(new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_blue), date));
    }

    // 리사이클러 뷰 업데이트
    private void updateRecycler(String date) {

        List<AmountItem> item = new ArrayList<>();
        item = db.getDateItems(date);
        if (item != null){
            List<AmountItem> existItems = adapter.getItems();

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
            int amountValue = Integer.parseInt(item.getAmount());

            if ((isCardChecked && item.getCard().equals("카드")) ||
                    (isCashChecked && item.getCard().equals("현금")) ||
                    (isCardChecked && isCashChecked)) {
                total += amountValue;
            }
        }
        TextView totalExpenseTV = binding.TotalExpense;
        totalExpenseTV.setText(String.valueOf(total));
    }

    private void initView() {
        todayViewDecorator = CalendarDeco.todayViewDecorator(requireContext());
        sundayDecorator = CalendarDeco.sundayDecorator();
        saturdayDecorator = CalendarDeco.saturdayDecorator();

        calendarView.addDecorators(todayViewDecorator, sundayDecorator, saturdayDecorator);

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
        List<AmountItem> amountList = new ArrayList<>();
        amountList = db.getDateItems(dateKey);

        if (amountList != null){
            Log.d("showDateAmount", "Amount List for"+dateKey+":"+amountList);
            PopShowDaylist popShowDaylist = PopShowDaylist.newInstance((ArrayList<AmountItem>) amountList, dateKey);
            popShowDaylist.show(getChildFragmentManager(), "특정날짜");
        }
        else {
            Log.d("showDateAmount", "null amountList");
        }
    }

    private void showMonthAmount(CalendarDay date) {
        String month = String.valueOf(date.getMonth());
        String year = String.valueOf(date.getYear());

        List<AmountItem> dateAmount = new ArrayList<>();
        dateAmount = db.getMonthItems(year, month);

        // 날짜 순서로 정렬
        Collections.sort(dateAmount, (item1, item2) ->
            item1.getDate().compareTo(item2.getDate()));

        adapter.clearItems();
        adapter.addItems((ArrayList<AmountItem>) dateAmount);
    }

}