package com.example.capstone_donworry.model.statistics;

import java.util.List;

public class ComparisonGroup {
    private Long avgExpense;
    private List<CategoryExpense> topCategories;

    public ComparisonGroup() {}

    public Long getAvgExpense() {
        return avgExpense;
    }

    public List<CategoryExpense> getTopCategories() {
        return topCategories;
    }
}
