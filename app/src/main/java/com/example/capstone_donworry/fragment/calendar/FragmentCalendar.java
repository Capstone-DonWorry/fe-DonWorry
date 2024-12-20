package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Canvas;
import android.hardware.camera2.CameraExtensionSession;
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
import androidx.fragment.app.FragmentTransaction;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FragmentCalendar extends Fragment implements PopAddItem.ItemAddListener, PopShowDaylist.OnDialogCancelListener, PopDetailItem.OnDialogCancelListener {

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private ViewModelCalendar viewModelCalendar;
    private DBHelper db;
    private String userID, currentDay, updateDate;
    private CalendarDay selectDay;
    private int updatePosition;

    private MaterialCalendarView calendarView;
    private TextView targetAmount, totalExpenseTV, ableAmount;
    private CheckBox checkBoxCard, checkBoxCash;
    private FragmentCalendarBinding binding;
    private DayViewDecorator todayViewDecorator, sundayDecorator, saturdayDecorator;
    private DecimalFormat decimalFormat;
    private Map<String, CalendarTextDeco> dots;

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

        // db 설정
        db = new DBHelper(getContext());

        // 목표 금액 설정
        targetAmount = binding.TargetAmount;
        viewModelCalendar.getExpenseGoal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String goal) {
                targetAmount.setText(decimalFormat.format(Integer.parseInt(goal.replace(",", "")))); // TextView 업데이트
            }
        });
        // 잔액 설정
        ableAmount = binding.AbleAmount;

        // 총 합 금액 설정
        totalExpenseTV = binding.TotalExpense;

        // 현재 날짜 설정
        CalendarDay curDate = CalendarDay.today();
        String curMon = String.format("%02d", curDate.getMonth());
        String curDay = String.format("%02d", curDate.getDay());
        currentDay = curDate.getYear() +"-"+ curMon +"-"+ curDay;

        // CheckBox
        checkBoxCard = binding.CheckBoxCard;
        checkBoxCash = binding.CheckBoxCash;

        checkBoxCard.setChecked(true);
        checkBoxCash.setChecked(true);

        checkBoxCard.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());
        checkBoxCash.setOnCheckedChangeListener((buttonView, isChecked) -> sumTotalExpense());

        decimalFormat = new DecimalFormat("#,###");
        // 점이 찍힌 날짜
        dots = new HashMap<>();

        // 아이템 이벤트 처리
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new AmountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AmountAdapter.ViewHolder holder, View view, int position) {
                AmountItem item = adapter.getItem(position);
                updateDate = item.getDate();
                updatePosition = position;

                // 아이템 클릭시 팝업 창 띄우기
                PopDetailItem popDetail = PopDetailItem.newInstance(item);
                // FragmentCalendar를 타겟으로 설정
                popDetail.setTargetFragment(FragmentCalendar.this, 0);
                popDetail.show(getFragmentManager(), "세부내역");
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

                        // 삭제 후 점 업데이트
                        deleteItemDot(position);
                        sumTotalExpense();
                        ableExpense();

                        // 복구
                        Snackbar.make(recyclerView, deleteItem.getContent()+"삭제 했습니다.", Snackbar.LENGTH_LONG).setAction("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                db.addItem(userID, deleteItem);
                                updateRecycler(deleteItem);
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
        // 로그인 사용자
        viewModelCalendar.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String user) {
                userID = user;

                initView();
            }
        });
