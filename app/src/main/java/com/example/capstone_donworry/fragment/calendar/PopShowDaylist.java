package com.example.capstone_donworry.fragment.calendar;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.DBHelper;
import com.example.capstone_donworry.R;
import com.example.capstone_donworry.databinding.PopShowDaylistBinding;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class PopShowDaylist  extends DialogFragment implements PopAddItem.ItemAddListener{

    RecyclerView recyclerView;
    AmountAdapter adapter;
    private PopShowDaylistBinding binding;
    private DBHelper db;

    private ArrayList<AmountItem> amountList;
    private String selectDate;
    private String userId;
    private boolean isAddList = false;
    private int recomAmount;
    private DecimalFormat decimalFormat;

    public interface OnDialogCancelListener {
        void onDialogCancel();
    }
    private OnDialogCancelListener dListener;

    public static PopShowDaylist newInstance(ArrayList<AmountItem> amountDay, String date) {
        PopShowDaylist popShowDaylist = new PopShowDaylist();
        Bundle args = new Bundle();
        args.putParcelableArrayList("amountDay", amountDay);
        args.putString("selectDate", date);
        popShowDaylist.setArguments(args);
        return popShowDaylist;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setDb(DBHelper db) {
        this.db = db;
    }
    public void setrecom(int recomAmount) {
        this.recomAmount = recomAmount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            amountList = getArguments().getParcelableArrayList("amountDay");
            selectDate = getArguments().getString("selectDate");
        }

        if (getTargetFragment() instanceof OnDialogCancelListener) {
            dListener = (OnDialogCancelListener) getTargetFragment();
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

        Toast.makeText(getContext(), isAddList+"ok" ,Toast.LENGTH_SHORT).show();
        // 팝업창 레이아웃 사용
        View view = inflater.inflate(R.layout.pop_show_daylist, container, false);
        if (view == null) {
            Log.d("PopAddItem", "View inflation failed");
        } else {
            Log.d("PopAddItem", "View inflated successfully");
        }

        binding = PopShowDaylistBinding.bind(view);
        decimalFormat = new DecimalFormat("#,###");

        // recyclerView 설정
        recyclerView = binding.DayRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // adapter 설정
        adapter = new AmountAdapter(getActivity().getApplicationContext());

        recyclerView.setAdapter(adapter);

        // 날짜 입력
        TextView dateTextView = binding.DayTextView;
        dateTextView.setText(selectDate);

        // 추천 금액 입력
        TextView dailyRecomAmount = binding.DailyRecommendedAmount;
        String dailyAmount = decimalFormat.format(recomAmount);
        dailyRecomAmount.setText(dailyAmount);

        updateRecyclerView();

        // 아이템 이벤트 처리
        adapter.setOnClickListener(new AmountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AmountAdapter.ViewHolder holder, View view, int position) {
                AmountItem item = adapter.getItem(position);

                // 아이템 클릭시 팝업 창 띄우기
                PopDetailItem popDetail = PopDetailItem.newInstance(item);
                popDetail.show(getChildFragmentManager(), "세부내역");


                // 토스트 메시지 확인
//                Toast.makeText(getActivity(), item.getAmount(), Toast.LENGTH_SHORT).show();
            }
        });

        // 아이템 삭제
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        // 삭제할 아이템 담아두기
                        AmountItem deleteItem = adapter.getItem(position);

                        // 삭제
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);
                        // db 삭제
                        db.deleteItem(userId, deleteItem);

                        isAddList = true;

                        // 복구
                        Snackbar.make(recyclerView, deleteItem.getContent()+"삭제 했습니다.", Snackbar.LENGTH_LONG).setAction("취소", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                db.addItem(userId, deleteItem);
                                updateRecyclerView();
                            }
                        }).show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.err_red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_forever_24)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        // 버튼 클릭 처리
        view.findViewById(R.id.AddNOBtn).setOnClickListener(v -> {
            onCancel(getDialog());
            dismiss();
        });
        view.findViewById(R.id.AddADDBtn).setOnClickListener(v -> showAddItem());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (dListener != null && isAddList) {
            dListener.onDialogCancel();
        }

    }

    // 항목추가 다이얼로그 표시
    private void showAddItem() {
        PopAddItem popAddItem = new PopAddItem();
        // FragmentCalendar를 타겟으로 설정
        popAddItem.setSettingDate(selectDate);
        popAddItem.setTargetFragment(this, 0);
        popAddItem.show(getParentFragmentManager(), "내용추가");
    }

    // 아이템 추가
    public void onItemAdded(AmountItem item) {
        // db에 item 등록
        db.addItem(userId, item);
        amountList.add(item);

        // recycler뷰 업데이트
        if (item.getDate() == selectDate) {
            updateRecyclerView();
        }
        isAddList = true;
        Toast.makeText(getContext(), isAddList+"ok" ,Toast.LENGTH_SHORT).show();
    }

    // 리사이클러 뷰 업데이트
    private void updateRecyclerView() {
        adapter.items.clear();

        for (AmountItem item : amountList) {
            adapter.addItem(item);
        }

        // RecyclerView 업데이트
        adapter.notifyDataSetChanged();

    }
}
