package Main;

import Service.PortfolioService;
import Model.Stock;
import Service.StockService; // Ensure this is your actual service class that fetches data from the internet

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    // Initialize the stock service with your actual API key
    StockService stockService = new StockService("W0M1JOKC82EZEQA8");
    PortfolioService portfolioService = new PortfolioService(stockService);

    // Example: Creating a portfolio and adding stocks
    // Note: The purchase price might be ignored or used differently depending on how you implement the stock value calculation
    String portfolioName = "Tech Portfolio";
    portfolioService.createPortfolio(portfolioName, new ArrayList<>()); // Initially empty, stocks added later

    // Example stock symbols
    String[] symbols = {"AAPL", "MSFT"};

    // Adding stocks to the "Tech Portfolio"
    for (String symbol : symbols) {
      // For demonstration, assuming fetching the most recent close price as the purchase price
      BigDecimal purchasePrice = stockService.fetchRecentClosePrice(symbol);
      Stock stock = new Stock(symbol, 10, purchasePrice, LocalDate.now());
      portfolioService.addStockToPortfolio(portfolioName, stock);
    }

    // Calculate the portfolio's value on a specific date
    BigDecimal value = portfolioService.calculatePortfolioValue(portfolioName, LocalDate.now());
    System.out.println("Current value of " + portfolioName + ": $" + value);

    // List all portfolios
    System.out.println("\nAll Portfolios:");
    portfolioService.listPortfolios().forEach(portfolio -> System.out.println(portfolio.getName()));
  }
}
