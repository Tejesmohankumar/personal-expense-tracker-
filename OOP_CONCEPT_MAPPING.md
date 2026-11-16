# 📘 OBJECT-ORIENTED DESIGN & ARCHITECTURE SPECIFICATION

**System:** Personal Expense & Budget Tracker (Production Edition)  
**Language & Platform:** Java 17 / 21 | Spring Boot 3.2.5  
**Architecture:** Layered Enterprise MVC with Clean OOP Domain Model  

---

## 🎯 Purpose of This Document
This document provides a comprehensive architectural specification of every Object-Oriented Programming (OOP) design pattern and Java core concept implemented in the **Personal Expense & Budget Tracker** application. Each section details:
1. **The OOP Concept** and theoretical background.
2. **The Source File Path & Class Name**.
3. **The Method / Variable** demonstrating the concept.
4. **Code Snippet** from the project.
5. **Architectural Rationale & Core Design Analysis**.

---

## 📑 Complete Concept Index (A to X)

| # | Concept Name | Primary Source File | Primary Class / Method |
|---|---|---|---|
| **A** | Classes & Objects | `com.expensetracker.model.*` | `Transaction`, `Income`, `Expense`, `Budget` |
| **B** | Constructors | `com.expensetracker.model.Transaction.java` | Default & Parameterized constructors |
| **C** | Static Members | `com.expensetracker.model.Transaction.java` | `transactionCounter`, `getTransactionCounter()` |
| **D** | Method Overloading | `com.expensetracker.model.Income.java` | `addIncome(double)`, `addIncome(double, String)` |
| **E** | Inheritance | `com.expensetracker.model.Income.java` | `Income extends Transaction`, `super(...)` |
| **F** | Method Overriding | `com.expensetracker.model.Expense.java` | `@Override calculateAmount()`, `displayDetails()` |
| **G** | Dynamic Method Dispatch | `com.expensetracker.service.OopConceptsDemoService.java` | `Transaction t = new Income(...)` |
| **H** | Abstract Classes | `com.expensetracker.model.Transaction.java` | `public abstract class Transaction` |
| **I** | Interfaces | `com.expensetracker.interfaces.TransactionOperations.java` | Interface implemented by `TransactionManager` |
| **J** | Packages | `com.expensetracker.*` | `model`, `service`, `interfaces`, `threading`, etc. |
| **K** | Access Specifiers | `com.expensetracker.model.Transaction.java` | `private`, `protected`, `public`, default getters/setters |
| **L** | Final Keyword | `com.expensetracker.model.Transaction.java` | `public static final String DEFAULT_CURRENCY = "₹"` |
| **M** | Exception Handling | `com.expensetracker.exception.*` | `InvalidAmountException`, `BudgetExceededException` |
| **N** | Multithreading | `com.expensetracker.threading.*` | `BudgetMonitorThread`, `ReportGenerationThread` |
| **O** | Thread Synchronization | `com.expensetracker.service.TransactionManager.java` | `synchronized` methods and `synchronized(lock)` blocks |
| **P** | Inter-Thread Communication | `com.expensetracker.threading.TransactionProcessor.java` | `wait()`, `notifyAll()` in bounded transaction buffer |
| **Q** | Thread Priorities | `com.expensetracker.demo.ThreadPriorityDemo.java` | `MIN_PRIORITY (1)`, `NORM_PRIORITY (5)`, `MAX_PRIORITY (10)` |
| **R** | File Handling | `com.expensetracker.utility.CSVUtility.java` | `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter` |
| **S** | String Handling | `com.expensetracker.utility.ReportUtility.java` | `String` vs `StringBuilder` vs `StringBuffer` |
| **T** | Wrapper Classes | `com.expensetracker.service.OopConceptsDemoService.java` | `Double`, `Integer`, `Boolean`, Autoboxing & Unboxing |
| **U** | Generics | `com.expensetracker.service.TransactionManager.java` | `public class TransactionManager<T>` |
| **V** | Bounded Generics | `com.expensetracker.service.TransactionManager.java` | `<T extends Transaction>` |
| **W** | Lambda Expressions | `com.expensetracker.service.TransactionManager.java` | Stream sorting `(t1, t2) -> ...`, filters, grouping |
| **X** | Collection Framework | `com.expensetracker.service.TransactionManager.java` | `ArrayList`, `HashSet`, `TreeSet`, `HashMap`, `Stack`, `PriorityQueue` |

