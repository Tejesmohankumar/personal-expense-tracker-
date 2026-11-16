package com.expensetracker.service;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.service.ReportGenerator.FinancialReportDTO;
import com.expensetracker.threading.ReportGenerationThread;
import com.expensetracker.utility.CSVUtility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing financial report compilation, background generation, and CSV file exchange.
 */
@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final ReportGenerator reportGenerator;
    private final TransactionService transactionService;

    public ReportService(TransactionRepository transactionRepository,
                         ReportGenerator reportGenerator,
                         TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.reportGenerator = reportGenerator;
        this.transactionService = transactionService;
    }

    /**
     * Generates standard report synchronously
     */
    public FinancialReportDTO generateFinancialReport(int year, Integer month) {
        List<Transaction> transactions = transactionRepository.findAll();
        return reportGenerator.generateReport(transactions, year, month);
    }

    /**
     * Demonstrates multithreading: triggers report generation in a background thread (Section 10.N.2)
     */
    public ReportGenerationThread generateReportAsync(int year, Integer month) {
        List<Transaction> transactions = transactionRepository.findAll();
        ReportGenerationThread threadTask = new ReportGenerationThread(reportGenerator, transactions, year, month);
        Thread worker = new Thread(threadTask, "Report-Worker-Thread");
        worker.start();
        return threadTask;
    }

    /**
     * Exports transactions to CSV
     */
    public void exportTransactionsToCSV(Writer writer) throws IOException {
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDescIdDesc();
        CSVUtility.exportToCSV(transactions, writer);
    }

    /**
     * Imports transactions from CSV and stores in database
     */
    @Transactional
    public int importTransactionsFromCSV(Reader reader) throws IOException {
        List<Transaction> imported = CSVUtility.importFromCSV(reader);
        int savedCount = 0;
        for (Transaction t : imported) {
            try {
                transactionService.saveTransaction(t);
                savedCount++;
            } catch (InvalidAmountException ignored) {
            }
        }
        return savedCount;
    }
}
