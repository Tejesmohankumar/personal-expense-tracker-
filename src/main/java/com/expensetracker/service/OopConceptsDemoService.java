package com.expensetracker.service;

import com.expensetracker.exception.BudgetExceededException;
import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.model.*;
import com.expensetracker.utility.ReportUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service orchestrating live diagnostic demonstrations of OOP Concepts and System Architecture.
 */
@Service
public class OopConceptsDemoService {

    private final TransactionManager<Transaction> transactionManager;

    public OopConceptsDemoService(@Qualifier("oopTransactionManager") TransactionManager<Transaction> transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Demonstrates Dynamic Method Dispatch (Section 10.G)
     * Parent reference 'Transaction' invokes overridden subclass methods at runtime.
     */
    public List<String> demonstrateDynamicMethodDispatch() {
        List<String> logs = new ArrayList<>();
        logs.add("=== Demonstrating Dynamic Method Dispatch (Runtime Polymorphism) ===");

        // Parent class references pointing to child class objects
        Transaction t1 = new Income("Project Stipend", 18000.0, "Scholarship", LocalDate.now(), "Bank Transfer", "Research Stipend");
        Transaction t2 = new Expense("Textbooks & Stationery", 2400.0, "Education", LocalDate.now(), "UPI", "Semester 3 books");

        logs.add("Reference Type: Transaction, Object Instance: " + t1.getClass().getSimpleName());
        logs.add("Polymorphic call: t1.calculateAmount() -> " + t1.calculateAmount() + " (Positive for Income)");
        logs.add("Polymorphic call: t1.displayDetails():\n" + t1.displayDetails());
        logs.add("------------------------------------------------------------------");

        logs.add("Reference Type: Transaction, Object Instance: " + t2.getClass().getSimpleName());
        logs.add("Polymorphic call: t2.calculateAmount() -> " + t2.calculateAmount() + " (Negative for Expense)");
        logs.add("Polymorphic call: t2.displayDetails():\n" + t2.displayDetails());

        return logs;
    }

    /**
     * Demonstrates Method Overloading (Section 10.D)
     */
    public List<String> demonstrateMethodOverloading() {
        List<String> logs = new ArrayList<>();
        logs.add("=== Demonstrating Method Overloading (Compile-Time Polymorphism) ===");

        Income income = new Income();
        logs.add("1. Invoking addIncome(double amount):");
        income.addIncome(5000.0);
        logs.add("   Result: " + income.getTransactionSummary());

        logs.add("2. Invoking addIncome(double amount, String source):");
        income.addIncome(12000.0, "Freelance Gig");
        logs.add("   Result: " + income.getTransactionSummary());

        logs.add("3. Invoking addIncome(double amount, String source, String description):");
        income.addIncome(25000.0, "Full-Time Salary", "Monthly corporate compensation");
        logs.add("   Result: " + income.getTransactionSummary());

        return logs;
    }

    /**
     * Demonstrates Wrapper Classes, Autoboxing and Unboxing (Section 10.T)
     */
    public List<String> demonstrateWrapperClasses() {
        List<String> logs = new ArrayList<>();
        logs.add("=== Demonstrating Wrapper Classes, Autoboxing & Unboxing ===");

        // Primitive double to Double object (Autoboxing)
        double primitiveAmount = 4500.75;
        Double boxedAmount = primitiveAmount; // Autoboxing
        logs.add("Autoboxing: primitive double (" + primitiveAmount + ") -> Double object: " + boxedAmount);

        // Double object to primitive double (Unboxing)
        double unboxedAmount = boxedAmount; // Unboxing
        logs.add("Unboxing: Double object (" + boxedAmount + ") -> primitive double: " + unboxedAmount);

        // Primitive int to Integer object
        int primitiveMonth = 11;
        Integer boxedMonth = primitiveMonth; // Autoboxing
        logs.add("Autoboxing: primitive int (" + primitiveMonth + ") -> Integer object: " + boxedMonth);

        // Boolean wrapper
        boolean primitiveFlag = true;
        Boolean boxedFlag = primitiveFlag;
        logs.add("Autoboxing: primitive boolean (" + primitiveFlag + ") -> Boolean object: " + boxedFlag);

        return logs;
    }

    /**
     * Demonstrates Custom Exception Handling with try-catch-finally (Section 10.M)
     */
    public List<String> demonstrateExceptionHandling() {
        List<String> logs = new ArrayList<>();
        logs.add("=== Demonstrating Custom Exception Handling & Nested Try Blocks ===");

        // Test 1: InvalidAmountException
        try {
            logs.add("1. Testing InvalidAmountException with amount = -500.0");
            Transaction t = new Expense("Invalid Item", -500.0, "Food", LocalDate.now(), "Cash", "Bad test");
            transactionManager.addTransaction(t);
        } catch (InvalidAmountException iae) {
            logs.add("   [CAUGHT InvalidAmountException]: " + iae.getMessage() + " (Invalid value: " + iae.getInvalidAmount() + ")");
        } catch (Exception ex) {
            logs.add("   [CAUGHT Generic Exception]: " + ex.getMessage());
        } finally {
            logs.add("   [FINALLY BLOCK]: Executed cleanup for Test 1.");
        }

        // Test 2: BudgetExceededException
        try {
            logs.add("2. Testing BudgetExceededException (spending 18000 on 15000 budget):");
            double budget = 15000.0;
            double spending = 18000.0;
            if (spending > budget) {
                throw new BudgetExceededException("Monthly budget threshold exceeded!", budget, spending);
            }
        } catch (BudgetExceededException bee) {
            logs.add("   [CAUGHT BudgetExceededException]: " + bee.getMessage());
        } finally {
            logs.add("   [FINALLY BLOCK]: Executed cleanup for Test 2.");
        }

        // Test 3: TransactionNotFoundException
        try {
            logs.add("3. Testing TransactionNotFoundException for ID #999999:");
            transactionManager.findTransactionById(999999L);
        } catch (TransactionNotFoundException tnfe) {
            logs.add("   [CAUGHT TransactionNotFoundException]: " + tnfe.getMessage());
        } finally {
            logs.add("   [FINALLY BLOCK]: Executed cleanup for Test 3.");
        }

        return logs;
    }

    /**
     * Demonstrates Java Collections Framework (Section 10.X)
     */
    public Map<String, Object> demonstrateCollections() {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("arrayListSize", transactionManager.displayTransactions().size());
        data.put("uniqueIdsHashSet", transactionManager.getUniqueTransactionIds());
        data.put("treeSetCategoriesSorted", transactionManager.getSortedCategories());
        data.put("hashMapCategoryTotals", transactionManager.getCategoryTotalsMap());
        data.put("priorityQueueAlerts", transactionManager.peekAllAlerts());

        return data;
    }
}
