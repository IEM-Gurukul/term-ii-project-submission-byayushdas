package service;

import model.Investment;
import java.util.Comparator;

/**
 * Strategy interface for sorting investments.
 * Implementations can be swapped at runtime.
 */
public interface SortStrategy {
    Comparator<Investment> getComparator();
    String getLabel();
}
