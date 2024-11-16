package com.example.capstone_donworry.fragment.calendar;

public class DateItem extends Item{
    private String date;

    public DateItem(String date) {
        super(Item.TYPE_AMOUNT);
        this.date = date;
    }

    public String getDate() {
        String[] dateEdit = date.split("-");
        String dateText = dateEdit[1] + "/" + dateEdit[2];
        return dateText;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
