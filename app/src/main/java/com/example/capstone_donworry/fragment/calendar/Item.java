package com.example.capstone_donworry.fragment.calendar;

public abstract class Item {
    public static final int TYPE_AMOUNT = 0;
    public static final int TYPE_DATE = 1;
    public static final int TYPE_EXPECTED_AMOUNT = 2;

    private int type;
    protected String date;

    public Item(int type, String date) {
        this.type = type;
        this.date = date;
    }

    public abstract int getType();

    public String getDate() {
        return date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }
}