package com.example.capstone_donworry.fragment.calendar;

import java.io.Serializable;

public class AmountItem implements Serializable {
    String name;
    String date;
    String card;
    String bank;
    String category;
    String amount;

    public AmountItem(String name, String date, String card, String bank, String category, String amount) {
        this.name = name;
        this.date = date;
        this.card = card;
        this.bank = bank;
        this.category = category;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}