package com.expensetracker.interfaces;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.model.Transaction;
import java.util.List;

/**
 * Interface defining standard CRUD and query operations for financial transactions.
 * Demonstrates:
 * - Java Interfaces (Section 10.I)
 * - Generic & Bounded Generics <T extends Transaction> (Section 10.U, 10.V)
 * - Custom Exception declarations (Section 10.M)
 */
public interface TransactionOperations<T extends Transaction> {

    /**
     * Adds a new transaction to the management system.
     * @param transaction the transaction instance to record
     * @throws InvalidAmountException when transaction amount is <= 0
     */
    void addTransaction(T transaction) throws InvalidAmountException;

    /**
     * Updates an existing transaction in the system.
     * @param transaction the transaction with updated details
     * @throws TransactionNotFoundException if the transaction ID does not exist
     * @throws InvalidAmountException if updated amount is <= 0
     */
    void updateTransaction(T transaction) throws TransactionNotFoundException, InvalidAmountException;

    /**
     * Deletes a transaction by its unique identifier.
     * @param id primary key ID of transaction
     * @throws TransactionNotFoundException if ID is not found
     */
    void deleteTransaction(Long id) throws TransactionNotFoundException;

    /**
     * Returns a list of all managed transactions.
     * @return List of transaction instances
     */
    List<T> displayTransactions();

    /**
     * Finds a single transaction by ID.
     * @param id primary key identifier
     * @return the transaction if found
     * @throws TransactionNotFoundException if not found
     */
    T findTransactionById(Long id) throws TransactionNotFoundException;
}
