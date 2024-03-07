package Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Portfolio;
import Model.Stock;

public class PortfolioService {
  private List<Portfolio> portfolios = new ArrayList<>();
  private StockService stockService;


  public PortfolioService(StockService stockService) {
    this.stockService = stockService;
  }

  public void createPortfolio(String name, List<Stock> initialStocks) {
    portfolios.add(new Portfolio(name));
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

  public void savePortfoliosToCSV(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date\n");
      for (Portfolio portfolio : portfolios) {
        for (Stock stock : portfolio.getStocks()) {
          writer.append(portfolio.getName()).append(",")
                  .append(stock.getSymbol()).append(",")
                  .append(String.valueOf(stock.getQuantity())).append(",")
                  .append(stock.getPurchasePrice().toString()).append(",")
                  .append(stock.getPurchaseDate().toString()).append("\n");
        }
      }
    }
  }

  public void loadPortfoliosFromCSV(String filePath) throws IOException {
    Map<String, Portfolio> portfolioMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line = reader.readLine(); // Skip header line
      while ((line = reader.readLine()) != null) {
        String[] data = line.split(",");
        String portfolioName = data[0];
        Stock stock = new Stock(data[1], Integer.parseInt(data[2]),
                new BigDecimal(data[3]), LocalDate.parse(data[4]));

        portfolioMap.computeIfAbsent(portfolioName, Portfolio::new).addStock(stock);
      }
    }

    portfolios.clear();
    portfolios.addAll(portfolioMap.values());
  }

  public Portfolio getPortfolioByName(String name) {
    for (Portfolio portfolio : portfolios) {
      if (portfolio.getName().equals(name)) {
        return portfolio;
      }
    }
    return null; // Portfolio not found
  }
  /**
   * Adds a new portfolio to the service's list of managed portfolios.
   *
   * @param portfolio The Portfolio object to add.
   */
  public void addPortfolio(Portfolio portfolio) {
    if (portfolio == null) {
      throw new IllegalArgumentException("Portfolio cannot be null");
    }

    // Optionally, check if a portfolio with the same name already exists to avoid duplicates
    for (Portfolio existingPortfolio : portfolios) {
      if (existingPortfolio.getName().equals(portfolio.getName())) {
        throw new IllegalArgumentException("A portfolio with the name " + portfolio.getName() + " already exists.");
      }
    }

    portfolios.add(portfolio);
  }


}
