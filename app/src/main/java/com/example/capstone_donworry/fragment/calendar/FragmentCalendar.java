package com.example.capstone_donworry.fragment.calendar;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class FragmentCalendar extends Fragment implements PopAddItem.ItemAddListener{

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private FragmentCalendarBinding binding;
    private HashMap<String, ArrayList<AmountItem>> amountMap;

    private DayViewDecorator todayViewDecorator;
    private DayViewDecorator sundayDecorator;
    private DayViewDecorator saturdayDecorator;


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

        // 예시 데이터 추가
        adapter.addItem(new AmountItem("가게1", "2024-10-01", "카드", "우리은행", "음식", "10000"));
        adapter.addItem(new AmountItem("가게2", "2024-10-02", "현금", "", "간식", "8000"));
        adapter.addItem(new AmountItem("가게3", "2024-10-03", "카드", "카카오뱅크", "병원", "15000"));
        adapter.addItem(new AmountItem("가게4", "2024-10-04", "카드", "신한은행", "쇼핑", "100000"));
        adapter.addItem(new AmountItem("가게5", "2024-10-05", "현금", "", "음식", "14000"));
        adapter.addItem(new AmountItem("가게6", "2024-10-06", "현금", "", "교통비", "1200"));
        adapter.addItem(new AmountItem("가게7", "2024-10-07", "현금", "", "관리비", "90000"));
        adapter.addItem(new AmountItem("가게8", "2024-10-08", "카드", "우리은행", "생활", "150000"));

        // 아이템 이벤트 처리
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new AmountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AmountAdapter.ViewHolder holder, View view, int position) {
                AmountItem item = adapter.getItem(position);

                // 아이템 클릭시 팝업 창 띄우기
                PopDetailItem popDetail = PopDetailItem.newInstance(item);
                popDetail.show(getChildFragmentManager(), "세부내역");


                // 토스트 메시지 확인
//                Toast.makeText(getActivity(), item.getAmount(), Toast.LENGTH_SHORT).show();
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

    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        // FragmentCalendar를 타겟으로 설정
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }




    // 아이템 추가
    public void onItemAdded(String date, AmountItem item) {
        amountMap.putIfAbsent(date, new ArrayList<>());
        amountMap.get(date).add(item);
        // recycler뷰 업데이트
        updateRecycler(date);
    }

    private void updateRecycler(String date) {
        ArrayList<AmountItem> item = amountMap.get(date);
        adapter.addItems(item != null ? item : new ArrayList<>());
    }

    // 캘린더 뷰
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
//        initViewModel();
    }

    private void initView() {
//        binding.RecyclerView.setAdapter(amountListAdapter);
        MaterialCalendarView calendarView = binding.calendarView;

//        dayViewDecorator = CalendarDeco.dayViewDecorator(requireContext());
        todayViewDecorator = CalendarDeco.todayViewDecorator(requireContext());
        sundayDecorator = CalendarDeco.sundayDecorator();
        saturdayDecorator = CalendarDeco.saturdayDecorator();
//        selectedMonthDecorator = CalendarDeco.selectedMonthDecorator(requireContext(), CalendarDay.today().getMonth());

        amountMap = new HashMap<>();

        calendarView.addDecorators(todayViewDecorator, sundayDecorator, saturdayDecorator);

        calendarView.setOnMonthChangedListener(((widget, date) -> {
            widget.clearSelection();
            widget.removeDecorators();
            widget.invalidateDecorators();
//            selectedMonthDecorator = CalendarDeco.selectedMonthDecorator(requireContext(), date.getMonth());
            widget.addDecorators(todayViewDecorator, sundayDecorator, saturdayDecorator);

            CalendarDay clickedDay = CalendarDay.from(date.getYear(), date.getMonth(), date.getDay());
            widget.setDateSelected(clickedDay, true);
//            viewModel.filterScheduleListByDate(date.toLocalDate());
//            viewModel.filterDataByMonth(date.toLocalDate());
        }));

        // 날짜 변경 시 처리
        calendarView.setOnDateChangedListener(((widget, date, selected) -> {
            showDateAmount(date);
        }));


    }

    private void showDateAmount(CalendarDay date) {
        ArrayList<AmountItem> dateAmount = amountMap.get(date);
            PopShowDaylist popShowDaylist = new PopShowDaylist(dateAmount);
            popShowDaylist.show(getChildFragmentManager(), "특정날짜");
    }

//    private void initViewModel() {
//
//    }
}