//        initViewModel();
    }


    // 항목추가 다이얼로그 표시
    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        popAddItem.setSettingDate(currentDay);// 현재 날짜 설정
        // FragmentCalendar를 타겟으로 설정
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    // 아이템 추가
    public void onItemAdded(AmountItem item) {
        String date = item.getDate();
        // db에 item 등록
        long uid = db.addItem(userID, item);
        if (uid != -1) {
            item.setUid(uid);
        }

        // recycler뷰 업데이트
        updateRecycler(item);

        String formattedAble = decimalFormat.format(Integer.parseInt(ableAmount.getText().toString().replace(",", "")) - item.getAmount());
        ableAmount.setText(formattedAble);

        if (!dots.containsKey(date)) {
            // 해당 날짜 점 표시
            CalendarTextDeco deco;

            int totalDayAmount = 0;
            List<AmountItem> itemsDay = db.getDateItems(userID, date);

            for (AmountItem dayItem : itemsDay) {
                totalDayAmount += dayItem.getAmount();
            }

            // 추천 금액 계산
            int recomAmount = calDailyRecommendedAmount(stringToCalendarDay(date));

            // 추천 금액 실제 금액 비교
            if (recomAmount >= totalDayAmount) {
                deco = new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_blue), date);
            } else {
                deco = new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_red), date);
            }
            calendarView.addDecorator(deco);
            dots.put(item.getDate(), deco);
        }
    }

    // 리사이클러 뷰 업데이트
    private void updateRecycler(AmountItem item) {
//
//        AmountItem newItem = db.getItem(userID, item);
        List<AmountItem> existItems = adapter.getItems();
        existItems.add(item);

        // 날짜 순서로 내림차순 정렬
        Collections.sort(existItems, (item1, item2) ->
                item2.getDate().compareTo(item1.getDate()));

        adapter.updateItems(existItems);

        // 총 금액 업데이트
        sumTotalExpense();

    }

    private void sumTotalExpense() {
        int total = 0;
        boolean isCardChecked = checkBoxCard.isChecked();
        boolean isCashChecked = checkBoxCash.isChecked();

        for (AmountItem item : adapter.getItems()) {
            int amountValue = item.getAmount();

            if ((isCardChecked && item.getCard().equals("카드")) ||
                    (isCashChecked && item.getCard().equals("현금")) ||
                    (isCardChecked && isCashChecked)) {
                total += amountValue;
            }
        }

        totalExpenseTV.setText(decimalFormat.format(total));
    }

    // 잔액 계산
    private void ableExpense() {
        int total = 0;

        for (AmountItem item : adapter.getItems()) {
            int amountValue = item.getAmount();

            total += amountValue;
        }

        int target = Integer.parseInt(targetAmount.getText().toString().replace(",", ""));
        ableAmount.setText(decimalFormat.format(target - total));
    }

    private void initView() {
        todayViewDecorator = CalendarDeco.todayViewDecorator(requireContext());
        sundayDecorator = CalendarDeco.sundayDecorator();
        saturdayDecorator = CalendarDeco.saturdayDecorator();

        calendarView.addDecorators(todayViewDecorator, sundayDecorator, saturdayDecorator);

        showMonthAmount(calendarView.getCurrentDate());
        ableExpense();
        sumTotalExpense();

        changeColor();

        calendarView.setOnMonthChangedListener(((widget, date) -> {
            showMonthAmount(date);
            sumTotalExpense(); // 월 별경 시 총 금액 변경
        }));

        // 날짜 변경 시 처리
        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            showDateAmount(date);
        }));
    }

    private void showDateAmount(CalendarDay date) {
        String strMon = String.format("%02d", date.getMonth());
        String strDay = String.format("%02d", date.getDay());
        String dateKey = date.getYear() +"-"+ strMon +"-"+ strDay;
        List<AmountItem> amountList = new ArrayList<>();
        amountList = db.getDateItems(userID, dateKey);
        selectDay = date;
        int recomAmount = calDailyRecommendedAmount(date);

        if (amountList != null){
            // 팝업 다이얼로그 일일 리스트
            PopShowDaylist popShowDaylist = PopShowDaylist.newInstance((ArrayList<AmountItem>) amountList, dateKey);
            popShowDaylist.setUserId(userID);
            popShowDaylist.setDb(db);
            popShowDaylist.setrecom(recomAmount);
            popShowDaylist.setTargetFragment(this, 0);
            popShowDaylist.show(getParentFragmentManager(), "특정날짜");
        }
        else {
            Log.d("showDateAmount", "null amountList");
        }
    }

    private void showMonthAmount(CalendarDay date) {
        adapter.clearItems();
        String strMon = String.format("%02d", date.getMonth());
        String dateKey = date.getYear() +"-"+ strMon;

        List<AmountItem> dateAmount = db.getMonthItems(userID, dateKey);

        // 날짜 순서로 내림차순 정렬
        Collections.sort(dateAmount, (item1, item2) ->
                item2.getDate().compareTo(item1.getDate()));

        adapter.updateItems(dateAmount);

        changeColor();
//        for (AmountItem item : dateAmount) {
//            CalendarTextDeco deco = new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_blue), item.getDate());
//            calendarView.addDecorator(deco);
//            dots.put(item.getDate(), deco);
//        }

    }

    // string을 CalendarDay로 변경
    private CalendarDay stringToCalendarDay(String dateString) {
        String[] parts = dateString.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        return CalendarDay.from(year, month, day);
    }

    // 추천 금액 계산
    private int calDailyRecommendedAmount(CalendarDay date) {
        int calDailyRecom = 0;
        if (date != null) {
            int year = date.getYear();
            int month = date.getMonth();

            // 마지막 날짜 계산
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month-1, 1);
            int monthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            int currentDay = date.getDay();
            int remainDay = monthLastDay - currentDay + 1;

            // 하루 추천 금액 계산
            calDailyRecom = Integer.parseInt(ableAmount.getText().toString().replace(",","")) / remainDay ;
            Log.d("dotColor", String.valueOf(calDailyRecom)+"-"+String.valueOf(date)+"-"+String.valueOf(remainDay)+"-"+ableAmount.getText().toString());
        }

        return calDailyRecom;
    }

    // 금액 초과시 점 색깔 변경
    private void changeColor() {
        CalendarTextDeco deco;

        for (AmountItem item : adapter.getItems()) {
            String dateString = item.getDate();
            // 사용 금액 총 합 계산
            int totalDayAmount = 0;
            List<AmountItem> itemsDay = db.getDateItems(userID, dateString);

            for (AmountItem dayItem : itemsDay) {
                totalDayAmount += dayItem.getAmount();
            }

            // 추천 금액 계산
            int recomAmount = calDailyRecommendedAmount(stringToCalendarDay(dateString));

            // 추천 금액 실제 금액 비교
            if (recomAmount >= totalDayAmount) {
                deco = new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_blue), dateString);
            } else {
                deco = new CalendarTextDeco(ContextCompat.getColor(getContext(), R.color.text_red), dateString);
            }
            calendarView.addDecorator(deco);
            dots.put(item.getDate(), deco);
        }

    }

    private void deleteItemDot(int position){
        AmountItem deleteItem = adapter.getItem(position);
        String deleteDate = deleteItem.getDate();

        // 삭제
        adapter.removeItem(position);
        adapter.notifyItemRemoved(position);

        // db 삭제
        db.deleteItem(userID, deleteItem);

        // 잔액 업데이트
        String decimalAble = decimalFormat.format(Integer.parseInt(ableAmount.getText().toString().replace(",", "")) + deleteItem.getAmount());
        ableAmount.setText(decimalAble);

        // 항목이 없으면 점 삭제
        updateDot(deleteDate, position);
    }
    private void updateDot(String date, int po) {
        List<AmountItem> items = db.getDateItems(userID, date);
        if (items == null || items.isEmpty()) {
            // 점이 있을 경우 제거
            if(dots.containsKey(date)) {
                CalendarTextDeco deco = dots.get(date);
                calendarView.removeDecorator(deco);
                dots.remove(date);
            }
            adapter.removeDateItem(po -1);
        }
    }

    @Override
    public void onDialogCancel() {
        showMonthAmount(selectDay);
        sumTotalExpense();
        ableExpense();
    }
    @Override
    public void onDialogUpdate(String date) {
        showMonthAmount(stringToCalendarDay(date));
        sumTotalExpense();
        ableExpense();
        updateDot(updateDate, updatePosition);
    }
}