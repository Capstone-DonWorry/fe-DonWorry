package com.example.capstone_donworry.fragment.chart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;
import com.example.capstone_donworry.model.statistics.CategoryAmount;

import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryAmount> categoryList;

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView amount;
        View colorIndicator;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textCategory);
            amount = itemView.findViewById(R.id.textAmount);
            colorIndicator = itemView.findViewById(R.id.viewColorIndicator);
        }
    }

    public CategoryAdapter(List<CategoryAmount> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_amount, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryAmount item = categoryList.get(position);
        holder.categoryName.setText(item.getCategory());
        holder.amount.setText(String.format(Locale.getDefault(), "%,d원", item.getTotalAmount()));

        // 색상 배열
        int[] categoryColors = {
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart1),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart2),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart3),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart4),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart5),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart6),
                ContextCompat.getColor(holder.itemView.getContext(), R.color.chart7)
        };

        int color = categoryColors[position % categoryColors.length];
        holder.colorIndicator.getBackground().setTint(color);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateData(List<CategoryAmount> newData) {
        categoryList = newData;
        notifyDataSetChanged();
    }

    public void setCategoryList(List<CategoryAmount> newList) {
        this.categoryList = newList;
    }
}
