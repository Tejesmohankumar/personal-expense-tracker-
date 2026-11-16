package com.expensetracker.controller;

import com.expensetracker.exception.InvalidAmountException;
import com.expensetracker.model.Income;
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
 * Controller handling Income record operations.
 */
@Controller
@RequestMapping("/income")
public class IncomeController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public IncomeController(TransactionService transactionService, CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String showIncomePage(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 Model model) {
        List<Transaction> incomeList = transactionService.getIncomeTransactions();

        // Filter by search query
        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            incomeList = incomeList.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(q)) ||
                                 (t.getCategory() != null && t.getCategory().toLowerCase().contains(q)) ||
                                 (t.getDescription() != null && t.getDescription().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // Filter by date range
        if (startDate != null) {
            incomeList = incomeList.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            incomeList = incomeList.stream()
                    .filter(t -> !t.getTransactionDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        double totalFilteredIncome = incomeList.stream().mapToDouble(Transaction::getAmount).sum();

        model.addAttribute("incomeList", incomeList);
        model.addAttribute("totalIncome", totalFilteredIncome);
        model.addAttribute("categories", categoryRepository.findByCategoryTypeIgnoreCaseOrderByCategoryNameAsc("INCOME"));
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activePage", "income");

        return "income";
    }

    @PostMapping("/add")
    public String addIncome(@RequestParam String title,
                            @RequestParam Double amount,
                            @RequestParam(defaultValue = "Salary") String category,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
                            @RequestParam(defaultValue = "Bank Transfer") String paymentMethod,
                            @RequestParam(required = false) String description,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate date = (transactionDate != null) ? transactionDate : LocalDate.now();
            Income income = new Income(title, amount, category, date, paymentMethod, description);
            transactionService.saveTransaction(income);
            redirectAttributes.addFlashAttribute("successMessage", "Income of ₹" + String.format("%.2f", amount) + " added successfully!");
        } catch (InvalidAmountException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add income: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving income: " + ex.getMessage());
        }
        return "redirect:/income";
    }

    @PostMapping("/edit")
    public String editIncome(@RequestParam Long id,
                             @RequestParam String title,
                             @RequestParam Double amount,
                             @RequestParam String category,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
                             @RequestParam String paymentMethod,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        try {
            Income updated = new Income(id, title, amount, category, transactionDate, paymentMethod, description);
            transactionService.updateTransaction(id, updated);
            redirectAttributes.addFlashAttribute("successMessage", "Income record updated successfully!");
        } catch (InvalidAmountException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating income: " + ex.getMessage());
        }
        return "redirect:/income";
    }

    @GetMapping("/delete/{id}")
    public String deleteIncome(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Income record deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting income record: " + ex.getMessage());
        }
        return "redirect:/income";
    }
}
