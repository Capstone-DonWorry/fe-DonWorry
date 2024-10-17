package com.example.capstone_donworry.fragment.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Button;

import com.example.capstone_donworry.R;

public class PopAddCategory extends DialogFragment {

    private Button foodBtn, shopBtn, hobbyBtn, hospitalBtn, lifeBtn, vehicleBtn, etcBtn, plusBtn;
    private Context context;

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
        View view = inflater.inflate(R.layout.pop_add_category, container, false);
        if (view == null) {
            Log.d("PopAddCategory", "View inflation failed");
        } else {
            Log.d("PopAddCategory", "View inflated successfully");

        }

        foodBtn = view.findViewById(R.id.CategoryFood);
        shopBtn = view.findViewById(R.id.CategoryShop);
        hobbyBtn = view.findViewById(R.id.CategoryHobby);
        hospitalBtn = view.findViewById(R.id.CategoryHospital);
        lifeBtn = view.findViewById(R.id.CategoryLife);
        vehicleBtn = view.findViewById(R.id.CategoryVehicle);
        etcBtn = view.findViewById(R.id.CategoryETC);
        plusBtn = view.findViewById(R.id.CategoryPlus);

        // 카테고리 버튼 클릭 리스너
        foodBtn.setOnClickListener(v -> {
            resetCategoryButton();
            foodBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            foodBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        shopBtn.setOnClickListener(v -> {
            resetCategoryButton();
            shopBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            shopBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        hobbyBtn.setOnClickListener(v -> {
            resetCategoryButton();
            hobbyBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            hobbyBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        hospitalBtn.setOnClickListener(v -> {
            resetCategoryButton();
            hospitalBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            hospitalBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        lifeBtn.setOnClickListener(v -> {
            resetCategoryButton();
            lifeBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            lifeBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        vehicleBtn.setOnClickListener(v -> {
            resetCategoryButton();
            vehicleBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            vehicleBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });
        etcBtn.setOnClickListener(v -> {
            resetCategoryButton();
            etcBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            etcBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
        });

        //TODO: 카테고리 입력&저장 기능
        plusBtn.setOnClickListener(v -> {
            resetCategoryButton();
        });

        // 버튼 클릭 처리
        //TODO:확인 버튼 클릭 시 데이터 저장
        view.findViewById(R.id.AddNOBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.AddOKBtn).setOnClickListener(v -> {

        });

        return view;
    }

    private void resetCategoryButton() {
        foodBtn.setBackgroundResource(R.drawable.round_shape_gray);
        foodBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        shopBtn.setBackgroundResource(R.drawable.round_shape_gray);
        shopBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        hobbyBtn.setBackgroundResource(R.drawable.round_shape_gray);
        hobbyBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        hospitalBtn.setBackgroundResource(R.drawable.round_shape_gray);
        hospitalBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        lifeBtn.setBackgroundResource(R.drawable.round_shape_gray);
        lifeBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        vehicleBtn.setBackgroundResource(R.drawable.round_shape_gray);
        vehicleBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
        etcBtn.setBackgroundResource(R.drawable.round_shape_gray);
        etcBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
    }
}