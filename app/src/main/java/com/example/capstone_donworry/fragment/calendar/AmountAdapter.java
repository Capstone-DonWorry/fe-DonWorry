package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
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

public class AmountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DATE = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    List<Item> items = new ArrayList<>(); // AmountItem, DateItem
    OnItemClickListener listener;

    public static interface OnItemClickListener {
        void onItemClick(ViewHolder holder, View view, int position);
    }

    public AmountAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof DateItem) {
            return TYPE_DATE;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더가 만들어지는 시점에서 호출
        // 아이템을 위한 뷰 홀더가 재사용 될 수 있다면 호출되지 않는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;

        if (viewType == TYPE_DATE) {
            itemView = inflater.inflate(R.layout.recycler_date_text, parent, false);
            return new DateViewHolder(itemView);
        } else {
            itemView = inflater.inflate(R.layout.recycler_amount_item, parent, false);
            return new ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 뷰 홀더가 바인딩되는 시점에 데이터 설정
        if (holder instanceof DateViewHolder) {
            DateItem dateItem = (DateItem) items.get(position);
            ((DateViewHolder) holder).bind(dateItem);

        } else if (holder instanceof ViewHolder) {
            AmountItem amountItem = (AmountItem) items.get(position);
            ((ViewHolder) holder).setItem(amountItem);
            ((ViewHolder) holder).setOnItemClickListener(listener);
        }

    }

    // item 추가
    public void addItem(AmountItem item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void addItemPo(int po, AmountItem item){
        items.add(po, item);
    }
    public void addItems(ArrayList<AmountItem> item){
        items.addAll(item);
        notifyDataSetChanged();
    }

    public void addDateItem(DateItem dateItem) {
        items.add(dateItem);
        notifyDataSetChanged();
    }

    // item 삭제
    public void removeItem(int position) {
        items.remove(position);
    }
    // Date 삭제
    public void removeDateItem(int position) {
        Item item = items.get(position);

        if (item instanceof DateItem) {
            items.remove(position);
            notifyItemRemoved(position);  // RecyclerView에서 해당 항목 제거
        }
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public AmountItem getItem(int position) {
        Item item = items.get(position);
        if (item instanceof AmountItem) {
            return (AmountItem) item;
        } else {
            // 처리할 로직: 예를 들어 null 반환하거나 예외 던지기
            return null;
        }
    }

    public List<AmountItem> getItems() {
        List<AmountItem> amountItems = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof AmountItem) {
                amountItems.add((AmountItem) item);  // AmountItem으로 형변환하여 추가
            }
        }
        return amountItems;
    }

    // 날짜별로 그룹화
    public void updateItems(List<AmountItem> newItems) {
        items.clear();

        if (newItems.isEmpty()) {
            return;
        }
        String lastDate = "";
        for (AmountItem item : newItems) {
            String itemDate = item.getDate();

            if (!itemDate.equals(lastDate)){
                addDateItem(new DateItem(itemDate));
                lastDate = itemDate;
            }
            addItem(item);
        }
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 항목
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemName;
        TextView itemCard;
        TextView itemCategory;
        TextView itemAmount;

        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.ItemName);
            itemCard = (TextView) itemView.findViewById(R.id.ItemCard);
            itemCategory = (TextView) itemView.findViewById(R.id.ItemCategory);
            itemAmount = (TextView) itemView.findViewById(R.id.ItemAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(AmountItem item) {
            itemName.setText(item.getContent());
            itemCard.setText(item.getCard());
            itemCategory.setText(item.getCategory());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            itemAmount.setText(decimalFormat.format(item.getAmount()));
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

    }

    // 날짜
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.DateText);
        }

        public void bind(DateItem dateItem) {
            dateText.setText(dateItem.getDate());
        }
    }
}
