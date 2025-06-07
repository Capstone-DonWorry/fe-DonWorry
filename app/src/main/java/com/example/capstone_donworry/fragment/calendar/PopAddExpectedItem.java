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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.capstone_donworry.CustomComma;
import com.example.capstone_donworry.R;

public class PopAddExpectedItem extends DialogFragment {

    private EditText contentEdit, amountEdit;
    private TextView dateTextView;
    private String settingDate;
    private ItemAddListener itemAddListener;

    public void setSettingDate(String date) {
        this.settingDate = date;
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
        View view = inflater.inflate(R.layout.pop_add_expected_item, container, false);

        contentEdit = view.findViewById(R.id.AddExpectedContent);
        amountEdit = view.findViewById(R.id.AddExpectedAmount);
        amountEdit.addTextChangedListener(new CustomComma(amountEdit));
        dateTextView = view.findViewById(R.id.AddExpectedDate);

        if (settingDate != null && !settingDate.isEmpty()) {
            dateTextView.setText(settingDate);
        }

        LinearLayout dateSelect = view.findViewById(R.id.AddDateSelect);
        dateSelect.setOnClickListener(v -> {
            PopAddDate popAddDate = new PopAddDate();
            popAddDate.setInitDate(settingDate);
            popAddDate.setTargetFragment(PopAddExpectedItem.this, 0);
            popAddDate.show(getParentFragmentManager(), "날짜 선택");
        });

        view.findViewById(R.id.AddNOExpectedBtn).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.AddExpectedSaveBtn).setOnClickListener(v -> {
            String contents = contentEdit.getText().toString();
            String strAmount = amountEdit.getText().toString().replace(",", "");
            String date = dateTextView.getText().toString();

            if (contents.isEmpty() || strAmount.isEmpty()) {
                Toast.makeText(getActivity(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                int amount = Integer.parseInt(strAmount);
                if (itemAddListener != null) {
                    Log.i("linster", "date " + date);
                    itemAddListener.onExpectedItemAdded(new AmountExpectedItem(contents, amount, date));
                }
                dismiss();
            }
        });

        return view;
    }

    public void updateDate(String date) {
        dateTextView.setText(date);
    }

    public void setItemAddListener(ItemAddListener listener) {
        this.itemAddListener = listener;
    }

    public interface ItemAddListener {
        void onExpectedItemAdded(AmountExpectedItem item);
    }
}

