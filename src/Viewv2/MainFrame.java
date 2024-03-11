package Viewv2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
  private JButton createPortfolioButton;
  private JButton loadPortfolioButton;

  public MainFrame(String title) {
    super(title);
    initializeComponents();
    layoutComponents();
  }

  private void initializeComponents() {
    // Initialize buttons
    createPortfolioButton = new JButton("Create Portfolio");
    loadPortfolioButton = new JButton("Load Portfolio");

    // Set up the frame
    setSize(400, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window
  }

  private void layoutComponents() {
    // Configure layout and add components
    setLayout(new FlowLayout()); // Use FlowLayout for simplicity
    add(createPortfolioButton);
    add(loadPortfolioButton);
  }

  // Method to attach an ActionListener to the "Create Portfolio" button
  public void addCreatePortfolioListener(ActionListener listener) {
    createPortfolioButton.addActionListener(listener);
  }

  // Method to attach an ActionListener to the "Load Portfolio" button
  public void addLoadPortfolioListener(ActionListener listener) {
    loadPortfolioButton.addActionListener(listener);
  }
}
