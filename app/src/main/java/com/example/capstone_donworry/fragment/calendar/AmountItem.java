package com.example.capstone_donworry.fragment.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AmountItem implements Parcelable {
    String content;
    String date;
    String card;
    String bank;
    String category;
    String amount;

    public AmountItem() {
        this.content = "";
        this.date = "";
        this.card = "";
        this.bank = "";
        this.category = "";
        this.amount = "";
    }

    public AmountItem(String content, String date, String card, String bank, String category, String amount) {
        this.content = content;
        this.date = date;
        this.card = card;
        this.bank = bank;
        this.category = category;
        this.amount = amount;
    }

    protected AmountItem(Parcel in) {
        content = in.readString();
        date = in.readString();
        card = in.readString();
        bank = in.readString();
        category = in.readString();
        amount = in.readString();
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeString(card);
        parcel.writeString(bank);
        parcel.writeString(category);
        parcel.writeString(amount);
    }
}