---

## 🔍 Detailed Concept Demonstrations

### A. Classes and Objects
- **File:** `src/main/java/com/expensetracker/model/Transaction.java`
- **Explanation:** In OOP, a class is a blueprint, and an object is a runtime instance. We defined entity classes modeling real-world financial entities: `Transaction`, `Income`, `Expense`, `Budget`, and `Category`.
```java
Transaction income = new Income("Salary", 25000.0, "Salary", LocalDate.now(), "Bank", "Monthly Pay");
Transaction expense = new Expense("Groceries", 2000.0, "Food", LocalDate.now(), "UPI", "Store");
```

---

### B. Constructors & Constructor Chaining
- **File:** `src/main/java/com/expensetracker/model/Transaction.java`
- **Explanation:** Constructors initialize an object's internal state. We implement:
  - Default constructor (required by Hibernate/JPA)
  - Parameterized constructor
  - Constructor chaining using `this(...)` and `super(...)`.
```java
// Default constructor
public Transaction() {
    incrementCounter();
    this.transactionDate = LocalDate.now();
    this.createdAt = LocalDateTime.now();
    this.paymentMethod = "Cash";
}

// Parameterized constructor
public Transaction(String title, Double amount, String type, String category, LocalDate date, String paymentMethod, String desc) {
    incrementCounter();
    this.title = title;
    this.amount = amount;
    this.type = type;
    this.category = category;
    this.transactionDate = (date != null) ? date : LocalDate.now();
    this.paymentMethod = paymentMethod;
    this.description = desc;
    this.createdAt = LocalDateTime.now();
}
```

---

### C. Static Members (Variables & Methods)
- **File:** `src/main/java/com/expensetracker/model/Transaction.java`
- **Explanation:** `static` members belong to the class rather than individual instances. A shared static variable `transactionCounter` counts how many transactions were instantiated.
```java
// Static variable shared across all Transaction objects
private static int transactionCounter = 0;

private static synchronized void incrementCounter() {
    transactionCounter++;
}

public static int getTransactionCounter() {
    return transactionCounter;
}
```

---

### D. Method Overloading (Compile-Time Polymorphism)
- **File:** `src/main/java/com/expensetracker/model/Income.java`
- **Explanation:** Multiple methods with the same name but different parameter signatures within the same class.
```java
// Overload 1: Amount only
public void addIncome(double amount) {
    this.setAmount(amount);
    this.setTitle("General Income");
    this.setCategory("Other");
}

// Overload 2: Amount and Source
public void addIncome(double amount, String source) {
    this.setAmount(amount);
    this.setTitle(source);
    this.setCategory(source);
}

// Overload 3: Amount, Source, and Description
public void addIncome(double amount, String source, String description) {
    this.setAmount(amount);
    this.setTitle(source);
    this.setCategory(source);
    this.setDescription(description);
}
```

---

### E. Inheritance (`extends` and `super`)
- **File:** `src/main/java/com/expensetracker/model/Income.java` and `Expense.java`
- **Explanation:** Child classes inherit fields (`title`, `amount`, `transactionDate`) and methods from abstract parent `Transaction`. The `super` keyword invokes parent constructors.
```java
@Entity
@DiscriminatorValue("INCOME")
public class Income extends Transaction {

    public Income(String title, Double amount, String category, LocalDate transactionDate, String paymentMethod, String description) {
        super(title, amount, "INCOME", category, transactionDate, paymentMethod, description);
    }
}
```

