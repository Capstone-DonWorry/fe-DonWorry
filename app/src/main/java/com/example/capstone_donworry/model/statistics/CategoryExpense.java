package com.example.capstone_donworry.model.statistics;

public class CategoryExpense {
    private String category; // enum으로 받을 수도 있지만, 서버에서 문자열로 올 가능성 있음
    private Long totalAmount;

    public CategoryExpense() {}

    public String getCategory() {
        return category;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
