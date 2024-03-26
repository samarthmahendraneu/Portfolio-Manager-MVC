package Controller;

import Model.Portfolio;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import Model.PortfolioInterface;
import View.View;


/**
 * Controller class for managing the interaction between user inputs and portfolio operations
 * through a menu interface.
 */
public class PortfolioMenuController implements PortfolioMenuControllerInterface {

  private static final Scanner scanner = new Scanner(System.in);
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
    String cacheFilePath = "stockDataCache.csv"; // Adjust the path as needed
    try {
      portfolioController.loadCache(cacheFilePath);
      System.out.println("Cache loaded successfully.");
    } catch (Exception e) {
      System.err.println("Failed to load cache: " + e.getMessage());
    }
  }
  private void addShutdownHookForCache() {
    // Specify the cache file path
    String cacheFilePath = "stockDataCache.csv"; // Adjust the path as needed

    // Add shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        portfolioController.saveCache(cacheFilePath);
        System.out.println("Cache saved successfully.");
      } catch (Exception e) {
        System.err.println("Failed to save cache: " + e.getMessage());
        // Handle the failure to save cache appropriately
      }
    }));
  }

  /**
   * Displays the main menu and handles user input for various portfolio operations.
   */
  public void displayMainMenu() {
    addShutdownHookForCache();
    boolean running = true;
    while (running) {
      try {
        view.displayMainMenu();
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

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
            this.view.writeMessage("Exiting...");
            running = false;
            break;
          case 7:
            this.CalculateGraph();
            break;
          case 8:
            // Purchase a specific number of shares of a specific stock on a specified date, and add them to the portfolio
            this.addStockToPortfolio();
          case 9:
            // Sell a specific number of shares of a specific stock on a specified date from a given portfolio
            this.sellStockFromPortfolio();
          case 10:
            // the total amount of money invested in a portfolio) by a specific date.
            this.calculateInvestment();
          default:
            this.view.writeMessage("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        this.view.writeMessage("Error: " + e.getMessage());
        scanner.nextLine(); // Consume newline
      }
    }
  }

  public void CalculateGraph() {
    LocalDate date, date2;

    this.view.writeMessage("Enter Stock or Portfolio name:");
    String name = scanner.nextLine().trim();
    this.view.writeMessage("Enter Start Date");
    date = dateValidator();
    this.view.writeMessage("Enter End Date");
    date2 = dateValidator();
    portfolioController.GenGraph(name, date, date2);
  }


  private LocalDate dateValidator()
  {
    LocalDate date ;

    while (true) {
      String dateString = scanner.nextLine().trim();
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
    this.view.writeMessage("Enter new portfolio name:");
    String name = scanner.nextLine().trim();
    Payload payload = portfolioController.createNewPortfolio(name);
    if (this.printIfError(payload)) {
      return;
    }
    Portfolio newPortfolio = (Portfolio) payload.getData();
    boolean flag = true;
    this.view.writeMessage("Enter the stocks you want to add to the portfolio");
    int quantity = 0;
    while (flag) {
      this.view.writeMessage("Enter the stock symbol:");
      String symbol = scanner.nextLine().trim();
      while (true) {
        this.view.writeMessage("Enter the quantity of the stock:");

        if (scanner.hasNextInt()) {
          quantity = scanner.nextInt();
          scanner.nextLine();
          if (quantity > 0) {
            break;
          } else {
            this.view.writeMessage("Quantity must be greater than 0");
          }
        } else {
          this.view.writeMessage("Cannot purchase Fractional Shares");
          scanner.nextLine();
        }
      }
      LocalDate date;
      while (true) {
        this.view.writeMessage("Enter the purchase date (YYYY-MM-DD):");
        String dateString = scanner.nextLine().trim();
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
      payload = portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        return;
      }
      this.view.writeMessage("Press q to exit, Press n to go on");
      String exitChar = scanner.nextLine().trim();
      if (exitChar.equals("q")) {
        flag = false;
      }

    }
    this.view.writeMessage("Portfolio '" + name + "' has been created and populated.");
  }

  /**
   * Purchase a specific number of shares of a specific stock on a specified date, and add them to the
   * portfolio.
   */
  public void addStockToPortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames());
      this.view.writeMessage("Enter the name of the portfolio to add the stock to:");
      String portfolioName = scanner.nextLine().trim();
      this.view.writeMessage("Enter the stock symbol:");
      String symbol = scanner.nextLine().trim();
      this.view.writeMessage("Enter the quantity of the stock:");
      int quantity = scanner.nextInt();
      scanner.nextLine(); // Consume newline
      this.view.writeMessage("Enter the purchase date (YYYY-MM-DD):");
      String dateString = scanner.nextLine().trim();
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
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Sell a specific number of shares of a specific stock on a specified date from a given portfolio.
   *
   */
  public void sellStockFromPortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames());
      this.view.writeMessage("Enter the name of the portfolio to sell the stock from:");
      String portfolioName = scanner.nextLine().trim();
      this.view.writeMessage("Enter the stock symbol:");
      String symbol = scanner.nextLine().trim();
      this.view.writeMessage("Enter the quantity of the stock:");
      int quantity = scanner.nextInt();
      scanner.nextLine(); // Consume newline
      this.view.writeMessage("Enter the purchase date (YYYY-MM-DD):");
      String dateString = scanner.nextLine().trim();
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
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Calculate the total amount of money invested in a portfolio by a specific date.
   */
public void calculateInvestment() {
  try {
    this.view.writeMessage("Enter the name of the portfolio:");
    String name = scanner.nextLine().trim();
    this.view.writeMessage("Enter the date (YYYY-MM-DD) to calculate the investment:");
    String dateInput = scanner.nextLine().trim();
    Payload payload = portfolioController.calculateTotalInvestment(name, LocalDate.parse(dateInput));
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
    scanner.nextLine(); // Consume newline
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
      String name = scanner.nextLine().trim();
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(name).orElse(null);

      if (portfolio != null) {
        view.displayPortfolioDetails(name, portfolio.getStocks());
      } else {
        this.view.writeMessage("Portfolio not found.");
      }
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }


  /**
   * Calculates the total value of a portfolio at a given date and displays the result.
   */
  public void calculatePortfolioValue() {
    try {
      this.view.writeMessage("Enter the name of the portfolio:");
      String name = scanner.nextLine().trim();
      this.view.writeMessage("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      String dateInput = scanner.nextLine().trim();
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
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void savePortfolio() {
    try {
      this.view.writeMessage("Enter the file path to save the portfolio:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.savePortfolio(filePath);
      if (payload.isError()) {
        this.view.writeMessage("Error: " + payload.getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadPortfolio() {
    try {
      this.view.writeMessage("Enter the file path to load portfolios from:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.loadPortfolio(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        this.view.writeMessage("Error: " + payload);
        return;
      }
      view.displayLoadSuccess();
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void saveStockCache() {
    try {
      this.view.writeMessage("Enter the file path to save the cache:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.saveCache(filePath);
      if (payload.isError()) {
        this.view.writeMessage("Error: " + payload.getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadStockCache() {
    try {
      this.view.writeMessage("Enter the file path to load portfolios from:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.loadCache(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        this.view.writeMessage("Error: " + payload);
        return;
      }
      view.displayLoadSuccess();
    } catch (Exception e) {
      this.view.writeMessage("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
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
