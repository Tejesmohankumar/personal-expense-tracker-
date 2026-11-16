package com.expensetracker.service;

import com.expensetracker.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportGenerator Service responsible for synthesizing comprehensive financial analytics.
 * Demonstrates:
 * - OOP Classes and Objects (Section 10.A)
 * - String vs StringBuilder vs StringBuffer processing (Section 10.S)
 * - Lambda Expressions and Stream transformations (Section 10.W)
 * - Autoboxing and Unboxing (Section 10.T)
 */
@Component
public class ReportGenerator {

    /**
     * Inner DTO representing a compiled financial report
     */
    public static class FinancialReportDTO {
        private String reportTitle;
        private LocalDate generatedOn;
        private double totalIncome;
        private double totalExpense;
        private double netSavings;
        private double savingsRate;
        private int totalTransactions;
        private Map<String, Double> categoryBreakdown;
        private List<MonthlyTrendDTO> monthlyTrends;
        private String textSummary;

        public FinancialReportDTO() {
            this.generatedOn = LocalDate.now();
            this.categoryBreakdown = new LinkedHashMap<>();
            this.monthlyTrends = new ArrayList<>();
        }

        public String getReportTitle() { return reportTitle; }
        public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
        public LocalDate getGeneratedOn() { return generatedOn; }
        public void setGeneratedOn(LocalDate generatedOn) { this.generatedOn = generatedOn; }
        public double getTotalIncome() { return totalIncome; }
        public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }
        public double getTotalExpense() { return totalExpense; }
        public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }
        public double getNetSavings() { return netSavings; }
        public void setNetSavings(double netSavings) { this.netSavings = netSavings; }
        public double getSavingsRate() { return savingsRate; }
        public void setSavingsRate(double savingsRate) { this.savingsRate = savingsRate; }
        public int getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
        public Map<String, Double> getCategoryBreakdown() { return categoryBreakdown; }
        public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }
        public List<MonthlyTrendDTO> getMonthlyTrends() { return monthlyTrends; }
        public void setMonthlyTrends(List<MonthlyTrendDTO> monthlyTrends) { this.monthlyTrends = monthlyTrends; }
        public String getTextSummary() { return textSummary; }
        public void setTextSummary(String textSummary) { this.textSummary = textSummary; }
    }

    public static class MonthlyTrendDTO {
        private String monthName;
        private int monthNumber;
        private double income;
        private double expense;
        private double savings;

        public MonthlyTrendDTO(String monthName, int monthNumber, double income, double expense) {
            this.monthName = monthName;
            this.monthNumber = monthNumber;
            this.income = income;
            this.expense = expense;
            this.savings = income - expense;
        }

        public String getMonthName() { return monthName; }
        public int getMonthNumber() { return monthNumber; }
        public double getIncome() { return income; }
        public double getExpense() { return expense; }
        public double getSavings() { return savings; }
    }

    /**
     * Generates a monthly or annual financial report using Stream APIs and String Builders.
     */
    public FinancialReportDTO generateReport(List<Transaction> transactions, int targetYear, Integer targetMonth) {
        FinancialReportDTO report = new FinancialReportDTO();

        // Filter transactions for target year and optional month
        List<Transaction> filtered = transactions.stream()
                .filter(t -> t.getTransactionDate() != null)
                .filter(t -> t.getTransactionDate().getYear() == targetYear)
                .filter(t -> targetMonth == null || t.getTransactionDate().getMonthValue() == targetMonth)
                .collect(Collectors.toList());

        report.setTotalTransactions(filtered.size());

        if (targetMonth != null) {
            String monthName = Month.of(targetMonth).name();
            report.setReportTitle("Monthly Financial Report: " + monthName + " " + targetYear);
        } else {
            report.setReportTitle("Annual Financial Report: " + targetYear);
        }

        // Calculate Income and Expense
        double totalIncome = filtered.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = filtered.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double netSavings = totalIncome - totalExpense;
        double savingsRate = (totalIncome > 0) ? Math.round(((netSavings / totalIncome) * 100.0) * 10.0) / 10.0 : 0.0;

        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setNetSavings(netSavings);
        report.setSavingsRate(savingsRate);

        // Category-wise Breakdown using Streams and Collectors (Section 10.W)
        Map<String, Double> categoryMap = filtered.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .collect(Collectors.groupingBy(
                        t -> (t.getCategory() != null ? t.getCategory() : "Others"),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        report.setCategoryBreakdown(categoryMap);

        // Monthly trends (1 to 12)
        List<MonthlyTrendDTO> trends = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int monthVal = m;
            double mIncome = transactions.stream()
                    .filter(t -> t.getTransactionDate().getYear() == targetYear && t.getTransactionDate().getMonthValue() == monthVal)
                    .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double mExpense = transactions.stream()
                    .filter(t -> t.getTransactionDate().getYear() == targetYear && t.getTransactionDate().getMonthValue() == monthVal)
                    .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            trends.add(new MonthlyTrendDTO(Month.of(m).name().substring(0, 3), m, mIncome, mExpense));
        }
        report.setMonthlyTrends(trends);

        // Demonstrates StringBuilder (Section 10.S)
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n")
          .append("           ").append(report.getReportTitle()).append("\n")
          .append("====================================================\n")
          .append("Generated On        : ").append(report.getGeneratedOn()).append("\n")
          .append("Total Transactions  : ").append(report.getTotalTransactions()).append("\n")
          .append("Total Income        : ₹").append(String.format("%.2f", totalIncome)).append("\n")
          .append("Total Expenses      : ₹").append(String.format("%.2f", totalExpense)).append("\n")
          .append("Net Available Funds : ₹").append(String.format("%.2f", netSavings)).append("\n")
          .append("Savings Rate        : ").append(savingsRate).append("%\n")
          .append("----------------------------------------------------\n")
          .append("Top Expense Categories:\n");

        categoryMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(e -> sb.append(String.format("  • %-18s: ₹%.2f\n", e.getKey(), e.getValue())));

        sb.append("====================================================\n");
        report.setTextSummary(sb.toString());

        return report;
    }
}
