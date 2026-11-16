package com.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing a monthly or category-wise budget constraint.
 * Demonstrates:
 * - Classes and Objects (Section 10.A)
 * - Encapsulation (Section 10.K)
 * - Constructors (Section 10.B)
 * - Wrapper Classes (Integer, Double) (Section 10.T)
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_month", nullable = false)
    private Integer month; // 1 to 12

    @Column(name = "budget_year", nullable = false)
    private Integer year; // e.g. 2026

    @Column(name = "total_limit", nullable = false)
    private Double totalLimit; // Overall monthly limit

    @Column(length = 60)
    private String category; // Specific category or null/"ALL" for overall

    @Column(name = "category_limit")
    private Double categoryLimit;

    /**
     * Default Constructor
     */
    public Budget() {
        LocalDate now = LocalDate.now();
        this.month = now.getMonthValue();
        this.year = now.getYear();
        this.totalLimit = 15000.0;
        this.category = "ALL";
    }

    /**
     * Parameterized Constructor for Overall Monthly Budget
     */
    public Budget(Integer month, Integer year, Double totalLimit) {
        this.month = month;
        this.year = year;
        this.totalLimit = totalLimit;
        this.category = "ALL";
    }

    /**
     * Parameterized Constructor for Category-specific Budget
     */
    public Budget(Integer month, Integer year, Double totalLimit, String category, Double categoryLimit) {
        this.month = month;
        this.year = year;
        this.totalLimit = totalLimit;
        this.category = (category != null && !category.isBlank()) ? category : "ALL";
        this.categoryLimit = categoryLimit;
    }

    // =========================================================================
    // BUSINESS LOGIC METHODS
    // =========================================================================

    /**
     * Calculates budget utilization percentage.
     * Formula: (currentSpending / totalLimit) * 100
     */
    public double calculateUtilization(double currentSpending) {
        if (totalLimit == null || totalLimit <= 0) {
            return 0.0;
        }
        return Math.round(((currentSpending / totalLimit) * 100.0) * 10.0) / 10.0;
    }

    /**
     * Calculates remaining available balance under this budget.
     */
    public double calculateRemaining(double currentSpending) {
        if (totalLimit == null) {
            return 0.0;
        }
        return totalLimit - currentSpending;
    }

    /**
     * Determines status category according to course requirements:
     * - Below 70%: Normal
     * - 70% to 90%: Warning
     * - Above 90% to 100%: Critical
     * - Above 100%: Budget Exceeded
     */
    public String getAlertStatus(double currentSpending) {
        double util = calculateUtilization(currentSpending);
        if (util > 100.0) {
            return "EXCEEDED";
        } else if (util >= 90.0) {
            return "CRITICAL";
        } else if (util >= 70.0) {
            return "WARNING";
        } else {
            return "NORMAL";
        }
    }

    /**
     * Returns appropriate Bootstrap UI color badge
     */
    public String getStatusBadgeClass(double currentSpending) {
        String status = getAlertStatus(currentSpending);
        return switch (status) {
            case "NORMAL" -> "bg-success";
            case "WARNING" -> "bg-warning text-dark";
            case "CRITICAL" -> "bg-danger";
            case "EXCEEDED" -> "bg-dark";
            default -> "bg-secondary";
        };
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getCategoryLimit() {
        return categoryLimit;
    }

    public void setCategoryLimit(Double categoryLimit) {
        this.categoryLimit = categoryLimit;
    }
}
