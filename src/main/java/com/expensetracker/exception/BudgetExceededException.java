package com.expensetracker.exception;

/**
 * Custom Unchecked (Runtime) Exception thrown when spending surpasses the defined budget limit.
 * Demonstrates:
 * - User-defined unchecked exception extending RuntimeException
 * - Encapsulation of budget details (budget limit, current expense)
 */
public class BudgetExceededException extends RuntimeException {
    
    private final double budgetLimit;
    private final double totalExpense;

    public BudgetExceededException(String message) {
        super(message);
        this.budgetLimit = 0.0;
        this.totalExpense = 0.0;
    }

    public BudgetExceededException(String message, double budgetLimit, double totalExpense) {
        super(String.format("%s [Limit: ₹%.2f, Total Spending: ₹%.2f, Exceeded by: ₹%.2f]", 
                message, budgetLimit, totalExpense, (totalExpense - budgetLimit)));
        this.budgetLimit = budgetLimit;
        this.totalExpense = totalExpense;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public double getTotalExpense() {
        return totalExpense;
    }
}
