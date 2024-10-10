package com.example.capstone_donworry.fragment.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.databinding.FragmentCalendarBinding;

import java.util.ArrayList;

public class FragmentCalendar extends Fragment {

    RecyclerView recyclerView;
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
        AmountAdapter adapter = new AmountAdapter(getActivity().getApplicationContext());

        // 데이터 추가
        adapter.addItem(new AmountItem("가게1", "카드", "음식", "10000"));
        adapter.addItem(new AmountItem("가게2", "현금", "간식", "8000"));
        adapter.addItem(new AmountItem("가게3", "카드", "병원", "15000"));
        adapter.addItem(new AmountItem("가게4", "카드", "쇼핑", "100000"));
        adapter.addItem(new AmountItem("가게5", "현금", "음식", "14000"));
        adapter.addItem(new AmountItem("가게6", "현금", "교통비", "1200"));
        adapter.addItem(new AmountItem("가게7", "현금", "관리비", "90000"));
        adapter.addItem(new AmountItem("가게8", "카드", "생활", "150000"));

        recyclerView.setAdapter(adapter);
//        final TextView textView = binding.textCalendar;
//        viewModelCalendar.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}