package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import model.utilities.BarChartPanel;

public class GUIViewU extends JFrame implements GUIInterface {
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
  private JButton normalCreatePortfolioButton;
  private JButton normalExaminePortfolioButton;
  private JButton normalCalculatePortfolioValueButton;
  private JButton normalSavePortfolioButton;
  private  JButton normalLoadPortfolioButton;
  private JButton normalGraphButton;
  private JButton normalInspectStockPerformanceButton;
  private JButton normalCalculateXDayMovingAverageButton;
  private JButton crossoverDays;
  private JButton movingCrossoverDays;
  private JButton dollarCostAverage;


  public GUIViewU() {
    // Create the main frame
    setTitle("Portfolio Management System");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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


  }
  @Override
  public void displayMainMenu() {

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

    normalCreatePortfolioButton = new JButton("Create a new portfolio");
    // Add ActionListener for createPortfolioButton
    panel.add(normalCreatePortfolioButton);

     normalExaminePortfolioButton = new JButton("Examine a portfolio");
    // Add ActionListener for examinePortfolioButton
    panel.add(normalExaminePortfolioButton);

    normalCalculatePortfolioValueButton = new JButton("Calculate portfolio value");
    // Add ActionListener for calculatePortfolioValueButton
    panel.add(normalCalculatePortfolioValueButton);

    normalSavePortfolioButton = new JButton("Save portfolio");
    // Add ActionListener for savePortfolioButton
    panel.add(normalSavePortfolioButton);

    normalLoadPortfolioButton = new JButton("Load portfolio");
    // Add ActionListener for loadPortfolioButton
    panel.add(normalLoadPortfolioButton);

    normalGraphButton = new JButton("Graph");
    // Add ActionListener for graphButton
    panel.add(normalGraphButton);

    normalInspectStockPerformanceButton = new JButton("Inspect Stock performance");
    // Add ActionListener for inspectStockPerformanceButton
    panel.add(normalInspectStockPerformanceButton);

    normalCalculateXDayMovingAverageButton = new JButton("Calculate X-Day Moving Average");
    // Add ActionListener for calculateXDayMovingAverageButton
    panel.add(normalCalculateXDayMovingAverageButton);


    backButton = new JButton("Back to Main Menu");
    backButton.addActionListener(e -> switchToCard(MAIN_MENU));
    panel.add(backButton);

    return panel;
  }

