package com.expensetracker.repository;

import com.expensetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Transaction persistence.
 * Provides standard CRUD and custom JPQL analytical queries.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTypeIgnoreCaseOrderByTransactionDateDescIdDesc(String type);

    List<Transaction> findByCategoryIgnoreCaseOrderByTransactionDateDescIdDesc(String category);

    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDescIdDesc(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByTransactionDateDescIdDesc(String title, String description);

    List<Transaction> findAllByOrderByTransactionDateDescIdDesc();

    List<Transaction> findTop10ByOrderByTransactionDateDescIdDesc();

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t WHERE UPPER(t.type) = UPPER(:type)")
    Double sumTotalByType(@Param("type") String type);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t WHERE UPPER(t.type) = 'INCOME' AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    Double sumMonthlyIncome(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t WHERE UPPER(t.type) = 'EXPENSE' AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    Double sumMonthlyExpense(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0.0) FROM Transaction t WHERE UPPER(t.type) = 'EXPENSE' AND t.category = :category AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    Double sumCategoryMonthlyExpense(@Param("category") String category, @Param("month") int month, @Param("year") int year);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE UPPER(t.type) = 'EXPENSE' GROUP BY t.category")
    List<Object[]> findExpenseTotalsByCategory();

    @Query("SELECT MONTH(t.transactionDate), UPPER(t.type), SUM(t.amount) FROM Transaction t WHERE YEAR(t.transactionDate) = :year GROUP BY MONTH(t.transactionDate), UPPER(t.type)")
    List<Object[]> findMonthlyBreakdownByYear(@Param("year") int year);
}
