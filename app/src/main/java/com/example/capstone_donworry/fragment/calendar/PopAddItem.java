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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone_donworry.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class PopAddItem extends DialogFragment {

    private EditText contentEdit, amountEdit, bankEdit;
    private CheckBox cardCheck, cashCheck;
    private TextView dateTextView;
    private View viewLine;
    private LinearLayout viewLayout;
    private ItemAddListener itemAddListener;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getTargetFragment() instanceof ItemAddListener) {
            itemAddListener = (ItemAddListener) getTargetFragment();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 팝업창 레이아웃 사용
        View view = inflater.inflate(R.layout.pop_add_item, container, false);
        if (view == null) {
            Log.d("PopAddItem", "View inflation failed");
        } else {
            Log.d("PopAddItem", "View inflated successfully");
        }

        // UI text 설정
        contentEdit = view.findViewById(R.id.AddContent);
        amountEdit = view.findViewById(R.id.AddAmount);
        cardCheck = view.findViewById(R.id.AddCard);
        cashCheck = view.findViewById(R.id.AddCash);
        bankEdit = view.findViewById(R.id.AddBank);
        viewLine = view.findViewById(R.id.visibleLine);
        viewLayout = view.findViewById(R.id.visibleLayout);
        dateTextView = view.findViewById(R.id.AddDate);

        // TODO:입력 받은 값 받아오기

        // card/cash 체크 박스 선택 시 이벤트
        cardCheck.setOnClickListener(checkC);
        cashCheck.setOnClickListener(checkC);

        // dateSelect 클릭 시 달력 표시
        LinearLayout dateSelect = view.findViewById(R.id.AddDateSelect);
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopAddDate popAddDate = new PopAddDate();
                popAddDate.setTargetFragment(PopAddItem.this, 0); // 현재 Fragment를 Target으로 설정
                popAddDate.show(getParentFragmentManager(), "날짜 선택");
            }
        });

        // 버튼 클릭 처리
        view.findViewById(R.id.AddNOBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.AddNEXTBtn).setOnClickListener(v -> {
            String contents = contentEdit.getText().toString();
            String amount = amountEdit.getText().toString();
            String bank = bankEdit.getText().toString();
            String card = cardCheck.isChecked() ? "카드" : "현금";
            String date = dateTextView.getText().toString();

            PopAddCategory popAddCategory = new PopAddCategory();

            // Bundle을 이용한 데이터 전달
            Bundle args = new Bundle();
            args.putString("content", contents);
            args.putString("amount", amount);
            args.putString("bank", bank);
            args.putString("card", card);
            args.putString("date", date);
            popAddCategory.setArguments(args);

            popAddCategory.setTargetFragment(PopAddItem.this, 0);
            popAddCategory.show(getParentFragmentManager(), "카테고리 선택");
        });

        return view;
    }

    // 체크 박스 이벤트 처리
    View.OnClickListener checkC = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = ((CheckBox) view).isChecked();
            int itemID = view.getId();

            if (itemID == R.id.AddCard) {
                if(checked) {
                    cashCheck.setChecked(false);
                    viewLine.setVisibility(View.VISIBLE);
                    viewLayout.setVisibility(View.VISIBLE);
                }else {
                    viewLine.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                }
            } else if (itemID == R.id.AddCash) {
                if(checked) {
                    cardCheck.setChecked(false);
                    viewLine.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                    // TODO: 현금 선택 시 기능 설정
                }else {

                }
            }
        }
    };

    // 선택한 날짜로 텍스트 변경
    public void updateDate(String date) {
        dateTextView.setText(date);
    }

    // 리스너 인터페이스
    public interface ItemAddListener {
        void onItemAdded(AmountItem item);
    }

    // AmountItem 생성
    public void createAmountItem(String content, String amount, String card, String bank, String date, String category) {

//        Toast.makeText(getActivity(), "createAmountItem"+content + category, Toast.LENGTH_SHORT).show();
        if (itemAddListener != null) {
//            Toast.makeText(getActivity(), "null"+content + category, Toast.LENGTH_SHORT).show();
            AmountItem item = new AmountItem(content, date, card, bank, category, amount);
            itemAddListener.onItemAdded(item);
            dismiss();
        }
    }
}