  private JPanel createFlexiblePortfolioPanel() {
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
    addButton = new JButton("Add Stock to Portfolio");

    // Add ActionListener for addButton
    panel.add(addButton);

    sellButton = new JButton("Sell Stock from Portfolio");
    // Add ActionListener for sellButton
    panel.add(sellButton);

    investmentButton = new JButton("Calculate Investment");
    // Add ActionListener for investmentButton
    panel.add(investmentButton);

    crossoverDays = new JButton("Crossover Days");
    // Add ActionListener for investmentButton
    panel.add(crossoverDays);

    movingCrossoverDays = new JButton("Moving Crossover Days");
    // Add ActionListener for investmentButton
    panel.add(movingCrossoverDays);

    dollarCostAverage = new JButton("Dollar Cost Averaging");
    // Add ActionListener for investmentButton
    panel.add(dollarCostAverage);

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

  public void setCrossoverButtonListener(ActionListener listener) {
    crossoverDays.addActionListener(listener);
  }

  public void setMovingCrossoverButtonListener(ActionListener listener) {
    movingCrossoverDays.addActionListener(listener);
  }

  public void setDollarCostButtonListener(ActionListener listener) {
    dollarCostAverage.addActionListener(listener);
  }

  public void setnormalCreatePortfolioAction(ActionListener actionListener) {
    normalCreatePortfolioButton.addActionListener(actionListener);
  }
  public void setnormalExaminePortfolioButtonListener(ActionListener listener) {
    normalExaminePortfolioButton.addActionListener(listener);
  }

  public void setnormalCalculatePortfolioValueButtonListener(ActionListener listener) {
    normalCalculatePortfolioValueButton.addActionListener(listener);
  }

  public void setnormalSavePortfolioButtonListener(ActionListener listener) {
    normalSavePortfolioButton.addActionListener(listener);
  }

  public void setnormalLoadPortfolioButtonListener(ActionListener listener) {
    normalLoadPortfolioButton.addActionListener(listener);
  }

  public void setnormalGraphButtonListener(ActionListener listener) {
    normalGraphButton.addActionListener(listener);
  }

  public void setnormalInspectStockPerformanceButtonListener(ActionListener listener) {
    normalInspectStockPerformanceButton.addActionListener(listener);
  }

  public void setnormalCalculateXDayMovingAverageButtonListener(ActionListener listener) {
    normalCalculateXDayMovingAverageButton.addActionListener(listener);
  }


  public void displayAvailablePortfolios(List<String> portfolioNames) {
    StringBuilder message = new StringBuilder("Available portfolios:\n");
    for (String name : portfolioNames) {
      message.append(name).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  public void displayPortfolioDetails(String name, List<model.Tradable> stocks){
    StringBuilder message = new StringBuilder("Stocks in ").append(name).append(":\n");
    for (model.Tradable stock : stocks) {
      message.append(stock.getSymbol()).append(" - Quantity: ")
              .append(stock.getQuantity()).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  public void displayPortfolioValue(String name, String date, String value) {
    String message = "Value of the portfolio '" + name + "' on " + date + ": " + value;
    JOptionPane.showMessageDialog(null, message);
  }

  public void displayPortfolioInvestment(String name, String date, String value) {
    String message = "Investment of the portfolio '" + name + "' on " + date + ": " + value;
    JOptionPane.showMessageDialog(null, message);
  }

  public void displaySaveSuccess(String filePath) {
    String message = "Portfolios have been saved successfully to " + filePath;
    JOptionPane.showMessageDialog(null, message);
  }

  public void displayLoadSuccess() {
    String message = "Portfolios have been loaded successfully.";
    JOptionPane.showMessageDialog(null, message);
  }

  public void displayStockAdded(String portfolioName, String stockSymbol, int quantity) {
    String message = "Stock " + stockSymbol + " with quantity " + quantity +
            " added to portfolio " + portfolioName;
    JOptionPane.showMessageDialog(null, message);
  }

  public void displayStockSold(String portfolioName, String symbol, int quantity) {
    String message = "Stock " + symbol + " with quantity " + quantity +
            " sold from portfolio " + portfolioName;
    JOptionPane.showMessageDialog(null, message);
  }


  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
}

  public void displayError(String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public String requestInput(String prompt) {
    return JOptionPane.showInputDialog(this, prompt);
  }

  @Override
  public void inputMessage(String message) {
    requestInput(message);
  }

  @Override
  public String readLine() {
    return null;
  }

  @Override
  public LocalDate requestDate(String message) {
    LocalDate date = null;
    while (date == null) {
      String dateString = requestInput( message);
      if (dateString == null || dateString.isEmpty()) {
        displayMessage( "Operation cancelled or no date entered.");
        break; // Exit the method if user cancels or inputs an empty string
      }

      try {
        date = LocalDate.parse(dateString);
        if (!date.isBefore(LocalDate.now())) {
          displayMessage("Date must be before today. Please try again.");
          date = null; // Reset date to null to continue the loop
        } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
          displayMessage("Date must be on a weekday. Please try again.");
          date = null; // Reset date to null to continue the loop
        }
      } catch (DateTimeParseException e) {
        displayMessage("Invalid date format. Please try again.");
        // No need to reset date to null here because it's already null
      }
    }
    return date;
  }

  public void displayPerformanceChart(Map<LocalDate, BigDecimal> data) {
    // Create the panel that will display the graph
    BarChartPanel barChartPanel = new BarChartPanel(data);
    barChartPanel.displayInWindow();
    // Create a new JFrame to display the graph

  }



  @Override
  public Integer readInt() {
    return 0;
  }

  @Override
  public void displayFlexiblePortfolioMenu() {

  }


  public void displayCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, List<LocalDate> dates) {
    StringBuilder message = new StringBuilder("Crossover days for stock " + symbol + " between " +
            startDate + " and " + endDate + ":\n");
    for (LocalDate date : dates) {
      message.append(date.toString()).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  @Override
  public void displayNormalPortfolioMenu() {

  }

  public void displayMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
                                                int shortMovingPeriod, int longMovingPeriod, Map<String, Object> result) {
    StringBuilder message = new StringBuilder();
    message.append("Moving crossover days for stock ").append(symbol).append(" between ")
            .append(startDate).append(" and ").append(endDate).append(":\n")
            .append("Short moving period: ").append(shortMovingPeriod)
            .append(", Long moving period: ").append(longMovingPeriod).append("\n");

    List<LocalDate> goldenCrosses = (List<LocalDate>) result.get("goldenCrosses");
    List<LocalDate> deathCrosses = (List<LocalDate>) result.get("deathCrosses");
    List<LocalDate> movingCrossoverDays = (List<LocalDate>) result.get("movingCrossoverDays");

    appendDateListToMessage("Golden Crosses:", goldenCrosses, message);
    appendDateListToMessage("Death Crosses:", deathCrosses, message);
    appendDateListToMessage("Moving Crossover Days:", movingCrossoverDays, message);

    JOptionPane.showMessageDialog(null, message.toString());
  }

  private static void appendDateListToMessage(String title, List<LocalDate> dates, StringBuilder message) {
    message.append(title).append("\n");
    for (LocalDate date : dates) {
      message.append(date.toString()).append("\n");
    }
  }



}