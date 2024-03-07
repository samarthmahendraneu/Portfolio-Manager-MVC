package Main;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;
import Service.PortfolioService;
import Service.StockService;

public class Main {
  private static final Scanner scanner = new Scanner(System.in);
  private static final StockService stockService = new StockService("W0M1JOKC82EZEQA8");
  private static final PortfolioService portfolioService = new PortfolioService(stockService);

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
    portfolioService.createAndPopulatePortfolio(name, scanner);
    System.out.println("Portfolio '" + name + "' has been created and populated.");
  }

  private static void examinePortfolio() {
    System.out.println("Available portfolios:");
    portfolioService.listPortfolioNames().forEach(System.out::println);

    System.out.println("Enter the name of the portfolio to examine:");
    String name = scanner.nextLine().trim();
    portfolioService.getPortfolioByName(name)
            .ifPresentOrElse(
                    portfolio -> {
                      System.out.println("Stocks in " + name + ":");
                      portfolio.getStocks().forEach(stock ->
                              System.out.println(stock.getSymbol() + " - Quantity: " + stock.getQuantity() + ", Purchase Price: " + stock.getPurchasePrice() + ", Purchase Date: " + stock.getPurchaseDate()));
                    },
                    () -> System.out.println("Portfolio not found.")
            );
  }

  private static void calculatePortfolioValue() {
    System.out.println("Enter the name of the portfolio:");
    String name = scanner.nextLine().trim();
    System.out.println("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
    String dateInput = scanner.nextLine().trim();
    try {
      BigDecimal value = portfolioService.calculatePortfolioValue(name, LocalDate.parse(dateInput));
      System.out.println("Value of the portfolio '" + name + "' on " + dateInput + ": " + value);
    } catch (Exception e) {
      System.out.println("Error calculating portfolio value: " + e.getMessage());
    }
  }

  private static void savePortfolio() {
    System.out.println("Enter the file path to save the portfolio:");
    String filePath = scanner.nextLine().trim();
    try {
      portfolioService.savePortfoliosToCSV(filePath);
      System.out.println("Portfolios have been saved successfully to " + filePath);
    } catch (IOException e) {
      System.out.println("Failed to save portfolios: " + e.getMessage());
    }
  }

  private static void loadPortfolio() {
    System.out.println("Enter the file path to load portfolios from:");
    String filePath = scanner.nextLine().trim();
    try {
      portfolioService.loadPortfoliosFromCSV(filePath);
      System.out.println("Portfolios have been loaded successfully.");
    } catch (IOException e) {
      System.out.println("Failed to load portfolios: " + e.getMessage());
    }
  }
}
