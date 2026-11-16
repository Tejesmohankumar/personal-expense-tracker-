package com.expensetracker.controller;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller handling Expense record operations.
 */
@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public ExpenseController(TransactionService transactionService, CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String showExpensesPage(@RequestParam(required = false) String search,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   Model model) {
        List<Transaction> expenseList = transactionService.getExpenseTransactions();

        // Filter by keyword
        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            expenseList = expenseList.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(q)) ||
                                 (t.getCategory() != null && t.getCategory().toLowerCase().contains(q)) ||
                                 (t.getDescription() != null && t.getDescription().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // Filter by category
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
            expenseList = expenseList.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // Filter by date range
        if (startDate != null) {
            expenseList = expenseList.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            expenseList = expenseList.stream()
                    .filter(t -> !t.getTransactionDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        double totalFilteredExpenses = expenseList.stream().mapToDouble(Transaction::getAmount).sum();

        model.addAttribute("expenseList", expenseList);
        model.addAttribute("totalExpenses", totalFilteredExpenses);
        model.addAttribute("categories", categoryRepository.findByCategoryTypeIgnoreCaseOrderByCategoryNameAsc("EXPENSE"));
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activePage", "expenses");

        return "expense";
    }

    @PostMapping("/add")
    public String addExpense(@RequestParam String title,
                             @RequestParam Double amount,
                             @RequestParam(defaultValue = "Food") String category,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
                             @RequestParam(defaultValue = "UPI") String paymentMethod,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        try {
            LocalDate date = (transactionDate != null) ? transactionDate : LocalDate.now();
            Expense expense = new Expense(title, amount, category, date, paymentMethod, description);
            transactionService.saveTransaction(expense);
            redirectAttributes.addFlashAttribute("successMessage", "Expense of ₹" + String.format("%.2f", amount) + " added successfully!");
        } catch (InvalidAmountException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add expense: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving expense: " + ex.getMessage());
        }
        return "redirect:/expenses";
    }

    @PostMapping("/edit")
    public String editExpense(@RequestParam Long id,
                              @RequestParam String title,
                              @RequestParam Double amount,
                              @RequestParam String category,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
                              @RequestParam String paymentMethod,
                              @RequestParam(required = false) String description,
                              RedirectAttributes redirectAttributes) {
        try {
            Expense updated = new Expense(id, title, amount, category, transactionDate, paymentMethod, description);
            transactionService.updateTransaction(id, updated);
            redirectAttributes.addFlashAttribute("successMessage", "Expense record updated successfully!");
        } catch (InvalidAmountException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating expense: " + ex.getMessage());
        }
        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Expense record deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting expense record: " + ex.getMessage());
        }
        return "redirect:/expenses";
    }
}
