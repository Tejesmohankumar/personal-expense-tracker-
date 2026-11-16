package com.expensetracker.service;

import com.expensetracker.model.GeneralSettings;
import org.springframework.stereotype.Service;

/**
 * Service managing user settings and application configuration.
 */
@Service
public class AppSettingsService {

    private final GeneralSettings settings = new GeneralSettings();

    public synchronized GeneralSettings getSettings() {
        return settings;
    }

    public synchronized void updateSettings(GeneralSettings updated) {
        if (updated.getUserName() != null && !updated.getUserName().trim().isEmpty()) {
            settings.setUserName(updated.getUserName().trim());
        }
        if (updated.getUserEmail() != null && !updated.getUserEmail().trim().isEmpty()) {
            settings.setUserEmail(updated.getUserEmail().trim());
        }
        if (updated.getCurrencySymbol() != null && !updated.getCurrencySymbol().trim().isEmpty()) {
            settings.setCurrencySymbol(updated.getCurrencySymbol().trim());
        }
        if (updated.getCurrencyCode() != null && !updated.getCurrencyCode().trim().isEmpty()) {
            settings.setCurrencyCode(updated.getCurrencyCode().trim());
        }
        if (updated.getDateFormat() != null && !updated.getDateFormat().trim().isEmpty()) {
            settings.setDateFormat(updated.getDateFormat().trim());
        }
        if (updated.getDefaultPaymentMethod() != null && !updated.getDefaultPaymentMethod().trim().isEmpty()) {
            settings.setDefaultPaymentMethod(updated.getDefaultPaymentMethod().trim());
        }
        if (updated.getMonthlySavingsTarget() != null && updated.getMonthlySavingsTarget() >= 0) {
            settings.setMonthlySavingsTarget(updated.getMonthlySavingsTarget());
        }
        if (updated.getWarningThresholdPercent() != null && updated.getWarningThresholdPercent() > 0) {
            settings.setWarningThresholdPercent(updated.getWarningThresholdPercent());
        }
        if (updated.getCriticalThresholdPercent() != null && updated.getCriticalThresholdPercent() > 0) {
            settings.setCriticalThresholdPercent(updated.getCriticalThresholdPercent());
        }
        settings.setEnableBudgetAlerts(Boolean.TRUE.equals(updated.getEnableBudgetAlerts()));
        settings.setEnableWeeklySummary(Boolean.TRUE.equals(updated.getEnableWeeklySummary()));
    }
}
