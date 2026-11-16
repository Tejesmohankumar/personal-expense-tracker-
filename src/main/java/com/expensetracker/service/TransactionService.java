package com.expensetracker.service;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.model.*;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * High-Level Service orchestrating Database persistence and OOP in-memory collection synchronization.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionManager<Transaction> transactionManager;

    public TransactionService(TransactionRepository transactionRepository,
                              BudgetRepository budgetRepository,
                              CategoryRepository categoryRepository,
                              @Qualifier("oopTransactionManager") TransactionManager<Transaction> transactionManager) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Initializes sample categories, budgets, and sample transactions on application startup (Section 15)
     */
    @PostConstruct
    @Transactional
    public void initSampleData() {
        // 1. Initialize default categories if empty
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = Arrays.asList(
                new Category("Salary", "INCOME", "#22C55E", "bi-cash-stack"),
                new Category("Freelancing", "INCOME", "#10B981", "bi-laptop"),
                new Category("Scholarship", "INCOME", "#06B6D4", "bi-mortarboard"),
                new Category("Business", "INCOME", "#3B82F6", "bi-briefcase"),
                new Category("Investments", "INCOME", "#6366F1", "bi-graph-up-arrow"),
                new Category("Other Income", "INCOME", "#8B5CF6", "bi-piggy-bank"),
                new Category("Food", "EXPENSE", "#EF4444", "bi-cup-hot"),
                new Category("Transportation", "EXPENSE", "#F97316", "bi-fuel-pump"),
                new Category("Education", "EXPENSE", "#3B82F6", "bi-book"),
                new Category("Shopping", "EXPENSE", "#EC4899", "bi-bag"),
                new Category("Entertainment", "EXPENSE", "#8B5CF6", "bi-controller"),
                new Category("Healthcare", "EXPENSE", "#14B8A6", "bi-heart-pulse"),
                new Category("Bills", "EXPENSE", "#F59E0B", "bi-receipt"),
                new Category("Rent", "EXPENSE", "#64748B", "bi-house"),
                new Category("Others", "EXPENSE", "#94A3B8", "bi-three-dots")
            );
            categoryRepository.saveAll(defaultCategories);
        }

        // 2. Initialize default budget if empty
        LocalDate now = LocalDate.now();
        if (budgetRepository.findFirstByMonthAndYearAndCategory(now.getMonthValue(), now.getYear(), "ALL").isEmpty()) {
            Budget defaultBudget = new Budget(now.getMonthValue(), now.getYear(), 15000.0);
            budgetRepository.save(defaultBudget);
        }

        // 3. Initialize sample transactions if table is empty (Section 15 Sample Data)
        if (transactionRepository.count() == 0) {
            LocalDate today = LocalDate.now();

            // Sample Income
            List<Transaction> sampleTx = new ArrayList<>();
            sampleTx.add(new Income("Monthly Salary", 20000.0, "Salary", today.minusDays(15), "Bank Transfer", "Monthly company salary credited"));
            sampleTx.add(new Income("Website Development Project", 5000.0, "Freelancing", today.minusDays(8), "UPI", "Frontend redesign for client"));
            sampleTx.add(new Income("Investment Dividend", 3000.0, "Investments", today.minusDays(3), "Bank Transfer", "Quarterly portfolio dividend yield"));

            // Sample Expenses
            sampleTx.add(new Expense("Grocery & Dining", 2000.0, "Food", today.minusDays(12), "UPI", "Weekly supermarket groceries & restaurant"));
            sampleTx.add(new Expense("Metro Card & Fuel", 1000.0, "Transportation", today.minusDays(10), "Credit Card", "Monthly transit recharge"));
            sampleTx.add(new Expense("New Clothes & Footwear", 3000.0, "Shopping", today.minusDays(7), "Debit Card", "Weekend seasonal shopping"));
            sampleTx.add(new Expense("Cloud Certification & Books", 1500.0, "Education", today.minusDays(4), "UPI", "Professional certification and books"));
            sampleTx.add(new Expense("Cinema & OTT Subscription", 500.0, "Entertainment", today.minusDays(1), "Net Banking", "Movie ticket and monthly subscription"));

            transactionRepository.saveAll(sampleTx);
        }

        // Synchronize in-memory TransactionManager collections
        syncManagerFromDb();
    }

    public synchronized void resetToSampleData() {
        transactionRepository.deleteAll();
        initSampleData();
    }

    public synchronized void clearAllTransactions() {
        transactionRepository.deleteAll();
        syncManagerFromDb();
    }

    public synchronized void syncManagerFromDb() {
        List<Transaction> all = transactionRepository.findAllByOrderByTransactionDateDescIdDesc();
        transactionManager.loadAll(all);
    }

    // =========================================================================
    // CRUD OPERATIONS
    // =========================================================================

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateDescIdDesc();
    }

    public List<Transaction> getIncomeTransactions() {
        return transactionRepository.findByTypeIgnoreCaseOrderByTransactionDateDescIdDesc("INCOME");
    }

    public List<Transaction> getExpenseTransactions() {
        return transactionRepository.findByTypeIgnoreCaseOrderByTransactionDateDescIdDesc("EXPENSE");
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Transactional
    public Transaction saveTransaction(Transaction transaction) throws InvalidAmountException {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0.0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero.",
                    transaction.getAmount() != null ? transaction.getAmount() : 0.0);
        }

        // Default missing values
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDate.now());
        }
        if (transaction.getPaymentMethod() == null || transaction.getPaymentMethod().isBlank()) {
            transaction.setPaymentMethod("Cash");
        }

        Transaction saved = transactionRepository.save(transaction);
        // Sync to TransactionManager
        transactionManager.addTransaction(saved);
        return saved;
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction updated) throws InvalidAmountException {
        Transaction existing = getTransactionById(id);

        if (updated.getAmount() == null || updated.getAmount() <= 0.0) {
            throw new InvalidAmountException("Updated amount must be greater than zero.",
                    updated.getAmount() != null ? updated.getAmount() : 0.0);
        }

        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setTransactionDate(updated.getTransactionDate());
        existing.setPaymentMethod(updated.getPaymentMethod());
        existing.setDescription(updated.getDescription());

        Transaction saved = transactionRepository.save(existing);
        transactionManager.updateTransaction(saved);
        return saved;
    }

    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
        transactionManager.deleteTransaction(id);
    }

    /**
     * Undo last transaction using Stack in TransactionManager
     */
    @Transactional
    public Optional<Transaction> undoLastTransaction() {
        Optional<Transaction> last = transactionManager.popLastTransaction();
        if (last.isPresent() && last.get().getId() != null) {
            Long id = last.get().getId();
            if (transactionRepository.existsById(id)) {
                transactionRepository.deleteById(id);
                syncManagerFromDb();
                return last;
            }
        }
        return Optional.empty();
    }

    // =========================================================================
    // SEARCH, FILTER, AND SORT
    // =========================================================================

    public List<Transaction> searchAndFilter(String query, String type, String category,
                                           LocalDate startDate, LocalDate endDate,
                                           String sortBy, String sortDir) {
        List<Transaction> list = transactionRepository.findAll();

        // 1. Filter by Query
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            list = list.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(q)) ||
                                 (t.getDescription() != null && t.getDescription().toLowerCase().contains(q)) ||
                                 (t.getCategory() != null && t.getCategory().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // 2. Filter by Type
        if (type != null && !type.isBlank() && !type.equalsIgnoreCase("ALL")) {
            list = list.stream()
                    .filter(t -> t.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        // 3. Filter by Category
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            list = list.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // 4. Filter by Date Range
        if (startDate != null) {
            list = list.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            list = list.stream()
                    .filter(t -> !t.getTransactionDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        // 5. Sorting via Lambda Expressions (Section 10.W)
        boolean asc = "asc".equalsIgnoreCase(sortDir);
        if ("amount".equalsIgnoreCase(sortBy)) {
            list.sort((a, b) -> asc ? Double.compare(a.getAmount(), b.getAmount())
                                    : Double.compare(b.getAmount(), a.getAmount()));
        } else {
            // Default sort by date
            list.sort((a, b) -> asc ? a.getTransactionDate().compareTo(b.getTransactionDate())
                                    : b.getTransactionDate().compareTo(a.getTransactionDate()));
        }

        return list;
    }

    // =========================================================================
    // DASHBOARD & FINANCIAL METRICS
    // =========================================================================

    public double getTotalIncome() {
        Double total = transactionRepository.sumTotalByType("INCOME");
        return (total != null) ? total : 0.0;
    }

    public double getTotalExpense() {
        Double total = transactionRepository.sumTotalByType("EXPENSE");
        return (total != null) ? total : 0.0;
    }

    public double getAvailableBalance() {
        return Math.round((getTotalIncome() - getTotalExpense()) * 100.0) / 100.0;
    }

    public double getMonthlyIncome(int month, int year) {
        Double total = transactionRepository.sumMonthlyIncome(month, year);
        return (total != null) ? total : 0.0;
    }

    public double getMonthlyExpense(int month, int year) {
        Double total = transactionRepository.sumMonthlyExpense(month, year);
        return (total != null) ? total : 0.0;
    }

    public double getMonthlySavings(int month, int year) {
        return getMonthlyIncome(month, year) - getMonthlyExpense(month, year);
    }

    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findAllByOrderByTransactionDateDescIdDesc().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Double> getCategoryWiseExpenseTotals() {
        List<Object[]> rows = transactionRepository.findExpenseTotalsByCategory();
        Map<String, Double> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String cat = (String) r[0];
            Double amt = (Double) r[1];
            map.put(cat != null ? cat : "Others", amt != null ? amt : 0.0);
        }
        return map;
    }

    /**
     * Builds data for monthly income vs expense bar chart (12 months of current year)
     */
    public Map<String, Object> getYearlyMonthlyBarChartData(int year) {
        List<Object[]> rows = transactionRepository.findMonthlyBreakdownByYear(year);

        double[] incomeArr = new double[12];
        double[] expenseArr = new double[12];

        for (Object[] row : rows) {
            int m = ((Number) row[0]).intValue(); // 1-12
            String type = (String) row[1];
            double amt = ((Number) row[2]).doubleValue();

            if (m >= 1 && m <= 12) {
                if ("INCOME".equalsIgnoreCase(type)) {
                    incomeArr[m - 1] = amt;
                } else if ("EXPENSE".equalsIgnoreCase(type)) {
                    expenseArr[m - 1] = amt;
                }
            }
        }

        String[] monthNames = new String[12];
        for (int i = 0; i < 12; i++) {
            monthNames[i] = Month.of(i + 1).name().substring(0, 3);
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", monthNames);
        chartData.put("income", incomeArr);
        chartData.put("expense", expenseArr);
        return chartData;
    }
}
