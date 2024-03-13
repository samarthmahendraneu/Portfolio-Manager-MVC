package Model.Service;

import Controller.Payload;
import Model.Portfolio;
import Model.Stock;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing portfolios.
 */
public class PortfolioService implements PortfolioServiceInterface {

  private final List<Portfolio> portfolios = new ArrayList<>();
  private final StockService stockService;

  /**
   * Constructor for the PortfolioService class.
   *
   * @param stockService The StockService model object to be used by the service.
   */
  public PortfolioService(StockService stockService) {
    this.stockService = stockService;
  }


  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The name of the new portfolio.
   * @return The newly created Portfolio object.
   */
  public Payload createNewPortfolio(String name) {
    String message = "";
    if (portfolioExists(name)) {
      message = "Portfolio already exists: " + name;
      return new Payload(null, message);
    }
    if (name.isEmpty()) {
      message = "Portfolio name cannot be empty";
      return new Payload(null, message);
    }
    Portfolio portfolio = new Portfolio(name);
    this.addPortfolio(portfolio);
    return new Payload(portfolio);
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
  public String addStockToPortfolio(String portfolioName, String symbol, int quantity,
      LocalDate date) {
    String message = "";
    Portfolio portfolio = getPortfolioByName(portfolioName).orElseThrow(
        () -> new IllegalArgumentException("Portfolio not found: " + portfolioName));

    // check if stock already exists in portfolio
    if (portfolio.getStocks().stream().anyMatch(
        s -> s.getSymbol().equalsIgnoreCase(symbol) && s.getPurchaseDate().equals(date))) {
      message = "Stock already exists in portfolio: " + symbol + " on " + date;
    }

    // check if quantity is positive
    else if (quantity <= 0) {
      message = "Quantity must be positive: " + quantity;
    }

    // check if quantity is whole number

    // check if date is in the future
    else if (date.isAfter(LocalDate.now())) {
      message = "Date cannot be in the future: " + date;
    } else {
      Payload price = stockService.fetchPriceOnDate(symbol, date);
      if (price.isError()) {
        message = (String) price.getMessage();
        return message;
      }
      Stock stock = new Stock(symbol, quantity, (BigDecimal) price.getData()
          , date);
      portfolio.addStock(stock);
    }
    return message;
  }

  /**
   * Fetches a portfolio by its name.
   *
   * @param name The name of the portfolio to fetch.
   * @return An Optional containing the portfolio if found, or an empty Optional otherwise.
   */
  public Optional<Portfolio> getPortfolioByName(String name) {
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
  public Payload calculatePortfolioValue(String portfolioName, LocalDate onDate) {
    String message = "";
    // check if date is in the future
    if (onDate.isAfter(LocalDate.now())) {
      message = "Date cannot be in the future: " + onDate;
      return new Payload(null, message);
    }

    // check if portfolio exists
    if (!portfolioExists(portfolioName)) {
      message = "Portfolio not found: " + portfolioName;
      return new Payload(null, message);
    }

    return new Payload(getPortfolioByName(portfolioName).map(portfolio -> {
      BigDecimal totalValue = BigDecimal.ZERO;
      for (Stock stock : portfolio.getStocks()) {
        if (stock.getPurchaseDate().isBefore(onDate) || stock.getPurchaseDate().isEqual(onDate)) {
          Payload priceOnDate = stockService.fetchPreviousClosePrice(stock.getSymbol(), onDate);
          BigDecimal value = ((BigDecimal) priceOnDate.getData()).multiply(
              new BigDecimal(stock.getQuantity()));
          totalValue = totalValue.add(value);
        }
      }
      return totalValue;
    }), "");
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
    return portfolios.stream().map(Portfolio::getName).collect(Collectors.toList());
  }

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path to which the portfolios will be saved.
   * @throws IOException If an error occurs while writing to the file.
   */
  public String savePortfoliosToCSV(String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date\n");
      for (Portfolio portfolio : portfolios) {
        for (Stock stock : portfolio.getStocks()) {
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

  /***
   * Calculate profit/loss for a portfolio on a given date.
   * @param portfolioName name of the portfolio.
   * @param onDate date on which profit is to be calculated.
   * @return profit/loss for the portfolio on the given date.
   */
  public BigDecimal calculateProfitLoss(String portfolioName, LocalDate onDate) {
    // check if date is in the future
    if (onDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future: " + onDate);
    }
    // calculate profit for each stock and then sum them up
    return getPortfolioByName(portfolioName).map(portfolio -> {
      BigDecimal totalProfitAndLoss = BigDecimal.ZERO;
      for (Stock stock : portfolio.getStocks()) {
        if (stock.getPurchaseDate().isBefore(onDate) || stock.getPurchaseDate().isEqual(onDate)) {
          Payload priceOnDate = stockService.fetchPreviousClosePrice(stock.getSymbol(), onDate);
          BigDecimal purchasePrice = stock.getPurchasePrice();
          BigDecimal profitAndLoss = ((BigDecimal) priceOnDate.getData()).subtract(purchasePrice)
              .multiply(new BigDecimal(stock.getQuantity()));
          totalProfitAndLoss = totalProfitAndLoss.add(profitAndLoss);
        }
      }
      return totalProfitAndLoss;
    }).orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));
  }
}
