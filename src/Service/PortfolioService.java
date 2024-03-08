package Service;

import Interface.IPortfolioService;
import Model.Portfolio;
import Model.Stock;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PortfolioService implements IPortfolioService {
  private List<Portfolio> portfolios = new ArrayList<>();
  private final StockService stockService;

  public PortfolioService(StockService stockService) {
    this.stockService = stockService;
  }

  public void addPortfolio(Portfolio portfolio) {
    Objects.requireNonNull(portfolio, "Portfolio cannot be null");
    if (portfolios.stream().anyMatch(p -> p.getName().equalsIgnoreCase(portfolio.getName()))) {
      throw new IllegalArgumentException("A portfolio with the name '" + portfolio.getName() + "' already exists.");
    }
    portfolios.add(portfolio);
  }

  public void addStockToPortfolio(String portfolioName, String symbol, int quantity, LocalDate date) {
    Portfolio portfolio = getPortfolioByName(portfolioName).orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));
    BigDecimal price = stockService.fetchPriceOnDate(symbol, date);
    Stock stock = new Stock(symbol, quantity, price, date);
    portfolio.addStock(stock);
  }

  public Optional<Portfolio> getPortfolioByName(String name) {
    return portfolios.stream()
            .filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst();
  }

  public BigDecimal calculatePortfolioValue(String portfolioName, LocalDate onDate) {
    return getPortfolioByName(portfolioName).map(portfolio ->
                    portfolio.getStocks().stream()
                            .map(stock -> stockService.fetchPriceOnDate(stock.getSymbol(), onDate)
                                    .multiply(BigDecimal.valueOf(stock.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
            .orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));
  }

  public List<String> listPortfolioNames() {
    return portfolios.stream().map(Portfolio::getName).collect(Collectors.toList());
  }

  public void savePortfoliosToCSV(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date\n");
      for (Portfolio portfolio : portfolios) {
        for (Stock stock : portfolio.getStocks()) {
          writer.append(String.join(",", portfolio.getName(), stock.getSymbol(),
                  String.valueOf(stock.getQuantity()), stock.getPurchasePrice().toString(),
                  stock.getPurchaseDate().toString())).append("\n");
        }
      }
    }
  }

  public void loadPortfoliosFromCSV(String filePath) throws IOException {
    List<Portfolio> loadedPortfolios = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      reader.readLine(); // Skip header
      Map<String, Portfolio> portfolioMap = new HashMap<>();
      reader.lines().forEach(line -> {
        String[] data = line.split(",");
        Portfolio portfolio = portfolioMap.computeIfAbsent(data[0], Portfolio::new);
        Stock stock = new Stock(data[1], Integer.parseInt(data[2]), new BigDecimal(data[3]), LocalDate.parse(data[4]));
        portfolio.addStock(stock);
      });
      loadedPortfolios.addAll(portfolioMap.values());
    }
    portfolios.clear();
    portfolios.addAll(loadedPortfolios);
  }

  public boolean portfolioExists(String portfolioName) {
    return false;
  }
}
