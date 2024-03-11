package Viewv2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PortfolioManagementOptionsWindow extends JFrame {
  private JButton examinePortfolioButton;
  private JButton calculateValueButton;
  private JButton savePortfolioButton;

  public PortfolioManagementOptionsWindow() {
    super("Portfolio Management Options");
    initializeComponents();
    layoutComponents();
    pack();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window
  }

  private void initializeComponents() {
    examinePortfolioButton = new JButton("Examine Portfolio");
    calculateValueButton = new JButton("Calculate Value on Date");
    savePortfolioButton = new JButton("Save Portfolio");
  }

  private void layoutComponents() {
    setLayout(new FlowLayout()); // Use FlowLayout for simplicity
    add(examinePortfolioButton);
    add(calculateValueButton);
    add(savePortfolioButton);
  }

  public void addExaminePortfolioListener(ActionListener listener) {
    examinePortfolioButton.addActionListener(listener);
  }

  public void addCalculateValueListener(ActionListener listener) {
    calculateValueButton.addActionListener(listener);
  }

  public void addSavePortfolioListener(ActionListener listener) {
    savePortfolioButton.addActionListener(listener);
  }
}
