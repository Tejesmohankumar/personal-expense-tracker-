package com.expensetracker.repository;

import com.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Category management.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    List<Category> findByCategoryTypeIgnoreCaseOrderByCategoryNameAsc(String categoryType);

    List<Category> findAllByOrderByCategoryTypeAscCategoryNameAsc();
}
