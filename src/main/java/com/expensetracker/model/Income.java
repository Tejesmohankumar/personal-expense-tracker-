package com.expensetracker.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

/**
 * Concrete Subclass representing an Income record.
 * Demonstrates:
 * - Inheritance using extends (Section 10.E)
 * - super keyword to invoke parent constructors (Section 10.E)
 * - Method Overriding of calculateAmount() and displayDetails() (Section 10.F)
 * - Method Overloading with addIncome(...) variants (Section 10.D)
 * - Encapsulation (Section 10.K)
 */
@Entity
@DiscriminatorValue("INCOME")
public class Income extends Transaction {

    /**
     * Default Constructor
     */
    public Income() {
        super();
        this.type = "INCOME";
        this.category = "Salary";
    }

    /**
     * Parameterized Constructor using super keyword
     */
    public Income(String title, Double amount, String category, LocalDate transactionDate, String paymentMethod, String description) {
        super(title, amount, "INCOME", (category != null && !category.isBlank()) ? category : "Other", transactionDate, paymentMethod, description);
    }

    /**
     * Overloaded Constructor with ID
     */
    public Income(Long id, String title, Double amount, String category, LocalDate transactionDate, String paymentMethod, String description) {
        super(id, title, amount, "INCOME", (category != null && !category.isBlank()) ? category : "Other", transactionDate, paymentMethod, description);
    }

    // =========================================================================
    // METHOD OVERRIDING (Section 10.F)
    // =========================================================================

    /**
     * Overrides abstract method calculateAmount()
     * Income positively adds to total financial balance.
     */
    @Override
    public double calculateAmount() {
        return (getAmount() != null) ? getAmount() : 0.0;
    }

    /**
     * Overrides abstract method displayDetails()
     * Demonstrates dynamic method dispatch when invoked on Transaction reference.
     */
    @Override
    public String displayDetails() {
        StringBuffer sbuf = new StringBuffer(); // Demonstrating StringBuffer (Section 10.S)
        sbuf.append("+++ INCOME RECORD +++\n")
            .append("Source/Title: ").append(getTitle()).append("\n")
            .append("Category: ").append(getCategory()).append("\n")
            .append("Amount Credited: ").append(DEFAULT_CURRENCY).append(String.format("%.2f", getAmount())).append("\n")
            .append("Date: ").append(getTransactionDate()).append("\n")
            .append("Payment Mode: ").append(getPaymentMethod()).append("\n")
            .append("Description: ").append(getDescription() != null ? getDescription() : "N/A");
        return sbuf.toString();
    }

    // =========================================================================
    // METHOD OVERLOADING (Section 10.D)
    // =========================================================================

    /**
     * Overloaded helper method 1: Sets amount only
     */
    public void addIncome(double amount) {
        this.setAmount(amount);
        this.setTitle("General Income");
        this.setCategory("Other");
    }

    /**
     * Overloaded helper method 2: Sets amount and source/category
     */
    public void addIncome(double amount, String source) {
        this.setAmount(amount);
        this.setTitle(source);
        this.setCategory(source);
    }

    /**
     * Overloaded helper method 3: Sets amount, source, and description
     */
    public void addIncome(double amount, String source, String description) {
        this.setAmount(amount);
        this.setTitle(source);
        this.setCategory(source);
        this.setDescription(description);
    }
}
