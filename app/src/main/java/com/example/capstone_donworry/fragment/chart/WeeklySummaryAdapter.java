package com.example.capstone_donworry.fragment.chart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.model.statistics.WeeklyStatistic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklySummaryAdapter extends RecyclerView.Adapter<WeeklySummaryAdapter.ViewHolder> {
    private List<WeeklyStatistic> data;
    private final OnWeekClickListener listener;

    public interface OnWeekClickListener {
        void onWeekClick(WeeklyStatistic item);
    }

    public WeeklySummaryAdapter(List<WeeklyStatistic> data, OnWeekClickListener listener) {
        this.data = data;
        this.listener = listener;
    }


    public void updateData(List<WeeklyStatistic> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeeklySummaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_amount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklySummaryAdapter.ViewHolder holder, int position) {
        WeeklyStatistic item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeekRange, tvTotalExpense;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeekRange = itemView.findViewById(R.id.textWeekRange);
            tvTotalExpense = itemView.findViewById(R.id.textTotalExpense);


        }

        void bind(WeeklyStatistic item) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M월 d일", Locale.KOREA);

            try {
                Date startDate = inputFormat.parse(item.getStartDate());
                Date endDate = inputFormat.parse(item.getEndDate());

                String formattedRange = outputFormat.format(startDate) + " - " + outputFormat.format(endDate);
                tvWeekRange.setText(formattedRange);
            } catch (ParseException e) {
                e.printStackTrace();
                tvWeekRange.setText("날짜 오류");
            }

            tvTotalExpense.setText(item.getTotalExpense() + "원");
            itemView.setOnClickListener(v -> listener.onWeekClick(item));
        }
    }
}
