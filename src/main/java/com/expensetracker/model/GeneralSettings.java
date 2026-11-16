package com.expensetracker.model;

/**
 * General Settings and User Preferences Model.
 * Represents user profile, currency formatting, budget alert thresholds, and system preferences.
 */
public class GeneralSettings {

    private String userName;
    private String userEmail;
    private String currencySymbol;
    private String currencyCode;
    private String dateFormat;
    private String defaultPaymentMethod;
    private Double monthlySavingsTarget;
    private Double warningThresholdPercent;
    private Double criticalThresholdPercent;
    private Boolean enableBudgetAlerts;
    private Boolean enableWeeklySummary;

    // Default Constructor
    public GeneralSettings() {
        this.userName = "Alex Morgan";
        this.userEmail = "alex.morgan@example.com";
        this.currencySymbol = "₹";
        this.currencyCode = "INR";
        this.dateFormat = "dd MMM yyyy";
        this.defaultPaymentMethod = "UPI";
        this.monthlySavingsTarget = 5000.0;
        this.warningThresholdPercent = 70.0;
        this.criticalThresholdPercent = 90.0;
        this.enableBudgetAlerts = true;
        this.enableWeeklySummary = false;
    }

    // Parameterized Constructor
    public GeneralSettings(String userName, String userEmail, String currencySymbol, String currencyCode,
                           String dateFormat, String defaultPaymentMethod, Double monthlySavingsTarget,
                           Double warningThresholdPercent, Double criticalThresholdPercent,
                           Boolean enableBudgetAlerts, Boolean enableWeeklySummary) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.dateFormat = dateFormat;
        this.defaultPaymentMethod = defaultPaymentMethod;
        this.monthlySavingsTarget = monthlySavingsTarget;
        this.warningThresholdPercent = warningThresholdPercent;
        this.criticalThresholdPercent = criticalThresholdPercent;
        this.enableBudgetAlerts = enableBudgetAlerts;
        this.enableWeeklySummary = enableWeeklySummary;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDefaultPaymentMethod() {
        return defaultPaymentMethod;
    }

    public void setDefaultPaymentMethod(String defaultPaymentMethod) {
        this.defaultPaymentMethod = defaultPaymentMethod;
    }

    public Double getMonthlySavingsTarget() {
        return monthlySavingsTarget;
    }

    public void setMonthlySavingsTarget(Double monthlySavingsTarget) {
        this.monthlySavingsTarget = monthlySavingsTarget;
    }

    public Double getWarningThresholdPercent() {
        return warningThresholdPercent;
    }

    public void setWarningThresholdPercent(Double warningThresholdPercent) {
        this.warningThresholdPercent = warningThresholdPercent;
    }

    public Double getCriticalThresholdPercent() {
        return criticalThresholdPercent;
    }

    public void setCriticalThresholdPercent(Double criticalThresholdPercent) {
        this.criticalThresholdPercent = criticalThresholdPercent;
    }

    public Boolean getEnableBudgetAlerts() {
        return enableBudgetAlerts != null ? enableBudgetAlerts : false;
    }

    public void setEnableBudgetAlerts(Boolean enableBudgetAlerts) {
        this.enableBudgetAlerts = enableBudgetAlerts;
    }

    public Boolean getEnableWeeklySummary() {
        return enableWeeklySummary != null ? enableWeeklySummary : false;
    }

    public void setEnableWeeklySummary(Boolean enableWeeklySummary) {
        this.enableWeeklySummary = enableWeeklySummary;
    }
}
