package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Class to represent the GUI view of the application.
 */
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
  private JButton normalLoadPortfolioButton;
  private JButton normalGraphButton;
  private JButton normalInspectStockPerformanceButton;
  private JButton normalCalculateXDayMovingAverageButton;
  private JButton crossoverDays;
  private JButton movingCrossoverDays;
  private JButton dollarCostAverage;
  private JButton normalCrossoverDays;
  private JButton normalMovingCrossoverDays;
  private JButton normalDollarCostAverage;

  private JButton valueBasedInvestment;

  private JButton normalvalueBasedInvestment;

  /**
   * Constructor for the GUIView class.
   */
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

  /**
   * Displays the main menu.
   */
  @Override
  public void displayMainMenu() {

    // Show the frame
    setVisible(true);
  }

  /**
   * Creates the main menu panel.
   *
   * @return The main menu panel.
   */
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

  /**
   * Creates the normal portfolio panel.
   *
   * @return The normal portfolio panel.
   */
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

    normalCrossoverDays = new JButton("Crossover Days");
    // Add ActionListener for investmentButton
    panel.add(normalCrossoverDays);

    normalMovingCrossoverDays = new JButton("Moving Crossover Days");
    // Add ActionListener for investmentButton
    panel.add(normalMovingCrossoverDays);

    normalDollarCostAverage = new JButton("Dollar Cost Averaging");
    // Add ActionListener for investmentButton
    panel.add(normalDollarCostAverage);


    normalvalueBasedInvestment = new JButton(" Value Based Investment");
    // Add ActionListener for investmentButton
    panel.add(normalvalueBasedInvestment);

    backButton = new JButton("Back to Main Menu");
    backButton.addActionListener(e -> switchToCard(MAIN_MENU));
    panel.add(backButton);

    return panel;
  }

  /**
   * Creates the flexible portfolio panel.
   *
   * @return The flexible portfolio panel.
   */
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

    valueBasedInvestment = new JButton("Value Based Investment");
    // Add ActionListener for investmentButton
    panel.add(valueBasedInvestment);

    backButton = new JButton("Back to Main Menu");
    backButton.addActionListener(e -> switchToCard(MAIN_MENU));
    panel.add(backButton);

    return panel;
  }

  /**
   * Switches the card layout to the card with the given name.
   *
   * @param cardName The name of the card to switch to.
   */
  private void switchToCard(String cardName) {
    CardLayout cl = (CardLayout) (cards.getLayout());
    cl.show(cards, cardName);
  }

  /**
   * Sets the action listener for the create portfolio button.
   *
   * @param actionListener The action listener for the create portfolio button.
   */
  public void setCreatePortfolioAction(ActionListener actionListener) {
    createPortfolioButton.addActionListener(actionListener);
  }

  /**
   * Sets the action listener for the examine portfolio button.
   *
   * @param listener The action listener for the examine portfolio button.
   */
  public void setExaminePortfolioButtonListener(ActionListener listener) {
    examinePortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the calculate portfolio value button.
   *
   * @param listener The action listener for the calculate portfolio value button.
   */
  public void setCalculatePortfolioValueButtonListener(ActionListener listener) {
    calculatePortfolioValueButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the save portfolio button.
   *
   * @param listener The action listener for the save portfolio button.
   */
  public void setSavePortfolioButtonListener(ActionListener listener) {
    savePortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the load portfolio button.
   *
   * @param listener The action listener for the load portfolio button.
   */
  public void setLoadPortfolioButtonListener(ActionListener listener) {
    loadPortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the graph button.
   *
   * @param listener The action listener for the graph button.
   */
  public void setGraphButtonListener(ActionListener listener) {
    graphButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the inspect stock performance button.
   *
   * @param listener The action listener for the inspect stock performance button.
   */
  public void setInspectStockPerformanceButtonListener(ActionListener listener) {
    inspectStockPerformanceButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the calculate X-Day Moving Average button.
   *
   * @param listener The action listener for the calculate X-Day Moving Average button.
   */
  public void setCalculateXDayMovingAverageButtonListener(ActionListener listener) {
    calculateXDayMovingAverageButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the add button.
   *
   * @param listener The action listener for the add button.
   */
  public void setAddButtonListener(ActionListener listener) {
    addButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the sell button.
   *
   * @param listener The action listener for the sell button.
   */
  public void setSellButtonListener(ActionListener listener) {
    sellButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the investment button.
   *
   * @param listener The action listener for the investment button.
   */
  public void setInvestmentButtonListener(ActionListener listener) {
    investmentButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the back button.
   *
   * @param listener The action listener for the back button.
   */
  public void setCrossoverButtonListener(ActionListener listener) {
    crossoverDays.addActionListener(listener);
  }

  /**
   * Sets the action listener for the moving crossover button.
   *
   * @param listener The action listener for the moving crossover button.
   */
  public void setMovingCrossoverButtonListener(ActionListener listener) {
    movingCrossoverDays.addActionListener(listener);
  }

  /**
   * Sets the action listener for the dollar cost averaging button.
   *
   * @param listener The action listener for the dollar cost averaging button.
   */
  public void setDollarCostButtonListener(ActionListener listener) {
    dollarCostAverage.addActionListener(listener);
  }

  /**
   * Sets the action listener for the Value Based Investment button.
   *
   * @param listener The action listener for the dollar cost averaging button.
   */
  public void setValueBasedInvestmentButtonListener(ActionListener listener) {
    valueBasedInvestment.addActionListener(listener);
  }


  /**
   * Sets the action listener for the Value Based Investment button.
   *
   * @param listener The action listener for the dollar cost averaging button.
   */
  public void setNormalValueBasedInvestmentButtonListener(ActionListener listener) {
    normalvalueBasedInvestment.addActionListener(listener);
  }


  /**
   * Sets the action listener for the normal create portfolio button.
   *
   * @param actionListener The action listener for the normal create portfolio button.
   */
  public void setnormalCreatePortfolioAction(ActionListener actionListener) {
    normalCreatePortfolioButton.addActionListener(actionListener);
  }

  /**
   * Sets the action listener for the normal examine portfolio button.
   *
   * @param listener The action listener for the normal examine portfolio button.
   */
  public void setnormalExaminePortfolioButtonListener(ActionListener listener) {
    normalExaminePortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal calculate portfolio value button.
   *
   * @param listener The action listener for the normal calculate portfolio value button.
   */
  public void setnormalCalculatePortfolioValueButtonListener(ActionListener listener) {
    normalCalculatePortfolioValueButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal save portfolio button.
   *
   * @param listener The action listener for the normal save portfolio button.
   */
  public void setnormalSavePortfolioButtonListener(ActionListener listener) {
    normalSavePortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal load portfolio button.
   *
   * @param listener The action listener for the normal load portfolio button.
   */
  public void setnormalLoadPortfolioButtonListener(ActionListener listener) {
    normalLoadPortfolioButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal graph button.
   *
   * @param listener The action listener for the normal graph button.
   */
  public void setnormalGraphButtonListener(ActionListener listener) {
    normalGraphButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal inspect stock performance button.
   *
   * @param listener The action listener for the normal inspect stock performance button.
   */
  public void setnormalInspectStockPerformanceButtonListener(ActionListener listener) {
    normalInspectStockPerformanceButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal calculate X-Day Moving Average button.
   *
   * @param listener The action listener for the normal calculate X-Day Moving Average button.
   */
  public void setnormalCalculateXDayMovingAverageButtonListener(ActionListener listener) {
    normalCalculateXDayMovingAverageButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the normal crossover button.
   *
   * @param listener The action listener to be set.
   */
  @Override
  public void setNormalCrossoverButtonListener(ActionListener listener) {
    normalCrossoverDays.addActionListener(listener);

  }

  /**
   * Sets the action listener for the normal moving crossover button.
   *
   * @param listener The action listener to be set.
   */
  @Override
  public void setNormalMovingCrossoverButtonListener(ActionListener listener) {
    normalMovingCrossoverDays.addActionListener(listener);

  }

  /**
   * Sets the action listener for the normal dollar cost averaging button.
   *
   * @param listener The action listener to be set.
   */
  @Override
  public void setNormalDollarCostButtonListener(ActionListener listener) {
    normalDollarCostAverage.addActionListener(listener);
  }


  /**
   * Displays the available portfolios.
   *
   * @param portfolioNames List of available portfolios.
   */
  public void displayAvailablePortfolios(List<String> portfolioNames) {
    StringBuilder message = new StringBuilder("Available portfolios:\n");
    for (String name : portfolioNames) {
      message.append(name).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  /**
   * Displays the details of a portfolio.
   *
   * @param name   Portfolio name.
   * @param stocks List of stocks.
   */
  public void displayPortfolioDetails(String name, List<model.Tradable> stocks) {
    StringBuilder message = new StringBuilder("Stocks in ").append(name).append(":\n");
    for (model.Tradable stock : stocks) {
      message.append(stock.getSymbol()).append(" - Quantity: ")
          .append(stock.getQuantity()).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  /**
   * Displays the value of a portfolio.
   *
   * @param name  Portfolio name.
   * @param date  Date input.
   * @param value String.
   */
  public void displayPortfolioValue(String name, String date, String value) {
    String message = "Value of the portfolio '" + name + "' on " + date + ": " + value;
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays the investment of a portfolio.
   *
   * @param name  Portfolio name.
   * @param date  Date input.
   * @param value String.
   */
  public void displayPortfolioInvestment(String name, String date, String value) {
    String message = "Investment of the portfolio '" + name + "' on " + date + ": " + value;
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays a success message when a portfolio is saved.
   *
   * @param filePath File path.
   */
  public void displaySaveSuccess(String filePath) {
    String message = "Portfolios have been saved successfully to " + filePath;
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays a success message when portfolios are loaded.
   */
  public void displayLoadSuccess() {
    String message = "Portfolios have been loaded successfully.";
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays a message when a stock is added to a portfolio.
   *
   * @param portfolioName Portfolio name.
   * @param stockSymbol   Stock symbol.
   * @param quantity      Quantity of stock.
   */
  public void displayStockAdded(String portfolioName, String stockSymbol, int quantity) {
    String message = "Stock "
        +
        stockSymbol
        +
        " with quantity "
        +
        quantity
        +
        " added to portfolio "
        + portfolioName;
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays a message when a stock is sold from a portfolio.
   *
   * @param portfolioName Portfolio name.
   * @param symbol        Stock symbol.
   * @param quantity      Quantity of stock.
   */
  public void displayStockSold(String portfolioName, String symbol, int quantity) {
    String message = "Stock "
        +
        symbol
        +
        " with quantity "
        +
        quantity
        +
        " sold from portfolio "
        + portfolioName;
    JOptionPane.showMessageDialog(null, message);
  }


  /**
   * Displays a message when a stock is sold from a portfolio.
   *
   * @param message The message to display.
   */
  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * Displays an error message.
   *
   * @param errorMessage The error message to display.
   */
  public void displayError(String errorMessage) {
    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Requests input from the user.
   *
   * @param prompt The prompt to display to the user.
   * @return The user's input.
   */
  @Override
  public String requestInput(String prompt) {
    return JOptionPane.showInputDialog(this, prompt);
  }

  /**
   * Requests input from the user.
   *
   * @param message The message to display to the user.
   */
  @Override
  public void inputMessage(String message) {
    requestInput(message);
  }

  /**
   * Requests a date from the user.
   *
   * @return The date input by the user.
   */
  @Override
  public String readLine() {
    return null;
  }


  /**
   * Reads an integer from the user.
   *
   * @return The user's input.
   */
  @Override
  public Integer readInt() {
    return 0;
  }

  /**
   * Displays the flexible portfolio menu.
   */
  @Override
  public void displayFlexiblePortfolioMenu() {
    // empty method
    return;

  }

  /**
   * Displays the crossover days.
   *
   * @param symbol    Stock symbol.
   * @param startDate Start date.
   * @param endDate   End date.
   * @param dates     List of crossover days.
   */
  public void displayCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      List<LocalDate> dates) {
    StringBuilder message = new StringBuilder(
        "Crossover (Buy) days for stock "
            +
            symbol
            +
            " between "
            +
            startDate
            +
            " and "
            +
            endDate
            +
            ":\n");
    for (LocalDate date : dates) {
      message.append(date.toString()).append("\n");
    }
    JOptionPane.showMessageDialog(null, message.toString());
  }

  /**
   * Displays the normal portfolio menu.
   */
  @Override
  public void displayNormalPortfolioMenu() {
    // empty method
    return;
  }

  /**
   * Display Moving Crossover Days.
   *
   * @param symbol            Stock symbol.
   * @param startDate         Start date.
   * @param endDate           End date.
   * @param shortMovingPeriod Short moving period.
   * @param longMovingPeriod  Long moving period.
   * @param result            List of crossover days.
   */
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

    appendDateListToMessage("Golden Crosses / Upward trend:", goldenCrosses, message);
    appendDateListToMessage("Death Crosses / Downward trend:", deathCrosses, message);
    appendDateListToMessage("Moving Crossover Days:", movingCrossoverDays, message);

    JOptionPane.showMessageDialog(null, message.toString());
  }

  /**
   * Appends a list of dates to a message.
   *
   * @param title   The title of the list.
   * @param dates   The list of dates.
   * @param message The message to append to.
   */
  private static void appendDateListToMessage(String title, List<LocalDate> dates,
      StringBuilder message) {
    message.append(title).append("\n");
    for (LocalDate date : dates) {
      message.append(date.toString()).append("\n");
    }
  }


}