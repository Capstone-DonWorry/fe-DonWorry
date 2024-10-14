package com.example.capstone_donworry.fragment.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.databinding.FragmentCalendarBinding;


public class FragmentCalendar extends Fragment {

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewModelCalendar viewModelCalendar =
                new ViewModelProvider(this).get(ViewModelCalendar.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // recyclerView 설정
        recyclerView = binding.RecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // adapter 설정
        adapter = new AmountAdapter(getActivity().getApplicationContext());

        // 데이터 추가
        adapter.addItem(new AmountItem("가게1", "2024-10-01", "카드", "우리은행", "음식", "10000"));
        adapter.addItem(new AmountItem("가게2", "2024-10-02", "현금", "국민은행", "간식", "8000"));
        adapter.addItem(new AmountItem("가게3", "2024-10-03", "카드", "카카오뱅크", "병원", "15000"));
        adapter.addItem(new AmountItem("가게4", "2024-10-04", "카드", "신한은행", "쇼핑", "100000"));
        adapter.addItem(new AmountItem("가게5", "2024-10-05", "현금", "농협은행", "음식", "14000"));
        adapter.addItem(new AmountItem("가게6", "2024-10-06", "현금", "하나은행", "교통비", "1200"));
        adapter.addItem(new AmountItem("가게7", "2024-10-07", "현금", "기업은행", "관리비", "90000"));
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
        popAddItem.show(getChildFragmentManager(), "내용추가");
    }
}