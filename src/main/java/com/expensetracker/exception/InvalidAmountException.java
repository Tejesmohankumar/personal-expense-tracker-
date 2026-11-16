package com.expensetracker.exception;

/**
 * Custom Checked Exception thrown when a transaction amount is invalid (<= 0).
 * Demonstrates:
 * - User-defined exception extending Exception (checked exception)
 * - Parameterized constructors with custom messages
 * - Encapsulation of invalid value
 */
public class InvalidAmountException extends Exception {
    
    private final double invalidAmount;

    public InvalidAmountException(String message) {
        super(message);
        this.invalidAmount = 0.0;
    }

    public InvalidAmountException(String message, double invalidAmount) {
        super(message + " (Provided: " + invalidAmount + ")");
        this.invalidAmount = invalidAmount;
    }

    public double getInvalidAmount() {
        return invalidAmount;
    }
}
