package com.expensetracker.service;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.interfaces.TransactionOperations;
import com.expensetracker.model.BudgetAlert;
import com.expensetracker.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core OOP Generic Transaction Manager.
 * Demonstrates:
 * - Generics & Bounded Generics <T extends Transaction> (Section 10.U, 10.V)
 * - Interface Implementation (Section 10.I)
 * - Java Collections Framework: ArrayList, HashSet, TreeSet, HashMap, Stack, PriorityQueue (Section 10.X)
 * - Thread Synchronization with synchronized methods and blocks (Section 10.O)
 * - Lambda Expressions & Streams (Section 10.W)
 * - Wrapper Classes, Autoboxing & Unboxing (Section 10.T)
 * - String, StringBuilder, and StringBuffer (Section 10.S)
 * - Exception Handling (Section 10.M)
 */
@Component("oopTransactionManager")
public class TransactionManager<T extends Transaction> implements TransactionOperations<T> {

    // 1. ArrayList: Dynamic list storage for transactions (Section 10.X.1)
    private final List<T> transactionList = new ArrayList<>();

    // 2. HashSet: Tracks unique transaction IDs to guarantee uniqueness (Section 10.X.2)
    private final Set<Long> uniqueTransactionIds = new HashSet<>();

    // 3. TreeSet: Maintains naturally sorted unique expense/income categories (Section 10.X.3)
    private final TreeSet<String> sortedCategories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    // 4. HashMap: Stores category-wise accumulated totals for fast lookup (Section 10.X.4)
    private final Map<String, Double> categoryTotalsMap = new HashMap<>();

    // 5. Stack: Tracks recently added transactions for history inspection and undo operations (Section 10.X.5)
    private final Stack<T> transactionUndoStack = new Stack<>();

    // 6. PriorityQueue: Prioritizes budget warnings based on severity (Section 10.X.6)
    private final PriorityQueue<BudgetAlert> alertPriorityQueue = new PriorityQueue<>();

    // Object monitor for synchronized blocks
    private final Object lock = new Object();

    public TransactionManager() {
        // Pre-populate default known categories into TreeSet
        sortedCategories.addAll(Arrays.asList(
            "Food", "Transportation", "Education", "Shopping", 
            "Entertainment", "Healthcare", "Bills", "Rent", "Salary", "Freelancing", "Others"
        ));
    }

    // =========================================================================
    // INTERFACE METHODS IMPLEMENTATION & SYNCHRONIZATION
    // =========================================================================

    /**
     * Adds a transaction with synchronization, validation and collection updates.
     * Demonstrates:
     * - Thread Synchronization (Section 10.O)
     * - User-defined Exception throwing (Section 10.M)
     * - Autoboxing & Unboxing (Section 10.T)
     * - Multiple collections synchronization
     */
    @Override
    public synchronized void addTransaction(T transaction) throws InvalidAmountException {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction instance cannot be null.");
        }

        // Unboxing Double to primitive double for validation check (Section 10.T)
        Double boxedAmount = transaction.getAmount();
        if (boxedAmount == null || boxedAmount <= 0.0) { // Unboxing occurs here
            double invalidVal = (boxedAmount != null) ? boxedAmount : 0.0;
            throw new InvalidAmountException("Transaction amount must be strictly greater than zero.", invalidVal);
        }