---

### F. Method Overriding (Runtime Polymorphism)
- **File:** `src/main/java/com/expensetracker/model/Income.java` and `Expense.java`
- **Explanation:** Subclasses provide their own implementation of abstract methods declared in the parent class.
```java
// In Income.java
@Override
public double calculateAmount() {
    return (getAmount() != null) ? getAmount() : 0.0; // Positive addition to balance
}

// In Expense.java
@Override
public double calculateAmount() {
    return (getAmount() != null) ? -getAmount() : 0.0; // Negative deduction from balance
}
```

---

### G. Dynamic Method Dispatch
- **File:** `src/main/java/com/expensetracker/service/OopConceptsDemoService.java`
- **Explanation:** A parent class reference resolves the overridden method based on the actual object created at runtime.
```java
// Parent reference holding Income child object
Transaction t1 = new Income("Freelancing", 5000.0, "Freelancing", LocalDate.now(), "UPI", "Client");

// Parent reference holding Expense child object
Transaction t2 = new Expense("Dinner", 800.0, "Food", LocalDate.now(), "Cash", "Restaurant");

// Polymorphic invocation at runtime
double bal1 = t1.calculateAmount(); // Returns +5000.0 (Calls Income.calculateAmount)
double bal2 = t2.calculateAmount(); // Returns -800.0  (Calls Expense.calculateAmount)

String details1 = t1.displayDetails(); // Calls Income.displayDetails()
String details2 = t2.displayDetails(); // Calls Expense.displayDetails()
```

---

### H. Abstract Classes & Methods
- **File:** `src/main/java/com/expensetracker/model/Transaction.java`
- **Explanation:** An `abstract` class cannot be instantiated directly using `new Transaction()`. It enforces contracts through abstract methods.
```java
public abstract class Transaction {
    // Abstract method: must be implemented by subclasses
    public abstract double calculateAmount();
    public abstract String displayDetails();
}
```

---

### I. Interfaces
- **File:** `src/main/java/com/expensetracker/interfaces/TransactionOperations.java`
- **Explanation:** An interface defines purely abstract contracts that a class must implement.
```java
public interface TransactionOperations<T extends Transaction> {
    void addTransaction(T transaction) throws InvalidAmountException;
    void updateTransaction(T transaction) throws TransactionNotFoundException, InvalidAmountException;
    void deleteTransaction(Long id) throws TransactionNotFoundException;
    List<T> displayTransactions();
    T findTransactionById(Long id) throws TransactionNotFoundException;
}
```
Implemented in `com.expensetracker.service.TransactionManager`:
```java
public class TransactionManager<T extends Transaction> implements TransactionOperations<T> { ... }
```

---

### J. Package Architecture
- **Directory:** `src/main/java/com/expensetracker/`
- **Explanation:** Clean modular organization into 9 functional packages:
  1. `model`: Entity beans & OOP domain classes (`Transaction`, `Income`, `Expense`, `Budget`, `Category`, `BudgetAlert`)
  2. `interfaces`: Public contracts (`TransactionOperations`)
  3. `service`: Business logic & generics manager (`TransactionService`, `BudgetService`, `ReportService`, `TransactionManager`, `ReportGenerator`)
  4. `controller`: Spring MVC web controllers (`DashboardController`, `IncomeController`, `ExpenseController`, etc.)
  5. `repository`: Spring Data JPA data access interfaces
  6. `exception`: Custom exceptions & centralized `@ControllerAdvice`
  7. `utility`: File I/O (`CSVUtility`) and String benchmarking (`ReportUtility`)
  8. `threading`: Multithreading workers (`BudgetMonitorThread`, `ReportGenerationThread`, `TransactionProcessor`)
  9. `demo`: Thread scheduling and priority benchmarking (`ThreadPriorityDemo`)

---

