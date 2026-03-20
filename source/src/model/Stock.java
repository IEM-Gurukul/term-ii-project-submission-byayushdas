package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a stock holding.
 * Return is based on current market price vs purchase price.
 */
public class Stock extends Investment implements Serializable {

    private static final long serialVersionUID = 2L;

    private String tickerSymbol;
    private double currentMarketPrice;

    public Stock(String id, String name, String tickerSymbol,
                 double purchasePrice, double currentMarketPrice,
                 int quantity, LocalDate purchaseDate) {
        super(id, name, purchasePrice, quantity, purchaseDate);
        this.tickerSymbol = tickerSymbol;
        this.currentMarketPrice = currentMarketPrice;
    }

    @Override
    public double getCurrentValue() {
        return currentMarketPrice * getQuantity();
    }

    @Override
    public double calculateReturn() {
        return getCurrentValue() - getTotalInvested();
    }

    @Override
    public String getRiskLevel() {
        double changePercent = getReturnPercentage();
        if (changePercent > 20) return "High";
        if (changePercent > 5)  return "Medium";
        return "Low";
    }

    @Override
    public String getType() { return "Stock"; }

    public String getTickerSymbol()                       { return tickerSymbol; }
    public double getCurrentMarketPrice()                 { return currentMarketPrice; }
    public void setCurrentMarketPrice(double price)       { this.currentMarketPrice = price; }
    public void setTickerSymbol(String tickerSymbol)      { this.tickerSymbol = tickerSymbol; }

    @Override
    public String toString() {
        return super.toString() + " | Ticker: " + tickerSymbol + " | Market Price: " + currentMarketPrice;
    }
}
