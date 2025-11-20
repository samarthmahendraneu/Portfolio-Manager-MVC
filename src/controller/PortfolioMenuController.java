package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import model.Portfolio;
import model.PortfolioInterface;
import model.Tradable;
import model.service.PortfolioServiceInterface;
import view.GUIInterface;
import view.UnifiedViewInterface;


/**
 * Controller class for managing the interaction between user inputs and portfolio operations
 * through a menu interface.
 */
public class PortfolioMenuController implements PortfolioMenuControllerInterface {

  private final PortfolioControllerInterface portfolioController;

  private final PortfolioServiceInterface portfolioService;

  private final UnifiedViewInterface view;

  /**
   * Controller class for managing the interaction between user inputs and portfolio operations
   * through a menu interface.
   *
   * @param portfolioController The portfolio controller to interact with.
   * @param view                The view for displaying messages.
   */
  public PortfolioMenuController(PortfolioControllerInterface portfolioController,
      UnifiedViewInterface view) {
    this.portfolioController = portfolioController;
    this.view = view;
    this.loadStockCache();
    this.portfolioService = portfolioController.getPortfolioService();
    setupView();
  }

  /**
   * sets up initial view for Management of Portfolio.
   */
  private void setupView() {
    if (view instanceof GUIInterface) {
      setupGUIViewListeners((GUIInterface) view);
      try {
        view.displayMainMenu();
      } catch (IOException ignored) {
      }
    } else {
      displayMainMenu();
    }
    // No setup needed for textual view as it handles inputs differently
  }

  /**
   * GUI View Listeners for the Portfolio Management System.
   *
   * @param guiView the GUI view to set up listeners for the Portfolio Management System.
   */
  private void setupGUIViewListeners(GUIInterface guiView) {
    guiView.setCreatePortfolioAction(e -> createNewPortfolio());
    guiView.setExaminePortfolioButtonListener(e -> examinePortfolio());
    guiView.setCalculatePortfolioValueButtonListener(e -> calculatePortfolioValue());
    guiView.setSavePortfolioButtonListener(e -> savePortfolio("Flexible"));
    guiView.setLoadPortfolioButtonListener(e -> loadPortfolio("Flexible"));
    guiView.setGraphButtonListener(e -> calculateGraph());
    guiView.setInspectStockPerformanceButtonListener(e -> inspectStockPerformance());
    guiView.setnormalCreatePortfolioAction(e -> createNewPortfolio());
    guiView.setnormalExaminePortfolioButtonListener(e -> examinePortfolio());
    guiView.setnormalCalculatePortfolioValueButtonListener(e -> calculatePortfolioValue());
    guiView.setnormalSavePortfolioButtonListener(e -> savePortfolio("Normal"));
    guiView.setnormalLoadPortfolioButtonListener(e -> loadPortfolio("Normal"));
    guiView.setnormalGraphButtonListener(e -> calculateGraph());
    guiView.setnormalInspectStockPerformanceButtonListener(e -> inspectStockPerformance());
    guiView.setAddButtonListener(e -> addStockToPortfolio());
    guiView.setSellButtonListener(e -> sellStockFromPortfolio());
    guiView.setDollarCostButtonListener(e -> dollarCostAveraging());
    guiView.setValueBasedInvestmentButtonListener(e -> valueBasedInvestment());
    guiView.setMovingCrossoverButtonListener(e -> findMovingCrossOverDays());
    guiView.setCrossoverButtonListener(e -> findCrossOverDays());
    guiView.setInvestmentButtonListener(e -> calculateInvestment());
    guiView.setnormalCalculateXDayMovingAverageButtonListener(e -> computeStockMovingAverage());
    guiView.setCalculateXDayMovingAverageButtonListener(e -> computeStockMovingAverage());
    guiView.setNormalDollarCostButtonListener(e -> dollarCostAveraging());
    guiView.setNormalValueBasedInvestmentButtonListener(e -> valueBasedInvestment());
    guiView.setNormalMovingCrossoverButtonListener(e -> findMovingCrossOverDays());
    guiView.setNormalCrossoverButtonListener(e -> findCrossOverDays());

  }

