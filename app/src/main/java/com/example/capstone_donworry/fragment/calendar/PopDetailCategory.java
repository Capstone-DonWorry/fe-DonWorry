package com.example.capstone_donworry.fragment.calendar;

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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.capstone_donworry.R;

public class PopDetailCategory extends DialogFragment {

    private Button foodBtn, shopBtn, hobbyBtn, hospitalBtn, lifeBtn, vehicleBtn, etcBtn, plusBtn;
    private String selectedCategory;
    private PopAddItem.ItemAddListener itemAddListener;

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

    // 리스너 인터페이스 정의
    public interface OnUpdateListener {
        void onUpdate(AmountItem updatedItem);
    }

    private OnUpdateListener updateListener;

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.updateListener = listener;
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
            foodBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("식비");
        });
        shopBtn.setOnClickListener(v -> {
            resetCategoryButton();
            shopBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            shopBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("쇼핑");
        });
        hobbyBtn.setOnClickListener(v -> {
            resetCategoryButton();
            hobbyBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            hobbyBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("여가");
        });
        hospitalBtn.setOnClickListener(v -> {
            resetCategoryButton();
            hospitalBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            hospitalBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("병원");
        });
        lifeBtn.setOnClickListener(v -> {
            resetCategoryButton();
            lifeBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            lifeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("생활비");
        });
        vehicleBtn.setOnClickListener(v -> {
            resetCategoryButton();
            vehicleBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            vehicleBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("교통비");
        });
        etcBtn.setOnClickListener(v -> {
            resetCategoryButton();
            etcBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
            etcBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            selectCategory("기타");
        });

        plusBtn.setOnClickListener(v -> {
            resetCategoryButton();
        });

        // ItemAddListener 설정
        if (getTargetFragment() instanceof PopAddItem.ItemAddListener) {
            itemAddListener = (PopAddItem.ItemAddListener) getTargetFragment();
        }
        // 버튼 클릭 처리
        view.findViewById(R.id.AddBackBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.AddOKBtn).setOnClickListener(v -> {
            // 카테고리가 선택 확인
            if (selectedCategory == null || selectedCategory.isEmpty()) {
                // 카테고리가 선택되지 않았을 경우 경고 메시지 표시
                Toast.makeText(getActivity(), "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 카테고리 처리
                if (getTargetFragment() instanceof PopDetailItem) {
                    PopDetailItem targetFragment = (PopDetailItem) getTargetFragment();
                    targetFragment.updateCategory(selectedCategory);
                }
                dismiss();
            }
        });

        return view;
    }

    private void resetCategoryButton() {
        foodBtn.setBackgroundResource(R.drawable.round_shape_gray);
        foodBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        shopBtn.setBackgroundResource(R.drawable.round_shape_gray);
        shopBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        hobbyBtn.setBackgroundResource(R.drawable.round_shape_gray);
        hobbyBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        hospitalBtn.setBackgroundResource(R.drawable.round_shape_gray);
        hospitalBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        lifeBtn.setBackgroundResource(R.drawable.round_shape_gray);
        lifeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        vehicleBtn.setBackgroundResource(R.drawable.round_shape_gray);
        vehicleBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        etcBtn.setBackgroundResource(R.drawable.round_shape_gray);
        etcBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
    }

    private void selectCategory (String category) {
        this.selectedCategory = category;
    }
}