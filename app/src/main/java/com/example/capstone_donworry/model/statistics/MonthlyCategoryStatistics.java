package com.example.capstone_donworry.model.statistics;

import java.util.List;

public class MonthlyCategoryStatistics {
    private long totalExpense;
    private long goalAmount;
    private List<CategoryAmount> categoryExpenses;
    private List<PaymentAmount> paymentMethodExpenses;
    public long getTotalExpense() { return totalExpense; }
    public long getGoalAmount() { return goalAmount; }
    public List<CategoryAmount> getCategoryExpenses() { return categoryExpenses; }
    public List<PaymentAmount> getPaymentMethodExpenses() { return paymentMethodExpenses; }
}

