package service;

/**
 * Background daemon thread that periodically checks portfolio thresholds.
 * Runs every 30 seconds without blocking the UI thread.
 */
public class PortfolioMonitorThread extends Thread {

    private final PortfolioService portfolioService;
    private volatile boolean running = true;
    private static final int INTERVAL_MS = 30_000; // 30 seconds

    public PortfolioMonitorThread(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
        setDaemon(true); // Dies when main application exits
        setName("PortfolioMonitor");
    }

    @Override
    public void run() {
        while (running) {
            try {
                portfolioService.checkAllThresholds();
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    public void stopMonitor() {
        running = false;
        interrupt();
    }
}
