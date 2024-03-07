package Service;

import Model.Portfolio;
import Model.Stock;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PortfolioService {
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

  public void createAndPopulatePortfolio(String name, Scanner scanner) {
    Portfolio newPortfolio = new Portfolio(name);
    this.addPortfolio(newPortfolio); // Adds the new portfolio

    while (true) {
      System.out.println("Enter stock symbol (or 'done' to finish):");
      String symbol = scanner.nextLine().trim();
      if ("done".equalsIgnoreCase(symbol)) break;

      System.out.println("Enter quantity:");
      int quantity = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("Enter purchase date (YYYY-MM-DD):");
      LocalDate date = LocalDate.parse(scanner.nextLine().trim());

      BigDecimal price = stockService.fetchPriceOnDate(symbol, date);

      Stock stock = new Stock(symbol, quantity, price, date);
      newPortfolio.addStock(stock); // Assumes this method exists in Portfolio
    }
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
      portfolios.forEach(p -> p.getStocks().forEach(stock -> {
        try {
          writer.append(String.join(",", p.getName(), stock.getSymbol(),
                  String.valueOf(stock.getQuantity()), stock.getPurchasePrice().toString(),
                  stock.getPurchaseDate().toString())).append("\n");
        } catch (IOException e) {
          e.printStackTrace(); // Consider proper error handling
        }
      }));
    }
  }

  public void loadPortfoliosFromCSV(String filePath) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      reader.readLine(); // Skip header
      Map<String, Portfolio> portfolioMap = new HashMap<>();
      reader.lines().forEach(line -> {
        String[] data = line.split(",");
        Portfolio portfolio = portfolioMap.computeIfAbsent(data[0], Portfolio::new);
        Stock stock = new Stock(data[1], Integer.parseInt(data[2]), new BigDecimal(data[3]), LocalDate.parse(data[4]));
        portfolio.addStock(stock);
      });
      portfolios.clear();
      portfolios.addAll(portfolioMap.values());
    }
  }
}
