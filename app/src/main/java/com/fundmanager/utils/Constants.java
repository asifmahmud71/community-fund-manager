package com.fundmanager.utils;

/**
 * Application constants for Community Fund Manager
 */
public class Constants {
    
    // Member Management
    public static final int MAX_MEMBERS = 20;
    
    // Request Codes
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Notification Channel
    public static final String NOTIFICATION_CHANNEL_ID = "community_fund_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Community Fund Notifications";
    
    // Firebase Database Paths
    public static final String DB_USERS = "users";
    public static final String DB_MEMBERS = "members";
    public static final String DB_DEPOSITS = "deposits";
    public static final String DB_EXPENSES = "expenses";
    public static final String DB_NOTIFICATIONS = "notifications";
    
    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
