package com.example.capstone_donworry.model.statistics;

import java.util.List;

public class ComparisonStatistics {
    private Long myTotalExpense;
    private List<CategoryExpense> myTopCategories;

    private ComparisonGroup byGoalAmount;
    private ComparisonGroup byAgeGroup;

    private Long myMonthExpenseGoal;
    private String ageGroup;

    // 기본 생성자 (Gson에서 필요)
    public ComparisonStatistics() {}

    public Long getMyTotalExpense() {
        return myTotalExpense;
    }

    public List<CategoryExpense> getMyTopCategories() {
        return myTopCategories;
    }

    public ComparisonGroup getByGoalAmount() {
        return byGoalAmount;
    }

    public ComparisonGroup getByAgeGroup() {
        return byAgeGroup;
    }

    public Long getMyMonthExpenseGoal() {
        return myMonthExpenseGoal;
    }

    public String getAgeGroup() {
        return ageGroup;
    }
}
