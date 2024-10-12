package com.example.capstone_donworry.fragment.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.capstone_donworry.R;

public class PopDetailItem extends DialogFragment {
    private static final String DETAIL_ITEM = "detailItem";

    public static PopDetailItem newInstance(AmountItem item){
        PopDetailItem fragment = new PopDetailItem();
        Bundle args = new Bundle();
        args.putSerializable(DETAIL_ITEM, item); // 아이템 데이터 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 팝업창 레이아웃 사용
        View view = inflater.inflate(R.layout.pop_detail_item, container, false);

        AmountItem item = (AmountItem) getArguments().getSerializable(DETAIL_ITEM);

        // UI text 설정
        TextView nameTextView = view.findViewById(R.id.DetailName);
        TextView dateTextView = view.findViewById(R.id.DetailDate);
        TextView cardTextView = view.findViewById(R.id.DetailCard);
        TextView bankTextView = view.findViewById(R.id.DetailBank);
        TextView categoryTextView = view.findViewById(R.id.DetailCategory);
        TextView amountTextView = view.findViewById(R.id.DetailAmount);

        // text 설정
        nameTextView.setText(item.getName());
        dateTextView.setText("아이템날짜");
        cardTextView.setText(item.getCard());
        bankTextView.setText("아이템은행");
        categoryTextView.setText(item.getCategory());
        amountTextView.setText(item.getAmount());

        // 버튼 클릭 처리
        view.findViewById(R.id.DetailOKBtn).setOnClickListener(v -> dismiss());

        return view;
    }
}