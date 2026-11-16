package com.expensetracker.service;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service managing monthly spending limits, thresholds, and warning status evaluations.
 */
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets or creates the current month's overall budget
     */
    public Budget getCurrentMonthlyBudget() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        Optional<Budget> opt = budgetRepository.findFirstByMonthAndYearAndCategory(month, year, "ALL");
        if (opt.isPresent()) {
            return opt.get();
        }

        // Create default if not found
        Budget defaultBudget = new Budget(month, year, 15000.0);
        return budgetRepository.save(defaultBudget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAllByOrderByYearDescMonthDesc();
    }

    @Transactional
    public Budget setMonthlyBudget(int month, int year, double totalLimit) {
        Optional<Budget> existing = budgetRepository.findFirstByMonthAndYearAndCategory(month, year, "ALL");
        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setTotalLimit(totalLimit);
        } else {
            budget = new Budget(month, year, totalLimit);
        }
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget setCategoryBudget(int month, int year, String category, double categoryLimit) {
        Optional<Budget> existing = budgetRepository.findFirstByMonthAndYearAndCategory(month, year, category);
        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setCategoryLimit(categoryLimit);
        } else {
            budget = new Budget(month, year, 0.0, category, categoryLimit);
        }
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    /**
     * Calculates comprehensive budget metrics for the current month
     */
    public Map<String, Object> getCurrentBudgetMetrics() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        Budget budget = getCurrentMonthlyBudget();
        double totalLimit = budget.getTotalLimit();

        Double monthlyExpense = transactionRepository.sumMonthlyExpense(month, year);
        if (monthlyExpense == null) {
            monthlyExpense = 0.0;
        }

        double remaining = budget.calculateRemaining(monthlyExpense);
        double utilization = budget.calculateUtilization(monthlyExpense);
        String alertStatus = budget.getAlertStatus(monthlyExpense);
        String badgeClass = budget.getStatusBadgeClass(monthlyExpense);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("budget", budget);
        metrics.put("totalLimit", totalLimit);
        metrics.put("currentSpending", monthlyExpense);
        metrics.put("remaining", remaining);
        metrics.put("utilization", utilization);
        metrics.put("alertStatus", alertStatus);
        metrics.put("badgeClass", badgeClass);
        metrics.put("month", month);
        metrics.put("year", year);

        return metrics;
    }
}
