// PopShowDaylist.java
package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.PopShowDaylistBinding;
import com.example.capstone_donworry.model.DailySummary;
import com.example.capstone_donworry.model.ExpectedExpense;
import com.example.capstone_donworry.model.Expense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopShowDaylist extends DialogFragment implements PopAddItem.ItemAddListener {

    RecyclerView recyclerView;
    MixedAmountAdapter adapter;
    private PopShowDaylistBinding binding;

    private ArrayList<AmountItem> amountList;
    private ArrayList<AmountExpectedItem> expectedList;
    private String selectDate;
    private String userId;
    private boolean isAddList = false;

    private long recomAmount;
    private long totalSpent;
    private long expectedSpent;

    private DecimalFormat decimalFormat;

    public void setOnDialogCancelListener(FragmentCalendar fragmentCalendar) {
    }

    public interface OnDialogCancelListener {
        void onDialogCancel();
        void onItemAdded(AmountItem item);
    }

    private OnDialogCancelListener dListener;

    public static PopShowDaylist newInstance(ArrayList<AmountItem> amountDay,
                                             ArrayList<AmountExpectedItem> expectedList,
                                             String date,
                                             long recomAmount,
                                             long totalSpent,
                                             long expectedSpent) {
        PopShowDaylist dialog = new PopShowDaylist();
        Bundle args = new Bundle();
        args.putParcelableArrayList("amountDay", amountDay);
        args.putParcelableArrayList("expectedDay", expectedList);
        args.putString("selectDate", date);
        args.putLong("recomAmount", recomAmount);
        args.putLong("totalSpent", totalSpent);
        args.putLong("expectedSpent", expectedSpent);
        dialog.setArguments(args);
        return dialog;
    }

    public void updateData(DailySummary summary) {
        this.recomAmount = summary.getDailyGoal();
        this.totalSpent = summary.getDailyTotalExpense();
        this.expectedSpent = summary.getDailyTotalExpectedExpense();

        List<Item> mergedList = new ArrayList<>();

        for (Expense expense : summary.getDailyExpenseList()) {
            AmountItem item = new AmountItem();
            item.setUid(expense.getId());
            item.setContent(expense.getTitle());
            item.setAmount((int) expense.getAmount());
            item.setDate(expense.getExpenseDate().toString());
            item.setCard(expense.getPayment());
            item.setCategory(expense.getCategory());
            item.setBank(expense.getBankName());
            mergedList.add(item);
        }

        for (ExpectedExpense expected : summary.getDailyExpectedList()) {
            AmountExpectedItem item = new AmountExpectedItem();
            item.setId(expected.getId());
            item.setContent(expected.getDetails());
            item.setAmount(expected.getAmount());
            item.setDate(expected.getDate().toString());
            mergedList.add(item);
        }

        // 날짜 기준 정렬
        Collections.sort(mergedList, (a, b) -> a.getDate().compareTo(b.getDate()));

        // 어댑터에 갱신
        adapter.updateItems(mergedList);

        if (binding != null) {
            binding.DailyRecommendedAmount.setText(decimalFormat.format(recomAmount) + " 원");
            binding.TotalSpent.setText(decimalFormat.format(totalSpent) + " 원");
            binding.ExpectedSpent.setText(decimalFormat.format(expectedSpent) + " 원");
        }
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            amountList = getArguments().getParcelableArrayList("amountDay");
            expectedList = getArguments().getParcelableArrayList("expectedDay");
            selectDate = getArguments().getString("selectDate");
            recomAmount = getArguments().getLong("recomAmount", 0);
            totalSpent = getArguments().getLong("totalSpent", 0);
            expectedSpent = getArguments().getLong("expectedSpent", 0);
        }

        Fragment target = getTargetFragment();
        if (target instanceof OnDialogCancelListener) {
            dListener = (OnDialogCancelListener) target;
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
        adapter = new MixedAmountAdapter(getContext());
        recyclerView.setAdapter(adapter);

        binding.DayTextView.setText(selectDate);
        binding.DailyRecommendedAmount.setText(decimalFormat.format(recomAmount) + " 원");
        binding.TotalSpent.setText(decimalFormat.format(totalSpent) + " 원");
        binding.ExpectedSpent.setText(decimalFormat.format(expectedSpent) + " 원");

        updateRecyclerView();

        adapter.setOnClickListener((holder, view, position) -> {
            Item item = adapter.getItem(position);

            if (item instanceof AmountItem) {
                PopDetailItem existing = (PopDetailItem) getChildFragmentManager().findFragmentByTag("세부내역");
                if (existing != null) existing.dismiss();

                PopDetailItem popDetail = PopDetailItem.newInstance((AmountItem) item);
                popDetail.setOnUpdateListener(updatedItem -> {
                    // 리스트에서 업데이트
                    for (int i = 0; i < amountList.size(); i++) {
                        if (amountList.get(i).getUid() == updatedItem.getUid()) {
                            amountList.set(i, updatedItem);
                            break;
                        }
                    }

                    adapter.notifyItemChanged(position);
                    updateRecyclerView();

                    // FragmentCalendar에 변경 알림
                    Fragment target = getTargetFragment();
                    if (target instanceof FragmentCalendar) {
                        ((FragmentCalendar) target).updateExpense(updatedItem);
                    }
                });

                popDetail.show(getChildFragmentManager(), "세부내역");

            } else if (item instanceof AmountExpectedItem) {
                PopDetailExpectedItem popExpected = PopDetailExpectedItem.newInstance((AmountExpectedItem) item);
                popExpected.setOnUpdateListener(updatedItem -> {
                    // 리스트에서 업데이트
                    for (int i = 0; i < expectedList.size(); i++) {
                        if (expectedList.get(i).getId() == updatedItem.getId()) {
                            expectedList.set(i, updatedItem);
                            break;
                        }
                    }

                    adapter.notifyItemChanged(position);
                    updateRecyclerView();

                    // FragmentCalendar에 변경 알림
                    Fragment target = getTargetFragment();
                    if (target instanceof FragmentCalendar) {
                        ((FragmentCalendar) target).updateExpectedExpense(updatedItem);
                    }
                });

                popExpected.show(getChildFragmentManager(), "예상세부내역");
            }
        });

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
        List<Item> mixedItems = new ArrayList<>();
        mixedItems.addAll(amountList);
        if (expectedList != null) mixedItems.addAll(expectedList);

        adapter.updateItems(mixedItems);

        int totalSpent = 0;
        for (AmountItem it : amountList) {
            totalSpent += it.getAmount();
        }

        long expectedSpent = 0;
        for (AmountExpectedItem it : expectedList) {
            expectedSpent += it.getAmount();
        }

        binding.TotalSpent.setText(decimalFormat.format(totalSpent) + " 원");
        binding.ExpectedSpent.setText(decimalFormat.format(expectedSpent) + " 원");
        binding.DailyRecommendedAmount.setText(decimalFormat.format(recomAmount) + " 원");
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
