package service;

import exception.InvalidInvestmentException;
import exception.PortfolioPersistenceException;
import model.Investment;
import repository.InvestmentDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Central service layer coordinating all portfolio operations.
 * Handles business logic, validation, sorting, and alert evaluation.
 */
public class PortfolioService {

    private final List<Investment> portfolio;
    private final InvestmentDAO dao;
    private final List<AlertListener> alertListeners;

    private SortStrategy sortStrategy;
    private double gainThreshold  = 20.0;  // Alert when gain % exceeds this
    private double lossThreshold  = -10.0; // Alert when loss % drops below this

    public PortfolioService(InvestmentDAO dao) {
        this.dao = dao;
        this.portfolio = new ArrayList<>();
        this.alertListeners = new ArrayList<>();
        this.sortStrategy = new SortByReturn(); // Default sort
    }

    // ── Observer registration ──────────────────────────────────────
    public void addAlertListener(AlertListener listener) {
        alertListeners.add(listener);
    }

    private void fireAlert(String message) {
        for (AlertListener listener : alertListeners) {
            listener.onAlert(message);
        }
    }

    // ── CRUD Operations ───────────────────────────────────────────
    public void addInvestment(Investment investment) throws InvalidInvestmentException {
        validateInvestment(investment);
        portfolio.add(investment);
        checkThresholds(investment);
    }

    public void updateInvestment(Investment updated) throws InvalidInvestmentException {
        validateInvestment(updated);
        for (int i = 0; i < portfolio.size(); i++) {
            if (portfolio.get(i).getId().equals(updated.getId())) {
                portfolio.set(i, updated);
                checkThresholds(updated);
                return;
            }
        }
        throw new InvalidInvestmentException("Investment with ID '" + updated.getId() + "' not found.");
    }

    public void removeInvestment(String id) throws InvalidInvestmentException {
        boolean removed = portfolio.removeIf(inv -> inv.getId().equals(id));
        if (!removed) {
            throw new InvalidInvestmentException("Investment with ID '" + id + "' not found.");
        }
    }

    public Optional<Investment> findById(String id) {
        return portfolio.stream().filter(inv -> inv.getId().equals(id)).findFirst();
    }

    // ── Portfolio Summary ─────────────────────────────────────────
    public double getTotalInvested() {
        return portfolio.stream().mapToDouble(Investment::getTotalInvested).sum();
    }

    public double getTotalCurrentValue() {
        return portfolio.stream().mapToDouble(Investment::getCurrentValue).sum();
    }

    public double getTotalReturn() {
        return getTotalCurrentValue() - getTotalInvested();
    }

    public double getTotalReturnPercentage() {
        double invested = getTotalInvested();
        if (invested == 0) return 0;
        return (getTotalReturn() / invested) * 100;
    }

    // ── Sorted View ───────────────────────────────────────────────
    public List<Investment> getSortedPortfolio() {
        List<Investment> sorted = new ArrayList<>(portfolio);
        Collections.sort(sorted, sortStrategy.getComparator());
        return sorted;
    }

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public String getCurrentSortLabel() {
        return sortStrategy.getLabel();
    }

    // ── Threshold Alerts ──────────────────────────────────────────
    public void checkThresholds(Investment inv) {
        double ret = inv.getReturnPercentage();
        if (ret >= gainThreshold) {
            fireAlert("GAIN ALERT: " + inv.getName() + " has gained " +
                    String.format("%.2f", ret) + "% — consider reviewing your position.");
        } else if (ret <= lossThreshold) {
            fireAlert("LOSS ALERT: " + inv.getName() + " has lost " +
                    String.format("%.2f", Math.abs(ret)) + "% — threshold breached.");
        }
    }

    public void checkAllThresholds() {
        portfolio.forEach(this::checkThresholds);
    }

    public void setGainThreshold(double threshold)  { this.gainThreshold = threshold; }
    public void setLossThreshold(double threshold)  { this.lossThreshold = threshold; }
    public double getGainThreshold()                { return gainThreshold; }
    public double getLossThreshold()                { return lossThreshold; }

    // ── Persistence ───────────────────────────────────────────────
    public void savePortfolio() throws PortfolioPersistenceException {
        dao.save(portfolio);
    }

    public void loadPortfolio() throws PortfolioPersistenceException {
        portfolio.clear();
        portfolio.addAll(dao.load());
    }

    // ── Validation ────────────────────────────────────────────────
    private void validateInvestment(Investment inv) throws InvalidInvestmentException {
        if (inv == null) {
            throw new InvalidInvestmentException("Investment cannot be null.");
        }
        if (inv.getName() == null || inv.getName().isBlank()) {
            throw new InvalidInvestmentException("Investment name cannot be empty.");
        }
        if (inv.getPurchasePrice() <= 0) {
            throw new InvalidInvestmentException("Purchase price must be greater than zero.");
        }
        if (inv.getQuantity() <= 0) {
            throw new InvalidInvestmentException("Quantity must be greater than zero.");
        }
        if (inv.getPurchaseDate() == null) {
            throw new InvalidInvestmentException("Purchase date cannot be null.");
        }
    }

    public List<Investment> getPortfolio() {
        return Collections.unmodifiableList(portfolio);
    }

    public boolean isEmpty() {
        return portfolio.isEmpty();
    }
}
