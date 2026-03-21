package ui;

import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog for editing an existing investment.
 * Pre-populates fields from the selected investment.
 */
public class EditInvestmentDialog extends JDialog {

    private Investment result = null;
    private final Investment original;

    private static final Color BG     = new Color(30, 30, 50);
    private static final Color FG     = new Color(230, 230, 240);
    private static final Color ACCENT = new Color(99, 179, 237);

    private JTextField nameField;
    private JTextField purchasePriceField;
    private JTextField quantityField;

    // Type-specific
    private JTextField marketPriceField;   // Stock
    private JTextField currentNAVField;    // MutualFund
    private JTextField categoryField;      // MutualFund
    private JTextField couponRateField;    // Bond

    public EditInvestmentDialog(Frame parent, Investment investment) {
        super(parent, "Edit Investment — " + investment.getName(), true);
        this.original = investment;
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setBackground(BG);

        // Common fields — pre-populated
        nameField         = styledField(original.getName());
        purchasePriceField = styledField(String.valueOf(original.getPurchasePrice()));
        quantityField     = styledField(String.valueOf(original.getQuantity()));

        fields.add(styledLabel("Name:"));           fields.add(nameField);
        fields.add(styledLabel("Purchase Price:")); fields.add(purchasePriceField);
        fields.add(styledLabel("Quantity:"));        fields.add(quantityField);

        // Type-specific fields
        if (original instanceof Stock stock) {
            marketPriceField = styledField(String.valueOf(stock.getCurrentMarketPrice()));
            fields.add(styledLabel("Current Market Price:")); fields.add(marketPriceField);
        } else if (original instanceof MutualFund fund) {
            currentNAVField = styledField(String.valueOf(fund.getCurrentNAV()));
            categoryField   = styledField(fund.getCategory());
            fields.add(styledLabel("Current NAV:"));  fields.add(currentNAVField);
            fields.add(styledLabel("Category:"));     fields.add(categoryField);
        } else if (original instanceof Bond bond) {
            couponRateField = styledField(String.valueOf(bond.getCouponRate()));
            fields.add(styledLabel("Coupon Rate (%):")); fields.add(couponRateField);
        }

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG);
        JButton btnOk = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnOk, ACCENT);
        styleButton(btnCancel, new Color(180, 80, 80));
        btnOk.addActionListener(e -> onOK());
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);

        main.add(fields, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);
        setMinimumSize(new Dimension(400, 300));
    }

    private void onOK() {
        try {
            String name          = nameField.getText().trim();
            double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
            int    quantity      = Integer.parseInt(quantityField.getText().trim());

            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
            if (purchasePrice <= 0) throw new IllegalArgumentException("Purchase price must be > 0.");
            if (quantity <= 0)      throw new IllegalArgumentException("Quantity must be > 0.");

            if (original instanceof Stock stock) {
                double mktPrice = Double.parseDouble(marketPriceField.getText().trim());
                result = new Stock(stock.getId(), name, stock.getTickerSymbol(),
                        purchasePrice, mktPrice, quantity, stock.getPurchaseDate());

            } else if (original instanceof MutualFund fund) {
                double nav = Double.parseDouble(currentNAVField.getText().trim());
                String cat = categoryField.getText().trim();
                result = new MutualFund(fund.getId(), name, fund.getFundHouse(), cat,
                        purchasePrice, nav, quantity, fund.getPurchaseDate());

            } else if (original instanceof Bond bond) {
                double coupon = Double.parseDouble(couponRateField.getText().trim());
                result = new Bond(bond.getId(), name, bond.getIssuer(),
                        purchasePrice, coupon, quantity, bond.getPurchaseDate(), bond.getMaturityDate());
            }

            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Investment getResult() { return result; }

    private JTextField styledField(String value) {
        JTextField f = new JTextField(value);
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
        btn.setBackground(BG);
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
