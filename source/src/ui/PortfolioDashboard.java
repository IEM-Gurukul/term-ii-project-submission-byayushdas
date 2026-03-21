package ui;

import exception.InvalidInvestmentException;
import exception.PortfolioPersistenceException;
import model.Investment;
import model.Stock;
import model.MutualFund;
import model.Bond;
import repository.FileInvestmentRepository;
import service.*;
import util.IdGenerator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Main application window — Portfolio Dashboard.
 * Presentation layer: no business logic here.
 */
public class PortfolioDashboard extends JFrame implements AlertListener {

    private final PortfolioService service;
    private final PortfolioMonitorThread monitorThread;

    // Summary labels
    private JLabel lblTotalInvested;
    private JLabel lblCurrentValue;
    private JLabel lblTotalReturn;
    private JLabel lblReturnPct;
    private JLabel lblAlertBanner;

    // Table
    private DefaultTableModel tableModel;
    private JTable holdingsTable;

    // Sort combo
    private JComboBox<String> sortCombo;

    private static final Color BG_DARK    = new Color(18, 18, 35);
    private static final Color BG_CARD    = new Color(30, 30, 50);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color GREEN      = new Color(72, 199, 142);
    private static final Color RED        = new Color(252, 110, 110);
    private static final Color TEXT_WHITE = new Color(230, 230, 240);
    private static final Color TEXT_MUTED = new Color(150, 150, 170);

