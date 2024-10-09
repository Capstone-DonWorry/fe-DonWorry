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

public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.ViewHolder> {
    Context context;

    ArrayList<AmountItem> items = new ArrayList<AmountItem>();

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

    }

    // item 추가
    public void addItem(AmountItem item){
        items.add(item);
    }

    public void addItems(ArrayList<AmountItem> items) {
        this.items = items;
    }

    public AmountItem getItem(int position) {
        return items.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemName;
        TextView itemCard;
        TextView itemCategory;
        TextView itemAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.ItemName);
            itemCard = (TextView) itemView.findViewById(R.id.ItemCard);
            itemCategory = (TextView) itemView.findViewById(R.id.ItemCategory);
            itemAmount = (TextView) itemView.findViewById(R.id.ItemAmount);
        }

        public void setItem(AmountItem item) {
            itemName.setText(item.getName());
            itemCard.setText(item.getCard());
            itemCategory.setText(item.getCategory());
            itemAmount.setText(item.getAmount());
        }
    }
}
