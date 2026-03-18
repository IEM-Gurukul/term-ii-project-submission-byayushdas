package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a mutual fund holding.
 * Return is calculated based on NAV (Net Asset Value) change.
 */
public class MutualFund extends Investment implements Serializable {

    private static final long serialVersionUID = 3L;

    private String fundHouse;
    private double currentNAV;
    private String category; // e.g. Equity, Debt, Hybrid

    public MutualFund(String id, String name, String fundHouse, String category,
                      double purchaseNAV, double currentNAV,
                      int units, LocalDate purchaseDate) {
        super(id, name, purchaseNAV, units, purchaseDate);
        this.fundHouse = fundHouse;
        this.currentNAV = currentNAV;
        this.category = category;
    }

    @Override
    public double getCurrentValue() {
        return currentNAV * getQuantity();
    }

    @Override
    public double calculateReturn() {
        return getCurrentValue() - getTotalInvested();
    }

    @Override
    public String getRiskLevel() {
        return switch (category.toLowerCase()) {
            case "equity"  -> "High";
            case "hybrid"  -> "Medium";
            case "debt"    -> "Low";
            default        -> "Medium";
        };
    }

    @Override
    public String getType() { return "Mutual Fund"; }

    public String getFundHouse()                  { return fundHouse; }
    public double getCurrentNAV()                 { return currentNAV; }
    public String getCategory()                   { return category; }
    public void setCurrentNAV(double nav)         { this.currentNAV = nav; }
    public void setCategory(String category)      { this.category = category; }

    @Override
    public String toString() {
        return super.toString() + " | Fund House: " + fundHouse + " | Category: " + category + " | NAV: " + currentNAV;
    }
}
