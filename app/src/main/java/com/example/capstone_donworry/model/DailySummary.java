package com.example.capstone_donworry.model;

import java.util.List;

public class DailySummary {
    private Long dailyTotalExpense;
    private Long dailyTotalExpectedExpense;
    private Long dailyGoal;
    private int dailyLevel;
    private List<Expense> dailyExpenseList;
    private List<ExpectedExpense> dailyExpectedList;

    public Long getDailyTotalExpense() {
        return dailyTotalExpense;
    }

    public void setDailyTotalExpense(Long dailyTotalExpense) {
        this.dailyTotalExpense = dailyTotalExpense;
    }

    public Long getDailyTotalExpectedExpense() {
        return dailyTotalExpectedExpense;
    }

    public void setDailyTotalExpectedExpense(Long dailyTotalExpectedExpense) {
        this.dailyTotalExpectedExpense = dailyTotalExpectedExpense;
    }

    public Long getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(Long dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public List<Expense> getDailyExpenseList() {
        return dailyExpenseList;
    }

    public void setDailyExpenseList(List<Expense> dailyExpenseList) {
        this.dailyExpenseList = dailyExpenseList;
    }

    public List<ExpectedExpense> getDailyExpectedList() {
        return dailyExpectedList;
    }

    public void setDailyExpectedList(List<ExpectedExpense> dailyExpectedList) {
        this.dailyExpectedList = dailyExpectedList;
    }

    public int getDailyLevel() {
        return dailyLevel;
    }

    public void setDailyLevel(int dailyLevel) {
        this.dailyLevel = dailyLevel;
    }
}
