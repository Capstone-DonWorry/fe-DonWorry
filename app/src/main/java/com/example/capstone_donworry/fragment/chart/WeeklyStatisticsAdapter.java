package com.example.capstone_donworry.fragment.chart;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.model.statistics.WeeklyStatistic;

import java.util.List;
import java.util.Locale;

public class WeeklyStatisticsAdapter extends RecyclerView.Adapter<WeeklyStatisticsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(WeeklyStatistic statistic);
    }

    private List<WeeklyStatistic> weeklyStatistics;
    private OnItemClickListener listener;

    public WeeklyStatisticsAdapter(List<WeeklyStatistic> weeklyStatistics, OnItemClickListener listener) {
        this.weeklyStatistics = weeklyStatistics;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewColorIndicator;
        TextView textWeekRange, textTotalExpense;

        public ViewHolder(View itemView) {
            super(itemView);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
            textWeekRange = itemView.findViewById(R.id.textWeekRange);
            textTotalExpense = itemView.findViewById(R.id.textTotalExpense);
        }

        public void bind(WeeklyStatistic statistic, OnItemClickListener listener) {
            // 색상은 임의로 주거나 범위에 따라 지정 가능
            viewColorIndicator.setBackgroundColor(Color.parseColor("#FF4081")); // 임시 색상

            String weekRange = statistic.getStartDate() + " ~ " + statistic.getEndDate();
            textWeekRange.setText(weekRange);

            String expenseFormatted = String.format("%,d원", statistic.getTotalExpense());
            textTotalExpense.setText(expenseFormatted);

            itemView.setOnClickListener(v -> listener.onItemClick(statistic));
        }
    }

    @Override
    public WeeklyStatisticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_amount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeeklyStatisticsAdapter.ViewHolder holder, int position) {
        holder.bind(weeklyStatistics.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return weeklyStatistics.size();
    }

    public void setData(List<WeeklyStatistic> data) {
        this.weeklyStatistics = data;
        notifyDataSetChanged();
    }
}
