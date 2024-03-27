package Controller;

import Model.Portfolio;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import Model.PortfolioInterface;
import View.View;


/**
 * Controller class for managing the interaction between user inputs and portfolio operations
 * through a menu interface.
 */
public class PortfolioMenuController implements PortfolioMenuControllerInterface {

  private final PortfolioControllerInterface portfolioController;
  private final View view;

  /**
   * Controller class for managing the interaction between user inputs and portfolio operations
   * through a menu interface.
   *
   * @param portfolioController The portfolio controller to interact with.
   * @param view                The view for displaying messages.
   */
  public PortfolioMenuController(PortfolioControllerInterface portfolioController, View view) {
    this.portfolioController = portfolioController;
    this.view = view;
    this.loadStockCache();
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
            this.view.writeMessage("Exiting...");
            running = false;
            break;
          default:
            this.view.writeMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.writeMessage("Error: " + e.getMessage());
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
            // Purchase a specific number of shares of a specific stock on a specified date, and add them to the portfolio
            this.addStockToPortfolio();
            break;
          case 5:
            // Sell a specific number of shares of a specific stock on a specified date from a given portfolio
            this.sellStockFromPortfolio();
            break;
          case 6:
            // the total amount of money invested in a portfolio) by a specific date.
            this.calculateInvestment();
            break;
          case 7:
            this.savePortfolio();
            break;
          case 8:
            this.loadPortfolio();
            break;
          case 9:
            this.CalculateGraph();
            break;
          case 10:
            this.inspectStockPerformance();
            break;
          case 11:
            this.computeStockMovingAverage();
            break;
          case 12:
            this.view.writeMessage("Exiting...");
            this.saveStockCache();
            running = false;
            break;
          default:
            this.view.writeMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.writeMessage("Error: " + e.getMessage());
      }
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
            this.savePortfolio();
            break;
          case 5:
            this.loadPortfolio();
            break;
          case 6:
            this.CalculateGraph();
            break;
          case 7:
            this.inspectStockPerformance();
            break;
          case 8:
            this.computeStockMovingAverage();
            break;
          case 9:
            this.view.writeMessage("Exiting...");
            this.saveStockCache();
            running = false;
            break;

