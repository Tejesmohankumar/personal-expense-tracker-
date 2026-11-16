package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstract Parent Class representing a financial transaction.
 * Demonstrates the following OOP Concepts:
 * - Abstract Class & Abstract Methods (Section 10.A, 10.H)
 * - Encapsulation with private and protected members (Section 10.K)
 * - Static Variable & Static Methods (Transaction Counter) (Section 10.C)
 * - Final Keyword for Constants (Section 10.L)
 * - Constructor Overloading (Default & Parameterized) (Section 10.B)
 * - Dynamic Method Dispatch & Method Overriding (Section 10.F, 10.G)
 */
@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "nature", discriminatorType = DiscriminatorType.STRING)
public abstract class Transaction {

    // Final constant defining the application standard currency symbol
    public static final String DEFAULT_CURRENCY = "₹";

    // Static counter tracking the total number of transaction objects instantiated during runtime
    private static int transactionCounter = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false)
    private Double amount;

    // Protected field accessible by subclasses (Income & Expense) and within package
    @Column(name = "type", nullable = false, length = 20)
    protected String type; // "INCOME" or "EXPENSE"

    @Column(length = 60)
    protected String category; // e.g. "Salary", "Food", "Rent"

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "payment_method", length = 40)
    private String paymentMethod; // "Cash", "UPI", "Credit Card", "Bank Transfer"

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default Constructor (Required by JPA / Hibernate)
     * Demonstrates Static Counter increment
     */
    public Transaction() {
        incrementCounter();
        this.transactionDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.paymentMethod = "Cash";
    }

    /**
     * Parameterized Constructor (Overloaded)
     */
    public Transaction(String title, Double amount, String type, String category, LocalDate transactionDate, String paymentMethod, String description) {
        incrementCounter();
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDate.now();
        this.paymentMethod = (paymentMethod != null && !paymentMethod.isBlank()) ? paymentMethod : "Cash";
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Overloaded Constructor with ID
     */
    public Transaction(Long id, String title, Double amount, String type, String category, LocalDate transactionDate, String paymentMethod, String description) {
        this(title, amount, type, category, transactionDate, paymentMethod, description);
        this.id = id;
    }

    /**
     * Synchronized static helper method to increment transaction counter in a thread-safe manner
     */
    private static synchronized void incrementCounter() {
        transactionCounter++;
    }

    /**
     * Static method to retrieve the total transaction objects instantiated
     */
    public static int getTransactionCounter() {
        return transactionCounter;
    }

    /**
     * Static method to reset transaction counter (useful for unit testing and diagnostic benchmarking)
     */
    public static synchronized void resetTransactionCounter() {
        transactionCounter = 0;
    }

    // =========================================================================
    // ABSTRACT METHODS (Must be implemented by concrete subclasses)
    // =========================================================================

    /**
     * Abstract method to calculate the net financial impact.
     * Income returns positive amount; Expense returns negative or net reduction.
     */
    public abstract double calculateAmount();

    /**
     * Abstract method to display formatted details of the transaction.
     * Demonstrates polymorphic behavior when called via parent references.
     */
    public abstract String displayDetails();

    // =========================================================================
    // CONCRETE METHODS & STRING HANDLING (Section 10.S)
    // =========================================================================

    /**
     * Demonstrates StringBuilder for string manipulation
     */
    public String getTransactionSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.type).append("] ")
          .append(this.title).append(" - ")
          .append(DEFAULT_CURRENCY).append(String.format("%.2f", this.amount))
          .append(" on ").append(this.transactionDate);
        return sb.toString();
    }

    // =========================================================================
    // GETTERS AND SETTERS (Encapsulation)
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction [ID=" + id + ", Title=" + title + ", Amount=" + amount +
               ", Type=" + type + ", Category=" + category + ", Date=" + transactionDate + "]";
    }
}