  /**
   * Main Menu to choose between Normal and Flexible Portfolio.
   */
  public void displayMainMenu() {
    boolean running = true;
    while (running) {
      try {
        view.displayMainMenu();
        int choice = this.view.readInt();
        switch (choice) {
          case 1:
            this.displayNormalPortfolioMenu();
            break;
          case 2:
            this.displayFlexiblePortfolioMenu();
            break;
          case 3:
            this.view.inputMessage("Exiting...");
            running = false;
            break;
          default:
            this.view.inputMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.inputMessage("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Displays the main menu for the portfolio management system.
   */
  public void displayFlexiblePortfolioMenu() {
    boolean running = true;
    while (running) {
      try {
        view.displayFlexiblePortfolioMenu();
        int choice = this.view.readInt();
        switch (choice) {
          case 1:
            this.createNewPortfolio();
            break;
          case 2:
            this.examinePortfolio();
            break;
          case 3:
            this.calculatePortfolioValue();
            break;
          case 4:
            // Purchase a specific number of shares of a specific stock on a specified date, and add
            // them to the portfolio
            this.addStockToPortfolio();
            break;
          case 5:
            // Sell a specific number of shares of a specific stock on a specified date from a
            // given portfolio
            this.sellStockFromPortfolio();
            break;
          case 6:
            // the total amount of money invested in a portfolio) by a specific date.
            this.calculateInvestment();
            break;
          case 7:
            this.savePortfolio("Flexible");
            break;
          case 8:
            this.loadPortfolio("Flexible");
            break;
          case 9:
            this.calculateGraph();
            break;
          case 10:
            this.inspectStockPerformance();
            break;
          case 11:
            this.computeStockMovingAverage();
            break;
          case 12:
            this.findCrossOverDays();
            break;
          case 13:
            this.findMovingCrossOverDays();
            break;
          case 14:
            this.dollarCostAveraging();
            break;
          case 15:
            this.valueBasedInvestment();
            break;
          case 16:
            this.view.inputMessage("Exiting...");
            this.saveStockCache();
            running = false;
            break;
          default:
            this.view.inputMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.inputMessage("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Value based investment for a given portfolio.
   */
  public void valueBasedInvestment() {
    try {
      String name = view.requestInput("Enter the Portfolio Name:");
      if (name == null || name.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }
      String startDateString = view.requestInput("Enter the date (YYYY-MM-DD):");
      LocalDate startDate = validateAndParseDate(startDateString);

      String investmentAmountString = view.requestInput(
          "Enter the investment amount in USD:");
      if (investmentAmountString == null || investmentAmountString.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }
      BigDecimal investmentAmount = new BigDecimal(investmentAmountString);

      Map<String, Float> stockWeights = new HashMap<>();
      boolean flag = true;
      while (flag) {
        // input stocks and weights
        String stockAndWeights = view.requestInput(
            "Enter the stock symbols and weights in the format: 'stock1:weight1,"
                +
                "stock2:weight2,...as in IBM:50,GOOGL:50");
        if (stockAndWeights == null || stockAndWeights.isEmpty()) {
          view.displayMessage("Operation cancelled or no symbol entered.");
          return;
        }
        try {
          // create map containing stock and weight
          int sumOFWeights = 0;
          String[] stockWeightPairs = stockAndWeights.split(",");
          for (String pair : stockWeightPairs) {
            String[] stockWeight = pair.split(":");
            stockWeights.put(stockWeight[0], Float.parseFloat(stockWeight[1]));
            sumOFWeights += (int) Float.parseFloat(stockWeight[1]);
          }
          if (sumOFWeights != 100) {
            view.displayMessage("Sum of weights should be 100");
          } else {
            flag = false;
          }
        } catch (Exception e) {
          view.displayMessage("Please enter a valid format for stock and weight.");
        }
      }
      this.portfolioService.valueBasedInvestment(name, investmentAmount, startDate, stockWeights);
      view.displayMessage(
          "Value Based Investment has been successfully applied to the portfolio: " + name);
    } catch (DateTimeParseException dtpe) {
      view.displayMessage("Error: Invalid date format.");
    } catch (NumberFormatException nfe) {
      view.displayMessage("Error: Invalid number format.");
    } catch (Exception e) {
      view.displayMessage("An error occurred: " + e.getMessage());
    }
  }

  /**
   * Dollar Cost Averaging for a given portfolio.
   */
  public void dollarCostAveraging() {
    try {
      String name = view.requestInput("Enter the Portfolio Name:");
      if (name == null || name.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }
      String startDateString = view.requestInput("Enter the start date (YYYY-MM-DD):");
      LocalDate startDate = customValidateAndParseDateForDollarCostAvg(startDateString);

      String endDateString = view.requestInput(
          "Enter the end date (YYYY-MM-DD), n to skip enddate:");
      LocalDate endDate = LocalDate.now();
      if (!Objects.equals(endDateString, "n")) {
        endDate = customValidateAndParseDateForDollarCostAvg(endDateString);
      }
      Map<String, Float> stockWeights = new HashMap<>();
      boolean flag = true;
      while (flag) {
        // input stocks and weights
        String stockAndWeights = view.requestInput(
            "Enter the stock symbols and weights in the format:"
                +
                " 'stock1:weight1,stock2:weight2,...as in IBM:50,GOOGL:50");
        if (stockAndWeights == null || stockAndWeights.isEmpty()) {
          view.displayMessage("Operation cancelled or no symbol entered.");
          return;
        }
        // create map containing stock and weight
        int sumOFWeights = 0;
        String[] stockWeightPairs = stockAndWeights.split(",");
        for (String pair : stockWeightPairs) {
          String[] stockWeight = pair.split(":");
          stockWeights.put(stockWeight[0], Float.parseFloat(stockWeight[1]));
          sumOFWeights += (int) Float.parseFloat(stockWeight[1]);
        }
        if (sumOFWeights != 100) {
          view.displayMessage("Sum of weights should be 100");
          return;
        } else {
          flag = false;
        }
      }

      String investmentAmountString = view.requestInput(
          "Enter the investment amount per month in USD:");
      if (investmentAmountString == null || investmentAmountString.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }
      BigDecimal investmentAmount = new BigDecimal(investmentAmountString);

      String frequencyString = view.requestInput(
          "Enter the frequency type: 1 for daily, "
              +
              "2 for weekly, 3 for monthly, 4 for yearly");
      if (frequencyString == null || frequencyString.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }
      int frequency = Integer.parseInt(frequencyString);

      this.portfolioService.dollarCostAveraging(name, investmentAmount, startDate, endDate,
          frequency, stockWeights);

      view.displayMessage(
          "Dollar Cost Averaging has been successfully applied to the portfolio: " + name);
    } catch (DateTimeParseException dtpe) {
      view.displayMessage("Error: Invalid date format.");
    } catch (NumberFormatException nfe) {
      view.displayMessage("Error: Invalid number format.");
    } catch (Exception e) {
      view.displayMessage("An error occurred: " + e.getMessage());
    }
  }

  /**
   * Finds crossover days for a given stock symbol within a specified date range. A moving crossover
   * day is a day when the closing price of the stock is higher than the moving average.
   */
  public void findCrossOverDays() {
    try {
      String symbol = view.requestInput("Enter the stock symbol:");
      if (symbol == null || symbol.isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }

      String startDateString = view.requestInput("Enter the start date (YYYY-MM-DD):");
      LocalDate startDate = validateAndParseDate(startDateString);
      if (startDate == null) {
        return; // Error message already shown by validateAndParseDate
      }

      String endDateString = view.requestInput("Enter the end date (YYYY-MM-DD):");
      LocalDate endDate = validateAndParseDate(endDateString);
      if (endDate == null) {
        return; // Error message already shown by validateAndParseDate
      }

      Payload payload = portfolioController.findCrossoverDays(symbol, startDate, endDate);
      if (payload.isError()) {
        view.displayMessage("Error finding crossover days: " + payload.getMessage());
        return;
      }
      // Assuming displayCrossoverDays can handle showing the results appropriately
      view.displayCrossoverDays(symbol, startDate, endDate, (List<LocalDate>) payload.getData());
    } catch (Exception e) {
      view.displayMessage("Error finding crossover days: " + e.getMessage());
    }
  }


  /**
   * Finds moving crossover days for a given stock symbol within a specified date range. A moving
   * crossover day is a day when the closing price of the stock is higher than the moving average.
   */
  public void findMovingCrossOverDays() {
    try {
      String symbol = view.requestInput("Enter the stock symbol:");
      if (symbol == null || symbol.trim().isEmpty()) {
        view.displayMessage("Operation cancelled or no symbol entered.");
        return;
      }

      String startDateString = view.requestInput("Enter the start date (YYYY-MM-DD):");
      LocalDate startDate = validateAndParseDate(startDateString);
      if (startDate == null) {
        return;
      }

      String endDateString = view.requestInput("Enter the end date (YYYY-MM-DD):");
      LocalDate endDate = validateAndParseDate(endDateString);
      if (endDate == null) {
        return;
      }

      String shortMovingPeriodString = view.requestInput("Enter the short moving period:");
      Integer shortMovingPeriod = validateAndParseInt(shortMovingPeriodString);
      if (shortMovingPeriod == null) {
        return;
      }

      String longMovingPeriodString = view.requestInput("Enter the long moving period:");
      Integer longMovingPeriod = validateAndParseInt(longMovingPeriodString);
      if (longMovingPeriod == null) {
        return;
      }

      Payload payload = portfolioController.findMovingCrossoverDays(symbol, startDate, endDate,
          shortMovingPeriod, longMovingPeriod);
      if (payload.isError()) {
        view.displayMessage("Error finding moving crossover days: " + payload.getMessage());
        return;
      }

      view.displayMovingCrossoverDays(symbol, startDate, endDate, shortMovingPeriod,
          longMovingPeriod, (Map<String, Object>) payload.getData());
    } catch (Exception e) {
      view.displayMessage("Error finding moving crossover days: " + e.getMessage());
    }
  }

  /**
   * Validate and parse an integer from a string.
   *
   * @param intString the string to parse
   * @return the parsed integer, or null if the string is not a valid integer
   */
  private Integer validateAndParseInt(String intString) {
    try {
      return Integer.parseInt(intString);
    } catch (NumberFormatException e) {
      view.displayMessage("Invalid number format. Please enter a valid integer.");
      return null;
    }
  }


  /**
   * Display the main menu for Normal Portfolio.
   */
  @Override
  public void displayNormalPortfolioMenu() {
    boolean running = true;
    while (running) {
      try {
        view.displayNormalPortfolioMenu();
        int choice = this.view.readInt();
        switch (choice) {
          case 1:
            this.createNewPortfolio();
            break;
          case 2:
            this.examinePortfolio();
            break;
          case 3:
            this.calculatePortfolioValue();
            break;
          case 4:
            this.savePortfolio("Normal");
            break;
          case 5:
            this.loadPortfolio("Normal");
            break;
          case 6:
            this.calculateGraph();
            break;
          case 7:
            this.inspectStockPerformance();
            break;
          case 8:
            this.computeStockMovingAverage();
            break;
          case 9:
            this.findCrossOverDays();
            break;
          case 10:
            this.findMovingCrossOverDays();
            break;
          case 11:
            this.dollarCostAveraging();
            break;
          case 12:
            this.valueBasedInvestment();
            break;
          case 13:
            this.view.inputMessage("Exiting...");
            this.saveStockCache();
            running = false;
            break;

          default:
            this.view.inputMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.inputMessage("Error: " + e.getMessage());
      }
    }

  }

  /**
   * Calculate the graph for the given stock or portfolio.
   */
  public void calculateGraph() {
    try {
      String name = view.requestInput("Enter Stock or Portfolio name:");
      if (name == null || name.isEmpty()) {
        view.displayMessage("Operation cancelled or no name entered.");
        return;
      }

      String startDateString = view.requestInput("Enter Start Date (YYYY-MM-DD):");
      LocalDate startDate = validateAndParseDate(startDateString);
      if (startDate == null) {
        return; // Error message already shown by validateAndParseDate
      }

      String endDateString = view.requestInput("Enter End Date (YYYY-MM-DD):");
      LocalDate endDate = validateAndParseDate(endDateString);
      if (endDate == null) {
        return; // Error message already shown by validateAndParseDate
      }

      // Assuming genGraph returns a StringBuilder or String representing the graph
      StringBuilder graphData = portfolioController.genGraph(name, startDate, endDate);
      view.displayMessage(graphData.toString());
    } catch (DateTimeParseException dtpe) {
      view.displayMessage("Error: Invalid date format.");
    } catch (Exception e) {
      view.displayMessage("An error occurred: " + e.getMessage());
    }
  }

  /**
   * Validate and parse a date from a string.
   *
   * @param dateString the string to parse
   * @return the parsed date, or null if the string is not a valid date
   */
  private LocalDate validateAndParseDate(String dateString) {

    try {
      LocalDate date = LocalDate.parse(dateString);
      if (!date.isBefore(LocalDate.now())) {
        throw new Exception("Date is in the future. Please try again.");
      }
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        throw new Exception("Date is Weekend");
      }
      return LocalDate.parse(dateString);
    } catch (Exception e) {
      view.displayMessage("Invalid date format. Please try again with format YYYY-MM-DD.");
      String endDateString = view.requestInput("Enter the date (YYYY-MM-DD):");
      return validateAndParseDate(endDateString);
    }
  }

  /**
   * Validate and parse a date from a string.
   *
   * @param dateString the string to parse
   * @return the parsed date, or null if the string is not a valid date
   */
  private LocalDate customValidateAndParseDateForDollarCostAvg(String dateString) {

    try {
      LocalDate date = LocalDate.parse(dateString);
      if (!date.isBefore(LocalDate.now())) {
        throw new Exception("Date is in the future. Please try again.");
      }
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        // return next weekday
        if (dayOfWeek == DayOfWeek.SATURDAY) {
          return date.plusDays(2);
        } else {
          return date.plusDays(1);
        }
      }
      return LocalDate.parse(dateString);
    } catch (Exception e) {
      view.displayMessage("Invalid date format. Please try again with format YYYY-MM-DD.");
      String endDateString = view.requestInput("Enter the date (YYYY-MM-DD):");
      return validateAndParseDate(endDateString);
    }
  }


  /**
   * Inspect the stock performance for a given stock symbol on a specified date.
   */
  public void inspectStockPerformance() {
    String symbol = view.requestInput("Enter the stock symbol:");
    if (symbol == null || symbol.trim().isEmpty()) {
      view.displayMessage("Operation cancelled or no symbol entered.");
      return;
    }

    String dateString = view.requestInput(
        "Enter the date (YYYY-MM-DD) to inspect the stock performance:");
    LocalDate date = validateAndParseDate(dateString);

    if (date == null) {
      return;
    }

    Payload result = portfolioController.inspectStockPerformance(symbol, date);

    if (!result.isError()) {
      view.displayMessage("Stock Performance on " + date + ": " + result.getData());
    } else {
      view.displayMessage("Error: " + result.getMessage());
    }
  }

  /**
   * Get the number of days for the moving average calculation.
   *
   * @return the number of days for the moving average calculation.
   */
  private int getValidNumberOfDays() {
    int days = 0;
    boolean isValidInput = false;

    while (!isValidInput) {
      try {
        String input = view.requestInput("Enter the number of days for the moving average:");
        if (input == null || input.trim().isEmpty()) {
          view.displayMessage("Operation cancelled.");
          break; // Or return a default value or throw an exception based on your flow.
        }
        days = Integer.parseInt(input.trim());

        if (days <= 0) {
          throw new NumberFormatException("The number of days must be greater than 0.");
        }

        isValidInput = true;
      } catch (NumberFormatException e) {
        view.displayMessage(
            "Invalid input: " + e.getMessage() + ". Please enter a positive integer.");
      }
    }
    return days;
  }


  /**
   * Compute the moving average for a given stock symbol on a specified date.
   */
  public void computeStockMovingAverage() {
    // Requesting stock symbol input
    String symbol = view.requestInput("Enter the stock symbol:");
    if (symbol == null) {
      view.displayMessage("Operation cancelled.");
      return;
    }

    // Requesting end date input and validating it
    String endDate = view.requestInput(
        "Enter the date (YYYY-MM-DD) to inspect the stock performance:");
    LocalDate date = validateAndParseDate(endDate);
    if (endDate == null) {
      view.displayMessage("Invalid or no date provided.");
      return;
    }

    // Requesting number of days for moving average calculation
    int days = getValidNumberOfDays();
    if (days <= 0) {
      view.displayMessage("Invalid number of days provided.");
      return;
    }

    // Perform the moving average computation
    try {
      Payload result = portfolioController.computeStockMovingAverage(symbol, date, days);
      if (!result.isError()) {
        view.displayMessage(days + "-Day Moving Average for " + symbol + " as of " + endDate + ": "
            + result.getData());
      } else {
        view.displayMessage("Error: " + result.getMessage());
      }
    } catch (Exception e) {
      view.displayMessage("Error while computing moving average: " + e.getMessage());
    }
  }

  /**
   * Validates the date input.
   *
   * @param date the date to validate.
   * @return the validated date.
   */
  public Boolean dateValidator(LocalDate date) {
    if (!date.isBefore(LocalDate.now())) {
      this.view.inputMessage("Date must be before today. Please try again.");
    }
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
      this.view.inputMessage("Date must be on a weekday. Please try again.");
    }
    return true;
  }

  /**
   * Create a new portfolio.
   */
  public void createNewPortfolio() {
    // Request portfolio name
    String name = view.requestInput("Enter new portfolio name:");
    if (name == null || name.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered");
      return;
    }

    Payload payload = portfolioController.createNewPortfolio(name);
    if (this.printIfError(payload)) {
      return;
    }
    Portfolio newPortfolio = (Portfolio) payload.getData();

    boolean flag = true;
    while (flag) {
      String symbol = view.requestInput("Enter the stock symbol to add to the portfolio,"
          +
          " or cancel to finish:");
      if (symbol == null || symbol.trim().isEmpty()) {
        flag = false;
        continue;
      }

      int quantity = 0;
      while (quantity <= 0) {
        try {
          String quantityStr = view.requestInput("Enter the quantity of the stock:");
          quantity = Integer.parseInt(quantityStr);
          if (quantity <= 0) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException e) {
          view.displayMessage("Please enter a valid quantity greater than 0.");
        }
      }

      LocalDate date = null;
      while (date == null) {
        try {
          String dateString = view.requestInput("Enter the purchase date (YYYY-MM-DD):");
          date = LocalDate.parse(dateString);
          // Additional validation can be performed here if necessary
        } catch (DateTimeParseException e) {
          view.displayMessage("Please enter a valid date in the format YYYY-MM-DD.");
        }
      }

      payload = portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        continue;
      }

      String continueAdding = view.requestInput(
          "Press 'n' to add more stocks or any other key to finish:");
      if (!"n".equalsIgnoreCase(continueAdding.trim())) {
        flag = false;
      }
    }

    view.displayMessage("Portfolio '" + name + "' has been created and populated.");
  }

  /**
   * Purchase a specific number of shares of a specific stock on a specified date, and add them to
   * the portfolio.
   */
  public void addStockToPortfolio() {
    // Display available portfolios
    try {
      List<String> portfolioNames = portfolioController.getPortfolioService().listPortfolioNames();
      view.displayAvailablePortfolios(portfolioNames);
    } catch (Exception e) {
      view.displayMessage("Error retrieving portfolio names: " + e.getMessage());
      return;
    }

    // Request portfolio name
    String portfolioName = view.requestInput(
        "Enter the name of the portfolio to add the stock to:");
    if (portfolioName == null || portfolioName.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered.");
      return;
    }

    // Request stock symbol
    String symbol = view.requestInput("Enter the stock symbol:");
    if (symbol == null || symbol.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered.");
      return;
    }

    // Request quantity
    int quantity = 0;
    while (quantity <= 0) {
      try {
        String quantityStr = view.requestInput("Enter the quantity of the stock:");
        quantity = Integer.parseInt(quantityStr);
      } catch (NumberFormatException e) {
        view.displayMessage("Please enter a valid quantity greater than 0.");
      }
    }

    // Request purchase date
    LocalDate date = null;
    while (date == null) {
      try {
        String dateString = view.requestInput("Enter the purchase date (YYYY-MM-DD):");
        date = LocalDate.parse(dateString);
      } catch (DateTimeParseException e) {
        view.displayMessage("Please enter a valid date in the format YYYY-MM-DD.");
      }
    }

    // Add stock to the portfolio
    try {
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(portfolioName).orElse(null);
      Payload payload = portfolioController.addStockToPortfolio(portfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        return;
      }
      view.displayStockAdded(portfolioName, symbol, quantity);
    } catch (Exception e) {
      view.displayMessage("Error adding stock to portfolio: " + e.getMessage());
    }
  }


  /**
   * Sell a specific number of shares of a specific stock on a specified date from a given
   * portfolio.
   */
  public void sellStockFromPortfolio() {
    // Display available portfolios
    try {
      List<String> portfolioNames = portfolioController.getPortfolioService().listPortfolioNames();
      view.displayAvailablePortfolios(portfolioNames);
    } catch (Exception e) {
      view.displayMessage("Error retrieving portfolio names: " + e.getMessage());
      return;
    }

    // Request portfolio name
    String portfolioName = view.requestInput(
        "Enter the name of the portfolio to sell the stock from:");
    if (portfolioName == null || portfolioName.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered");
      return;
    }

    // Request stock symbol
    String symbol = view.requestInput("Enter the stock symbol:");
    if (symbol == null || symbol.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered");
      return;
    }

    // Request quantity
    int quantity = 0;
    while (quantity <= 0) {
      try {
        String quantityStr = view.requestInput("Enter the quantity of the stock to be sold:");
        quantity = Integer.parseInt(quantityStr);
      } catch (NumberFormatException e) {
        view.displayMessage("Please enter a valid quantity greater than 0.");
      }
    }

    // Request sale date
    LocalDate date = null;
    while (date == null) {
      try {
        String dateString = view.requestInput("Enter the sale date (YYYY-MM-DD):");
        date = LocalDate.parse(dateString);
      } catch (DateTimeParseException e) {
        view.displayMessage("Please enter a valid date in the format YYYY-MM-DD.");
      }
    }

    // Attempt to sell stock
    try {
      PortfolioInterface portfolio = portfolioService.getPortfolioByName(portfolioName)
          .orElse(null);

      Payload payload = portfolioController.sellStockFromPortfolio(portfolio, symbol, quantity,
          date);
      if (this.printIfError(payload)) {
        return;
      }
      view.displayStockSold(portfolioName, symbol, quantity);
    } catch (Exception e) {
      view.displayMessage("Error selling stock from portfolio: " + e.getMessage());
    }
  }


  /**
   * Calculate the total amount of money invested in a portfolio by a specific date.
   */
  public void calculateInvestment() {
    // Request the portfolio name
    String name = view.requestInput("Enter the name of the portfolio:");
    if (name == null || name.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered");
      return;
    }

    // Request the date for calculation
    String dateInput = view.requestInput(
        "Enter the date (YYYY-MM-DD) to calculate the investment:");
    LocalDate date;
    try {
      date = LocalDate.parse(dateInput);
    } catch (DateTimeParseException e) {
      view.displayMessage("Invalid date format. Please use YYYY-MM-DD.");
      return;
    }

    // Calculate investment
    try {
      Payload payload = portfolioController.calculateTotalInvestment(name, date);
      if (payload.isError()) {
        view.displayMessage(payload.getMessage());
        return;
      }

      Optional<BigDecimal> portfolioValue = (Optional<BigDecimal>) payload.getData();
      if (portfolioValue.isPresent()) {
        view.displayMessage(
            String.format("Investment of the portfolio '%s' on %s: %s", name, dateInput,
                portfolioValue.get().toString()));
      } else {
        view.displayMessage(
            String.format("No value found for the portfolio '%s' on %s.", name, dateInput));
      }
    } catch (Exception e) {
      view.displayMessage("Error calculating portfolio investment: " + e.getMessage());
    }
  }

  /**
   * Allows the user to examine details of a specific portfolio, such as its stocks and their
   * quantities.
   */
  public void examinePortfolio() {
    // Display available portfolios before requesting user input
    List<String> portfolioNames = portfolioController.getPortfolioService().listPortfolioNames();
    if (portfolioNames.isEmpty()) {
      view.displayMessage("No portfolios available to examine.");
      return;
    }
    view.displayMessage("Available portfolios: " + String.join(", ", portfolioNames));

    // Request the name of the portfolio to examine
    String name = view.requestInput("Enter the name of the portfolio to examine:");
    if (name == null || name.trim().isEmpty()) {
      view.displayMessage("Operation Cancelled or no name entered");
      return;
    }

    // request the date for which to examine the portfolio
    String dateInput = view.requestInput("Enter the date (YYYY-MM-DD)"
        +
        " to examine the portfolio:");
    LocalDate date = LocalDate.parse(dateInput); // Consider adding date validation
    // check if the date is valid, it should be before today and validate
    if (date.isAfter(LocalDate.now())) {
      view.displayMessage("Invalid date. Please enter a date before today.");
      return;
    }

    // Attempt to get and display the portfolio details
    try {
      PortfolioServiceInterface portfolioOpt = portfolioController.getPortfolioService();

      List<Tradable> data = portfolioOpt.examinePortfolioDetails(name, date);
      StringBuilder details = new StringBuilder();
      // for  each stock in data add the stock details to the details string
      for (Tradable stock : data) {
        details.append(stock.toString()).append("\n");
      }

      view.displayMessage(details.toString());
    } catch (Exception e) {
      view.displayMessage("Error examining portfolio: " + e.getMessage());
    }
  }


  /**
   * Calculates the total value of a portfolio at a given date and displays the result.
   */
  public void calculatePortfolioValue() {
    try {
      // Request the name of the portfolio to calculate its value
      String name = view.requestInput("Enter the name of the portfolio:");
      if (name == null || name.trim().isEmpty()) {
        view.displayMessage("Operation Cancelled or no name entered");
        return;
      }

      // Request the date for which to calculate the portfolio value
      String dateInput = view.requestInput(
          "Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      LocalDate date = LocalDate.parse(dateInput); // Consider adding date validation

      // Calculate the portfolio value
      Optional<BigDecimal> portfolioValueOpt = portfolioService.calculatePortfolioValue(name, date);
      if (portfolioValueOpt.isPresent()) {
        BigDecimal portfolioValue = portfolioValueOpt.get();
        view.displayMessage("Value of the portfolio '" + name + "' on " + dateInput + ": "
            + portfolioValue.toString());
      } else {
        view.displayMessage("No value found for the portfolio '" + name + "' on " + dateInput);
      }
    } catch (DateTimeParseException e) {
      view.displayMessage("Invalid date format. Please use YYYY-MM-DD format.");
    } catch (Exception e) {
      view.displayMessage("Error calculating portfolio value: " + e.getMessage());
    }
  }


  /**
   * Saves the portfolio to a specified file path.
   *
   * @param type the type of portfolio to save
   */
  public void savePortfolio(String type) {
    try {
      // Request the file path to save the portfolio
      String filePath = view.requestInput("Enter the file path "
          +
          "to save the portfolio (.csv):");
      if (filePath == null || filePath.trim().isEmpty()) {
        view.displayMessage("Operation Cancelled or no path entered");
        return;
      }

      // Attempt to save the portfolio to the specified file
      portfolioService.savePortfoliosToCSV(filePath, type);
      view.displayMessage("Portfolio has been saved successfully to " + filePath);
    } catch (Exception e) {
      view.displayMessage("Error saving portfolio.");
    }
  }

  /**
   * Loads portfolios from a specified file path.
   *
   * @param type the type of portfolio to load
   */
  public void loadPortfolio(String type) {
    try {
      // Request the file path from which to load portfolios
      String filePath = view.requestInput("Enter the file path"
          +
          " to load portfolios from (.csv):");
      if (filePath == null || filePath.trim().isEmpty()) {
        view.displayMessage("Operation Cancelled or no path entered");
        return;
      }

      // Attempt to load the portfolio from the specified file
      String success = portfolioService.loadPortfoliosFromCSV(filePath, type);
      if (success != null) {
        view.displayMessage("Portfolios have been loaded successfully from " + filePath);
      } else {
        view.displayMessage("Error loading portfolios.");
      }
    } catch (Exception e) {
      view.displayMessage("Error: " + e.getMessage());
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void saveStockCache() {
    try {
      String filePath = "cache.csv";
      Payload payload = portfolioController.saveCache(filePath);
      if (payload.isError()) {
        this.view.displayMessage("Error: " + payload.getMessage());
        return;
      }
      view.displayMessage("Cache have been saved successfully to " + filePath + "\n");
    } catch (Exception e) {
      this.view.displayMessage("Error: " + e.getMessage());
      this.view.readLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadStockCache() {
    try {
      String filePath = "cache.csv";
      Payload payload = portfolioController.loadCache(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        return;
      }
      view.displayMessage("Cache have been loaded successfully.\n");
    } catch (Exception e) {
      this.view.displayMessage("Error: " + e.getMessage());
    }
  }

  /**
   * prints error.
   *
   * @param payload takes message from payload.
   * @return true or false.
   */
  public boolean printIfError(Payload payload) {
    if (payload.isError()) {
      try {
        view.displayError(payload.getMessage());
      } catch (Exception e) {
        this.view.inputMessage("Error: " + e.getMessage());
      }
      return true;
    }
    return false;
  }


}
