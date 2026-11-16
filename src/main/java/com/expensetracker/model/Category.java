package com.expensetracker.model;

import jakarta.persistence.*;

/**
 * Entity representing an income or expense classification category.
 * Demonstrates:
 * - Classes and Objects (Section 10.A)
 * - Encapsulation (Section 10.K)
 * - Constructors (Section 10.B)
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false, unique = true, length = 80)
    private String categoryName;

    @Column(name = "category_type", nullable = false, length = 20)
    private String categoryType; // "EXPENSE" or "INCOME"

    @Column(name = "color_code", length = 20)
    private String colorCode; // Hex color code for UI cards and charts

    @Column(name = "icon_name", length = 40)
    private String iconName; // Bootstrap icon class name

    /**
     * Default Constructor
     */
    public Category() {
    }

    /**
     * Parameterized Constructor
     */
    public Category(String categoryName, String categoryType, String colorCode, String iconName) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.colorCode = colorCode;
        this.iconName = iconName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