### K. Access Specifiers & Encapsulation
- **File:** `src/main/java/com/expensetracker/model/Transaction.java`
- **Explanation:**
  - `private`: `id`, `title`, `amount`, `transactionDate`, `paymentMethod`, `description` (strictly encapsulated).
  - `protected`: `type`, `category` (accessible by child classes `Income` and `Expense`).
  - `public`: Getters, setters, and business methods.
```java
private Double amount;

public Double getAmount() {
    return amount;
}

public void setAmount(Double amount) {
    if (amount != null && amount < 0) {
        throw new IllegalArgumentException("Amount cannot be negative");
    }
    this.amount = amount;
}
```

---

### L. Final Keyword
- **File:** `src/main/java/com/expensetracker/model/Transaction.java` and `exception/InvalidAmountException.java`
- **Explanation:** Used for constants and immutable properties that cannot be changed once assigned.
```java
public static final String DEFAULT_CURRENCY = "₹";
private final double invalidAmount;
```

---

### M. Exception Handling
- **Files:**
  - `src/main/java/com/expensetracker/exception/InvalidAmountException.java` (Checked exception)
  - `src/main/java/com/expensetracker/exception/BudgetExceededException.java` (Unchecked exception)
  - `src/main/java/com/expensetracker/exception/TransactionNotFoundException.java` (Unchecked exception)
  - `src/main/java/com/expensetracker/exception/GlobalExceptionHandler.java` (`@ControllerAdvice`)
- **Demonstration:**
```java
try {
    if (transaction.getAmount() <= 0) {
        throw new InvalidAmountException("Transaction amount must be strictly greater than zero.", transaction.getAmount());
    }
} catch (InvalidAmountException iae) {
    System.err.println("Validation failed: " + iae.getMessage());
} catch (Exception ex) {
    System.err.println("Generic error: " + ex.getMessage());
} finally {
    System.out.println("Cleanup completed.");
}
```

---

### N. Multithreading
- **Files:**
  - `src/main/java/com/expensetracker/threading/BudgetMonitorThread.java` (`extends Thread`)
  - `src/main/java/com/expensetracker/threading/ReportGenerationThread.java` (`implements Runnable`)
- **Explanation:**
  - `BudgetMonitorThread` runs in the background as a daemon thread, periodically polling expenditure against budget limits.
  - `ReportGenerationThread` computes heavy annual report summaries asynchronously without blocking web requests.
```java
// Thread approach
public class BudgetMonitorThread extends Thread {
    @Override
    public void run() {
        while (running) {
            checkBudgetAndGenerateAlerts();
            Thread.sleep(30000);
        }
    }
}

// Runnable approach
public class ReportGenerationThread implements Runnable {
    @Override
    public void run() {
        this.generatedReport = reportGenerator.generateReport(transactions, year, month);
    }
}
```

---

### O. Thread Synchronization
- **File:** `src/main/java/com/expensetracker/service/TransactionManager.java`
- **Explanation:** Prevents race conditions when multiple threads access shared transaction collections and balance calculations.
```java
// Synchronized method
public synchronized void addTransaction(T transaction) throws InvalidAmountException {
    synchronized (lock) {
        transactionList.add(transaction);
        uniqueTransactionIds.add(transaction.getId());
        transactionUndoStack.push(transaction);
    }
}

public synchronized double calculateTotalNetBalance() {
    double balance = 0.0;
    for (T t : transactionList) {
        balance += t.calculateAmount();
    }
    return balance;
}
```

---

### P. Inter-Thread Communication (`wait`, `notify`, `notifyAll`)
- **File:** `src/main/java/com/expensetracker/threading/TransactionProcessor.java`
- **Explanation:** Demonstrates Producer-Consumer pattern on a bounded queue.
```java
public synchronized void produce(Transaction transaction) throws InterruptedException {
    while (queue.size() == capacity) {
        wait(); // Suspends producer when queue is full
    }
    queue.add(transaction);
    notifyAll(); // Wakes waiting consumers
}

public synchronized Transaction consume() throws InterruptedException {
    while (queue.isEmpty()) {
        wait(); // Suspends consumer when queue is empty
    }
    Transaction t = queue.poll();
    notifyAll(); // Wakes waiting producers
    return t;
}
```

