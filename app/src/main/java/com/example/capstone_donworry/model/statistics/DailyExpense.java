package com.example.capstone_donworry.model.statistics;

public class DailyExpense {
    public String date;  // yyyy-MM-dd
    public Long amount;

    public DailyExpense(String date, long amount) {
        this.date = date;
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}