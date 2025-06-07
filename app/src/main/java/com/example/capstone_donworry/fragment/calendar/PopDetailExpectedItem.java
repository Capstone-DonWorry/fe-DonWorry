package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.capstone_donworry.R;

import java.text.DecimalFormat;

public class PopDetailExpectedItem extends DialogFragment {

    private TextView nameTextView, dateTextView, amountTextView;
    private EditText nameEditText;
    private Button okButton;

    private static final String DETAIL_ITEM = "expectedDetailItem";
    private AmountExpectedItem item;
    private DecimalFormat decimalFormat;

    public interface OnUpdateListener {
        void onUpdate(AmountExpectedItem updatedItem);
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.onUpdateListener = listener;
    }

    public static PopDetailExpectedItem newInstance(AmountExpectedItem item) {
        PopDetailExpectedItem fragment = new PopDetailExpectedItem();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = (int) (metrics.widthPixels * 0.9);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_detail_expected_item, container, false);

        item = getArguments().getParcelable(DETAIL_ITEM);
        decimalFormat = new DecimalFormat("#,###");

        nameTextView = view.findViewById(R.id.ExpectedDetailName);
        nameEditText = view.findViewById(R.id.ExpectedDetailNameEdit);
        dateTextView = view.findViewById(R.id.ExpectedDetailDate);
        amountTextView = view.findViewById(R.id.ExpectedDetailAmount);
        okButton = view.findViewById(R.id.ExpectedDetailOKBtn);

        if (item != null) {
            nameTextView.setText(item.getContent());
            dateTextView.setText(item.getDate());
            amountTextView.setText(decimalFormat.format(item.getAmount()));
        }

        nameTextView.setOnClickListener(v -> {
            nameEditText.setVisibility(View.VISIBLE);
            nameEditText.setText(nameTextView.getText().toString());
            nameTextView.setVisibility(View.GONE);
        });

        nameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                applyNameEdit();
                return true;
            }
            return false;
        });

        view.setOnTouchListener((v, event) -> {
            if (nameEditText.getVisibility() == View.VISIBLE && !isEditTextTouched(nameEditText, event)) {
                applyNameEdit();
                return true;
            }
            return false;
        });

        dateTextView.setOnClickListener(dateClick -> {
            PopAddDate popAddDate = new PopAddDate();
            popAddDate.setInitDate(dateTextView.getText().toString());
            popAddDate.setTargetFragment(PopDetailExpectedItem.this, 0);
            popAddDate.show(getParentFragmentManager(), "날짜 선택");
        });

        amountTextView.setOnClickListener(v -> {
            Fragment prev = getParentFragmentManager().findFragmentByTag("금액 수정");
            if (prev != null) {
                ((DialogFragment) prev).dismiss(); // 이전 다이얼로그 제거
            }

            PopDetailAmount popDetailAmount = new PopDetailAmount();
            Bundle bundle = new Bundle();
            bundle.putLong("currentAmount", item.getAmount());
            Log.d("PopDetailExpectedItem", "전달할 금액: " + item.getAmount());
            popDetailAmount.setArguments(bundle);

            popDetailAmount.setAmountUpdateListener(updatedAmount -> {
                item.setAmount(updatedAmount);
                amountTextView.setText(decimalFormat.format(updatedAmount));
            });

            popDetailAmount.show(getParentFragmentManager(), "금액 수정");
        });

        okButton.setOnClickListener(v -> {
            // 1. 이름 적용
            String newName = nameEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(newName)) {
                item.setContent(newName);
                nameTextView.setText(newName);
            }

            // 2. 날짜 적용
            String newDate = dateTextView.getText().toString().trim();
            item.setDate(newDate);

            // 3. 콜백
            if (onUpdateListener != null) {
                onUpdateListener.onUpdate(item);
            }

            dismiss();
        });



        return view;
    }

    private void applyNameEdit() {
        String newText = nameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            item.setContent(newText);
            nameTextView.setText(newText);
        }
        nameEditText.setVisibility(View.GONE);
        nameTextView.setVisibility(View.VISIBLE);
    }

    public void updateDate(String date) {
        dateTextView.setText(date);
        item.setDate(date);
    }

    private boolean isEditTextTouched(EditText editText, MotionEvent event) {
        int[] location = new int[2];
        editText.getLocationOnScreen(location);
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        return x > location[0] && x < location[0] + editText.getWidth()
                && y > location[1] && y < location[1] + editText.getHeight();
    }
}
