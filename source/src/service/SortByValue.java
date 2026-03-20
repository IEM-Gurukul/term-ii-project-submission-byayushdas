package service;

import model.Investment;
import java.util.Comparator;

/** Sorts by current value descending */
public class SortByValue implements SortStrategy {
    @Override
    public Comparator<Investment> getComparator() {
        return Comparator.comparingDouble(Investment::getCurrentValue).reversed();
    }
    @Override
    public String getLabel() { return "Current Value"; }
}
