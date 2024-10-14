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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstone_donworry.R;

public class PopAddItem extends DialogFragment {

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
        View view = inflater.inflate(R.layout.pop_add_item, container, false);
        if (view == null) {
            Log.d("PopDetailItem", "View inflation failed");
        } else {
            Log.d("PopDetailItem", "View inflated successfully");
        }

        // UI text 설정
        EditText contentEdit = view.findViewById(R.id.AddContent);
        EditText amountEdit = view.findViewById(R.id.AddAmount);
        EditText cardEdit = view.findViewById(R.id.AddCard);
        TextView dateTextView = view.findViewById(R.id.AddDate);

        // TODO:입력 받은 값 받아오기


        // dateSelect 클릭 시 달력 표시
        LinearLayout dateSelect = view.findViewById(R.id.AddDateSelect);
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopAddDate popAddDate = new PopAddDate();
                popAddDate.show(getChildFragmentManager(), "날짜 선택");
            }
        });

        // 버튼 클릭 처리
        view.findViewById(R.id.AddNOBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.AddNEXTBtn).setOnClickListener(v -> dismiss());

        return view;
    }
}