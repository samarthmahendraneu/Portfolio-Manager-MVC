package Viewv2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PortfolioNameEntryWindow extends JFrame {
  private JTextField portfolioNameField;
  private JButton confirmButton;

  public PortfolioNameEntryWindow() {
    super("Enter Portfolio Name");
    initializeComponents();
    layoutComponents();
    pack();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window
  }

  private void initializeComponents() {
    portfolioNameField = new JTextField(20);
    confirmButton = new JButton("Confirm");
  }

  private void layoutComponents() {
    setLayout(new FlowLayout()); // Simple layout for this dialog
    add(new JLabel("Portfolio Name:"));
    add(portfolioNameField);
    add(confirmButton);
  }

  public void addConfirmButtonListener(ActionListener listener) {
    confirmButton.addActionListener(listener);
  }

  public String getPortfolioName() {
    return portfolioNameField.getText().trim();
  }

  public void clearField() {
    portfolioNameField.setText("");
  }
}
