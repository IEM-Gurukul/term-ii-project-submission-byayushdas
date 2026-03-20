package service;

import model.Investment;
import java.util.Comparator;

/** Sorts by risk level: High → Medium → Low */
public class SortByRisk implements SortStrategy {

    private int riskOrder(String risk) {
        return switch (risk) {
            case "High"   -> 0;
            case "Medium" -> 1;
            default       -> 2;
        };
    }

    @Override
    public Comparator<Investment> getComparator() {
        return Comparator.comparingInt(inv -> riskOrder(inv.getRiskLevel()));
    }

    @Override
    public String getLabel() { return "Risk Level"; }
}
