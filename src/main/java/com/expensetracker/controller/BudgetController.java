package com.expensetracker.controller;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller handling Budget definition, thresholds, and category limits.
 */
@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public BudgetController(BudgetService budgetService,
                            TransactionService transactionService,
                            CategoryRepository categoryRepository) {
        this.budgetService = budgetService;
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String showBudgetsPage(Model model) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        Map<String, Object> metrics = budgetService.getCurrentBudgetMetrics();
        List<Budget> allBudgets = budgetService.getAllBudgets();

        model.addAttribute("metrics", metrics);
        model.addAttribute("allBudgets", allBudgets);
        model.addAttribute("categories", categoryRepository.findByCategoryTypeIgnoreCaseOrderByCategoryNameAsc("EXPENSE"));
        model.addAttribute("currentMonth", month);
        model.addAttribute("currentYear", year);
        model.addAttribute("activePage", "budgets");

        return "budgets";
    }

    @PostMapping("/set")
    public String setMonthlyBudget(@RequestParam Integer month,
                                   @RequestParam Integer year,
                                   @RequestParam Double totalLimit,
                                   RedirectAttributes redirectAttributes) {
        if (totalLimit == null || totalLimit <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Budget limit must be a positive number.");
            return "redirect:/budgets";
        }

        budgetService.setMonthlyBudget(month, year, totalLimit);
        redirectAttributes.addFlashAttribute("successMessage", "Monthly budget of ₹" + String.format("%.2f", totalLimit) + " set successfully!");
        return "redirect:/budgets";
    }

    @PostMapping("/set-category")
    public String setCategoryBudget(@RequestParam Integer month,
                                    @RequestParam Integer year,
                                    @RequestParam String category,
                                    @RequestParam Double categoryLimit,
                                    RedirectAttributes redirectAttributes) {
        if (categoryLimit == null || categoryLimit <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category budget limit must be a positive number.");
            return "redirect:/budgets";
        }

        budgetService.setCategoryBudget(month, year, category, categoryLimit);
        redirectAttributes.addFlashAttribute("successMessage", "Category budget for " + category + " set successfully!");
        return "redirect:/budgets";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            budgetService.deleteBudget(id);
            redirectAttributes.addFlashAttribute("successMessage", "Budget record deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting budget: " + ex.getMessage());
        }
        return "redirect:/budgets";
    }
}
