import ui.PortfolioDashboard;
import javax.swing.*;

/**
 * Application entry point.
 * Launches the Portfolio Dashboard on the Swing Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            PortfolioDashboard dashboard = new PortfolioDashboard();
            dashboard.setVisible(true);
        });
    }
}
