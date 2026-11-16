package com.expensetracker.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

/**
 * Concrete Subclass representing an Expense record.
 * Demonstrates:
 * - Inheritance using extends (Section 10.E)
 * - super keyword to initialize parent properties (Section 10.E)
 * - Method Overriding of calculateAmount() and displayDetails() (Section 10.F)
 * - Method Overloading with addExpense(...) variants (Section 10.D)
 * - Encapsulation (Section 10.K)
 */
@Entity
@DiscriminatorValue("EXPENSE")
public class Expense extends Transaction {

    /**
     * Default Constructor
     */
    public Expense() {
        super();
        this.type = "EXPENSE";
        this.category = "Food";
    }

    /**
     * Parameterized Constructor using super keyword
     */
    public Expense(String title, Double amount, String category, LocalDate transactionDate, String paymentMethod, String description) {
        super(title, amount, "EXPENSE", (category != null && !category.isBlank()) ? category : "Others", transactionDate, paymentMethod, description);
    }

    /**
     * Overloaded Constructor with ID
     */
    public Expense(Long id, String title, Double amount, String category, LocalDate transactionDate, String paymentMethod, String description) {
        super(id, title, amount, "EXPENSE", (category != null && !category.isBlank()) ? category : "Others", transactionDate, paymentMethod, description);
    }

    // =========================================================================
    // METHOD OVERRIDING (Section 10.F)
    // =========================================================================

    /**
     * Overrides abstract method calculateAmount()
     * Expense negatively impacts total financial balance.
     */
    @Override
    public double calculateAmount() {
        return (getAmount() != null) ? -getAmount() : 0.0;
    }

    /**
     * Overrides abstract method displayDetails()
     * Demonstrates dynamic method dispatch when called via parent reference Transaction.
     */
    @Override
    public String displayDetails() {
        StringBuffer sbuf = new StringBuffer(); // Demonstrating StringBuffer (Section 10.S)
        sbuf.append("--- EXPENSE RECORD ---\n")
            .append("Expense Title: ").append(getTitle()).append("\n")
            .append("Category: ").append(getCategory()).append("\n")
            .append("Amount Debited: ").append(DEFAULT_CURRENCY).append(String.format("%.2f", getAmount())).append("\n")
            .append("Date: ").append(getTransactionDate()).append("\n")
            .append("Payment Method: ").append(getPaymentMethod()).append("\n")
            .append("Description: ").append(getDescription() != null ? getDescription() : "N/A");
        return sbuf.toString();
    }

    // =========================================================================
    // METHOD OVERLOADING (Section 10.D)
    // =========================================================================

    /**
     * Overloaded helper method 1: Sets amount only
     */
    public void addExpense(double amount) {
        this.setAmount(amount);
        this.setTitle("Daily Expense");
        this.setCategory("Others");
    }

    /**
     * Overloaded helper method 2: Sets amount and category
     */
    public void addExpense(double amount, String category) {
        this.setAmount(amount);
        this.setTitle(category + " Expense");
        this.setCategory(category);
    }

    /**
     * Overloaded helper method 3: Sets amount, category, and description
     */
    public void addExpense(double amount, String category, String description) {
        this.setAmount(amount);
        this.setTitle(category + " Expense");
        this.setCategory(category);
        this.setDescription(description);
    }
}
