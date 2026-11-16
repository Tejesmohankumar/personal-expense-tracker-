package com.expensetracker.controller;

import com.expensetracker.service.ReportGenerator.FinancialReportDTO;
import com.expensetracker.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Controller handling financial reporting, visual charts, and CSV file exchange.
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String showReportsPage(@RequestParam(required = false) Integer year,
                                  @RequestParam(required = false) Integer month,
                                  Model model) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        Integer targetMonth = (month != null && month > 0 && month <= 12) ? month : now.getMonthValue();

        FinancialReportDTO report = reportService.generateFinancialReport(targetYear, targetMonth);

        model.addAttribute("report", report);
        model.addAttribute("selectedYear", targetYear);
        model.addAttribute("selectedMonth", targetMonth);
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("activePage", "reports");

        return "reports";
    }

    @GetMapping("/print")
    public String showPrintableReport(@RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) Integer month,
                                     Model model) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        Integer targetMonth = (month != null && month > 0 && month <= 12) ? month : now.getMonthValue();

        FinancialReportDTO report = reportService.generateFinancialReport(targetYear, targetMonth);
        model.addAttribute("report", report);
        return "print_report";
    }

    /**
     * CSV Export using Java File I/O (Section 10.R)
     */
    @GetMapping("/export-csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"ExpenseTracker_Records_" + LocalDate.now() + ".csv\"");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
        reportService.exportTransactionsToCSV(writer);
    }

    /**
     * CSV Import using Java File I/O (Section 10.R)
     */
    @PostMapping("/import-csv")
    public String importFromCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a non-empty CSV file to upload.");
            return "redirect:/reports";
        }

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            int count = reportService.importTransactionsFromCSV(reader);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + count + " transaction records from CSV!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to import CSV: " + e.getMessage());
        }

        return "redirect:/reports";
    }
}
