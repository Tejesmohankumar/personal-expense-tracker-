package com.expensetracker.controller;

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
import java.util.Optional;

/**
 * Controller managing comprehensive Transaction history, filtering, sorting, and undo operations.
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionService transactionService, CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String showTransactions(@RequestParam(required = false) String search,
                                   @RequestParam(defaultValue = "ALL") String type,
                                   @RequestParam(defaultValue = "ALL") String category,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   @RequestParam(defaultValue = "date") String sortBy,
                                   @RequestParam(defaultValue = "desc") String sortDir,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        List<Transaction> filtered = transactionService.searchAndFilter(
                search, type, category, startDate, endDate, sortBy, sortDir);

        // Pagination
        int totalItems = filtered.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        if (totalPages == 0) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);
        List<Transaction> pagedList = (fromIndex <= totalItems) ? filtered.subList(fromIndex, toIndex) : List.of();

        model.addAttribute("transactions", pagedList);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        // Filter form bindings
        model.addAttribute("search", search);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("categories", categoryRepository.findAllByOrderByCategoryTypeAscCategoryNameAsc());
        model.addAttribute("activePage", "transactions");

        return "transactions";
    }

    /**
     * Demonstrates Stack collection (Section 10.X.5) by popping the last created transaction
     */
    @PostMapping("/undo")
    public String undoLastTransaction(RedirectAttributes redirectAttributes) {
        Optional<Transaction> undone = transactionService.undoLastTransaction();
        if (undone.isPresent()) {
            Transaction t = undone.get();
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Undone last transaction: [" + t.getType() + "] " + t.getTitle() + " (₹" + t.getAmount() + ")");
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "No recent transaction available on the undo stack.");
        }
        return "redirect:/transactions";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting transaction: " + ex.getMessage());
        }
        return "redirect:/transactions";
    }

    /**
     * Demonstrates Dynamic Method Dispatch via AJAX / Modal:
     * returns polymorphic t.displayDetails()
     */
    @GetMapping("/details/{id}")
    @ResponseBody
    public String getTransactionDetails(@PathVariable Long id) {
        try {
            Transaction t = transactionService.getTransactionById(id);
            return t.displayDetails(); // Dynamic Method Dispatch!
        } catch (Exception e) {
            return "Transaction details not found.";
        }
    }
}
