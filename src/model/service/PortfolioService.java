package model.service;

import controller.Payload;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedReader;
import model.Portfolio;
import model.Stock;
import model.Tradable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import model.PortfolioInterface;

/**
 * Service class for managing portfolios.
 */
public class PortfolioService implements PortfolioServiceInterface {

  private final List<PortfolioInterface> portfolios = new ArrayList<>();
  private final StockServiceInterface stockService;

  /**
   * Constructor for the PortfolioService class.
   *
   * @param stockService The StockService model object to be used by the service.
   */
  public PortfolioService(StockServiceInterface stockService) {
    this.stockService = stockService;
  }


  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The name of the new portfolio.
   */
  public void createNewPortfolio(String name) throws IllegalArgumentException {
    String message;
    if (portfolioExists(name)) {
      message = "Portfolio already exists: " + name;
      throw new IllegalArgumentException(message);
    }
    if (name.isEmpty()) {
      message = "Portfolio name cannot be empty";
      throw new IllegalArgumentException(message);
    }
    Portfolio portfolio = new Portfolio(name);
    this.addPortfolio(portfolio);
  }

  /**
   * Adds a portfolio to the list of portfolios.
   *
   * @param portfolio The portfolio to add.
   */
  public void addPortfolio(Portfolio portfolio) {
    Objects.requireNonNull(portfolio, "Portfolio cannot be null");
    if (portfolios.stream().anyMatch(p -> p.getName().equalsIgnoreCase(portfolio.getName()))) {
      throw new IllegalArgumentException(
          "A portfolio with the name '" + portfolio.getName() + "' already exists.");
    }
    portfolios.add(portfolio);
  }


  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolioName The name of the portfolio to which the stock will be added.
   * @param symbol        The symbol of the stock to be added.
   * @param quantity      The quantity of the stock to be added.
   * @param date          The date on which the stock was purchased.
   */
  public void addStockToPortfolio(String portfolioName, String symbol, int quantity,
      LocalDate date) throws IllegalArgumentException {
    String message;
    PortfolioInterface portfolio = getPortfolioByName(portfolioName).orElseThrow(
        () -> new IllegalArgumentException("Portfolio not found: " + portfolioName));

    // check if stock already exists in portfolio
    if (portfolio.getStocks().stream().anyMatch(
        s -> s.getSymbol().equalsIgnoreCase(symbol) && s.getPurchaseDate().equals(date))) {
      message = "Stock already exists in portfolio: " + symbol + " on " + date;
      throw new IllegalArgumentException(message);
    }

    // check if quantity is positive
    else if (quantity <= 0) {
      message = "Quantity must be positive: " + quantity;
      throw new IllegalArgumentException(message);
    }

    // check if quantity is whole number

    // check if date is in the future
    else if (date.isAfter(LocalDate.now())) {
      message = "Date cannot be in the future: " + date;
      throw new IllegalArgumentException(message);
    } else {
      Payload price = stockService.fetchPriceOnDate(symbol, date);
      if (price.isError()) {
        message = price.getMessage();
        throw new IllegalArgumentException(message);
      }
      Tradable stock = new Stock(symbol, quantity, (BigDecimal) price.getData(),
          date);
      portfolio.addStock(stock);
    }
  }

  /**
   * Fetches a portfolio by its name.
   *
   * @param name The name of the portfolio to fetch.
   * @return An Optional containing the portfolio if found, or an empty Optional otherwise.
   */
  public Optional<PortfolioInterface> getPortfolioByName(String name) {
    return portfolios.stream()
        .filter(p -> p.getName().equalsIgnoreCase(name))
        .findFirst();
  }

  /**
   * Calculates the total value of a portfolio on a given date.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the value is to be calculated.
   * @return The total value of the portfolio on the given date.
   */
  public Optional<BigDecimal> calculatePortfolioValue(String portfolioName, LocalDate onDate)
      throws IllegalArgumentException {
    String message;
    // check if date is in the future
    if (onDate.isAfter(LocalDate.now())) {
      message = "Date cannot be in the future: " + onDate;
      throw new IllegalArgumentException(message);
    }

    // check if portfolio exists
    if (!portfolioExists(portfolioName)) {
      message = "Portfolio not found: " + portfolioName;
      throw new IllegalArgumentException(message);
    }

    return getPortfolioByName(portfolioName).map(portfolio -> {
      BigDecimal totalValue = BigDecimal.ZERO;
      for (Tradable stock : portfolio.getStocks()) {
        if (stock.getPurchaseDate().isBefore(onDate) || stock.getPurchaseDate().isEqual(onDate)) {
          Payload priceOnDate = stockService.fetchPreviousClosePrice(stock.getSymbol(), onDate);
          BigDecimal value = ((BigDecimal) priceOnDate.getData()).multiply(
              new BigDecimal(stock.getQuantity()));
          totalValue = totalValue.add(value);
        }
      }
      return totalValue;
    });
  }

  /**
   * Returns number of portfolios.
   *
   * @return number of portfolios.
   */
  public int getNumberOfPortfolios() {
    return portfolios.size();
  }

  /**
   * Returns a list of all portfolio names.
   *
   * @return A list of all portfolio names.
   */
  public List<String> listPortfolioNames() {
    return portfolios.stream().map(PortfolioInterface::getName).collect(Collectors.toList());
  }

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path to which the portfolios will be saved.
   */
  public String savePortfoliosToCSV(String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date\n");
      for (PortfolioInterface portfolio : portfolios) {
        for (Tradable stock : portfolio.getStocks()) {
          writer.append(String.join(",", portfolio.getName(), stock.getSymbol(),
              String.valueOf(stock.getQuantity()), stock.getPurchasePrice().toString(),
              stock.getPurchaseDate().toString())).append("\n");
        }
      }
    } catch (IOException e) {
      return "Error saving portfolio to file: " + e.getMessage();
    }
    return "";
  }

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @throws IOException If an error occurs while reading from the file.
   */
  public String loadPortfoliosFromCSV(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      return "File not found: " + filePath;
    }
    List<Portfolio> loadedPortfolios;
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      reader.readLine(); // Skip header
      Map<String, Portfolio> portfolioMap = new HashMap<>();
      reader.lines().forEach(line -> {
        String[] data = line.split(",");
        Portfolio portfolio = portfolioMap.computeIfAbsent(data[0], Portfolio::new);
        Stock stock = new Stock(data[1], Integer.parseInt(data[2]), new BigDecimal(data[3]),
            LocalDate.parse(data[4]));
        portfolio.addStock(stock);
      });
      loadedPortfolios = new ArrayList<>(portfolioMap.values());
    }
    portfolios.clear();
    portfolios.addAll(loadedPortfolios);
    return "";
  }

  /**
   * Checks if a portfolio with the given name exists.
   *
   * @param portfolioName The name of the portfolio to check.
   * @return True if the portfolio exists, false otherwise.
   */
  public boolean portfolioExists(String portfolioName) {
    return portfolios.stream().anyMatch(p -> p.getName().equalsIgnoreCase(portfolioName));
  }
}