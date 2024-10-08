package com.example.capstone_donworry.fragment.calendar;

public class AmountItem {
    String name;
    String card;
    String category;
    Integer amount;

    public AmountItem(String name, String card, String category, Integer amount) {
        this.name = name;
        this.card = card;
        this.category = category;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AmountItem{" +
                "name='" + name + '\'' +
                ", card='" + card + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                '}';
    }
}
