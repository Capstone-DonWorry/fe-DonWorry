package com.example.capstone_donworry.fragment.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AmountExpectedItem extends Item implements Parcelable {
    private long id;
    private String content;
    private long amount;

    public AmountExpectedItem() {
        super(Item.TYPE_AMOUNT, "");
        this.id = 0;
        this.content = "";
        this.amount = 0;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public AmountExpectedItem(String content, long amount, String date) {
        super(Item.TYPE_EXPECTED_AMOUNT, date);
        this.content = content;
        this.amount = amount;
    }

    protected AmountExpectedItem(Parcel in) {
        super(Item.TYPE_EXPECTED_AMOUNT, in.readString());
        id = in.readLong();
        content = in.readString();
        amount = in.readLong();
    }

    public static final Creator<AmountExpectedItem> CREATOR = new Creator<AmountExpectedItem>() {
        @Override
        public AmountExpectedItem createFromParcel(Parcel in) {
            return new AmountExpectedItem(in);
        }

        @Override
        public AmountExpectedItem[] newArray(int size) {
            return new AmountExpectedItem[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(content);
        parcel.writeLong(amount);
    }

    @Override
    public int getType() {
        return Item.TYPE_EXPECTED_AMOUNT;
    }
}
