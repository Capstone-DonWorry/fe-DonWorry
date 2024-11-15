package com.example.capstone_donworry.fragment.calendar;

public class Item {
    public static final int TYPE_AMOUNT = 0;
    public static final int TYPE_DATE = 1;

    private int type;

    public Item(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}