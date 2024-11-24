package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.capstone_donworry.CustomComma;
import com.example.capstone_donworry.R;

import java.text.DecimalFormat;

public class PopDetailAmount extends DialogFragment {

    private TextView amountTextView;
    private EditText amountEditText;
    private Button twoBtn, threeBtn, fourBtn, numBtn, cancelBtn, addOkBtn;

    private int currentAmount;
    private DecimalFormat decimalFormat;
    private String newTextAmount;
    private int newIntAmount;
    private AmountUpdateListener listener;
    private boolean isBtnClicked = false;

    // 금액 업데이트
    public interface AmountUpdateListener {
        void onAmountUpdated(int updateAmount);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentAmount = getArguments().getInt("currentAmount", 0);
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
        View view = inflater.inflate(R.layout.pop_detail_amount, container, false);

        decimalFormat = new DecimalFormat("#,###");

        // TextView 설정
        amountTextView = view.findViewById(R.id.DetailAmount);
        amountTextView.setText(decimalFormat.format(currentAmount));

        // EditText 설정
        amountEditText = view.findViewById(R.id.DetailAmountEdit);

        // 버튼 설정
        twoBtn = view.findViewById(R.id.TwoPerson);
        threeBtn = view.findViewById(R.id.ThreePerson);
        fourBtn = view.findViewById(R.id.FourPerson);
        numBtn = view.findViewById(R.id.NumPerson);
        cancelBtn = view.findViewById(R.id.CancelBtn);
        addOkBtn = view.findViewById(R.id.AddOKBtn);

        // 금액 직접 수정
        // 지출 데이터 수정
        amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountEditText.addTextChangedListener(new CustomComma(amountEditText));
                amountEditText.setVisibility(View.VISIBLE);
                amountEditText.setText(amountTextView.getText().toString().replace(",", ""));
                amountTextView.setVisibility(View.GONE);

                // edit 수정 후 db 수정
                amountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (!hasFocus) {
                            newTextAmount = amountEditText.getText().toString().trim().replace(",", "");
                            newIntAmount = Integer.parseInt(newTextAmount);
                            currentAmount = newIntAmount;

                            amountTextView.setText(decimalFormat.format(newIntAmount)); // 수정된 텍스트
                            amountEditText.setVisibility(View.GONE);
                            amountTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        // 화면 터치 시 자동 저장
        view.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (amountEditText.getVisibility() == View.VISIBLE && !isEditTextTouched(amountEditText, motionEvent)) {
                    newTextAmount = amountEditText.getText().toString().trim().replace(",", "");
                    newIntAmount = Integer.parseInt(newTextAmount);
                    currentAmount = newIntAmount;
                    amountTextView.setText(decimalFormat.format(newIntAmount));

                    amountEditText.setVisibility(View.GONE);
                    amountTextView.setVisibility(View.VISIBLE);
                    return true; // 이벤트 처리 완료
                }
                return false; // 다른 부분에서 터치 처리
            }
        });

        // 나누기 금액 버튼 클릭 리스너
        twoBtn.setOnClickListener(v -> {
            resetPersonButton();
            if (!isBtnClicked) {
                divideAmount(twoBtn, 2);
            } else {
                resetBtn(twoBtn);
            }
        });
        threeBtn.setOnClickListener(v -> {
            resetPersonButton();
            if (!isBtnClicked) {
                divideAmount(threeBtn, 3);
            } else {
                resetBtn(threeBtn);
            }
        });
        fourBtn.setOnClickListener(v -> {
            resetPersonButton();
            if (!isBtnClicked) {
                divideAmount(fourBtn, 4);
            } else {
                resetBtn(fourBtn);
            }
        });
        numBtn.setOnClickListener(v -> {
            resetPersonButton();
            if (!isBtnClicked) {
                inputForBtn(numBtn);
            } else {
                resetBtn(numBtn);
            }
        });

        // 버튼 클릭 처리
        cancelBtn.setOnClickListener(v -> dismiss());
        addOkBtn.setOnClickListener(v -> {
            if (listener != null) {
                Toast.makeText(getContext(), String.valueOf(newIntAmount), Toast.LENGTH_SHORT).show();
                listener.onAmountUpdated(newIntAmount);
            }
            dismiss();
        });

        return view;
    }

    // 리스너 설정
    public void setAmountUpdateListener(AmountUpdateListener listener) {
        this.listener = listener;
    }

    // EditText 터치 확인
    private boolean isEditTextTouched(EditText editText, MotionEvent event) {
        int[] location = new int[2];
        editText.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];
        int right = left + editText.getWidth();
        int bottom = top + editText.getHeight();

        return event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom;
    }

    private void resetPersonButton() {
        twoBtn.setBackgroundResource(R.drawable.round_shape_gray);
        twoBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        threeBtn.setBackgroundResource(R.drawable.round_shape_gray);
        threeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        fourBtn.setBackgroundResource(R.drawable.round_shape_gray);
        fourBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        numBtn.setBackgroundResource(R.drawable.round_shape_gray);
        numBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
    }

    // 금액 나누기
    private void divideAmount(Button btn, int num) {
        isBtnClicked = true;
        int divisionAmount = Integer.parseInt(amountTextView.getText().toString().replace(",","")) / num;
        amountTextView.setText(decimalFormat.format(divisionAmount));
        newIntAmount = divisionAmount;
        btn.setBackgroundResource(R.drawable.round_shape_mid_blue);
        btn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    // 버튼 상태 돌리기
    private void resetBtn(Button btn) {
        isBtnClicked = false;
        amountTextView.setText(decimalFormat.format(currentAmount));
        btn.setBackgroundResource(R.drawable.round_shape_gray);
        btn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
    }

    // 인원수 추가 받기
    private void inputForBtn(Button btn) {
        isBtnClicked = true;
        // 인원수 입력
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("인원수 입력");

        // EditText 생성
        final EditText inputNum = new EditText(getContext());
        inputNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputNum.setHint("나눌 인원수를 입력하세요");

        builder.setView(inputNum);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String inputNumText = inputNum.getText().toString().trim();
                if (!inputNumText.isEmpty()) {
                    btn.setText(inputNumText);

                    divideAmount(btn, Integer.parseInt(inputNumText));
                } else {
                    Toast.makeText(getContext(), "숫자를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isBtnClicked = false;
                resetBtn(btn);
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
}

