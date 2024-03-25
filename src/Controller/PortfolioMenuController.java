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
  }

  /**
   * Displays the main menu and handles user input for various portfolio operations.
   */
  public void displayMainMenu() {
    boolean running = true;
    while (running) {
      try {
        view.displayMainMenu(System.out);
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
            System.out.println("Exiting...");
            running = false;
            break;
          case 7:
            this.CalculateGraph();
            break;
          case 8:
            this.saveStockCache();
            break;
          case 9:
            this.loadStockCache();
          default:
            System.out.println("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        scanner.nextLine(); // Consume newline
      }
    }
  }

  public void CalculateGraph() {
    LocalDate date, date2;

    System.out.println("Enter Stock or Portfolio name:");
    String name = scanner.nextLine().trim();
    System.out.println("Enter Start Date");
    date = dateValidator();
    System.out.println("Enter End Date");
    date2 = dateValidator();
    portfolioController.GenGraph(name, date, date2);
  }


  private static LocalDate dateValidator()
  {
    LocalDate date ;

    while (true) {
      System.out.println("Enter the purchase date (YYYY-MM-DD):");
      String dateString = scanner.nextLine().trim();
      try {
        date = LocalDate.parse(dateString);
      } catch (Exception e) {
        System.out.println("Invalid date format. Please try again.");
        continue;
      }
      date = LocalDate.parse(dateString);
      if (!date.isBefore(LocalDate.now())) {
        System.out.println("Date must be before today. Please try again.");
        continue;
      }
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        System.out.println("Date must be on a weekday. Please try again.");
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
    System.out.println("Enter new portfolio name:");
    String name = scanner.nextLine().trim();
    Payload payload = portfolioController.createNewPortfolio(name);
    if (this.printIfError(payload)) {
      return;
    }
    Portfolio newPortfolio = (Portfolio) payload.getData();
    boolean flag = true;
    System.out.println("Enter the stocks you want to add to the portfolio");
    int quantity = 0;
    while (flag) {
      System.out.println("Enter the stock symbol:");
      String symbol = scanner.nextLine().trim();
      while (true) {
        System.out.println("Enter the quantity of the stock:");

        if (scanner.hasNextInt()) {
          quantity = scanner.nextInt();
          scanner.nextLine();
          if (quantity > 0) {
            break;
          } else {
            System.out.println("Quantity must be greater than 0");
          }
        } else {
          System.out.println("Cannot purchase Fractional Shares");
          scanner.nextLine();
        }
      }
      LocalDate date;
      while (true) {
        System.out.println("Enter the purchase date (YYYY-MM-DD):");
        String dateString = scanner.nextLine().trim();
        try {
          date = LocalDate.parse(dateString);
        } catch (Exception e) {
          System.out.println("Invalid date format. Please try again.");
          continue;
        }
        date = LocalDate.parse(dateString);
        if (!date.isBefore(LocalDate.now())) {
          System.out.println("Date must be before today. Please try again.");
          continue;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
          System.out.println("Date must be on a weekday. Please try again.");
          continue;
        }
        break;
      }
      payload = portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
      if (this.printIfError(payload)) {
        return;
      }
      System.out.println("Press q to exit, Press n to go on");
      String exitChar = scanner.nextLine().trim();
      if (exitChar.equals("q")) {
        flag = false;
      }

    }
    System.out.println("Portfolio '" + name + "' has been created and populated.");
  }

  /**
   * Allows the user to examine details of a specific portfolio, such as its stocks and their
   * quantities.
   */
  public void examinePortfolio() {
    try {
      view.displayAvailablePortfolios(
          portfolioController.getPortfolioService().listPortfolioNames(), System.out);
      System.out.println("Enter the name of the portfolio to examine:");
      String name = scanner.nextLine().trim();
      PortfolioInterface portfolio = portfolioController.getPortfolioService()
          .getPortfolioByName(name).orElse(null);

      if (portfolio != null) {
        view.displayPortfolioDetails(name, portfolio.getStocks(), System.out);
      } else {
        System.out.println("Portfolio not found.");
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }


  /**
   * Calculates the total value of a portfolio at a given date and displays the result.
   */
  public void calculatePortfolioValue() {
    try {
      System.out.println("Enter the name of the portfolio:");
      String name = scanner.nextLine().trim();
      System.out.println("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      String dateInput = scanner.nextLine().trim();
      Payload payload = portfolioController.calculatePortfolioValue(name,
          LocalDate.parse(dateInput));
      if (this.printIfError(payload)) {
        return;
      }

      Optional<BigDecimal> portfolioValue = (Optional<BigDecimal>) payload.getData();
      if (portfolioValue.isPresent()) {
        BigDecimal value = portfolioValue.get();
        view.displayPortfolioValue(name, dateInput, value.toString(), System.out);
      } else {
        System.out.println("No value found for the portfolio '" + name + "' on " + dateInput);
      }
    } catch (Exception e) {
      System.out.println("Error calculating portfolio value: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void savePortfolio() {
    try {
      System.out.println("Enter the file path to save the portfolio:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.savePortfolio(filePath);
      if (payload.isError()) {
        System.out.println("Error: " + payload.getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadPortfolio() {
    try {
      System.out.println("Enter the file path to load portfolios from:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.loadPortfolio(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        System.out.println("Error: " + payload);
        return;
      }
      view.displayLoadSuccess(System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Saves the portfolio to a specified file path.
   */
  public void saveStockCache() {
    try {
      System.out.println("Enter the file path to save the cache:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.saveCache(filePath);
      if (payload.isError()) {
        System.out.println("Error: " + payload.getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Loads portfolios from a specified file path.
   */
  public void loadStockCache() {
    try {
      System.out.println("Enter the file path to load portfolios from:");
      String filePath = scanner.nextLine().trim();
      Payload payload = portfolioController.loadCache(filePath);
      if (Objects.nonNull(payload) && payload.isError()) {
        System.out.println("Error: " + payload);
        return;
      }
      view.displayLoadSuccess(System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
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
        view.displayError(payload.getMessage(), System.out);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      return true;
    }
    return false;
  }
}
