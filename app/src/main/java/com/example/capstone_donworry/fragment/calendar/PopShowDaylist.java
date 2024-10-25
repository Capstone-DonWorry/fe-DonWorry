package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.PopShowDaylistBinding;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class PopShowDaylist  extends DialogFragment {

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private PopShowDaylistBinding binding;

    private CalendarDay selectedDate;
    private ArrayList<AmountItem> amountList;

    public PopShowDaylist(ArrayList<AmountItem> amountItems) {
        this.amountList = amountItems;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Dialog의 윈도우 설정
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // 디바이스의 화면 크기를 가져오기 위한 DisplayMetrics 객체 생성
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                // 화면 크기의 90%를 계산
                int width = (int) (displayMetrics.widthPixels * 0.9);

                // Dialog의 크기 설정
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = width;
                window.setAttributes(layoutParams);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 팝업창 레이아웃 사용
        View view = inflater.inflate(R.layout.pop_show_daylist, container, false);
        if (view == null) {
            Log.d("PopAddItem", "View inflation failed");
        } else {
            Log.d("PopAddItem", "View inflated successfully");
        }

        binding = PopShowDaylistBinding.bind(view);
//        Toast.makeText(getActivity(), amountList.toString(), Toast.LENGTH_SHORT).show();
        // recyclerView 설정
        recyclerView = binding.DayRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // adapter 설정
        adapter = new AmountAdapter(getActivity().getApplicationContext());

        recyclerView.setAdapter(adapter);
        // 예시 데이터 추가
        adapter.addItem(new AmountItem("가게1", "2024-10-01", "카드", "우리은행", "음식", "10000"));
        adapter.addItem(new AmountItem("가게2", "2024-10-01", "현금", "", "간식", "8000"));
        adapter.addItem(new AmountItem("가게3", "2024-10-01", "카드", "카카오뱅크", "병원", "15000"));
        adapter.addItem(new AmountItem("가게4", "2024-10-01", "카드", "신한은행", "쇼핑", "100000"));
        adapter.addItem(new AmountItem("가게5", "2024-10-01", "현금", "", "음식", "14000"));
        adapter.addItem(new AmountItem("가게6", "2024-10-01", "현금", "", "교통비", "1200"));
        adapter.addItem(new AmountItem("가게7", "2024-10-01", "현금", "", "관리비", "90000"));
        adapter.addItem(new AmountItem("가게8", "2024-10-01", "카드", "우리은행", "생활", "150000"));

        // 아이템 이벤트 처리
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

        // 버튼 클릭 처리
        view.findViewById(R.id.AddNOBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.AddADDBtn).setOnClickListener(v -> {
            PopAddItem popAddItem = new PopAddItem();
            // FragmentCalendar를 타겟으로 설정
            popAddItem.setTargetFragment(this, 0);
            popAddItem.show(getParentFragmentManager(), "내용추가");
        });

        return binding.getRoot();
    }

    // 아이템 추가
    public void onItemAdded(AmountItem item) {
//        if (selectedDate != null) {
//            addExpense(selectedDate, item);
//        }
//        adapter.addItem(item);
        adapter.notifyDataSetChanged(); // recycler뷰 업데이트
    }

    private void updateRecyclerView(CalendarDay date) {
//        adapter.items.clear();

//        if (amountMap.containsKey(date)) {
//            ArrayList<AmountItem> items = amountMap.get(date);
//            for (AmountItem item : items) {
//                adapter.addItem(item);
//            }
//        }
//
//        // 리사이클러 뷰 업데이트
//        private void updateRecycler(String date) {
//            ArrayList<AmountItem> item = amountMap.get(date);
//            adapter.addItems(item != null ? item : new ArrayList<>());
//        }
//
        // RecyclerView 업데이트
        adapter.notifyDataSetChanged();
    }

    // 지출 추가하는 메소드
    private void addExpense(CalendarDay date, AmountItem item) {
//        amountMap.putIfAbsent(date, new ArrayList<>());
//        amountMap.get(date).add(item);
//        updateRecyclerView(date);
    }
}
