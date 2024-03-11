package View;

import Controller.PortfolioController;
import Model.Portfolio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Class to represent the view for the portfolio.
 */
public class PortfolioView {

  private static final Scanner scanner = new Scanner(System.in);
  private final PortfolioController portfolioController;

  public PortfolioView(PortfolioController portfolioController) {
    this.portfolioController = portfolioController;
  }

  /**
   * Create a new portfolio.
   */
  public void createNewPortfolio() {
    System.out.println("Enter new portfolio name:");
    String name = scanner.nextLine().trim();
    Portfolio newPortfolio = portfolioController.createNewPortfolio(name);

    System.out.println("Enter the number of stocks you want to add to the portfolio:");
    int numberOfStocks = scanner.nextInt();
    scanner.nextLine(); // Consume newline
    for (int i = 0; i < numberOfStocks; i++) {
      System.out.println("Enter the stock symbol:");
      String symbol = scanner.nextLine().trim();
      System.out.println("Enter the quantity of the stock:");
      int quantity = scanner.nextInt();
      scanner.nextLine(); // Consume newline
      System.out.println("Enter the purchase date (YYYY-MM-DD):");
      String dateString = scanner.nextLine().trim();
      LocalDate date = LocalDate.parse(dateString);
      portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
    }
    System.out.println("Portfolio '" + name + "' has been created and populated.");
  }

  /**
   * Examine a portfolio.
   */
  public void examinePortfolio() {
    System.out.println("Available portfolios:");
    portfolioController.getPortfolioService().listPortfolioNames().forEach(System.out::println);

    System.out.println("Enter the name of the portfolio to examine:");
    String name = scanner.nextLine().trim();
    portfolioController.getPortfolioService().getPortfolioByName(name)
        .ifPresentOrElse(
            portfolio -> {
              System.out.println("Stocks in " + name + ":");
              portfolio.getStocks().forEach(stock ->
                  System.out.println(stock.getSymbol() + " - Quantity: " + stock.getQuantity()
                      + ", Purchase Price: " + stock.getPurchasePrice() + ", Purchase Date: "
                      + stock.getPurchaseDate()));
            },
            () -> System.out.println("Portfolio not found.")
        );
  }

  /**
   * Calculate the portfolio value.
   */
  public void calculatePortfolioValue() {
    System.out.println("Enter the name of the portfolio:");
    String name = scanner.nextLine().trim();
    System.out.println("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
    String dateInput = scanner.nextLine().trim();
    try {
      BigDecimal value = portfolioController.calculatePortfolioValue(name, LocalDate.parse(dateInput));
      System.out.println("Value of the portfolio '" + name + "' on " + dateInput + ": " + value);
    } catch (Exception e) {
      System.out.println("Error calculating portfolio value: " + e.getMessage());
    }
  }

  /**
   * Save the portfolio.
   */
  public void savePortfolio() {
    System.out.println("Enter the file path to save the portfolio:");
    String filePath = scanner.nextLine().trim();
    portfolioController.savePortfolio(filePath);
    System.out.println("Portfolios have been saved successfully to " + filePath);
  }

  /**
   * Load the portfolio.
   */
  public void loadPortfolio() {
    System.out.println("Enter the file path to load portfolios from:");
    String filePath = scanner.nextLine().trim();
    portfolioController.loadPortfolio(filePath);
    System.out.println("Portfolios have been loaded successfully.");
  }
}
