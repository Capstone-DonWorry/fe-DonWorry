package com.example.capstone_donworry.fragment.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_donworry.R;

import java.util.ArrayList;
import java.util.List;

public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.ViewHolder> {
    Context context;

    List<AmountItem> items = new ArrayList<AmountItem>();

    OnItemClickListener listener;
    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public AmountAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더가 만들어지는 시점에서 호출
        // 아이템을 위한 뷰 홀더가 재사용 될 수 있다면 호출되지 않는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.recycler_amount_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 뷰 홀더가 바인딩되는 시점에 데이터 설정
        AmountItem item = items.get(position);
        holder.setItem(item);

        holder.setOnItemClickListener(listener);
    }

    // item 추가
    public void addItem(AmountItem item){
        items.add(item);
    }
    public void addItemPo(int po, AmountItem item){
        items.add(po, item);
    }
    public void addItems(ArrayList<AmountItem> item){
        items.addAll(item);
        notifyDataSetChanged();
    }

    // item 삭제
    public void removeItem(int position) {
        items.remove(position);
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public AmountItem getItem(int position) {
        return items.get(position);
    }

    public List<AmountItem> getItems() {
        return items;
    }

    public void updateItems(List<AmountItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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
            itemAmount.setText(item.getAmount());
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }
}