        // Synchronized block to prevent concurrent mutation of collections (Section 10.O)
        synchronized (lock) {
            // Add to ArrayList
            transactionList.add(transaction);

            // Add ID to HashSet if ID is assigned
            if (transaction.getId() != null) {
                uniqueTransactionIds.add(transaction.getId());
            }

            // Add category to TreeSet (automatically sorts alphabetically)
            if (transaction.getCategory() != null && !transaction.getCategory().isBlank()) {
                sortedCategories.add(transaction.getCategory());
            }

            // Update HashMap for category totals using Autoboxing
            String cat = transaction.getCategory() != null ? transaction.getCategory() : "Others";
            Double currentTotal = categoryTotalsMap.getOrDefault(cat, 0.0); // Autoboxing 0.0 to Double
            categoryTotalsMap.put(cat, currentTotal + boxedAmount);

            // Push to Stack for undo/recent history tracking
            transactionUndoStack.push(transaction);
        }
    }

    /**
     * Updates an existing transaction.
     */
    @Override
    public synchronized void updateTransaction(T transaction) throws TransactionNotFoundException, InvalidAmountException {
        if (transaction == null || transaction.getId() == null) {
            throw new TransactionNotFoundException("Cannot update transaction: Missing ID.");
        }

        Double boxedAmount = transaction.getAmount();
        if (boxedAmount == null || boxedAmount <= 0.0) {
            throw new InvalidAmountException("Updated amount must be greater than zero.", boxedAmount != null ? boxedAmount : 0.0);
        }

        synchronized (lock) {
            boolean found = false;
            for (int i = 0; i < transactionList.size(); i++) {
                if (Objects.equals(transactionList.get(i).getId(), transaction.getId())) {
                    transactionList.set(i, transaction);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new TransactionNotFoundException(transaction.getId());
            }

            // Recalculate HashMap totals
            recalculateCategoryTotals();
        }
    }

    /**
     * Deletes a transaction by ID.
     */
    @Override
    public synchronized void deleteTransaction(Long id) throws TransactionNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null for deletion.");
        }

        synchronized (lock) {
            boolean removed = transactionList.removeIf(t -> Objects.equals(t.getId(), id));
            if (!removed) {
                throw new TransactionNotFoundException(id);
            }
            uniqueTransactionIds.remove(id);
            recalculateCategoryTotals();
        }
    }

    /**
     * Displays all managed transactions as an unmodifiable list.
     */
    @Override
    public synchronized List<T> displayTransactions() {
        return Collections.unmodifiableList(new ArrayList<>(this.transactionList));
    }

    /**
     * Finds transaction by ID using Streams and Lambda.
     */
    @Override
    public synchronized T findTransactionById(Long id) throws TransactionNotFoundException {
        return transactionList.stream()
                .filter(t -> Objects.equals(t.getId(), id))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    // =========================================================================
    // LAMBDA EXPRESSIONS & STREAM OPERATIONS (Section 10.W)
    // =========================================================================

    /**
     * Sorts transactions by amount in descending order using Lambda Expression.
     */
    public List<T> getTransactionsSortedByAmountDesc() {
        return transactionList.stream()
                .sorted((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount())) // Lambda (t1, t2)
                .collect(Collectors.toList());
    }

    /**
     * Sorts transactions by date in descending order using Lambda Expression.
     */
    public List<T> getTransactionsSortedByDateDesc() {
        return transactionList.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate())) // Lambda
                .collect(Collectors.toList());
    }

    /**
     * Filters transactions by type using Lambda Expression.
     */
    public List<T> filterByType(String type) {
        return transactionList.stream()
                .filter(t -> t.getType().equalsIgnoreCase(type)) // Lambda Predicate
                .collect(Collectors.toList());
    }

    /**
     * Filters expenses by category using Lambda Expression.
     */
    public List<T> filterExpensesByCategory(String category) {
        return transactionList.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Groups expenses by category and sums their totals using Streams.
     */
    public Map<String, Double> calculateCategoryWiseTotals() {
        return transactionList.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    // =========================================================================
    // SYNCHRONIZED AGGREGATE CALCULATIONS
    // =========================================================================

    /**
     * Synchronized method to calculate overall net balance (Income - Expense).
     * Demonstrates dynamic method dispatch: t.calculateAmount() executes
     * Income or Expense overridden implementation at runtime!
     */
    public synchronized double calculateTotalNetBalance() {
        double balance = 0.0;
        for (T t : transactionList) {
            balance += t.calculateAmount(); // Dynamic Method Dispatch (Section 10.G)
        }
        return Math.round(balance * 100.0) / 100.0;
    }

    public synchronized double calculateTotalIncome() {
        return transactionList.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public synchronized double calculateTotalExpense() {
        return transactionList.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private void recalculateCategoryTotals() {
        categoryTotalsMap.clear();
        for (T t : transactionList) {
            if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                String cat = t.getCategory() != null ? t.getCategory() : "Others";
                categoryTotalsMap.put(cat, categoryTotalsMap.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
    }

    // =========================================================================
    // STACK & PRIORITY QUEUE ACCESS (Undo & Budget Alerts)
    // =========================================================================

    /**
     * Pops and returns the last added transaction from Stack for undo demonstration.
     */
    public synchronized Optional<T> popLastTransaction() {
        if (!transactionUndoStack.isEmpty()) {
            T last = transactionUndoStack.pop();
            return Optional.of(last);
        }
        return Optional.empty();
    }

    public synchronized void enqueueBudgetAlert(BudgetAlert alert) {
        alertPriorityQueue.offer(alert);
    }

    public synchronized List<BudgetAlert> pollTopAlerts(int limit) {
        List<BudgetAlert> list = new ArrayList<>();
        int count = 0;
        while (!alertPriorityQueue.isEmpty() && count < limit) {
            list.add(alertPriorityQueue.poll());
            count++;
        }
        return list;
    }

    public synchronized List<BudgetAlert> peekAllAlerts() {
        return new ArrayList<>(alertPriorityQueue);
    }

    // =========================================================================
    // COLLECTION GETTERS
    // =========================================================================

    public Set<Long> getUniqueTransactionIds() {
        return Collections.unmodifiableSet(uniqueTransactionIds);
    }

    public TreeSet<String> getSortedCategories() {
        return new TreeSet<>(sortedCategories);
    }

    public Map<String, Double> getCategoryTotalsMap() {
        return Collections.unmodifiableMap(categoryTotalsMap);
    }

    public synchronized void clearAll() {
        transactionList.clear();
        uniqueTransactionIds.clear();
        categoryTotalsMap.clear();
        transactionUndoStack.clear();
        alertPriorityQueue.clear();
    }

    public synchronized void loadAll(List<T> transactions) {
        clearAll();
        for (T t : transactions) {
            try {
                addTransaction(t);
            } catch (InvalidAmountException ignored) {
            }
        }
    }
}