---

### Q. Thread Priorities
- **File:** `src/main/java/com/expensetracker/demo/ThreadPriorityDemo.java`
- **Explanation:** Sets thread priorities (`Thread.MIN_PRIORITY = 1`, `Thread.NORM_PRIORITY = 5`, `Thread.MAX_PRIORITY = 10`) and observes scheduler favoring the higher priority worker.
```java
Thread lowPriority = new Thread(worker, "Low");
lowPriority.setPriority(Thread.MIN_PRIORITY); // 1

Thread normalPriority = new Thread(worker, "Normal");
normalPriority.setPriority(Thread.NORM_PRIORITY); // 5

Thread highPriority = new Thread(worker, "High");
highPriority.setPriority(Thread.MAX_PRIORITY); // 10
```

---

### R. File Handling (CSV Import and Export)
- **File:** `src/main/java/com/expensetracker/utility/CSVUtility.java`
- **Explanation:**
  - Export: Uses `BufferedWriter` and `FileWriter` to format transactions into CSV rows.
  - Import: Uses `BufferedReader` and `FileReader` to parse line tokens and reconstitute Java objects.
```java
// Writing CSV
try (BufferedWriter bw = new BufferedWriter(writer)) {
    bw.write("Transaction ID,Title,Amount,Type,Category,Date,Payment Method,Description");
    bw.newLine();
    bw.write(line.toString());
}

// Reading CSV
try (BufferedReader br = new BufferedReader(reader)) {
    String line;
    while ((line = br.readLine()) != null) {
        String[] tokens = parseCsvLine(line);
        // Build Income or Expense object
    }
}
```

---

### S. String Handling (`String`, `StringBuilder`, `StringBuffer`)
- **Files:** `com.expensetracker.utility.ReportUtility.java` & `ReportGenerator.java`
- **Explanation:**
  - `String`: Immutable (creates new objects in String Constant Pool).
  - `StringBuilder`: Mutable and fast for single-threaded construction.
  - `StringBuffer`: Mutable and thread-safe (`synchronized` methods).
```java
// StringBuilder in ReportGenerator
StringBuilder sb = new StringBuilder();
sb.append("Total Income: ₹").append(totalIncome).append("\n");

// StringBuffer in Income displayDetails
StringBuffer sbuf = new StringBuffer();
sbuf.append("Source: ").append(getTitle());
```

---

### T. Wrapper Classes, Autoboxing & Unboxing
- **File:** `src/main/java/com/expensetracker/service/TransactionManager.java` & `OopConceptsDemoService.java`
- **Explanation:**
  - `Double`, `Integer`, `Boolean` encapsulate primitive types.
  - Autoboxing: automatic conversion from primitive to wrapper (`Double d = 2500.0`).
  - Unboxing: automatic conversion from wrapper to primitive (`double val = d`).
```java
// Autoboxing
double primitiveVal = 500.0;
Double boxedAmount = primitiveVal;

// Unboxing
double unboxedVal = boxedAmount;
```

---

### U. Generics & V. Bounded Generics
- **File:** `src/main/java/com/expensetracker/service/TransactionManager.java`
- **Explanation:**
  - Generic class `TransactionManager<T>`.
  - Bounded Type Parameter `<T extends Transaction>` restricts `T` to `Transaction` or its subclasses (`Income`, `Expense`), enabling access to `t.getAmount()` and `t.calculateAmount()`.
```java
public class TransactionManager<T extends Transaction> implements TransactionOperations<T> {
    private final List<T> transactionList = new ArrayList<>();

    public synchronized void addTransaction(T transaction) throws InvalidAmountException {
        transactionList.add(transaction);
    }
}
```

---

