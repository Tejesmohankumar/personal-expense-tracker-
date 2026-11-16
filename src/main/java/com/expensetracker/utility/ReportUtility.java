package com.expensetracker.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Report Formatting and String Handling Utility.
 * Demonstrates:
 * - String Manipulation Methods
 * - StringBuilder for efficient single-threaded string construction
 * - StringBuffer for synchronized, thread-safe string accumulation
 * - Performance benchmark for String vs StringBuilder vs StringBuffer
 */
public class ReportUtility {

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Formats currency with Indian Rupee symbol
     */
    public static String formatCurrency(Double amount) {
        if (amount == null) {
            return "₹0.00";
        }
        return String.format("₹%,.2f", amount);
    }

    /**
     * Formats date into readable string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Demonstrates differences between String, StringBuilder, and StringBuffer performance.
     * Compares execution time for 10,000 append operations.
     */
    public static String runStringPerformanceBenchmark(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=== String vs StringBuilder vs StringBuffer Benchmark ===\n");
        result.append("Iterations: ").append(iterations).append("\n\n");

        // 1. Immutable String Concatenation (+)
        long startStr = System.currentTimeMillis();
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str += "a";
        }
        long durationStr = System.currentTimeMillis() - startStr;
        result.append("1. String (+)       : ").append(durationStr).append(" ms (Creates new String objects in Heap)\n");

        // 2. StringBuilder (Unsynchronized, Fast)
        long startSb = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        long durationSb = System.currentTimeMillis() - startSb;
        result.append("2. StringBuilder   : ").append(durationSb).append(" ms (Mutable, Non-synchronized, Fast)\n");

        // 3. StringBuffer (Synchronized, Thread-Safe)
        long startSbf = System.currentTimeMillis();
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            sbf.append("a");
        }
        long durationSbf = System.currentTimeMillis() - startSbf;
        result.append("3. StringBuffer    : ").append(durationSbf).append(" ms (Mutable, Synchronized, Thread-Safe)\n\n");

        result.append("Engineering Takeaways:\n")
              .append("- Use String for constants & immutable text.\n")
              .append("- Use StringBuilder for dynamic text creation in single thread.\n")
              .append("- Use StringBuffer when multiple threads append concurrently.\n");

        return result.toString();
    }
}
