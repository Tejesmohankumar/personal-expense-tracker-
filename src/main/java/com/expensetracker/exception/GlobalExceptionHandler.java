package com.expensetracker.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Centralized Global Exception Handler for Web Controllers.
 * Demonstrates:
 * - Exception Handling (Section 10.M)
 * - User-Friendly notifications without exposing internal stack traces (Section 12)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAmountException.class)
    public String handleInvalidAmountException(InvalidAmountException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Validation Error: " + ex.getMessage());
        return "redirect:/transactions";
    }

    @ExceptionHandler(BudgetExceededException.class)
    public String handleBudgetExceededException(BudgetExceededException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warningMessage", "Budget Alert: " + ex.getMessage());
        return "redirect:/budgets";
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public String handleTransactionNotFoundException(TransactionNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transactions";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        System.err.println("[GlobalExceptionHandler] Unexpected error occurred: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected system error occurred. Please try again.");
        return "redirect:/dashboard";
    }
}
