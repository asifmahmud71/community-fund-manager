package com.fundmanager.models;

public class Deposit {
    private String depositId;
    private String memberId;
    private String memberName;
    private double amount;
    private String month; // Format: "MMM-yy" (e.g., "Jan-26")
    private long depositDate;
    private String addedBy; // userId

    public Deposit() {
        // Default constructor required for Firebase
    }

    public Deposit(String depositId, String memberId, String memberName, double amount,
                   String month, long depositDate, String addedBy) {
        this.depositId = depositId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.amount = amount;
        this.month = month;
        this.depositDate = depositDate;
        this.addedBy = addedBy;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(long depositDate) {
        this.depositDate = depositDate;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
