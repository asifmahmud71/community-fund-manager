package com.fundmanager.models;

public class Member {
    private String memberId;
    private String name;
    private double baseAmount;
    private double totalDeposit;
    private double amountDue;
    private String phone;
    private String address;
    private long joinedDate;

    public Member() {
        // Default constructor required for Firebase
    }

    public Member(String memberId, String name, double baseAmount, double totalDeposit,
                  double amountDue, String phone, String address, long joinedDate) {
        this.memberId = memberId;
        this.name = name;
        this.baseAmount = baseAmount;
        this.totalDeposit = totalDeposit;
        this.amountDue = amountDue;
        this.phone = phone;
        this.address = address;
        this.joinedDate = joinedDate;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(double totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(long joinedDate) {
        this.joinedDate = joinedDate;
    }
}
