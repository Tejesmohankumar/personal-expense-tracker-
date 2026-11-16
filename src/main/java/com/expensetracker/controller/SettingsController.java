package com.expensetracker.controller;

import com.expensetracker.model.GeneralSettings;
import com.expensetracker.service.AppSettingsService;
import com.expensetracker.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for Managing Application & User General Settings.
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final AppSettingsService appSettingsService;
    private final TransactionService transactionService;

    public SettingsController(AppSettingsService appSettingsService,
                              TransactionService transactionService) {
        this.appSettingsService = appSettingsService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String showSettingsPage(Model model) {
        GeneralSettings settings = appSettingsService.getSettings();

        model.addAttribute("settings", settings);
        model.addAttribute("totalTransactions", transactionService.getAllTransactions().size());
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("javaVendor", System.getProperty("java.vendor"));
        model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("activePage", "settings");

        return "settings";
    }

    @PostMapping("/update")
    public String updateSettings(@ModelAttribute GeneralSettings updatedSettings,
                                 RedirectAttributes redirectAttributes) {
        appSettingsService.updateSettings(updatedSettings);
        redirectAttributes.addFlashAttribute("successMessage", "General settings and preferences saved successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/reset-sample-data")
    public String resetSampleData(RedirectAttributes redirectAttributes) {
        transactionService.resetToSampleData();
        redirectAttributes.addFlashAttribute("successMessage", "Default sample transactions and budgets restored successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/clear-all-data")
    public String clearAllData(RedirectAttributes redirectAttributes) {
        transactionService.clearAllTransactions();
        redirectAttributes.addFlashAttribute("warningMessage", "All transactions cleared. Your ledger is now completely clean.");
        return "redirect:/settings";
    }
}
