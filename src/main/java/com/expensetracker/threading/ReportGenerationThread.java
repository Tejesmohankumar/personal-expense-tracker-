package com.expensetracker.threading;

import com.expensetracker.model.Transaction;
import com.expensetracker.service.ReportGenerator;
import com.expensetracker.service.ReportGenerator.FinancialReportDTO;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background Runnable task for heavy financial report generation.
 * Demonstrates:
 * - Multithreading by implementing Runnable (Section 10.N.2)
 * - Separation of asynchronous report generation from web request thread
 */
public class ReportGenerationThread implements Runnable {

    private final ReportGenerator reportGenerator;
    private final List<Transaction> transactions;
    private final int targetYear;
    private final Integer targetMonth;
    private FinancialReportDTO generatedReport;
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public ReportGenerationThread(ReportGenerator reportGenerator,
                                  List<Transaction> transactions,
                                  int targetYear,
                                  Integer targetMonth) {
        this.reportGenerator = reportGenerator;
        this.transactions = transactions;
        this.targetYear = targetYear;
        this.targetMonth = targetMonth;
    }

    @Override
    public void run() {
        System.out.println("[ReportGenerationThread] Background calculation started on thread: " 
                + Thread.currentThread().getName());
        try {
            // Generate report
            this.generatedReport = reportGenerator.generateReport(transactions, targetYear, targetMonth);
            System.out.println("[ReportGenerationThread] Report synthesis completed successfully.");
        } catch (Exception e) {
            System.err.println("[ReportGenerationThread] Report synthesis encountered error: " + e.getMessage());
        } finally {
            completed.set(true);
        }
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public FinancialReportDTO getGeneratedReport() {
        return generatedReport;
    }
}
