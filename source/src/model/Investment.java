package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Abstract base class for all investment types.
 * Defines shared attributes and enforces contract for subclasses.
 */
public abstract class Investment implements Serializable, Comparable<Investment> {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double purchasePrice;
    private int quantity;
    private LocalDate purchaseDate;

    public Investment(String id, String name, double purchasePrice, int quantity, LocalDate purchaseDate) {
        this.id = id;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }

    // Abstract methods — each subclass must define its own logic
    public abstract double getCurrentValue();
    public abstract double calculateReturn();
    public abstract String getRiskLevel();
    public abstract String getType();

    // Return percentage
    public double getReturnPercentage() {
        double invested = purchasePrice * quantity;
        if (invested == 0) return 0;
        return ((getCurrentValue() - invested) / invested) * 100;
    }

    public double getTotalInvested() {
        return purchasePrice * quantity;
    }

    // Natural ordering by return percentage descending
    @Override
    public int compareTo(Investment other) {
        return Double.compare(other.getReturnPercentage(), this.getReturnPercentage());
    }

    // Getters & Setters
    public String getId()                        { return id; }
    public String getName()                      { return name; }
    public double getPurchasePrice()             { return purchasePrice; }
    public int getQuantity()                     { return quantity; }
    public LocalDate getPurchaseDate()           { return purchaseDate; }
    public void setName(String name)             { this.name = name; }
    public void setPurchasePrice(double price)   { this.purchasePrice = price; }
    public void setQuantity(int quantity)        { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Type: %s | Invested: %.2f | Current: %.2f | Return: %.2f%%",
                id, name, getType(), getTotalInvested(), getCurrentValue(), getReturnPercentage());
    }
}
