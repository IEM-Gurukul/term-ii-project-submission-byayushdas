package service;

/**
 * Observer interface for portfolio threshold alerts.
 * Any UI component can register to receive alert notifications.
 */
public interface AlertListener {
    void onAlert(String message);
}