### W. Lambda Expressions & Stream API
- **File:** `src/main/java/com/expensetracker/service/TransactionManager.java`
- **Explanation:** Replaces anonymous inner classes with concise syntax for filtering, sorting, and grouping.
```java
// 1. Sorting by amount descending
list.sort((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));

// 2. Sorting by date descending
list.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

// 3. Filtering expenses
List<T> expenses = transactionList.stream()
    .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
    .collect(Collectors.toList());

// 4. Grouping by category and summing
Map<String, Double> categoryTotals = transactionList.stream()
    .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
    .collect(Collectors.groupingBy(
        Transaction::getCategory,
        Collectors.summingDouble(Transaction::getAmount)
    ));
```

---

### X. Java Collection Framework
- **File:** `src/main/java/com/expensetracker/service/TransactionManager.java`
- **Explanation of All 6 Required Collections:**
  1. `ArrayList<T>`: Dynamic indexed array storing chronological transaction records.
  2. `HashSet<Long>`: Unordered mathematical set ensuring all transaction IDs are unique without duplicates.
  3. `TreeSet<String>`: Red-Black tree structure maintaining naturally sorted, unique expense categories.
  4. `HashMap<String, Double>`: Key-value hash table providing $O(1)$ fast category total lookups.
  5. `Stack<T>`: LIFO (Last-In-First-Out) stack supporting undo operations for the most recently added transaction.
  6. `PriorityQueue<BudgetAlert>`: Binary heap prioritizing budget warning alerts based on severity rank (`EXCEEDED` > `CRITICAL` > `WARNING` > `NORMAL`).
```java
private final List<T> transactionList = new ArrayList<>();
private final Set<Long> uniqueTransactionIds = new HashSet<>();
private final TreeSet<String> sortedCategories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
private final Map<String, Double> categoryTotalsMap = new HashMap<>();
private final Stack<T> transactionUndoStack = new Stack<>();
private final PriorityQueue<BudgetAlert> alertPriorityQueue = new PriorityQueue<>();
```

---

## 💡 Key Architectural Design Q&A

### Q1: What is the difference between Method Overloading and Method Overriding in your project?
> **Answer:** Method Overloading (`addIncome(double)` vs `addIncome(double, String)`) occurs in the same class and is resolved at compile time based on parameter signatures. Method Overriding (`calculateAmount()` and `displayDetails()`) occurs between parent `Transaction` and child classes `Income`/`Expense` and is resolved at runtime using Dynamic Method Dispatch.

### Q2: Why did you declare `Transaction` as an abstract class?
> **Answer:** A generic "transaction" cannot exist by itself in real financial accounting—it is fundamentally either an Income or an Expense. Declaring `Transaction` as abstract prevents direct instantiation while defining common properties and enforcing the implementation of `calculateAmount()` and `displayDetails()`.

### Q3: How did you implement Inter-Thread Communication?
> **Answer:** In `TransactionProcessor.java`, we implemented a thread-safe bounded buffer. When the buffer is full, the producer calls `wait()`, releasing the monitor lock. Once the consumer removes an item, it calls `notifyAll()` to wake the waiting producer. Similarly, when the buffer is empty, the consumer calls `wait()`, and the producer awakens it with `notifyAll()`.

### Q4: Why use Bounded Generics `<T extends Transaction>`?
> **Answer:** Bounded generics restrict the type parameter so that `TransactionManager` only accepts objects that inherit from `Transaction`. This guarantees compile-time type safety while allowing the manager to directly invoke methods like `t.calculateAmount()` and `t.getCategory()` without unsafe type casting.

### Q5: How does your PriorityQueue order BudgetAlert objects?
> **Answer:** `BudgetAlert` implements `Comparable<BudgetAlert>`. It assigns numeric severity ranks: `EXCEEDED = 1`, `CRITICAL = 2`, `WARNING = 3`, `NORMAL = 4`. The `compareTo()` method orders lower numeric values first, ensuring that urgent warnings are dequeued before normal status messages.
