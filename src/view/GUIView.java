package View;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUIView extends JFrame {
  private JPanel cards; // a panel that uses CardLayout
  private final String MAIN_MENU = "Main Menu";
  private final String NORMAL_PORTFOLIO = "Normal Portfolio";
  private final String FLEXIBLE_PORTFOLIO = "Flexible Portfolio";
  private JButton createPortfolioButton;
  private JButton examinePortfolioButton;
  private JButton calculatePortfolioValueButton;
  private JButton savePortfolioButton;
  private JButton loadPortfolioButton;
  private JButton graphButton;
  private JButton inspectStockPerformanceButton;
  private JButton calculateXDayMovingAverageButton;
  private JButton addButton;
  private JButton sellButton;
  private JButton investmentButton;
  private JButton backButton;

  public GUIView() {
    // Create the main frame
    setTitle("Portfolio Management System");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Set up the main menu bar
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(e -> System.exit(0));
    menu.add(exitItem);
    menuBar.add(menu);
    setJMenuBar(menuBar);

    // Create the card layout and the cards
    cards = new JPanel(new CardLayout());
    JPanel mainMenuPanel = createMainMenuPanel();
    JPanel normalPortfolioPanel = createNormalPortfolioPanel();
    JPanel flexiblePortfolioPanel = createFlexiblePortfolioPanel();

    cards.add(mainMenuPanel, MAIN_MENU);
    cards.add(normalPortfolioPanel, NORMAL_PORTFOLIO);
    cards.add(flexiblePortfolioPanel, FLEXIBLE_PORTFOLIO);

    // Add the card panel to the frame
    add(cards, BorderLayout.CENTER);

    // Show the frame
    setVisible(true);


  }

  private JPanel createMainMenuPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Single column layout

    JButton normalButton = new JButton("Normal Portfolio Management");
    normalButton.addActionListener(e -> switchToCard(NORMAL_PORTFOLIO));
    panel.add(normalButton);

    JButton flexibleButton = new JButton("Flexible Portfolio Management");
    flexibleButton.addActionListener(e -> switchToCard(FLEXIBLE_PORTFOLIO));
    panel.add(flexibleButton);

    JButton exitButton = new JButton("Exit");
    exitButton.addActionListener(e -> System.exit(0));
    panel.add(exitButton);

    return panel;
  }

  private JPanel createNormalPortfolioPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Single column layout

    createPortfolioButton = new JButton("Create a new portfolio");
    // Add ActionListener for createPortfolioButton
    panel.add(createPortfolioButton);

    examinePortfolioButton = new JButton("Examine a portfolio");
    // Add ActionListener for examinePortfolioButton
    panel.add(examinePortfolioButton);

    calculatePortfolioValueButton = new JButton("Calculate portfolio value");
    // Add ActionListener for calculatePortfolioValueButton
    panel.add(calculatePortfolioValueButton);

    savePortfolioButton = new JButton("Save portfolio");
    // Add ActionListener for savePortfolioButton
    panel.add(savePortfolioButton);

    loadPortfolioButton = new JButton("Load portfolio");
    // Add ActionListener for loadPortfolioButton
    panel.add(loadPortfolioButton);

    graphButton = new JButton("Graph");
    // Add ActionListener for graphButton
    panel.add(graphButton);

    inspectStockPerformanceButton = new JButton("Inspect Stock performance");
    // Add ActionListener for inspectStockPerformanceButton
    panel.add(inspectStockPerformanceButton);

    calculateXDayMovingAverageButton = new JButton("Calculate X-Day Moving Average");
    // Add ActionListener for calculateXDayMovingAverageButton
    panel.add(calculateXDayMovingAverageButton);

    backButton = new JButton("Back to Main Menu");
    backButton.addActionListener(e -> switchToCard(MAIN_MENU));
    panel.add(backButton);

    return panel;
  }

  private JPanel createFlexiblePortfolioPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Single column layout

    // Add buttons for flexible portfolio actions here, similar to the NormalPortfolioPanel
    // For example:
    addButton = new JButton("Add Stock to Portfolio");
    // Add ActionListener for addButton
    panel.add(addButton);

    sellButton = new JButton("Sell Stock from Portfolio");
    // Add ActionListener for sellButton
    panel.add(sellButton);

    investmentButton = new JButton("Calculate Investment");
    // Add ActionListener for investmentButton
    panel.add(investmentButton);

    // Include other buttons for actions like saving/loading portfolios, graphing, inspecting stock performance, calculating moving averages, crossover days, etc.

     backButton = new JButton("Back to Main Menu");
    backButton.addActionListener(e -> switchToCard(MAIN_MENU));
    panel.add(backButton);

    return panel;
  }

  private void switchToCard(String cardName) {
    CardLayout cl = (CardLayout)(cards.getLayout());
    cl.show(cards, cardName);
  }

  public void setCreatePortfolioAction(ActionListener actionListener) {
    createPortfolioButton.addActionListener(actionListener);
  }
  public void setExaminePortfolioButtonListener(ActionListener listener) {
    examinePortfolioButton.addActionListener(listener);
  }

  public void setCalculatePortfolioValueButtonListener(ActionListener listener) {
    calculatePortfolioValueButton.addActionListener(listener);
  }

  public void setSavePortfolioButtonListener(ActionListener listener) {
    savePortfolioButton.addActionListener(listener);
  }

  public void setLoadPortfolioButtonListener(ActionListener listener) {
    loadPortfolioButton.addActionListener(listener);
  }

  public void setGraphButtonListener(ActionListener listener) {
    graphButton.addActionListener(listener);
  }

  public void setInspectStockPerformanceButtonListener(ActionListener listener) {
    inspectStockPerformanceButton.addActionListener(listener);
  }

  public void setCalculateXDayMovingAverageButtonListener(ActionListener listener) {
    calculateXDayMovingAverageButton.addActionListener(listener);
  }

  public void setAddButtonListener(ActionListener listener) {
    addButton.addActionListener(listener);
  }

  public void setSellButtonListener(ActionListener listener) {
    sellButton.addActionListener(listener);
  }

  public void setInvestmentButtonListener(ActionListener listener) {
    investmentButton.addActionListener(listener);
  }

  public void setBackButtonListener(ActionListener listener) {
    backButton.addActionListener(listener);
  }

  /**
   * Shows a message dialog.
   *
   * @param message The message to display.
   * @param title The title of the message dialog.
   * @param messageType The type of message (e.g., JOptionPane.INFORMATION_MESSAGE, JOptionPane.ERROR_MESSAGE).
   */
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * Shows an error message dialog.
   *
   * @param errorMessage The error message to display.
   */
  public void showErrorMessage(String errorMessage) {
    showMessage(errorMessage);
  }

  /**
   * Shows an information message dialog.
   *
   * @param infoMessage The information message to display.
   */
  public void showInfoMessage(String infoMessage) {
    showMessage(infoMessage);
  }


}
