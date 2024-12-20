package com.example.capstone_donworry.fragment.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AmountItem extends Item implements Parcelable {
    private long uid;
    private String content;
    private String date;
    private String card;
    private String bank;
    private String category;
    private int amount;

    public AmountItem() {
        super(Item.TYPE_AMOUNT);
        this.content = "";
        this.date = "";
        this.card = "";
        this.bank = "";
        this.category = "";
        this.amount = 0;
    }

    public AmountItem(String content, String date, String card, String bank, String category, int amount) {
        super(Item.TYPE_AMOUNT);
        this.content = content;
        this.date = date;
        this.card = card;
        this.bank = bank;
        this.category = category;
        this.amount = amount;
    }

    protected AmountItem(Parcel in) {
        super(Item.TYPE_AMOUNT);
        uid = in.readLong();
        content = in.readString();
        date = in.readString();
        card = in.readString();
        bank = in.readString();
        category = in.readString();
        amount = in.readInt();
    }

    public static final Creator<AmountItem> CREATOR = new Creator<AmountItem>() {
        @Override
        public AmountItem createFromParcel(Parcel in) {
            return new AmountItem(in);
        }

        @Override
        public AmountItem[] newArray(int size) {
            return new AmountItem[size];
        }
    };

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(uid);
        parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeString(card);
        parcel.writeString(bank);
        parcel.writeString(category);
        parcel.writeInt(amount);
    }
}