# Personal Investment Portfolio Tracker

## Project Title
Personal Investment Portfolio Tracker

## Problem Statement
Individual investors managing personal portfolios across multiple asset types — stocks, mutual funds, and bonds — often rely on disconnected spreadsheets or manual tracking, which lack real-time recalculation and proactive alerts. This leads to poor visibility into actual returns and missed threshold-based decisions. The Personal Investment Portfolio Tracker addresses this by providing a structured, object-oriented system that models each investment type distinctly, calculates returns polymorphically, persists portfolio state across sessions, and notifies users when gain or loss thresholds are breached.

## Target User
Individual retail investors, finance students, and early-career professionals who self-manage small personal investment portfolios.

## Core Features
- Add, update, and remove investment holdings (Stock, Mutual Fund, Bond)
- Polymorphic return calculation specific to each investment type
- Real-time portfolio summary with total value and net gain/loss
- Threshold-based alerts when gain or loss exceeds a user-defined limit
- Sortable holdings view — by return %, current value, or risk level
- Persistent portfolio storage (serialized `.dat` file via DAO pattern)
- Robust exception handling for invalid inputs and file I/O failures
- Background thread for periodic portfolio recalculation and alert checks

## OOP Concepts Used
- **Abstraction**: Abstract `Investment` class defining shared contract
- **Inheritance**: `Stock`, `MutualFund`, `Bond` extend `Investment`
- **Polymorphism**: Overridden `calculateReturn()` per subclass
- **Exception Handling**: `InvalidInvestmentException`, `PortfolioPersistenceException`
- **Collections**: `List<Investment>` with `Comparator`-based sorting
- **Threads**: `PortfolioMonitorThread` daemon thread (Observer pattern)

## Architecture Description
Three-layer architecture: Presentation (Swing GUI) → Service (PortfolioService, AlertManager, SortStrategy) → Persistence (InvestmentDAO → FileInvestmentRepository). The abstract `Investment` class and subclasses form the core domain model.

```
Investment (abstract)
    ├── Stock
    ├── MutualFund
    └── Bond
        ↓
PortfolioService
    ├── AlertManager (Observer)
    └── SortStrategy (Strategy)
        ↓
InvestmentDAO → FileInvestmentRepository
```

## How to Run

### Requirements
- Java 17 or higher
- IntelliJ IDEA (Community Edition recommended)

### Steps
1. Clone or open the project in IntelliJ IDEA
2. Mark `src/` as Sources Root (right-click → Mark Directory as → Sources Root)
3. Run `Main.java`

### From Command Line
```bash
cd PortfolioTracker
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Package Structure
```
src/
├── Main.java
├── model/
│   ├── Investment.java       (abstract)
│   ├── Stock.java
│   ├── MutualFund.java
│   └── Bond.java
├── service/
│   ├── PortfolioService.java
│   ├── PortfolioMonitorThread.java
│   ├── AlertListener.java
│   ├── SortStrategy.java
│   ├── SortByReturn.java
│   ├── SortByValue.java
│   └── SortByRisk.java
├── repository/
│   ├── InvestmentDAO.java
│   └── FileInvestmentRepository.java
├── exception/
│   ├── InvalidInvestmentException.java
│   └── PortfolioPersistenceException.java
├── ui/
│   ├── PortfolioDashboard.java
│   ├── AddInvestmentDialog.java
│   └── EditInvestmentDialog.java
└── util/
    └── IdGenerator.java
```
