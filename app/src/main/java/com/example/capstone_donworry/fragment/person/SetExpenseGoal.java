package com.example.capstone_donworry.fragment.person;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone_donworry.CustomComma;
import com.example.capstone_donworry.GoalSettingRequest;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.fragment.calendar.PopAddCategory;
import com.example.capstone_donworry.fragment.calendar.PopAddItem;
import com.example.capstone_donworry.fragment.calendar.ViewModelCalendar;

import java.text.DecimalFormat;

public class SetExpenseGoal extends DialogFragment {

    private TextView currentGoal;
    private EditText newGoal;
    private Button cancelBtn, addOkBtn;

    private ViewModelCalendar shareViewModel;

//    private int currentAmount;
    private DecimalFormat decimalFormat;
    private String userID;
//    private String newTextAmount;
//    private int newIntAmount;
//    private AmountUpdateListener listener;
//    private boolean isBtnClicked = false;

    // 금액 업데이트
//    public interface AmountUpdateListener {
//        void onAmountUpdated(int updateAmount);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            currentAmount = getArguments().getInt("currentAmount", 0);
        }
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
        View view = inflater.inflate(R.layout.person_set_goal, container, false);

        shareViewModel = new ViewModelProvider(requireActivity()).get(ViewModelCalendar.class);

        decimalFormat = new DecimalFormat("#,###");

        // 사용자 정보
        shareViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String user) {
                userID = user;
            }
        });

        // 현재 목표 금액 설정
        currentGoal = view.findViewById(R.id.CurrentExpenseGoal);
        shareViewModel.getExpenseGoal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String goal) {
                currentGoal.setText(decimalFormat.format(Integer.parseInt(goal)));
            }
        });



        // EditText 설정
        newGoal = view.findViewById(R.id.NewExpenseGoal);
        newGoal.addTextChangedListener(new CustomComma(newGoal));


        // 버튼 설정
        cancelBtn = view.findViewById(R.id.NOBtn);
        addOkBtn = view.findViewById(R.id.AddBtn);

        // 버튼 클릭 처리
        cancelBtn.setOnClickListener(v -> dismiss());
        addOkBtn.setOnClickListener(v -> {
            String strGoal = newGoal.getText().toString().replace(",", "");
            // 빈 항목이 있는지 체크
            if (strGoal.isEmpty()) {
                // 빈 칸이 있을 경우 경고 메시지 띄우기
                Toast.makeText(getActivity(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }else {
                shareViewModel.setExpenseGoal(strGoal);
                new GoalSettingRequest().execute(userID, strGoal);
            }
            dismiss();
        });

        return view;
    }
}