          default:
            this.view.writeMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.writeMessage("Error: " + e.getMessage());
      }
    }

  }

  public void CalculateGraph() {
    LocalDate date, date2;
    this.view.writeMessage("Enter Stock or Portfolio name:");
    String name = this.view.readLine();
    this.view.writeMessage("Enter Start Date");
    date = dateValidator();
    this.view.writeMessage("Enter End Date");
    date2 = dateValidator();
    portfolioController.GenGraph(name, date, date2);
  }

  public void inspectStockPerformance() {
    this.view.writeMessage("Enter the stock symbol:");
    String symbol = this.view.readLine();
    this.view.writeMessage("Enter the date (YYYY-MM-DD) to inspect the stock performance:");
    LocalDate date = dateValidator();
    Payload result = portfolioController.inspectStockPerformance(symbol, date);

    if (!result.isError()) {
      this.view.writeMessage("Stock Performance on " + date + ": " + result.getData());
    } else {
      this.view.writeMessage("Error: " + result.getMessage());
    }
  }

  private int getValidNumberOfDays() {
    int days = 0;
    boolean isValidInput = false;

    while (!isValidInput) {
      try {
        days = Integer.parseInt(this.view.readLine());

        if (days <= 0) {
          throw new NumberFormatException("The number of days must be greater than 0.");
        }

        isValidInput = true;
      } catch (NumberFormatException e) {
        this.view.writeMessage("Invalid input: " + e.getMessage() + " Please try again.");
      }
    }
    return days;
  }
  public void computeStockMovingAverage() {
    this.view.writeMessage("Enter the stock symbol:");
    String symbol = this.view.readLine();
    this.view.writeMessage("Enter the end date (YYYY-MM-DD) for the moving average calculation:");
    LocalDate endDate = dateValidator();
    this.view.writeMessage("Enter the number of days for the moving average:");
    int days = getValidNumberOfDays() ;// Ensure proper error handling or validation here

    Payload result = portfolioController.computeStockMovingAverage(symbol, endDate, days);

    if (!result.isError()) {
      this.view.writeMessage(days + "-Day Moving Average for " + symbol + " as of " + endDate + ": " + result.getData());
    } else {
      this.view.writeMessage("Error: " + result.getMessage());
    }
  }

  private LocalDate dateValidator() {
    LocalDate date;

    while (true) {
      String dateString = this.view.readLine().trim();
      try {
        date = LocalDate.parse(dateString);
      } catch (Exception e) {
        this.view.writeMessage("Invalid date format. Please try again.");
        continue;
      }
      date = LocalDate.parse(dateString);
      if (!date.isBefore(LocalDate.now())) {
        this.view.writeMessage("Date must be before today. Please try again.");
        continue;
      }
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        this.view.writeMessage("Date must be on a weekday. Please try again.");
        continue;
      }
      break;
    }
    return date;
  }

  /**
   * Create a new portfolio.
   */
  public void createNewPortfolio() {
    this.view.writeMessage("Enter new portfolio name:\n");
    String name = this.view.readLine().trim();
    Payload payload = portfolioController.createNewPortfolio(name);
    if (this.printIfError(payload)) {
      return;
    }
    Portfolio newPortfolio = (Portfolio) payload.getData();
    boolean flag = true;
    this.view.writeMessage("Enter the stocks you want to add to the portfolio \n");
    int quantity = 0;
    while (flag) {
      this.view.writeMessage("Enter the stock symbol:\n");
      String symbol = this.view.readLine().trim();
      while (true) {
        this.view.writeMessage("Enter the quantity of the stock:\n");
        quantity = this.view.readInt();
        if (quantity > 0) {
          break;
        } else {
          this.view.writeMessage("Quantity must be greater than 0\n");
        }
      }
      LocalDate date;
      while (true) {
        this.view.writeMessage("\nEnter the purchase date (YYYY-MM-DD):\n");
        String dateString = this.view.readLine().trim();
        try {
          date = LocalDate.parse(dateString);
        } catch (Exception e) {
          this.view.writeMessage("Invalid date format. Please try again.\n");
          continue;
        }
        date = LocalDate.parse(dateString);
        if (!date.isBefore(LocalDate.now())) {
          this.view.writeMessage("Date must be before today. Please try again.\n");
          continue;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
          this.view.writeMessage("Date must be on a weekday. Please try again.\n");
          continue;
        }
        break;
      }
      payload = portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        return;
      }
      this.view.writeMessage("Press q to exit, Press n to go on: \n");
      String exitChar = this.view.readLine().trim();
      if (exitChar.equals("q")) {
        flag = false;
      }

    }
    this.view.writeMessage("Portfolio '" + name + "' has been created and populated.\n");
  }

  /**
   * Purchase a specific number of shares of a specific stock on a specified date, and add them to
   * the portfolio.
   */
  public void addStockToPortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames());
      this.view.writeMessage("Enter the name of the portfolio to add the stock to:\n");
      String portfolioName = this.view.readLine().trim();
      this.view.writeMessage("Enter the stock symbol:\n");
      String symbol = this.view.readLine().trim();
      this.view.writeMessage("Enter the quantity of the stock to be added:\n");
      int quantity = this.view.readInt();
      this.view.readLine(); // Consume newline
      this.view.writeMessage("Enter the purchase date (YYYY-MM-DD):\n");
      String dateString = this.view.readLine().trim();
      LocalDate date = LocalDate.parse(dateString);
      // get the portfolio by name
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(portfolioName).orElse(null);
      Payload payload = portfolioController.addStockToPortfolio(portfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        return;
      }
      view.displayStockAdded(portfolioName, symbol, quantity);
    } catch (Exception e) {
      this.view.writeMessage("Error adding stock to portfolio: " + e.getMessage());
      this.view.readLine(); // Consume newline
    }
  }

  /**
   * Sell a specific number of shares of a specific stock on a specified date from a given
   * portfolio.
   */
  public void sellStockFromPortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames());
      this.view.writeMessage("Enter the name of the portfolio to sell the stock from:");
      String portfolioName = this.view.readLine().trim();
      this.view.writeMessage("Enter the stock symbol:");
      String symbol = this.view.readLine().trim();
      this.view.writeMessage("Enter the quantity of the stock to be sold:");
      int quantity = this.view.readInt();
      this.view.readLine(); // Consume newline
      this.view.writeMessage("Enter the purchase date (YYYY-MM-DD):");
      String dateString = this.view.readLine().trim();
      LocalDate date = LocalDate.parse(dateString);
      // get the portfolio by name
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(portfolioName).orElse(null);
      Payload payload = portfolioController.sellStockFromPortfolio(portfolio, symbol, quantity,
          date);
      if (this.printIfError(payload)) {
        return;
      }
      view.displayStockSold(portfolioName, symbol, quantity);
    } catch (Exception e) {
      this.view.writeMessage("Error selling stock from portfolio: " + e.getMessage());
      this.view.readLine(); // Consume newline
    }
  }

  /**
   * Calculate the total amount of money invested in a portfolio by a specific date.
   */
  public void calculateInvestment() {
    try {
      this.view.writeMessage("Enter the name of the portfolio:");
      String name = this.view.readLine().trim();
      this.view.writeMessage("Enter the date (YYYY-MM-DD) to calculate the investment:");
      String dateInput = this.view.readLine().trim();
      Payload payload = portfolioController.calculateTotalInvestment(name,
          LocalDate.parse(dateInput));
      if (this.printIfError(payload)) {
        return;
      }

      Optional<BigDecimal> portfolioValue = (Optional<BigDecimal>) payload.getData();
      if (portfolioValue.isPresent()) {
        BigDecimal value = portfolioValue.get();
        view.displayPortfolioInvestment(name, dateInput, value.toString());
      } else {
        this.view.writeMessage("No value found for the portfolio '" + name + "' on " + dateInput);
      }
    } catch (Exception e) {
      this.view.writeMessage("Error calculating portfolio value: " + e.getMessage());
    }
  }

  /**
   * Allows the user to examine details of a specific portfolio, such as its stocks and their
   * quantities.
   */
  public void examinePortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames());
      this.view.writeMessage("Enter the name of the portfolio to examine:");
      String name = this.view.readLine().trim();
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(name).orElse(null);

      if (portfolio != null) {
        view.displayPortfolioDetails(name, portfolio.getStocks());
      } else {
        this.view.writeMessage("Portfolio not found.");
      }
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
    }
  }


  /**
   * Calculates the total value of a portfolio at a given date and displays the result.
   */
  public void calculatePortfolioValue() {
    try {
      this.view.writeMessage("Enter the name of the portfolio:");
      String name = this.view.readLine().trim();
      this.view.writeMessage("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      String dateInput = this.view.readLine().trim();
      Payload payload = portfolioController.calculatePortfolioValue(name,
          LocalDate.parse(dateInput));
      if (this.printIfError(payload)) {
        return;
      }

      Optional<BigDecimal> portfolioValue = (Optional<BigDecimal>) payload.getData();
      if (portfolioValue.isPresent()) {
        BigDecimal value = portfolioValue.get();
        view.displayPortfolioValue(name, dateInput, value.toString());
      } else {
        this.view.writeMessage("No value found for the portfolio '" + name + "' on " + dateInput);
      }
    } catch (Exception e) {
      this.view.writeMessage("Error calculating portfolio value: " + e.getMessage());
      this.view.readLine(); // Consume newline
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void savePortfolio() {
    try {
      this.view.writeMessage("Enter the file path to save the portfolio (.csv):");
      String filePath = this.view.readLine().trim();
      Payload payload = portfolioController.savePortfolio(filePath);
      if (payload.isError()) {
        this.view.writeMessage("Error: " + payload.getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      this.view.readLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadPortfolio() {
    try {
      this.view.writeMessage("Enter the file path to load portfolios from (.csv):");
      String filePath = this.view.readLine().trim();
      Payload payload = portfolioController.loadPortfolio(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        this.view.writeMessage("Error: " + payload);
        return;
      }
      view.displayLoadSuccess();
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      this.view.readLine(); // Consume newline
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
        this.view.writeMessage("Error: " + payload.getMessage());
        return;
      }
      view.writeMessage("Cache have been saved successfully to " + filePath + "\n");
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
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
      view.writeMessage("Cache have been loaded successfully.\n");
    }
    catch (Exception e) {return;
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
        this.view.writeMessage("Error: " + e.getMessage());
      }
      return true;
    }
    return false;
  }
}
