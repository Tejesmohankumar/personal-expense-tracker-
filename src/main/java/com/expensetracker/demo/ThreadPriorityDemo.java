package com.expensetracker.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Diagnostic Demonstration Class for Thread Priorities and CPU Allocation.
 * Demonstrates:
 * - Thread.MIN_PRIORITY (1)
 * - Thread.NORM_PRIORITY (5)
 * - Thread.MAX_PRIORITY (10)
 */
@Component
public class ThreadPriorityDemo {

    public static class PriorityResult {
        private final String threadName;
        private final int priority;
        private final long counterValue;

        public PriorityResult(String threadName, int priority, long counterValue) {
            this.threadName = threadName;
            this.priority = priority;
            this.counterValue = counterValue;
        }

        public String getThreadName() { return threadName; }
        public int getPriority() { return priority; }
        public long getCounterValue() { return counterValue; }

        @Override
        public String toString() {
            return String.format("Thread '%s' [Priority %d]: Completed %d computations", 
                    threadName, priority, counterValue);
        }
    }

    /**
     * Executes 3 concurrent worker threads with different priorities for a fixed duration
     * and measures the work completed by each thread.
     */
    public List<PriorityResult> runPriorityDemonstration(long durationMillis) {
        List<PriorityResult> results = new ArrayList<>();
        AtomicLong lowCounter = new AtomicLong(0);
        AtomicLong normalCounter = new AtomicLong(0);
        AtomicLong highCounter = new AtomicLong(0);
        volatileFlagHolder flag = new volatileFlagHolder();

        // 1. Thread with MIN_PRIORITY (1)
        Thread lowPriorityThread = new Thread(() -> {
            while (flag.running) {
                lowCounter.incrementAndGet();
            }
        }, "Worker-MIN-Priority");
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY); // 1

        // 2. Thread with NORM_PRIORITY (5)
        Thread normalPriorityThread = new Thread(() -> {
            while (flag.running) {
                normalCounter.incrementAndGet();
            }
        }, "Worker-NORM-Priority");
        normalPriorityThread.setPriority(Thread.NORM_PRIORITY); // 5

        // 3. Thread with MAX_PRIORITY (10)
        Thread highPriorityThread = new Thread(() -> {
            while (flag.running) {
                highCounter.incrementAndGet();
            }
        }, "Worker-MAX-Priority");
        highPriorityThread.setPriority(Thread.MAX_PRIORITY); // 10

        // Start all threads simultaneously
        lowPriorityThread.start();
        normalPriorityThread.start();
        highPriorityThread.start();

        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException ignored) {
        } finally {
            flag.running = false; // Stop all threads
        }

        try {
            lowPriorityThread.join(1000);
            normalPriorityThread.join(1000);
            highPriorityThread.join(1000);
        } catch (InterruptedException ignored) {
        }

        results.add(new PriorityResult("Low Priority Worker (Thread.MIN_PRIORITY)", Thread.MIN_PRIORITY, lowCounter.get()));
        results.add(new PriorityResult("Normal Priority Worker (Thread.NORM_PRIORITY)", Thread.NORM_PRIORITY, normalCounter.get()));
        results.add(new PriorityResult("High Priority Worker (Thread.MAX_PRIORITY)", Thread.MAX_PRIORITY, highCounter.get()));

        return results;
    }

    private static class volatileFlagHolder {
        volatile boolean running = true;
    }
}
