package Model.Service;
import Controller.Payload;
import Controller.fileio.CsvFileIO;
import Controller.fileio.FileIO;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import Model.Portfolio;
import Model.PortfolioInterface;
import java.time.format.DateTimeFormatter;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;


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
   * @return The newly created portfolio.
   * @throws IllegalArgumentException If the portfolio name already exists or is empty.
   */
  public PortfolioInterface createNewPortfolio(String name) {
    validatePortfolioName(name);
    PortfolioInterface portfolio = new Portfolio(name);
    addPortfolio(portfolio);
    return portfolio;
  }

  private void validatePortfolioName(String name) {
    if (portfolioExists(name)) {
      throw new IllegalArgumentException("Portfolio already exists: " + name);
    }
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Portfolio name cannot be empty");
    }
  }

  /**
   * Adds a portfolio to the list of portfolios.
   *
   * @param portfolio The portfolio to add.
   * @throws IllegalArgumentException If the portfolio name already exists.
   */
  public void addPortfolio(PortfolioInterface portfolio) {
    Objects.requireNonNull(portfolio, "Portfolio cannot be null");
    if (portfolioExists(portfolio.getName())) {
      throw new IllegalArgumentException("A portfolio with the name '" + portfolio.getName() + "' already exists.");
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
   * @return The updated portfolio.
   * @throws IllegalArgumentException If stock already exists, quantity is not positive, or date is in the future.
   */
  public PortfolioInterface addStockToPortfolio(String portfolioName, String symbol, int quantity,
      LocalDate date) {
    validateStockInput(portfolioName, symbol, quantity, date);
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));
    Payload price = stockService.fetchPriceOnDate(symbol, date);
    if (price.isError()) {
      throw new IllegalArgumentException(price.getMessage());
    }
    portfolio.addStock(symbol, quantity, (BigDecimal) price.getData(), date);
    return portfolio;
  }

  private void validateStockInput(String portfolioName, String symbol, int quantity, LocalDate date) {
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));

    if (portfolio.getStocks().stream().anyMatch(
        s -> s.getSymbol().equalsIgnoreCase(symbol) && s.getPurchaseDate().equals(date))) {
      throw new IllegalArgumentException("Stock already exists in portfolio: " + symbol + " on " + date);
    } else if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive: " + quantity);
    } else if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future: " + date);
    }
  }

  /**
   * Sell a stock from the portfolio.
   *
   * @param portfolioName The name of the portfolio from which to sell the stock.
   * @param stockSymbol   The symbol of the stock to sell.
   * @param quantity      The quantity of the stock to sell.
   * @param date          The date of the sale.
   */
  public void sellStockFromPortfolio(String portfolioName, String stockSymbol, int quantity,
      LocalDate date) {
    getPortfolioByName(portfolioName).ifPresent(portfolio -> {
      portfolio.sellStock(stockSymbol, quantity, date,
          (BigDecimal) stockService.fetchPriceOnDate(stockSymbol, date).getData());
    });
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
   * @throws IllegalArgumentException If date is in the future or portfolio not found.
   */
  public Optional<BigDecimal> calculatePortfolioValue(String portfolioName, LocalDate onDate) {
    validatePortfolioValueInput(portfolioName, onDate);
    return getPortfolioByName(portfolioName).map(p -> p.calculateValue(this.stockService, onDate));
  }

  private void validatePortfolioValueInput(String portfolioName, LocalDate onDate) {
    if (onDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future: " + onDate);
    }
    if (!portfolioExists(portfolioName)) {
      throw new IllegalArgumentException("Portfolio not found: " + portfolioName);
    }
  }

  /**
   * Returns number of portfolios.
   *
   * @return number of portfolios.
   */
  public int getNumberOfPortfolios() {
    return portfolios.size();
  }

  @Override
  public SortedMap<LocalDate, BigDecimal> fetchValuesForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
    YearMonth startMonth = YearMonth.from(startDate);
    YearMonth endMonth = YearMonth.from(endDate);
    SortedMap<LocalDate, BigDecimal> monthlyValues = new TreeMap<>();

    // Directly use fetchMonthlyClosingPricesForPeriod from StockService.
    SortedMap<LocalDate, BigDecimal> fetchedData = stockService.fetchMonthlyClosingPricesForPeriod
        (symbol, startMonth, endMonth);

    // Assuming fetchedData is correctly populated, it can be directly used for plotting.
    monthlyValues.putAll(fetchedData);

    return monthlyValues;
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
   * @return Empty string if successful, error message if failed.
   */
  public void savePortfoliosToCSV(String filePath) {
    FileIO fileio = new CsvFileIO();
    try {
      fileio.writeFile(portfolios, filePath);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error saving portfolios to file: " + e.getMessage());
    }
  }

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @return Empty string if successful, error message if failed.
   * @throws IOException If an error occurs while reading from the file.
   */
  public String loadPortfoliosFromCSV(String filePath) throws IOException {
    FileIO fileio = new CsvFileIO();
    List<PortfolioInterface> loadedPortfolios = fileio.readFile(filePath);
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

  public void plotPerformanceChart(String identifier, LocalDate startDate, LocalDate endDate) {
    Map<LocalDate, BigDecimal> values = fetchValuesForPeriod(identifier, startDate, endDate);

    BigDecimal minValue = values.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    BigDecimal maxValue = values.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

    // Assume a fixed scale for simplicity; could be dynamic based on minValue and maxValue
    BigDecimal scale = calculateScale(minValue, maxValue);
    int maxAsterisks = 50; // Maximum asterisks per line

    System.out.println("Performance of portfolio " + identifier + " from " + startDate + " to " + endDate + "\n");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
    values.forEach((date, value) -> {
      int asterisks = value.divide(scale, RoundingMode.HALF_UP).intValue();
      System.out.println(formatter.format(date) + ": " + "*".repeat(Math.max(0, asterisks)));
    });

    System.out.println("\nScale: * = " + scale + " dollars");
  }

  private BigDecimal calculateScale(BigDecimal minValue, BigDecimal maxValue) {
    // Calculate the range of values
    BigDecimal range = maxValue.subtract(minValue);

    // Determine the maximum number of asterisks we want to display
    int maxAsterisks = 50;

    // Calculate the scale: divide the range by the maximum number of asterisks to find out
    // what value each asterisk represents. We use the ceiling of the division to ensure that
    // we do not exceed 50 asterisks even after rounding.
    BigDecimal scale = range.divide(BigDecimal.valueOf(maxAsterisks), RoundingMode.CEILING);

    // Ensure the scale is at least 1 to avoid a scale of 0 when minValue equals maxValue
    if (scale.compareTo(BigDecimal.ZERO) == 0) {
      scale = BigDecimal.ONE;
    }

    return scale;
  }
}
