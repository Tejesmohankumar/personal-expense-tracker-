package com.expensetracker.utility;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * File Handling Utility for importing and exporting financial transactions in CSV format.
 * Demonstrates:
 * - Java File I/O (Section 10.R)
 * - FileReader, FileWriter, BufferedReader, BufferedWriter
 * - Exception Handling (try-with-resources & nested try/catch) (Section 10.M)
 * - String manipulation & String.split (Section 10.S)
 */
public class CSVUtility {

    private static final String CSV_HEADER = "Transaction ID,Title,Amount,Type,Category,Date,Payment Method,Description";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Exports a list of transactions to a Writer using BufferedWriter.
     */
    public static void exportToCSV(List<Transaction> transactions, Writer writer) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(writer)) {
            // Write CSV header
            bw.write(CSV_HEADER);
            bw.newLine();

            // Write each transaction line
            for (Transaction t : transactions) {
                StringBuilder line = new StringBuilder();
                line.append(t.getId() != null ? t.getId() : "").append(",")
                    .append(escapeSpecialChars(t.getTitle())).append(",")
                    .append(t.getAmount() != null ? String.format("%.2f", t.getAmount()) : "0.00").append(",")
                    .append(t.getType() != null ? t.getType() : "EXPENSE").append(",")
                    .append(escapeSpecialChars(t.getCategory())).append(",")
                    .append(t.getTransactionDate() != null ? t.getTransactionDate().format(DATE_FORMATTER) : LocalDate.now().toString()).append(",")
                    .append(escapeSpecialChars(t.getPaymentMethod())).append(",")
                    .append(escapeSpecialChars(t.getDescription()));
                
                bw.write(line.toString());
                bw.newLine();
            }
            bw.flush();
        }
    }

    /**
     * Imports transactions from a CSV Reader using BufferedReader.
     */
    public static List<Transaction> importFromCSV(Reader reader) throws IOException {
        List<Transaction> importedList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            boolean isHeader = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Skip header line
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().contains("title") || line.toLowerCase().contains("amount")) {
                        continue;
                    }
                }

                try {
                    String[] tokens = parseCsvLine(line);
                    if (tokens.length >= 5) {
                        // Tokens: [ID, Title, Amount, Type, Category, Date, PaymentMethod, Description]
                        String title = tokens.length > 1 ? tokens[1].trim() : "Imported Transaction";
                        double amount = Double.parseDouble(tokens[2].trim());
                        String type = tokens.length > 3 ? tokens[3].trim().toUpperCase() : "EXPENSE";
                        String category = tokens.length > 4 ? tokens[4].trim() : "Others";
                        
                        LocalDate date = LocalDate.now();
                        if (tokens.length > 5 && !tokens[5].trim().isEmpty()) {
                            try {
                                date = LocalDate.parse(tokens[5].trim(), DATE_FORMATTER);
                            } catch (Exception ignored) {
                            }
                        }

                        String paymentMethod = (tokens.length > 6 && !tokens[6].trim().isEmpty()) ? tokens[6].trim() : "Cash";
                        String description = (tokens.length > 7) ? tokens[7].trim() : "Imported via CSV file";

                        Transaction t;
                        if ("INCOME".equalsIgnoreCase(type)) {
                            t = new Income(title, amount, category, date, paymentMethod, description);
                        } else {
                            t = new Expense(title, amount, category, date, paymentMethod, description);
                        }

                        importedList.add(t);
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping malformed CSV record at line " + lineNumber + ": " + line);
                }
            }
        }

        return importedList;
    }

    /**
     * Escapes CSV delimiter commas and quotes
     */
    private static String escapeSpecialChars(String data) {
        if (data == null) return "";
        String escaped = data.replaceAll("\\R", " ");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = escaped.replace("\"", "\"\"");
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }

    /**
     * Basic CSV line splitter supporting quoted strings
     */
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder curVal = new StringBuilder();
        boolean inQuotes = false;

        char[] chars = line.toCharArray();
        for (char ch : chars) {
            if (ch == '\"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                result.add(curVal.toString());
                curVal = new StringBuilder();
            } else {
                curVal.append(ch);
            }
        }
        result.add(curVal.toString());
        return result.toArray(new String[0]);
    }
}
