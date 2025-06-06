package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.DBHelper;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.PopShowDaylistBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class PopShowDaylist extends DialogFragment implements PopAddItem.ItemAddListener {

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private PopShowDaylistBinding binding;

    private ArrayList<AmountItem> amountList;
    private String selectDate;
    private String userId;
    private boolean isAddList = false;

    private long recomAmount;
    private long totalSpent;
    private long expectedSpent;

    private DecimalFormat decimalFormat;

    public interface OnDialogCancelListener {
        void onDialogCancel();
        void onItemAdded(AmountItem item);
    }

    private OnDialogCancelListener dListener;

    public static PopShowDaylist newInstance(ArrayList<AmountItem> amountDay, String date, long recomAmount, long totalSpent, long expectedSpent) {
        PopShowDaylist popShowDaylist = new PopShowDaylist();
        Bundle args = new Bundle();
        args.putParcelableArrayList("amountDay", amountDay);
        args.putString("selectDate", date);
        args.putLong("recomAmount", recomAmount);
        args.putLong("totalSpent", totalSpent);
        args.putLong("expectedSpent", expectedSpent);
        popShowDaylist.setArguments(args);
        return popShowDaylist;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            amountList = getArguments().getParcelableArrayList("amountDay");
            selectDate = getArguments().getString("selectDate");
            recomAmount = getArguments().getLong("recomAmount", 0);
            totalSpent = getArguments().getLong("totalSpent", 0);
            expectedSpent = getArguments().getLong("expectedSpent", 0);
        }

        if (getTargetFragment() instanceof OnDialogCancelListener) {
            dListener = (OnDialogCancelListener) getTargetFragment();
        }
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

                // 화면 너비의 90%로 고정
                int width = (int) (displayMetrics.widthPixels * 0.9);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = width;
                window.setAttributes(layoutParams);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PopShowDaylistBinding.inflate(inflater, container, false);
        decimalFormat = new DecimalFormat("#,###");

        recyclerView = binding.DayRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AmountAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // 날짜
        binding.DayTextView.setText(selectDate);

        // 금액 표시
        binding.DailyRecommendedAmount.setText(decimalFormat.format(recomAmount) + " 원");
        binding.TotalSpent.setText(decimalFormat.format(totalSpent) + " 원");
        binding.ExpectedSpent.setText(decimalFormat.format(expectedSpent) + " 원");

        updateRecyclerView();

        // 클릭 리스너
        adapter.setOnClickListener((holder, view, position) -> {
            AmountItem item = adapter.getItem(position);
            PopDetailItem popDetail = PopDetailItem.newInstance(item);

            popDetail.setOnUpdateListener(updatedItem -> {
                if (getTargetFragment() instanceof FragmentCalendar) {
                    ((FragmentCalendar) getTargetFragment()).updateExpense(updatedItem);
                }

                // 리스트 업데이트
                amountList.set(position, updatedItem);
                adapter.notifyItemChanged(position);
                updateRecyclerView();
            });

            popDetail.show(getChildFragmentManager(), "세부내역");
        });

        // 버튼
        binding.AddNOBtn.setOnClickListener(v -> dismiss());
        binding.AddADDBtn.setOnClickListener(v -> showAddItem());

        return binding.getRoot();
    }

    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        popAddItem.setSettingDate(selectDate);
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    public void onItemAdded(AmountItem item) {
        amountList.add(item);
        updateRecyclerView();
        isAddList = true;

        if (dListener != null) {
            dListener.onItemAdded(item);
        }
    }

    private void updateRecyclerView() {
        adapter.items.clear();
        adapter.items.addAll(amountList);
        adapter.notifyDataSetChanged();

        // 프론트에서 임시로 금액 정보 업데이트
        int totalSpent = 0;
        for (AmountItem it : amountList) {
            totalSpent += it.getAmount();
        }

        long expectedSpent = 0; // 예: 예상지출 로직 따로 없다면 0 유지
        long recommendedAmount = recomAmount; // 기존 추천 금액 유지 (또는 재계산 가능)

        // 금액 표시 UI 업데이트
        binding.TotalSpent.setText(decimalFormat.format(totalSpent) + " 원");
        binding.ExpectedSpent.setText(decimalFormat.format(expectedSpent) + " 원");
        binding.DailyRecommendedAmount.setText(decimalFormat.format(recommendedAmount) + " 원");

        isAddList = true;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dListener != null && isAddList) {
            dListener.onDialogCancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
