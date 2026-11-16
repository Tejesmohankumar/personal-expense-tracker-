package com.expensetracker.threading;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Thread-Safe Bounded Transaction Buffer implementing Producer-Consumer pattern.
 * Demonstrates:
 * - Inter-Thread Communication using wait(), notify(), and notifyAll() (Section 10.P)
 * - Thread Synchronization with synchronized methods and monitors (Section 10.O)
 * - Safe concurrent transaction queuing without blocking web threads
 */
@Component
public class TransactionProcessor {

    private final Queue<Transaction> queue = new LinkedList<>();
    private final int capacity = 3; // Bounded buffer capacity
    private final List<String> executionLogs = new ArrayList<>();

    /**
     * Producer method: Adds transaction to buffer.
     * Blocks using wait() if buffer is full; awakens consumers via notifyAll().
     */
    public synchronized void produce(Transaction transaction) throws InterruptedException {
        while (queue.size() == capacity) {
            String log = String.format("[Producer %s] Buffer FULL (size=%d). Calling wait()...",
                    Thread.currentThread().getName(), queue.size());
            logMessage(log);
            wait(); // Releases lock and suspends thread
        }

        queue.add(transaction);
        String log = String.format("[Producer %s] PRODUCED: '%s' (₹%.2f). Buffer size: %d. Calling notifyAll()...",
                Thread.currentThread().getName(), transaction.getTitle(), transaction.getAmount(), queue.size());
        logMessage(log);

        notifyAll(); // Wakes up waiting consumers
    }

    /**
     * Consumer method: Retrieves and processes transaction from buffer.
     * Blocks using wait() if buffer is empty; awakens producers via notifyAll().
     */
    public synchronized Transaction consume() throws InterruptedException {
        while (queue.isEmpty()) {
            String log = String.format("[Consumer %s] Buffer EMPTY. Calling wait()...",
                    Thread.currentThread().getName());
            logMessage(log);
            wait(); // Releases lock and suspends thread
        }

        Transaction transaction = queue.poll();
        String log = String.format("[Consumer %s] CONSUMED: '%s' (₹%.2f). Buffer size: %d. Calling notifyAll()...",
                Thread.currentThread().getName(), transaction.getTitle(), transaction.getAmount(), queue.size());
        logMessage(log);

        notifyAll(); // Wakes up waiting producers
        return transaction;
    }

    private synchronized void logMessage(String msg) {
        System.out.println(msg);
        executionLogs.add(msg);
        if (executionLogs.size() > 50) {
            executionLogs.remove(0);
        }
    }

    public synchronized List<String> getExecutionLogs() {
        return new ArrayList<>(executionLogs);
    }

    /**
     * Runs an asynchronous Producer-Consumer pipeline simulation for concurrency diagnostics.
     */
    public List<String> runDemoSimulation(int transactionCount) {
        executionLogs.clear();
        logMessage("=== Starting Inter-Thread Communication (wait/notifyAll) Demo ===");

        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= transactionCount; i++) {
                    Transaction t;
                    if (i % 2 == 0) {
                        t = new Income("Salary Batch #" + i, 15000.0 * i, "Salary", LocalDate.now(), "Bank", "Batch Income");
                    } else {
                        t = new Expense("Hardware Purchase #" + i, 2500.0 * i, "Shopping", LocalDate.now(), "UPI", "Equipment");
                    }
                    produce(t);
                    Thread.sleep(60); // Small interval to observe interleaving
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Tx-Producer");

        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= transactionCount; i++) {
                    consume();
                    Thread.sleep(120); // Consumer slightly slower than producer to demonstrate buffer filling and wait()
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Tx-Consumer");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join(4000);
            consumerThread.join(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logMessage("=== Producer-Consumer Demo Execution Completed Successfully ===");
        return getExecutionLogs();
    }
}
