-- ===================================================================
-- MySQL Database Schema: Personal Expense & Budget Tracker
-- ===================================================================

CREATE DATABASE IF NOT EXISTS expense_tracker_db;
USE expense_tracker_db;

-- 1. Transactions Table (Inheritance Single-Table Strategy for Transaction, Income, Expense)
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nature VARCHAR(31) NOT NULL,
    title VARCHAR(150) NOT NULL,
    amount DOUBLE NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(60),
    transaction_date DATE NOT NULL,
    payment_method VARCHAR(40),
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Budgets Table
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    total_limit DOUBLE NOT NULL,
    category VARCHAR(60) DEFAULT 'ALL',
    category_limit DOUBLE
);

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(80) NOT NULL UNIQUE,
    category_type VARCHAR(20) NOT NULL,
    color_code VARCHAR(20),
    icon_name VARCHAR(40)
);

-- Sample Categories
INSERT INTO categories (category_name, category_type, color_code, icon_name) VALUES
('Salary', 'INCOME', '#22C55E', 'bi-cash-stack'),
('Freelancing', 'INCOME', '#10B981', 'bi-laptop'),
('Scholarship', 'INCOME', '#06B6D4', 'bi-mortarboard'),
('Business', 'INCOME', '#3B82F6', 'bi-briefcase'),
('Investments', 'INCOME', '#6366F1', 'bi-graph-up-arrow'),
('Other Income', 'INCOME', '#8B5CF6', 'bi-piggy-bank'),
('Food', 'EXPENSE', '#EF4444', 'bi-cup-hot'),
('Transportation', 'EXPENSE', '#F97316', 'bi-fuel-pump'),
('Education', 'EXPENSE', '#3B82F6', 'bi-book'),
('Shopping', 'EXPENSE', '#EC4899', 'bi-bag'),
('Entertainment', 'EXPENSE', '#8B5CF6', 'bi-controller'),
('Healthcare', 'EXPENSE', '#14B8A6', 'bi-heart-pulse'),
('Bills', 'EXPENSE', '#F59E0B', 'bi-receipt'),
('Rent', 'EXPENSE', '#64748B', 'bi-house'),
('Others', 'EXPENSE', '#94A3B8', 'bi-three-dots')
ON DUPLICATE KEY UPDATE category_type=VALUES(category_type);

-- Sample Initial Budget
INSERT INTO budgets (month, year, total_limit, category, category_limit)
VALUES (MONTH(CURRENT_DATE()), YEAR(CURRENT_DATE()), 15000.0, 'ALL', NULL);
