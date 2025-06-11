package com.example.capstone_donworry.fragment.chart;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.model.statistics.WeeklyStatistic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            viewColorIndicator.setBackgroundColor(Color.parseColor("#FF4081"));

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M월 d일", Locale.KOREA);

            try {
                Date startDate = inputFormat.parse(statistic.getStartDate());
                Date endDate = inputFormat.parse(statistic.getEndDate());

                String weekRange = outputFormat.format(startDate) + " - " + outputFormat.format(endDate);
                textWeekRange.setText(weekRange);
            } catch (ParseException e) {
                e.printStackTrace();
                textWeekRange.setText("날짜 오류");
            }

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
