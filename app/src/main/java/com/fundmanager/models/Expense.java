package com.fundmanager.models;

public class Expense {
    private String expenseId;
    private String category;
    private String description;
    private double amount;
    private long expenseDate;
    private String addedBy; // userId

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String expenseId, String category, String description, double amount,
                   long expenseDate, String addedBy) {
        this.expenseId = expenseId;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.addedBy = addedBy;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(long expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
