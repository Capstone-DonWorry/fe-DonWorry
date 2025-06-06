package com.example.capstone_donworry.model;

import java.util.Map;

public class MonthlySummary {
    private Long goalAmount;
    private Long totalExpenseAndExpectedExpense;
    private Long remaining;
    private Long cardExpenses;
    private Long  cashExpenses;
    private Map<String, DailySummary> days;

    public Long getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(Long goalAmount) {
        this.goalAmount = goalAmount;
    }

    public Long getTotalExpenseAndExpectedExpense() {
        return totalExpenseAndExpectedExpense;
    }

    public void setTotalExpenseAndExpectedExpense(Long totalExpenseAndExpectedExpense) {
        this.totalExpenseAndExpectedExpense = totalExpenseAndExpectedExpense;
    }

    public Long getRemaining() {
        return remaining;
    }

    public void setRemaining(Long remaining) {
        this.remaining = remaining;
    }

    public Long getCardExpenses() {
        return cardExpenses;
    }

    public void setCardExpenses(Long cardExpenses) {
        this.cardExpenses = cardExpenses;
    }

    public Long getCashExpenses() {
        return cashExpenses;
    }

    public void setCashExpenses(Long cashExpenses) {
        this.cashExpenses = cashExpenses;
    }

    public Map<String, DailySummary> getDays() {
        return days;
    }

    public void setDays(Map<String, DailySummary> days) {
        this.days = days;
    }
}
