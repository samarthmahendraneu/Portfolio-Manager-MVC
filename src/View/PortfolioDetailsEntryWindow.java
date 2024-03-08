package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PortfolioDetailsEntryWindow extends JFrame {
  private JTextField tickerSymbolField;
  private JTextField dateField;
  private JTextField quantityField;
  private JButton addButton;
  private JButton endButton;

  public PortfolioDetailsEntryWindow() {
    super("Add Stock to Portfolio");
    initializeComponents();
    layoutComponents();
    pack();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window
  }

  private void initializeComponents() {
    tickerSymbolField = new JTextField(10);
    dateField = new JTextField(10);
    quantityField = new JTextField(10);
    addButton = new JButton("Add");
    endButton = new JButton("End");
  }

  private void layoutComponents() {
    setLayout(new GridLayout(5, 2)); // Simple grid layout
    add(new JLabel("Ticker Symbol:"));
    add(tickerSymbolField);
    add(new JLabel("Date (YYYY-MM-DD):"));
    add(dateField);
    add(new JLabel("Quantity:"));
    add(quantityField);
    add(addButton);
    add(endButton);
  }

  public void addAddButtonListener(ActionListener listener) {
    addButton.addActionListener(listener);
  }

  public void addEndButtonListener(ActionListener listener) {
    endButton.addActionListener(listener);
  }

  public String getTickerSymbol() {
    return tickerSymbolField.getText();
  }

  public String getDate() {
    return dateField.getText();
  }

  public String getQuantity() {
    return quantityField.getText();
  }

  public void clearFields() {
    tickerSymbolField.setText("");
    dateField.setText("");
    quantityField.setText("");
  }
}
