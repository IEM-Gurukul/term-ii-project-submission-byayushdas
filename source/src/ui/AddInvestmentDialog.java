package ui;

import model.*;
import util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding a new investment.
 * Dynamically shows fields based on selected investment type.
 */
public class AddInvestmentDialog extends JDialog {

    private Investment result = null;

    private static final Color BG     = new Color(30, 30, 50);
    private static final Color FG     = new Color(230, 230, 240);
    private static final Color ACCENT = new Color(99, 179, 237);

    // Common fields
    private JComboBox<String> typeCombo;
    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField quantityField;
    private JTextField purchaseDateField;

    // Stock-specific
    private JPanel stockPanel;
    private JTextField tickerField;
    private JTextField marketPriceField;

    // MutualFund-specific
    private JPanel fundPanel;
    private JTextField fundHouseField;
    private JTextField categoryField;
    private JTextField currentNAVField;

    // Bond-specific
    private JPanel bondPanel;
    private JTextField issuerField;
    private JTextField couponRateField;
    private JTextField maturityDateField;

    private JPanel dynamicPanel;

    public AddInvestmentDialog(Frame parent) {
        super(parent, "Add New Investment", true);
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setBackground(BG);
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Type selector
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        typePanel.setBackground(BG);
        typeCombo = new JComboBox<>(new String[]{"Stock", "Mutual Fund", "Bond"});
        typeCombo.setBackground(new Color(45, 45, 70));
        typeCombo.setForeground(FG);
        typePanel.add(styledLabel("Investment Type:"));
        typePanel.add(typeCombo);

        // Common fields
        JPanel commonPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        commonPanel.setBackground(BG);
        nameField         = styledField(); purchasePriceField = styledField();
        quantityField     = styledField(); purchaseDateField  = styledField();
        purchaseDateField.setText(LocalDate.now().toString());

        commonPanel.add(styledLabel("Name:"));          commonPanel.add(nameField);
        commonPanel.add(styledLabel("Purchase Price:")); commonPanel.add(purchasePriceField);
        commonPanel.add(styledLabel("Quantity/Units:")); commonPanel.add(quantityField);
        commonPanel.add(styledLabel("Purchase Date (YYYY-MM-DD):")); commonPanel.add(purchaseDateField);

        // Dynamic panels
        stockPanel = buildStockPanel();
        fundPanel  = buildFundPanel();
        bondPanel  = buildBondPanel();

        dynamicPanel = new JPanel(new CardLayout());
        dynamicPanel.setBackground(BG);
        dynamicPanel.add(stockPanel, "Stock");
        dynamicPanel.add(fundPanel,  "Mutual Fund");
        dynamicPanel.add(bondPanel,  "Bond");

        typeCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) dynamicPanel.getLayout();
            cl.show(dynamicPanel, (String) typeCombo.getSelectedItem());
        });

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG);
        JButton btnOk     = new JButton("Add");
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnOk, ACCENT);
        styleButton(btnCancel, new Color(180, 80, 80));
        btnOk.addActionListener(e -> onOK());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);

        main.add(typePanel,    BorderLayout.NORTH);
        main.add(commonPanel,  BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BG);
        south.add(dynamicPanel, BorderLayout.CENTER);
        south.add(btnPanel,     BorderLayout.SOUTH);
        main.add(south, BorderLayout.SOUTH);

        add(main);
        setMinimumSize(new Dimension(420, 480));
    }

    private JPanel buildStockPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        tickerField      = styledField();
        marketPriceField = styledField();
        p.add(styledLabel("Ticker Symbol:")); p.add(tickerField);
        p.add(styledLabel("Current Market Price:")); p.add(marketPriceField);
        return p;
    }

    private JPanel buildFundPanel() {
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        fundHouseField  = styledField();
        categoryField   = styledField(); categoryField.setText("Equity");
        currentNAVField = styledField();
        p.add(styledLabel("Fund House:"));        p.add(fundHouseField);
        p.add(styledLabel("Category (Equity/Debt/Hybrid):")); p.add(categoryField);
        p.add(styledLabel("Current NAV:"));       p.add(currentNAVField);
        return p;
    }

    private JPanel buildBondPanel() {
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        issuerField      = styledField();
        couponRateField  = styledField();
        maturityDateField = styledField();
        p.add(styledLabel("Issuer:"));             p.add(issuerField);
        p.add(styledLabel("Coupon Rate (%):")); p.add(couponRateField);
        p.add(styledLabel("Maturity Date (YYYY-MM-DD):")); p.add(maturityDateField);
        return p;
    }

    private void onOK() {
        try {
            String name          = nameField.getText().trim();
            double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
            int    quantity      = Integer.parseInt(quantityField.getText().trim());
            LocalDate date       = LocalDate.parse(purchaseDateField.getText().trim());
            String type          = (String) typeCombo.getSelectedItem();

            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
            if (purchasePrice <= 0) throw new IllegalArgumentException("Purchase price must be > 0.");
            if (quantity <= 0)      throw new IllegalArgumentException("Quantity must be > 0.");

            result = switch (type) {
                case "Stock" -> {
                    String ticker = tickerField.getText().trim();
                    double mktPrice = Double.parseDouble(marketPriceField.getText().trim());
                    if (ticker.isEmpty()) throw new IllegalArgumentException("Ticker symbol required.");
                    yield new Stock(IdGenerator.generate("STK"), name, ticker, purchasePrice, mktPrice, quantity, date);
                }
                case "Mutual Fund" -> {
                    String house    = fundHouseField.getText().trim();
                    String category = categoryField.getText().trim();
                    double nav      = Double.parseDouble(currentNAVField.getText().trim());
                    yield new MutualFund(IdGenerator.generate("MF"), name, house, category, purchasePrice, nav, quantity, date);
                }
                case "Bond" -> {
                    String issuer   = issuerField.getText().trim();
                    double coupon   = Double.parseDouble(couponRateField.getText().trim());
                    LocalDate mat   = LocalDate.parse(maturityDateField.getText().trim());
                    yield new Bond(IdGenerator.generate("BND"), name, issuer, purchasePrice, coupon, quantity, date, mat);
                }
                default -> throw new IllegalArgumentException("Unknown type.");
            };

            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Date format must be YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Investment getResult() { return result; }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(45, 45, 70));
        f.setForeground(FG);
        f.setCaretColor(FG);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 200));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(new Color(30, 30, 50));
        btn.setForeground(color);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(5, 16, 5, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
