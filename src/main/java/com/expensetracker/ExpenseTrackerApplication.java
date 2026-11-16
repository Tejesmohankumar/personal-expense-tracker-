package com.expensetracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for Personal Expense & Budget Tracker.
 */
@SpringBootApplication
public class ExpenseTrackerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=========================================================");
        System.out.println(" Personal Expense & Budget Tracker Web Application Ready");
        System.out.println(" Application URL: http://localhost:8080");
        System.out.println("=========================================================");
    }
}
