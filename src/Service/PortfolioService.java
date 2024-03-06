package Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Portfolio;
import Model.Stock;
import Service.StockService;

public class PortfolioService {
  private List<Portfolio> portfolios = new ArrayList<>();
  private StockService stockService;
  private final String portfolioFilePath = "portfolios.dat";


  public PortfolioService(StockService stockService) {
    this.stockService = stockService;
  }

  public void createPortfolio(String name, List<Stock> initialStocks) {
    portfolios.add(new Portfolio(name, initialStocks));
  }


  public BigDecimal calculatePortfolioValue(String portfolioName, LocalDate onDate) {
    for (Portfolio portfolio : portfolios) {
      if (portfolio.getName().equals(portfolioName)) {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Stock stock : portfolio.getStocks()) {
          // Assuming stockService can fetch the price on a specific date
          BigDecimal price = stockService.fetchPriceOnDate(stock.getSymbol(), onDate);
          BigDecimal stockValue = price.multiply(BigDecimal.valueOf(stock.getQuantity()));
          totalValue = totalValue.add(stockValue);
        }
        return totalValue;
      }
    }
    return BigDecimal.ZERO; // Portfolio not found
  }

  public List<Portfolio> listPortfolios() {
    return Collections.unmodifiableList(portfolios); // Prevents external modification
  }

  public void savePortfoliosToFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new
            FileOutputStream(portfolioFilePath))) {
      oos.writeObject(portfolios);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error saving portfolios to file.");
    }
  }

  public void loadPortfoliosFromFile() {
    File file = new File(portfolioFilePath);
    if (file.exists()) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        portfolios = (List<Portfolio>) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        System.out.println("Error loading portfolios from file.");
      }
    } else {
      portfolios = new ArrayList<>(); // No portfolios to load, initialize to empty list
    }
  }
  public void addStockToPortfolio(String portfolioName, Stock stock) {
    // Find the portfolio by name
    Portfolio portfolio = portfolios.stream()
            .filter(p -> p.getName().equals(portfolioName))
            .findFirst()
            .orElse(null);

    // If the portfolio exists, add the stock to it
    if (portfolio != null) {
      portfolio.addStock(stock);
    } else {
      System.out.println("Portfolio '" + portfolioName + "' not found.");
    }
  }

}
