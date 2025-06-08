package com.example.capstone_donworry.model.statistics;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeeklyDetail {
    @SerializedName("dailyExpenses")
    public List<DailyExpense> dailyAmounts;

    @SerializedName("paymentExpenses")
    public List<PaymentExpense> paymentAmounts;
}

