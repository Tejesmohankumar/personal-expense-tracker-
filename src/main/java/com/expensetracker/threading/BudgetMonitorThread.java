package com.expensetracker.threading;

import com.expensetracker.model.Budget;
import com.expensetracker.model.BudgetAlert;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.service.TransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Background Thread continuously monitoring current monthly expenditure against budget limits.
 * Demonstrates:
 * - Multithreading by extending Thread (Section 10.N.1)
 * - Thread lifecycle & sleep()
 * - Priority Queue alert dispatching
 */
@Component
public class BudgetMonitorThread extends Thread {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionManager<Transaction> transactionManager;
    private volatile boolean running = true;

    public BudgetMonitorThread(TransactionRepository transactionRepository,
                               BudgetRepository budgetRepository,
                               @Qualifier("oopTransactionManager") TransactionManager<Transaction> transactionManager) {
        super("Budget-Monitor-Worker");
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.transactionManager = transactionManager;
        setDaemon(true); // Daemon thread so JVM exits cleanly
    }

    @Override
    public void run() {
        System.out.println("[BudgetMonitorThread] Started background budget monitoring service.");
        while (running) {
            try {
                checkBudgetAndGenerateAlerts();
                // Check once every 30 seconds in background
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                System.out.println("[BudgetMonitorThread] Thread interrupted, terminating gracefully.");
                break;
            } catch (Exception ex) {
                System.err.println("[BudgetMonitorThread] Error in budget monitoring cycle: " + ex.getMessage());
            }
        }
    }

    /**
     * Inspects current month's expenses vs budget and issues prioritized alert
     */
    public void checkBudgetAndGenerateAlerts() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        Double monthlyExpense = transactionRepository.sumMonthlyExpense(month, year);
        if (monthlyExpense == null) {
            monthlyExpense = 0.0;
        }

        Optional<Budget> budgetOpt = budgetRepository.findFirstByMonthAndYearAndCategory(month, year, "ALL");
        if (budgetOpt.isPresent()) {
            Budget budget = budgetOpt.get();
            double totalLimit = budget.getTotalLimit();
            double util = budget.calculateUtilization(monthlyExpense);
            String status = budget.getAlertStatus(monthlyExpense);

            String message;
            if ("EXCEEDED".equals(status)) {
                message = String.format("Budget Limit Exceeded! Spending: ₹%.2f / ₹%.2f (%.1f%%). Immediate spending control required!",
                        monthlyExpense, totalLimit, util);
            } else if ("CRITICAL".equals(status)) {
                message = String.format("Critical Budget Warning: You have reached %.1f%% of your ₹%.2f budget.", util, totalLimit);
            } else if ("WARNING".equals(status)) {
                message = String.format("Caution: %.1f%% of monthly budget utilized (₹%.2f of ₹%.2f).", util, monthlyExpense, totalLimit);
            } else {
                message = String.format("Spending is healthy: %.1f%% utilized (Remaining: ₹%.2f).", util, (totalLimit - monthlyExpense));
            }

            BudgetAlert alert = new BudgetAlert(message, status, util);
            transactionManager.enqueueBudgetAlert(alert);
        }
    }

    public void stopMonitoring() {
        this.running = false;
        interrupt();
    }
}
