package com.expensetracker.controller;

import com.expensetracker.model.Budget;
import com.expensetracker.model.BudgetAlert;
import com.expensetracker.model.Transaction;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.TransactionManager;
import com.expensetracker.service.TransactionService;
import com.expensetracker.threading.BudgetMonitorThread;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Controller serving the central SaaS Financial Dashboard.
 */
@Controller
public class DashboardController {

    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final TransactionManager<Transaction> transactionManager;
    private final BudgetMonitorThread budgetMonitorThread;

    public DashboardController(TransactionService transactionService,
                               BudgetService budgetService,
                               @Qualifier("oopTransactionManager") TransactionManager<Transaction> transactionManager,
                               BudgetMonitorThread budgetMonitorThread) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.transactionManager = transactionManager;
        this.budgetMonitorThread = budgetMonitorThread;

        // Start background budget monitor thread if not already running
        try {
            if (!budgetMonitorThread.isAlive()) {
                budgetMonitorThread.start();
            }
        } catch (IllegalThreadStateException ignored) {
        }
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        // Trigger real-time budget check
        budgetMonitorThread.checkBudgetAndGenerateAlerts();

        // 1. Core Summary Metrics
        double totalIncome = transactionService.getTotalIncome();
        double totalExpense = transactionService.getTotalExpense();
        double availableBalance = transactionService.getAvailableBalance();
        double monthlySavings = transactionService.getMonthlySavings(currentMonth, currentYear);

        // 2. Budget Metrics
        Map<String, Object> budgetMetrics = budgetService.getCurrentBudgetMetrics();
        Budget budget = (Budget) budgetMetrics.get("budget");
        double monthlyBudget = budget.getTotalLimit();
        double currentSpending = (double) budgetMetrics.get("currentSpending");
        double budgetUtilization = (double) budgetMetrics.get("utilization");
        String alertStatus = (String) budgetMetrics.get("alertStatus");
        String badgeClass = (String) budgetMetrics.get("badgeClass");

        // 3. Recent Transactions
        List<Transaction> recentTransactions = transactionService.getRecentTransactions(6);

        // 4. Chart Data
        Map<String, Object> barChartData = transactionService.getYearlyMonthlyBarChartData(currentYear);
        Map<String, Double> categoryTotals = transactionService.getCategoryWiseExpenseTotals();

        // 5. Budget Alerts from PriorityQueue
        List<BudgetAlert> activeAlerts = transactionManager.peekAllAlerts();

        // Model Bindings
        model.addAttribute("currentDate", today.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("availableBalance", availableBalance);
        model.addAttribute("monthlyBudget", monthlyBudget);
        model.addAttribute("currentSpending", currentSpending);
        model.addAttribute("monthlySavings", monthlySavings);
        model.addAttribute("budgetUtilization", budgetUtilization);
        model.addAttribute("alertStatus", alertStatus);
        model.addAttribute("badgeClass", badgeClass);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("barChartData", barChartData);
        model.addAttribute("categoryTotals", categoryTotals);
        model.addAttribute("activeAlerts", activeAlerts);
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}
