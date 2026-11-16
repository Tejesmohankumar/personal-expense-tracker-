package com.expensetracker.repository;

import com.expensetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Budget limits.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findFirstByMonthAndYearAndCategory(Integer month, Integer year, String category);

    List<Budget> findByMonthAndYear(Integer month, Integer year);

    List<Budget> findAllByOrderByYearDescMonthDesc();
}
