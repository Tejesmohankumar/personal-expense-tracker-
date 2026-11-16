package com.expensetracker.model;

import java.time.LocalDateTime;

/**
 * Model representing an automated budget utilization warning.
 * Implements Comparable to enable natural ordering in PriorityQueue (Section 10.X.6).
 * Higher severity alerts appear first in the priority queue.
 */
public class BudgetAlert implements Comparable<BudgetAlert> {

    private final String message;
    private final String severity; // "NORMAL", "WARNING", "CRITICAL", "EXCEEDED"
    private final double utilizationPercent;
    private final int priorityRank; // 1 (Highest: EXCEEDED), 2 (CRITICAL), 3 (WARNING), 4 (NORMAL)
    private final LocalDateTime timestamp;

    public BudgetAlert(String message, String severity, double utilizationPercent) {
        this.message = message;
        this.severity = severity;
        this.utilizationPercent = utilizationPercent;
        this.timestamp = LocalDateTime.now();

        // Assign numeric rank for PriorityQueue ordering
        this.priorityRank = switch (severity.toUpperCase()) {
            case "EXCEEDED" -> 1;
            case "CRITICAL" -> 2;
            case "WARNING" -> 3;
            default -> 4;
        };
    }

    /**
     * Comparable implementation: lower rank number = higher alert priority
     */
    @Override
    public int compareTo(BudgetAlert other) {
        if (other == null) return -1;
        int rankComparison = Integer.compare(this.priorityRank, other.priorityRank);
        if (rankComparison != 0) {
            return rankComparison;
        }
        // If ranks are identical, more recent alert comes first
        return other.timestamp.compareTo(this.timestamp);
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    public double getUtilizationPercent() {
        return utilizationPercent;
    }

    public int getPriorityRank() {
        return priorityRank;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getBadgeClass() {
        return switch (severity.toUpperCase()) {
            case "EXCEEDED" -> "danger";
            case "CRITICAL" -> "danger";
            case "WARNING" -> "warning text-dark";
            default -> "success";
        };
    }

    @Override
    public String toString() {
        return String.format("[%s Alert (%.1f%%)]: %s at %s", 
                severity, utilizationPercent, message, timestamp);
    }
}
