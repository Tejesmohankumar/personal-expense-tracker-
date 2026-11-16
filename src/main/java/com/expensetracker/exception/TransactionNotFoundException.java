package com.expensetracker.exception;

/**
 * Custom Unchecked Exception thrown when a transaction lookup fails for a given ID.
 * Demonstrates:
 * - User-defined RuntimeException
 * - Parameterized constructors with long identifier
 */
public class TransactionNotFoundException extends RuntimeException {
    
    private final Long transactionId;

    public TransactionNotFoundException(String message) {
        super(message);
        this.transactionId = null;
    }

    public TransactionNotFoundException(Long transactionId) {
        super("Transaction record with ID #" + transactionId + " was not found in the system.");
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
}
