# 💰 Personal Expense & Budget Tracker

A modern, responsive full-stack personal finance and budget management SaaS web application built with **Java 17** and **Spring Boot 3**.

**Live URL:** [http://localhost:8080](http://localhost:8080)  

---

## 🌟 1. Project Introduction
The **Personal Expense & Budget Tracker** is a clean, enterprise-grade personal finance application engineered in pure Java and Spring Boot. It empowers users to take full control of their financial health: tracking daily incomes and expenses, enforcing monthly and category-specific budget ceilings, analyzing savings rates, exporting and importing records via CSV, and visualizing cashflow trends with interactive Chart.js graphs.

---

## 🎯 2. Project Objectives
- Provide intuitive management of daily financial transactions (Incomes & Expenses).
- Dynamically calculate total earnings, total expenditures, net available funds, and monthly savings.
- Enforce proactive budget thresholds with automated visual alerts:
  - 🟢 **Normal:** Below 70% budget utilization
  - 🟡 **Warning:** 70% to 90% budget utilization
  - 🔴 **Critical:** 90% to 100% budget utilization
  - ⚫ **Budget Exceeded:** Above 100% budget utilization
- Maintain background spending monitoring via concurrent worker threads.
- Seamless CSV import and export utilizing Java File I/O (`BufferedReader`, `BufferedWriter`).
- Provide an integrated **General Settings** console for user profile, regional currency formatting, budget alert thresholds, and data maintenance.

---

## 🛠️ 3. Technology Stack

### Backend
- **Language:** Java 17 / Java 21 (LTS)
- **Framework:** Spring Boot 3.2.5 (Spring MVC, Spring Data JPA)
- **Build System:** Apache Maven 3.9.6
- **Database:** MySQL 8.x (with zero-setup H2 in-memory MySQL-mode fallback)
- **ORM:** Hibernate 6.x

### Frontend
- **Template Engine:** Thymeleaf
- **Styling:** Bootstrap 5.3.3 & Custom Modern SaaS CSS
- **Icons:** Bootstrap Icons 1.11.3
- **Data Visualization:** Chart.js 4.4.3
- **Scripting:** Vanilla JavaScript (ES6+)

---

## ✨ 4. Key Application Features

1. **SaaS Financial Dashboard:**
   - 4 Dynamic metric cards: Total Income, Total Expenses, Available Balance, Monthly Budget.
   - Interactive Bar Chart: Monthly Income vs Expenses (12 months).
   - Doughnut Chart: Category-wise spending breakdown.
   - Line Chart: Monthly spending trajectory and savings.
   - Live budget utilization progress bar with adaptive color transitions.
   - Recent transactions table with one-click OOP Polymorphic Inspector.

2. **Income Management Module:**
   - Add, edit, delete, search, and date-filter earnings.
   - Categories: *Salary, Freelancing, Scholarship, Business, Investments, Other*.
   - Modal forms with real-time client & server validation.

3. **Expense Management Module:**
   - Add, edit, delete, search, and filter expenses by category and date.
   - Categories: *Food, Transportation, Education, Shopping, Entertainment, Healthcare, Bills, Rent, Others*.
   - Color-coded badges and payment method tracking (*UPI, Credit Card, Debit Card, Cash, Net Banking*).

4. **Budget Management Module:**
   - Define and update monthly budget limits.
   - Configure category-specific limits.
   - Visual threshold indicators and real-time utilization calculation.

5. **Unified Transaction Ledger:**
   - Consolidated table displaying both Incomes and Expenses.
   - Multi-parameter filtering (type, category, date range).
   - Dynamic sorting by amount or date (ascending/descending).
   - Pagination.
   - **Stack Undo Operation:** Pops and reverts the most recently added transaction.

6. **Financial Reports & Analytics:**
   - Monthly and annual financial statement synthesis.
   - Net savings and savings rate percentage calculations.
   - Formatted text summary generation using `StringBuilder`.
   - CSV Export & CSV Import with validation.
   - Clean, watermark-free printable statement view (`@media print`).

7. **General Settings & Preferences:**
   - User profile configuration, monthly savings targets, and default payment methods.
   - Regional currency symbols (₹, $, €, £, ¥) and date formatting preferences.
   - Customizable budget warning and critical alert threshold percentages.
   - Data management controls: CSV ledger backup, sample data restore, and ledger reset.
   - Environment and runtime specifications.

---

## 🏛️ 5. OOP Concepts Implemented

A comprehensive mapping of each concept to code is detailed in [`OOP_CONCEPT_MAPPING.md`](OOP_CONCEPT_MAPPING.md).

- **Classes & Objects:** Real-world entities (`Transaction`, `Income`, `Expense`, `Budget`, `Category`).
- **Constructors:** Default, parameterized, and chained constructors (`this()`, `super()`).
- **Static Members:** `transactionCounter` maintaining count of instantiated transaction objects.
- **Method Overloading:** `addIncome(double)`, `addIncome(double, String)`, `addIncome(double, String, String)`.
- **Inheritance:** `Income extends Transaction`, `Expense extends Transaction`.
- **Method Overriding:** `@Override calculateAmount()` and `@Override displayDetails()`.
- **Dynamic Method Dispatch:** Parent reference `Transaction` executing subclass implementations at runtime.
- **Abstract Classes:** `public abstract class Transaction` with abstract methods.
- **Interfaces:** `TransactionOperations<T>` implemented by `TransactionManager`.
- **Package Architecture:** 9 clean modular packages (`model`, `interfaces`, `service`, `controller`, etc.).
- **Access Specifiers & Encapsulation:** `private`, `protected`, `public`, default getters/setters.
- **Final Keyword:** `public static final String DEFAULT_CURRENCY = "₹"`.
- **Custom Exception Handling:** `InvalidAmountException`, `BudgetExceededException`, `TransactionNotFoundException`.
- **Multithreading:** `BudgetMonitorThread` (daemon `Thread`) and `ReportGenerationThread` (`Runnable`).
- **Thread Synchronization:** `synchronized` methods and `synchronized(lock)` blocks.
- **Inter-Thread Communication:** Bounded transaction buffer using `wait()` and `notifyAll()`.
- **Thread Priorities:** Live benchmark of `MIN_PRIORITY (1)`, `NORM_PRIORITY (5)`, and `MAX_PRIORITY (10)`.
- **File Handling:** CSV read/write with `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`.
- **String Handling:** Performance benchmark of `String` vs `StringBuilder` vs `StringBuffer`.
- **Wrapper Classes:** Autoboxing and unboxing with `Double`, `Integer`, `Boolean`.
- **Generics & Bounded Generics:** `TransactionManager<T extends Transaction>`.
- **Lambda Expressions:** Stream sorting `(t1, t2) -> ...`, filter predicates, and collectors.
- **Collection Framework:** `ArrayList`, `HashSet`, `TreeSet`, `HashMap`, `Stack`, `PriorityQueue`.

---

## 📁 6. Project Structure

```text
PersonalExpenseTracker/
│
├── .tools/
│   └── apache-maven-3.9.6/        # Embedded portable Maven distribution
├── src/
│   ├── main/
│   │   ├── java/com/expensetracker/
│   │   │   ├── ExpenseTrackerApplication.java  # Spring Boot Main Class
│   │   │   │
│   │   │   ├── model/             # OOP Domain Models & Entities
│   │   │   │   ├── Transaction.java  (Abstract Parent Class)
│   │   │   │   ├── Income.java       (Child Class)
│   │   │   │   ├── Expense.java      (Child Class)
│   │   │   │   ├── Budget.java
│   │   │   │   ├── Category.java
│   │   │   │   └── BudgetAlert.java  (Implements Comparable for PriorityQueue)
│   │   │   │
│   │   │   ├── interfaces/        # Interface Contracts
│   │   │   │   └── TransactionOperations.java
│   │   │   │
│   │   │   ├── service/           # Business Logic & Generics Manager
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── BudgetService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   ├── TransactionManager.java    (Generic Manager <T extends Transaction>)
│   │   │   │   ├── ReportGenerator.java
│   │   │   │   └── OopConceptsDemoService.java
│   │   │   │
│   │   │   ├── controller/        # Spring MVC Web Controllers
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── IncomeController.java
│   │   │   │   ├── ExpenseController.java
│   │   │   │   ├── BudgetController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   └── SettingsController.java
│   │   │   │
│   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   ├── BudgetRepository.java
│   │   │   │   └── CategoryRepository.java
│   │   │   │
│   │   │   ├── exception/         # Custom Exceptions & Handlers
│   │   │   │   ├── InvalidAmountException.java
│   │   │   │   ├── BudgetExceededException.java
│   │   │   │   ├── TransactionNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── utility/           # File I/O & String Benchmarks
│   │   │   │   ├── CSVUtility.java
│   │   │   │   └── ReportUtility.java
│   │   │   │
│   │   │   ├── threading/         # Concurrency & Synchronization
│   │   │   │   ├── BudgetMonitorThread.java
│   │   │   │   ├── ReportGenerationThread.java
│   │   │   │   └── TransactionProcessor.java  (wait / notifyAll producer-consumer)
│   │   │   │
│   │   │   └── demo/              # Multi-Threading & Priority Demos
│   │   │       └── ThreadPriorityDemo.java
│   │   │
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf HTML5 Views
│   │       │   ├── fragments/
│   │       │   │   └── sidebar.html
│   │       │   ├── dashboard.html
│   │       │   ├── income.html
│   │       │   ├── expense.html
│   │       │   ├── transactions.html
│   │       │   ├── budgets.html
│   │       │   ├── reports.html
│   │       │   ├── print_report.html
│   │       │   └── settings.html
│   │       │
│   │       ├── static/            # Static Assets
│   │       │   ├── css/style.css
│   │       │   └── js/app.js
│   │       │
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-mysql.properties
│   │       └── mysql-schema.sql
│   │
├── mvn.cmd                        # Windows Maven Runner Batch Script
├── mvnw.cmd                       # Maven Wrapper Script
├── pom.xml                        # Maven Dependencies & Build Configuration
├── OOP_CONCEPT_MAPPING.md         # Architecture & OOP Concept Mapping
└── README.md                      # Project Documentation
```

---

## 🗄️ 7. Database Configuration

The application is configured with a dual-mode persistence architecture:

### Mode A: Zero-Setup Development Fallback (Default)
Out-of-the-box, the application runs on the `dev` profile using an in-memory H2 database operating in **MySQL Compatibility Mode**:
- `jdbc:h2:mem:expensedb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`
- Requires **no local MySQL installation or credentials**.
- Automatically seeds initial sample records for immediate evaluation.
- H2 Web Console accessible at: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:expensedb`, Username: `sa`, Password: empty).

### Mode B: MySQL Production Mode
To run against your local MySQL server:
1. Ensure your MySQL server is running on port `3306`.
2. Create database or let Hibernate auto-create it:
   ```sql
   CREATE DATABASE expense_tracker_db;
   ```
3. Update credentials in `src/main/resources/application-mysql.properties` if your MySQL password differs from `root`.
4. Run with the MySQL profile:
   ```powershell
   .\mvn.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 🚀 8. Execution Instructions

### Prerequisites
- Java JDK 17 or Java JDK 21 installed.
- PowerShell or Command Prompt.

### Step-by-Step Run Commands

1. **Navigate to the Project Root:**
   ```powershell
   cd s:\Studies\oops\PersonalExpenseTracker
   ```

2. **Clean & Build:**
   ```powershell
   .\mvn.cmd clean install
   ```

3. **Start the Web Application:**
   ```powershell
   .\mvn.cmd spring-boot:run
   ```

4. **Access the Application:**
   Open any web browser and visit:
   ```text
   http://localhost:8080
   ```

---

## 📊 9. Sample Initial Data (Pre-Populated)

On first run, the database automatically initializes with sample records:

### Income:
- **Monthly Salary:** ₹20,000.00 (Category: Salary)
- **Website Development Project:** ₹5,000.00 (Category: Freelancing)
- **Merit Scholarship:** ₹3,000.00 (Category: Scholarship)

### Expenses:
- **Grocery & Dining:** ₹2,000.00 (Category: Food)
- **Metro Card & Fuel:** ₹1,000.00 (Category: Transportation)
- **New Clothes & Footwear:** ₹3,000.00 (Category: Shopping)
- **Data Structures Reference Books:** ₹1,500.00 (Category: Education)
- **Cinema & OTT Subscription:** ₹500.00 (Category: Entertainment)

### Budget:
- **Monthly Total Budget:** ₹15,000.00
- **Total Expenses:** ₹8,000.00
- **Remaining Budget:** ₹7,000.00
- **Budget Utilization:** 53.3% (Status: 🟢 **NORMAL**)

*You can edit, delete, or reset these records anytime via the UI.*

---

## 🏆 10. Application Feature Verification Checklist

| Step | Action in UI | Expected Result | Technical Layer |
|---|---|---|---|
| 1 | Visit `http://localhost:8080/dashboard` | Dashboard displays 4 metric cards, charts, and table | Spring MVC, Thymeleaf, Chart.js |
| 2 | Click the eye icon on any transaction | Modal displays formatted audit record details | Polymorphic `displayDetails()` service |
| 3 | Add Income with negative amount (-100) | Application catches error and displays warning message | Custom `InvalidAmountException`, validation |
| 4 | Add Expense of ₹10,000 | Budget progress turns orange/red, banner displays warning | Concurrent `BudgetMonitorThread`, PriorityQueue |
| 5 | Visit `/transactions` and click **"Undo Last"** | Reverts the last added transaction | Synchronized `Stack` collection (LIFO) |
| 6 | Visit `/reports` and click **"Export CSV"** | Downloads `.csv` file with all database records | Java File I/O (`BufferedWriter`, `FileWriter`) |
| 7 | Visit `/settings` and click **"Run Priority Test"** | Displays thread iteration counts for priorities 1, 5, 10 | Concurrency, `setPriority()` |
| 8 | Visit `/settings` and click **"Run Queue Buffer Test"** | Displays trace log of `wait()` and `notifyAll()` | Inter-Thread Communication |
| 9 | Visit `/settings` and click **"Run String Benchmark"** | Measures millisecond timing for String, StringBuilder, StringBuffer | String Handling & Memory Architecture |