    public PortfolioDashboard() {
        service = new PortfolioService(
                new FileInvestmentRepository("data/portfolio.dat")
        );
        service.addAlertListener(this);

        // Load saved data
        try {
            service.loadPortfolio();
        } catch (PortfolioPersistenceException e) {
            showError("Could not load saved portfolio: " + e.getMessage());
        }

        // Start background monitor thread
        monitorThread = new PortfolioMonitorThread(service);
        monitorThread.start();

        buildUI();
        refreshTable();
        refreshSummary();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });
    }

    // ── UI Construction ───────────────────────────────────────────

    private void buildUI() {
        setTitle("Personal Investment Portfolio Tracker");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomBar(),  BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("📈  Investment Portfolio Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT);

        lblAlertBanner = new JLabel(" ");
        lblAlertBanner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlertBanner.setForeground(RED);
        lblAlertBanner.setHorizontalAlignment(SwingConstants.RIGHT);

        bar.add(title, BorderLayout.WEST);
        bar.add(lblAlertBanner, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(14, 16, 6, 16));

        center.add(buildSummaryPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(),   BorderLayout.CENTER);
        return center;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(BG_DARK);

        lblTotalInvested = makeSummaryCard("Total Invested",  "₹ 0.00",   TEXT_WHITE);
        lblCurrentValue  = makeSummaryCard("Current Value",   "₹ 0.00",   TEXT_WHITE);
        lblTotalReturn   = makeSummaryCard("Total Return",    "₹ 0.00",   TEXT_WHITE);
        lblReturnPct     = makeSummaryCard("Return %",        "0.00 %",   TEXT_WHITE);

        panel.add(wrapCard("Total Invested", lblTotalInvested));
        panel.add(wrapCard("Current Value",  lblCurrentValue));
        panel.add(wrapCard("Total Return",   lblTotalReturn));
        panel.add(wrapCard("Return %",       lblReturnPct));
        return panel;
    }

    private JLabel makeSummaryCard(String title, String value, Color color) {
        JLabel lbl = new JLabel(value, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel wrapCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 60, 90), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_MUTED);
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG_DARK);

        JButton btnAdd    = makeButton("+ Add",    ACCENT);
        JButton btnEdit   = makeButton("✏ Edit",   new Color(120, 140, 200));
        JButton btnDelete = makeButton("✕ Delete", RED);
        JButton btnThresh = makeButton("⚙ Alerts", new Color(200, 160, 80));

        sortCombo = new JComboBox<>(new String[]{"Sort: Return %", "Sort: Value", "Sort: Risk"});
        sortCombo.setBackground(BG_CARD);
        sortCombo.setForeground(TEXT_WHITE);
        sortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(sortCombo);
        toolbar.add(btnThresh);

        // Table
        String[] columns = {"ID", "Name", "Type", "Invested (₹)", "Current (₹)", "Return (₹)", "Return %", "Risk"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        holdingsTable = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(holdingsTable);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(new LineBorder(new Color(60, 60, 90), 1));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);

        // Button actions
        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnThresh.addActionListener(e -> openThresholdDialog());
        sortCombo.addActionListener(e -> applySortAndRefresh());

        return panel;
    }

    private JButton makeButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg, 1, true),
                new EmptyBorder(5, 14, 5, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        bar.setBackground(BG_CARD);
        JButton btnSave = makeButton("💾 Save Portfolio", GREEN);
        btnSave.addActionListener(e -> savePortfolio());
        bar.add(btnSave);
        return bar;
    }

    private void styleTable() {
        holdingsTable.setBackground(BG_CARD);
        holdingsTable.setForeground(TEXT_WHITE);
        holdingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        holdingsTable.setRowHeight(30);
        holdingsTable.setGridColor(new Color(50, 50, 70));
        holdingsTable.setSelectionBackground(new Color(60, 80, 130));
        holdingsTable.setSelectionForeground(Color.WHITE);
        holdingsTable.getTableHeader().setBackground(new Color(40, 40, 65));
        holdingsTable.getTableHeader().setForeground(ACCENT);
        holdingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        holdingsTable.setShowVerticalLines(false);

        // Column widths
        int[] widths = {80, 160, 100, 110, 110, 100, 90, 70};
        for (int i = 0; i < widths.length; i++) {
            holdingsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Color return % column
        holdingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (value != null) {
                    String v = value.toString().replace("%", "").trim();
                    try {
                        double d = Double.parseDouble(v);
                        c.setForeground(d >= 0 ? GREEN : RED);
                    } catch (NumberFormatException ignored) {}
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });
    }

    // ── Data Refresh ──────────────────────────────────────────────

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Investment> sorted = service.getSortedPortfolio();
        for (Investment inv : sorted) {
            tableModel.addRow(new Object[]{
                    inv.getId(),
                    inv.getName(),
                    inv.getType(),
                    String.format("%.2f", inv.getTotalInvested()),
                    String.format("%.2f", inv.getCurrentValue()),
                    String.format("%.2f", inv.calculateReturn()),
                    String.format("%.2f%%", inv.getReturnPercentage()),
                    inv.getRiskLevel()
            });
        }
    }

    private void refreshSummary() {
        lblTotalInvested.setText(String.format("₹ %.2f", service.getTotalInvested()));
        lblCurrentValue.setText(String.format("₹ %.2f", service.getTotalCurrentValue()));

        double ret = service.getTotalReturn();
        double retPct = service.getTotalReturnPercentage();

        lblTotalReturn.setText(String.format("₹ %.2f", ret));
        lblTotalReturn.setForeground(ret >= 0 ? GREEN : RED);

        lblReturnPct.setText(String.format("%.2f %%", retPct));
        lblReturnPct.setForeground(retPct >= 0 ? GREEN : RED);
    }

    private void applySortAndRefresh() {
        int idx = sortCombo.getSelectedIndex();
        service.setSortStrategy(switch (idx) {
            case 1  -> new SortByValue();
            case 2  -> new SortByRisk();
            default -> new SortByReturn();
        });
        refreshTable();
    }

    // ── Dialogs ───────────────────────────────────────────────────

    private void openAddDialog() {
        AddInvestmentDialog dialog = new AddInvestmentDialog(this);
        dialog.setVisible(true);
        Investment inv = dialog.getResult();
        if (inv != null) {
            try {
                service.addInvestment(inv);
                refreshTable();
                refreshSummary();
            } catch (InvalidInvestmentException e) {
                showError(e.getMessage());
            }
        }
    }

    private void openEditDialog() {
        int row = holdingsTable.getSelectedRow();
        if (row < 0) { showError("Please select a holding to edit."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        service.findById(id).ifPresent(inv -> {
            EditInvestmentDialog dialog = new EditInvestmentDialog(this, inv);
            dialog.setVisible(true);
            Investment updated = dialog.getResult();
            if (updated != null) {
                try {
                    service.updateInvestment(updated);
                    refreshTable();
                    refreshSummary();
                } catch (InvalidInvestmentException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void deleteSelected() {
        int row = holdingsTable.getSelectedRow();
        if (row < 0) { showError("Please select a holding to delete."); return; }
        String id   = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + name + "\" from your portfolio?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.removeInvestment(id);
                refreshTable();
                refreshSummary();
            } catch (InvalidInvestmentException e) {
                showError(e.getMessage());
            }
        }
    }

    private void openThresholdDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBackground(BG_CARD);
        JTextField gainField = new JTextField(String.valueOf(service.getGainThreshold()));
        JTextField lossField = new JTextField(String.valueOf(service.getLossThreshold()));
        panel.add(styledLabel("Gain Alert Threshold (%):")); panel.add(gainField);
        panel.add(styledLabel("Loss Alert Threshold (%):")); panel.add(lossField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configure Alert Thresholds",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double gain = Double.parseDouble(gainField.getText().trim());
                double loss = Double.parseDouble(lossField.getText().trim());
                service.setGainThreshold(gain);
                service.setLossThreshold(loss);
            } catch (NumberFormatException e) {
                showError("Please enter valid numeric thresholds.");
            }
        }
    }

    // ── Persistence ───────────────────────────────────────────────

    private void savePortfolio() {
        try {
            service.savePortfolio();
            lblAlertBanner.setForeground(GREEN);
            lblAlertBanner.setText("✔ Portfolio saved successfully.");
        } catch (PortfolioPersistenceException e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    private void saveAndExit() {
        try {
            service.savePortfolio();
        } catch (PortfolioPersistenceException e) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Could not save portfolio. Exit anyway?",
                    "Save Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        monitorThread.stopMonitor();
        dispose();
        System.exit(0);
    }

    // ── AlertListener (Observer) ──────────────────────────────────

    @Override
    public void onAlert(String message) {
        SwingUtilities.invokeLater(() -> {
            lblAlertBanner.setForeground(RED);
            lblAlertBanner.setText("⚠ " + message);
        });
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_WHITE);
        return lbl;
    }
}
