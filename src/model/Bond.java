package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a bond investment.
 * Return is calculated based on annual coupon rate and holding period.
 */
public class Bond extends Investment implements Serializable {

    private static final long serialVersionUID = 4L;

    private double couponRate;      // Annual interest rate (%)
    private LocalDate maturityDate;
    private String issuer;

    public Bond(String id, String name, String issuer,
                double faceValue, double couponRate,
                int quantity, LocalDate purchaseDate, LocalDate maturityDate) {
        super(id, name, faceValue, quantity, purchaseDate);
        this.couponRate = couponRate;
        this.maturityDate = maturityDate;
        this.issuer = issuer;
    }

    /**
     * Current value = face value * quantity (bonds trade at par here).
     */
    @Override
    public double getCurrentValue() {
        return getPurchasePrice() * getQuantity();
    }

    /**
     * Return = accumulated coupon payments based on holding period.
     */
    @Override
    public double calculateReturn() {
        long daysHeld = java.time.temporal.ChronoUnit.DAYS.between(getPurchaseDate(), LocalDate.now());
        double years = daysHeld / 365.0;
        return getTotalInvested() * (couponRate / 100) * years;
    }

    @Override
    public double getReturnPercentage() {
        double invested = getTotalInvested();
        if (invested == 0) return 0;
        return (calculateReturn() / invested) * 100;
    }

    @Override
    public String getRiskLevel() {
        if (couponRate > 10) return "High";
        if (couponRate > 6)  return "Medium";
        return "Low";
    }

    @Override
    public String getType() { return "Bond"; }

    public double getCouponRate()               { return couponRate; }
    public LocalDate getMaturityDate()          { return maturityDate; }
    public String getIssuer()                   { return issuer; }
    public void setCouponRate(double rate)      { this.couponRate = rate; }

    @Override
    public String toString() {
        return super.toString() + " | Issuer: " + issuer + " | Coupon: " + couponRate + "% | Matures: " + maturityDate;
    }
}
