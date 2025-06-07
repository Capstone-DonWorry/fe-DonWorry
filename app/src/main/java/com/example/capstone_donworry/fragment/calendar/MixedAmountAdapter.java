package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MixedAmountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 1;
    private static final int TYPE_AMOUNT = 0;
    private static final int TYPE_EXPECTED_AMOUNT = 2;

    private final Context context;
    private final List<Item> items = new ArrayList<>();
    private OnItemClickListener listener;

    public MixedAmountAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int type = items.get(position).getType();
        Log.d("ITEM_TYPE_CHECK", "position: " + position + ", type: " + type);
        return type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;

        if (viewType == TYPE_DATE) {
            itemView = inflater.inflate(R.layout.recycler_date_text, parent, false);
            return new MixedDateViewHolder(itemView);
        } else if (viewType == TYPE_EXPECTED_AMOUNT) {
            itemView = inflater.inflate(R.layout.recycler_expected_item, parent, false);
            return new MixedExpectedViewHolder(itemView);
        } else {
            itemView = inflater.inflate(R.layout.recycler_amount_item, parent, false);
            return new MixedAmountViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        if (holder instanceof MixedAmountViewHolder && item instanceof AmountItem) {
            ((MixedAmountViewHolder) holder).bind((AmountItem) item);
        } else if (holder instanceof MixedExpectedViewHolder && item instanceof AmountExpectedItem) {
            ((MixedExpectedViewHolder) holder).bind((AmountExpectedItem) item);
            Log.d("BIND", "예상 지출 bind: " + ((AmountExpectedItem) item).getContent());
        } else if (holder instanceof MixedDateViewHolder && item instanceof DateItem) {
            ((MixedDateViewHolder) holder).bind((DateItem) item);
        } else{
            Log.d("BIND", "예상 실패");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder, v, position);
            }
        });
    }

    public void updateItems(List<Item> newItems) {
        items.clear();

        Log.d("DEBUG_ADAPTER", "newItems size = " + newItems.size());

        String lastDate = "";
        for (Item item : newItems) {
            String itemDate = item.getDate();
            if (!itemDate.equals(lastDate)) {
                items.add(new DateItem(itemDate));
                lastDate = itemDate;
            }
            items.add(item);
            Log.d("DEBUG_ADAPTER", "added item = " + item.getClass().getSimpleName());
        }

        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    static class MixedAmountViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCard, itemCategory, itemAmount;

        public MixedAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.ItemName);
            itemCard = itemView.findViewById(R.id.ItemCard);
            itemCategory = itemView.findViewById(R.id.ItemCategory);
            itemAmount = itemView.findViewById(R.id.ItemAmount);
        }

        public void bind(AmountItem item) {
            itemName.setText(item.getContent());
            itemCard.setText(item.getCard());
            itemCategory.setText(item.getCategory());
            itemAmount.setText(new DecimalFormat("#,###").format(item.getAmount()));
        }
    }

    static class MixedExpectedViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemAmount;

        public MixedExpectedViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.ExpectedItemName);
            itemAmount = itemView.findViewById(R.id.ExpectedItemAmount);
        }

        public void bind(AmountExpectedItem item) {
            Log.d("BIND", item.getContent());
            itemName.setText(item.getContent());
            itemAmount.setText(new DecimalFormat("#,###").format(item.getAmount()));
        }
    }

    static class MixedDateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        public MixedDateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.DateText);
        }

        public void bind(DateItem item) {
            dateText.setText(item.getDate());
        }
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

}