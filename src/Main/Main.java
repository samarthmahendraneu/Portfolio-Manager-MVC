package Main;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

import Model.Portfolio;
import Model.Stock;
import Service.PortfolioService;
import Service.StockService;

public class Main {
  private static Scanner scanner = new Scanner(System.in);
  private static  StockService stockService = new StockService("W0M1JOKC82EZEQA8");

  private static PortfolioService portfolioService =
          new PortfolioService(stockService);


  public static void main(String[] args) {
    boolean running = true;

    while (running) {
      System.out.println("\nPortfolio Management System:");
      System.out.println("1. Create a new portfolio");
      System.out.println("2. Examine a portfolio");
      System.out.println("3. Calculate portfolio value");
      System.out.println("4. Save portfolio");
      System.out.println("5. Load portfolio");
      System.out.println("6. Exit");
      System.out.print("Select an option: ");

      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          createNewPortfolio();
          break;
        case 2:
          examinePortfolio();
          break;
        case 3:
          calculatePortfolioValue();
          break;
        case 4:
          savePortfolio();
          break;
        case 5:
          loadPortfolio();
          break;
        case 6:
          System.out.println("Exiting...");
          running = false;
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    }
  }

  private static void createNewPortfolio() {
    System.out.println("Enter new portfolio name:");
    String name = scanner.nextLine().trim();

    Portfolio newPortfolio = new Portfolio(name);
    portfolioService.addPortfolio(newPortfolio);  // Assuming this method adds the portfolio to the service

    System.out.println("Portfolio '" + name + "' created. Add stocks to this portfolio:");

    while (true) {
      System.out.println("Enter stock symbol (or 'done' to finish):");
      String symbol = scanner.nextLine().trim();
      if ("done".equalsIgnoreCase(symbol)) break;

      System.out.println("Enter quantity:");
      int quantity = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("Enter purchase date (YYYY-MM-DD):");
      LocalDate date = LocalDate.parse(scanner.nextLine().trim());

      BigDecimal price = stockService.fetchPriceOnDate(symbol, date);

      if (price.compareTo(BigDecimal.ZERO) > 0) {
        Stock stock = new Stock(symbol, quantity, price, date);
        newPortfolio.addStock(stock); // Assuming addStock method exists
        System.out.println(String.format("Added %d shares of %s at %s on %s.", quantity, symbol, price, date));
      } else {
        System.out.println("Failed to fetch price for " + symbol + " on " + date + ". Stock not added.");
      }
    }
  }


  private static void examinePortfolio() {
    System.out.println("Available portfolios:");
    portfolioService.listPortfolios().forEach(p -> System.out.println(p.getName()));

    System.out.println("Enter the name of the portfolio to examine:");
    String name = scanner.nextLine().trim();
    Portfolio portfolio = portfolioService.getPortfolioByName(name);

    if (portfolio != null) {
      System.out.println("Stocks in " + name + ":");
      portfolio.getStocks().forEach(stock ->
              System.out.println(stock.getSymbol() + " - Quantity: " + stock.getQuantity() + ", Purchase Price: " + stock.getPurchasePrice() + ", Purchase Date: " + stock.getPurchaseDate()));
    } else {
      System.out.println("Portfolio not found.");
    }
  }


  private static void calculatePortfolioValue() {
    System.out.println("Enter the name of the portfolio:");
    String name = scanner.nextLine().trim();
    System.out.println("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
    LocalDate date = LocalDate.parse(scanner.nextLine().trim());

    BigDecimal value = portfolioService.calculatePortfolioValue(name, date);
    System.out.println("Value of the portfolio '" + name + "' on " + date + ": " + value);
  }


  private static void savePortfolio() {
    System.out.println("Enter the name of the portfolio to save:");
    String name = scanner.nextLine().trim();
    System.out.println("Enter the file path to save the portfolio:");
    String filePath = scanner.nextLine().trim();

    try {
      portfolioService.savePortfoliosToCSV(filePath); // Assuming this method in PortfolioService handles saving to a file
      System.out.println("Portfolio saved successfully to " + filePath);
    } catch (IOException e) {
      System.out.println("Failed to save portfolio: " + e.getMessage());
    }
  }


  private static void loadPortfolio() {
    System.out.println("Enter the file path to load portfolios from:");
    String filePath = scanner.nextLine().trim();

    try {
      portfolioService.loadPortfoliosFromCSV(filePath); // Using the existing loadPortfoliosFromCSV method
      System.out.println("Portfolios loaded successfully.");
    } catch (IOException e) {
      System.out.println("Failed to load portfolios: " + e.getMessage());
    }
  }

}
