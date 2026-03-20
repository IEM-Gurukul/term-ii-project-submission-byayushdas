package service;

import model.Investment;
import java.util.Comparator;

/** Sorts by return percentage descending */
public class SortByReturn implements SortStrategy {
    @Override
    public Comparator<Investment> getComparator() {
        return Comparator.comparingDouble(Investment::getReturnPercentage).reversed();
    }
    @Override
    public String getLabel() { return "Return %"; }
}
