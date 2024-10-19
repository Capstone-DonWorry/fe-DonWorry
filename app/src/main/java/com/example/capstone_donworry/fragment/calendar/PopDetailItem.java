package com.example.capstone_donworry.fragment.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
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

    @Override
    public void onStart() {
        super.onStart();
//        Log.d("PopDetailItem", "DialogFragment started");
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
//                int height = (int) (displayMetrics.heightPixels * 0.9);

                // Dialog의 크기 설정
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = width;
//                layoutParams.height = height;
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
        View view = inflater.inflate(R.layout.pop_detail_item, container, false);
        if (view == null) {
            Log.d("PopDetailItem", "View inflation failed");
        } else {
            Log.d("PopDetailItem", "View inflated successfully");
        }

        AmountItem item = (AmountItem) getArguments().getSerializable(DETAIL_ITEM);


        // 로그 출력
//        Log.d("PopDetailItem", "onCreateView called"+item.getBank());

        // UI text 설정
        TextView nameTextView = view.findViewById(R.id.DetailName);
        TextView dateTextView = view.findViewById(R.id.DetailDate);
        TextView cardTextView = view.findViewById(R.id.DetailCard);
        TextView bankTextView = view.findViewById(R.id.DetailBank);
        TextView categoryTextView = view.findViewById(R.id.DetailCategory);
        TextView amountTextView = view.findViewById(R.id.DetailAmount);

        // text 설정
        if (item != null) {
            nameTextView.setText(item.getContent());
            dateTextView.setText(item.getDate());
            cardTextView.setText(item.getCard());
            bankTextView.setText(item.getBank());
            categoryTextView.setText(item.getCategory());
            amountTextView.setText(item.getAmount());
            // 로그 확인
            Log.d("PopDetailItem", "Setting name: " + item.getContent());
            Log.d("PopDetailItem", "Getting name: " + nameTextView.getText());
        }

        // 버튼 클릭 처리
        view.findViewById(R.id.DetailOKBtn).setOnClickListener(v -> dismiss());

        return view;
    }
    // TODO:입력한 데이터로 내용 추가
    // TODO:입력한 데이터로 내용 변경

}