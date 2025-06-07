package com.example.capstone_donworry.fragment.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.capstone_donworry.R;

import java.text.DecimalFormat;

public class PopDetailItem extends DialogFragment {
    private TextView nameTextView, dateTextView, cardTextView, bankTextView, categoryTextView, amountTextView;
    private EditText nameEditText, bankEditText;
    private static final String DETAIL_ITEM = "detailItem";
    private DecimalFormat decimalFormat;
    private AmountItem item;

    public interface OnDialogCancelListener {
        void onDialogUpdate(String date);
    }

    public static PopDetailItem newInstance(AmountItem item) {
        PopDetailItem fragment = new PopDetailItem();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = (int) (displayMetrics.widthPixels * 0.9);
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
        View rootView = inflater.inflate(R.layout.pop_detail_item, container, false);
        item = getArguments().getParcelable(DETAIL_ITEM);

        nameEditText = rootView.findViewById(R.id.DetailNameEdit);
        bankEditText = rootView.findViewById(R.id.DetailBankEdit);
        nameTextView = rootView.findViewById(R.id.DetailName);
        dateTextView = rootView.findViewById(R.id.DetailDate);
        cardTextView = rootView.findViewById(R.id.DetailCard);
        bankTextView = rootView.findViewById(R.id.DetailBank);
        categoryTextView = rootView.findViewById(R.id.DetailCategory);
        amountTextView = rootView.findViewById(R.id.DetailAmount);

        if (item != null) {
            nameTextView.setText(item.getContent());
            dateTextView.setText(item.getDate());
            cardTextView.setText("CARD".equals(item.getCard()) ? "카드" : "현금");
            bankTextView.setText("CARD".equals(item.getCard()) ? (item.getBank().equals("null") || item.getBank().isEmpty() ? "없음" : item.getBank()) : "");
            categoryTextView.setText(item.getCategory());
            decimalFormat = new DecimalFormat("#,###");
            amountTextView.setText(decimalFormat.format(item.getAmount()));
        }

        if (!"카드".equals(cardTextView.getText().toString())) {
            bankTextView.setVisibility(View.GONE);
        }

        rootView.findViewById(R.id.DetailOKBtn).setOnClickListener(okButton -> {
            if (onUpdateListener != null) {
                if (!"카드".equals(cardTextView.getText().toString())) {
                    item.setBank(null);
                }
                item.setCard("카드".equals(cardTextView.getText().toString()) ? "CARD" : "CASH");
                onUpdateListener.onUpdate(item);
            }
            dismiss();
        });

        nameTextView.setOnClickListener(nameClick -> {
            nameEditText.setVisibility(View.VISIBLE);
            nameEditText.setText(nameTextView.getText().toString());
            nameTextView.setVisibility(View.GONE);
        });

        nameEditText.setOnEditorActionListener((editView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String newText = nameEditText.getText().toString().trim();
                nameTextView.setText(newText);
                item.setContent(newText);
                nameEditText.setVisibility(View.GONE);
                nameTextView.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        rootView.setOnTouchListener((touchView, motionEvent) -> {
            if (nameEditText.getVisibility() == View.VISIBLE && !isEditTextTouched(nameEditText, motionEvent)) {
                String newText = nameEditText.getText().toString().trim();
                nameTextView.setText(newText);
                item.setContent(newText);
                nameEditText.setVisibility(View.GONE);
                nameTextView.setVisibility(View.VISIBLE);
                return true;
            }
            if (bankEditText.getVisibility() == View.VISIBLE && !isEditTextTouched(bankEditText, motionEvent)) {
                String newText = bankEditText.getText().toString().trim();
                item.setBank(newText);
                bankTextView.setText(newText.isEmpty() ? " " : newText);
                bankEditText.setVisibility(View.GONE);
                bankTextView.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        dateTextView.setOnClickListener(dateClick -> {
            PopAddDate popAddDate = new PopAddDate();
            popAddDate.setInitDate(dateTextView.getText().toString());
            popAddDate.setTargetFragment(PopDetailItem.this, 0);
            popAddDate.show(getParentFragmentManager(), "날짜 선택");
        });

        cardTextView.setOnClickListener(cardClick -> {
            final String[] options = {"카드", "현금"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("결제 방법 선택")
                    .setSingleChoiceItems(options, getCheckedItem(), (dialogInterface, which) -> {
                        String selectCard = options[which];
                        cardTextView.setText(selectCard);
                        item.setCard("카드".equals(selectCard) ? "CARD" : "CASH");
                        Log.i("PopDetailItem", "getBank() = " + item.getBank());
                        Log.i("PopDetailItem", "getCard() = " + item.getCard());
                        if ("CARD".equals(item.getCard())) {
                            bankTextView.setText(item.getBank() == null || item.getBank().equals("null") ? "은행명" : item.getBank());
                            bankTextView.setVisibility(View.VISIBLE);
                            bankEditText.setVisibility(View.GONE);
                        } else {
                            item.setBank("");  // bank null로 초기화
                            bankTextView.setText("");
                            bankTextView.setVisibility(View.GONE);
                            bankEditText.setVisibility(View.GONE);
                        }
                    })
                    .setNegativeButton("확인", null)
                    .show();
        });


        bankTextView.setOnClickListener(bankClick -> {
            if ("카드".equals(cardTextView.getText().toString()) && bankEditText != null) {
                bankEditText.setVisibility(View.VISIBLE);
                bankEditText.setText(item.getBank() == null || item.getBank().equals("null") ? "" : item.getBank());
                bankTextView.setVisibility(View.GONE);

                bankEditText.setOnEditorActionListener((editView, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        String newText = bankEditText.getText().toString().trim();
                        item.setBank(newText);  // 모델에 반영
                        bankTextView.setText(newText.isEmpty() ? " " : newText);  // 빈 문자열은 공백 처리
                        bankEditText.setVisibility(View.GONE);
                        bankTextView.setVisibility(View.VISIBLE);
                        return true;
                    }
                    return false;
                });
            }
        });


        categoryTextView.setOnClickListener(categoryClick -> {
            PopDetailCategory popDetailCategory = new PopDetailCategory();
            popDetailCategory.setTargetFragment(PopDetailItem.this, 0);
            popDetailCategory.show(getParentFragmentManager(), "카테고리 선택");
        });

        amountTextView.setOnClickListener(amountClick -> {
            PopDetailAmount popDetailAmount = new PopDetailAmount();
            Bundle bundle = new Bundle();
            bundle.putLong("currentAmount", item.getAmount());
            popDetailAmount.setArguments(bundle);
            popDetailAmount.setAmountUpdateListener(updateAmount -> {
                item.setAmount(updateAmount);
                amountTextView.setText(decimalFormat.format(updateAmount));
            });
            popDetailAmount.show(getParentFragmentManager(), "금액 수정");
        });

        return rootView;
    }

    public void updateDate(String date) {
        dateTextView.setText(date);
        item.setDate(date);
    }

    private int getCheckedItem() {
        String currentText = cardTextView.getText().toString();
        return "카드".equals(currentText) ? 0 : 1;
    }

    public void updateCategory(String category) {
        categoryTextView.setText(category);
        item.setCategory(category);
    }

    private boolean isEditTextTouched(EditText editText, MotionEvent event) {
        int[] location = new int[2];
        editText.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + editText.getWidth();
        int bottom = top + editText.getHeight();
        return event.getRawX() > left && event.getRawX() < right && event.getRawY() > top && event.getRawY() < bottom;
    }

    public interface OnUpdateListener {
        void onUpdate(AmountItem updatedItem);
    }

    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.onUpdateListener = listener;
    }
}
