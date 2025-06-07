package com.example.capstone_donworry.model.statistics;

public class PaymentAmount {
    private String paymentMethod;
    private long totalAmount;

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { this.totalAmount = totalAmount; }
}