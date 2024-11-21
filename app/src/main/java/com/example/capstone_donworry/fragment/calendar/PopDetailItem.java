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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstone_donworry.CustomComma;
import com.example.capstone_donworry.R;

import java.text.DecimalFormat;

public class PopDetailItem extends DialogFragment {
    private TextView nameTextView, dateTextView, cardTextView, bankTextView, categoryTextView, amountTextView;
    private static final String DETAIL_ITEM = "detailItem";
    private DecimalFormat decimalFormat;

    public static PopDetailItem newInstance(AmountItem item){
        PopDetailItem fragment = new PopDetailItem();
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_ITEM, item); // 아이템 데이터 전달
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.pop_detail_item, container, false);
        if (view == null) {
            Log.d("PopDetailItem", "View inflation failed");
        } else {
            Log.d("PopDetailItem", "View inflated successfully");
        }

        AmountItem item = (AmountItem) getArguments().getParcelable(DETAIL_ITEM);

        // UI text 설정
        nameTextView = view.findViewById(R.id.DetailName);
        dateTextView = view.findViewById(R.id.DetailDate);
        cardTextView = view.findViewById(R.id.DetailCard);
        bankTextView = view.findViewById(R.id.DetailBank);
        categoryTextView = view.findViewById(R.id.DetailCategory);
        amountTextView = view.findViewById(R.id.DetailAmount);

        // text 설정
        if (item != null) {
            nameTextView.setText(item.getContent());
            dateTextView.setText(item.getDate());
            cardTextView.setText(item.getCard());
            bankTextView.setText(item.getBank());
            categoryTextView.setText(item.getCategory());
            decimalFormat = new DecimalFormat("#,###");
            amountTextView.setText(decimalFormat.format(item.getAmount()));
        }

        // 버튼 클릭 처리
        view.findViewById(R.id.DetailOKBtn).setOnClickListener(v -> dismiss());

        // 가게명 데이터 수정
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = view.getRootView().findViewById(R.id.DetailNameEdit);
                if (editText != null) {
                    editText.setVisibility(View.VISIBLE);
                    editText.setText(nameTextView.getText().toString());
                    nameTextView.setVisibility(View.GONE);

                    // edit 수정 후 db 수정
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (!hasFocus) {
                                String newText = editText.getText().toString().trim();
                                nameTextView.setText(newText); // 수정된 텍스트

                                // db 추가

                                editText.setVisibility(View.GONE);
                                nameTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        // 날짜 데이터 수정
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopAddDate popAddDate = new PopAddDate();

                String currentDate = dateTextView.getText().toString();
                popAddDate.setInitDate(currentDate);
                popAddDate.setTargetFragment(PopDetailItem.this, 0);
                popAddDate.show(getParentFragmentManager(), "날짜 선택");
            }
        });

        // 카드 데이터 수정
        cardTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"카드", "현금"};
                boolean[] checkedCard = new boolean[options.length];

                if (cardTextView.getText().toString().equals("카드")) {
                    checkedCard[0] = true;
                } else if (cardTextView.getText().toString().equals("현금")) {
                    checkedCard[1] = true;

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("결제 방법 선택")
                        .setSingleChoiceItems(options, getCheckedItem(), (dialogInterface, which) -> {
                            // 선택 항목 반영
                            String selectCard = options[which];
                            cardTextView.setText(selectCard);

                            // 은행 설정 여부
                            if (selectCard.equals("카드")) {
                                bankTextView.setVisibility(View.VISIBLE);

                                final EditText editText = view.getRootView().findViewById(R.id.DetailBankEdit);
                                if (editText != null){
                                    editText.setVisibility(View.VISIBLE);
                                    editText.setText("");
                                    bankTextView.setVisibility(View.GONE);
                                    editText.requestFocus();

                                    // edit 수정 후 db 수정
                                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View view, boolean hasFocus) {
                                            if (!hasFocus) {
                                                String newText = editText.getText().toString().trim();
                                                bankTextView.setText(newText); // 수정된 텍스트

                                                // db 추가

                                                editText.setVisibility(View.GONE);
                                                bankTextView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }

                            } else if (selectCard.equals("현금")) {
                                bankTextView.setText("");
                                bankTextView.setVisibility(View.GONE);
                            }

                        })
                        .setNegativeButton("확인", null)
                        .show();
            }
        });

        // 은행 데이터 수정
        if (cardTextView.getText().toString().equals("카드")) {
            bankTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editText = view.getRootView().findViewById(R.id.DetailBankEdit);
                    if (editText != null) {
                        editText.setVisibility(View.VISIBLE);
                        editText.setText(bankTextView.getText().toString());
                        bankTextView.setVisibility(View.GONE);

                        // edit 수정 후 db 수정
                        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean hasFocus) {
                                if (!hasFocus) {
                                    String newText = editText.getText().toString().trim();
                                    bankTextView.setText(newText); // 수정된 텍스트

                                    // db 추가

                                    editText.setVisibility(View.GONE);
                                    bankTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            bankTextView.setVisibility(View.GONE);
        }

        // 카테고리 데이터 수정
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopDetailCategory popDetailCategory = new PopDetailCategory();

                popDetailCategory.setTargetFragment(PopDetailItem.this, 0);
                popDetailCategory.show(getParentFragmentManager(), "카테고리 선택");
            }
        });

        // 지출 데이터 수정
        amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = view.getRootView().findViewById(R.id.DetailAmountEdit);
                editText.addTextChangedListener(new CustomComma(editText));
                editText.setVisibility(View.VISIBLE);
                editText.setText(amountTextView.getText().toString().replace(",", ""));
                amountTextView.setVisibility(View.GONE);

                // edit 수정 후 db 수정
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (!hasFocus) {
                            String newTextAmount = editText.getText().toString().trim().replace(",", "");
                            int newIntAmount = Integer.parseInt(newTextAmount);
                            amountTextView.setText(decimalFormat.format(newIntAmount)); // 수정된 텍스트

                            // db 추가


                            editText.setVisibility(View.GONE);
                            amountTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        return view;
    }

    // 날짜 TextView 설정
    public void updateDate(String date) {
        dateTextView.setText(date);
    }

    // 카드 설정
    private int getCheckedItem() {
        String currentText = cardTextView.getText().toString();

        if("카드".equals(currentText)) {
            return 0;
        } else if ("현금".equals(currentText)) {
            return 1;
        }
        return -1;
    }

    // 카테고리 TextView 설정
    public void updateCategory(String category) {
        categoryTextView.setText(category);
    }

}