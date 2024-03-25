package Model.Service;
import Controller.Payload;
import Controller.fileio.CsvFileIO;
import Controller.fileio.FileIO;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import Model.Portfolio;
import Model.PortfolioInterface;
import Model.Tradable;
import Model.utilities.DateUtils;
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
            (symbol, startDate, endDate);

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

  public SortedMap<LocalDate, BigDecimal> fetchPortfolioValuesForPeriod(String portfolioName, LocalDate startDate, LocalDate endDate) {
    String resolution = DateUtils.determineResolution(startDate, endDate);
    SortedMap<LocalDate, BigDecimal> portfolioValues = new TreeMap<>();
    PortfolioInterface portfolio = getPortfolioByName(portfolioName).orElse(null);
    LocalDate earliestStockDate = stockService.findEarliestStockDate(portfolio);

    // Adjust the start date if it's before the earliest stock addition date
    LocalDate currentDate = startDate.isBefore(earliestStockDate) ? earliestStockDate : startDate;
    while (!currentDate.isAfter(endDate)) {
      LocalDate targetDate = DateUtils.getTargetDateBasedOnResolution(currentDate, resolution, endDate);

      // Use calculatePortfolioValue for the target date
      BigDecimal portfolioValue = calculatePortfolioValue(portfolioName, targetDate).orElse(null);;
      portfolioValues.put(targetDate, portfolioValue);

      // Increment the date based on the resolution
      currentDate = incrementDateByResolution(currentDate, resolution);
    }

    return portfolioValues;
  }



  private LocalDate incrementDateByResolution(LocalDate date, String resolution) {
    switch (resolution) {
      case "daily":
        return date.plusDays(1);
      case "monthly":
        return date.plusMonths(1);
      case "every 3 months":
        return date.plusMonths(3);
      case "yearly":
        return date.plusYears(1);
      default:
        throw new IllegalArgumentException("Unknown resolution: " + resolution);
    }
  }


  public void plotPerformanceChart(String identifier, LocalDate startDate, LocalDate endDate) {
    try {
      // Determine whether the identifier is for a stock or a portfolio
      Map<LocalDate, BigDecimal> values = portfolioExists(identifier) ?
              fetchPortfolioValuesForPeriod(identifier, startDate, endDate) :
              fetchValuesForPeriod(identifier, startDate, endDate);

      if (values.isEmpty()) {
        System.out.println("No data available for " + identifier + " from " + startDate + " to " + endDate);
        return;
      }

      BigDecimal minValue = values.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
      BigDecimal maxValue = values.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

      AtomicReference<String> scaleType = new AtomicReference<>("absolute");
      BigDecimal scale = calculateScale(minValue, maxValue, scaleType);

      System.out.println("Performance of " + (portfolioExists(identifier) ? "portfolio" : "stock") +
              " " + identifier + " from " + startDate + " to " + endDate + "\n");

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuu");
      values.forEach((date, value) -> {
        int asterisksCount = value.divide(scale, RoundingMode.HALF_UP).intValue();
        System.out.println(formatter.format(date) + ": " + "*".repeat(Math.max(0, asterisksCount)));
      });

      System.out.println("\nScale: * = " + scale + " dollars (" + scaleType.get() + ")");
    } catch (Exception e) {
      System.err.println("An error occurred while plotting performance chart: " + e.getMessage());
    }
  }


  private BigDecimal calculateScale(BigDecimal minValue, BigDecimal maxValue, AtomicReference<String> scaleType) {
    // Calculate the range of values
    BigDecimal range = maxValue.subtract(minValue);
    BigDecimal scale;

    // Determine the maximum number of asterisks we want to display
    int maxAsterisks = 50;

    if (range.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ONE; // Avoid division by zero
    }

    // Dynamic scaling based on the range magnitude
    if (range.compareTo(new BigDecimal("500")) > 0) {
      // For large ranges, make each asterisk represent a larger value (e.g., $1000 or more)
      scale = new BigDecimal("1000");
      scaleType.set("absolute");
    } else if (range.compareTo(new BigDecimal("50")) > 0) {
      // For moderate ranges, adjust the scale to have meaningful but not overwhelming representation
      scale = range.divide(BigDecimal.valueOf(maxAsterisks / 2), RoundingMode.UP);
      scaleType.set("absolute");
    } else {
      // For smaller ranges, use a finer scale to highlight differences
      scale = range.divide(BigDecimal.valueOf(maxAsterisks), RoundingMode.UP);
      scaleType.set("relative");
    }

    // Adjust the scale to ensure it's practical and avoids producing overly long bars
    while (maxValue.divide(scale, RoundingMode.HALF_UP).intValue() > maxAsterisks) {
      scale = scale.multiply(BigDecimal.valueOf(2)); // Double the scale to reduce the number of asterisks
    }

    return scale;
  }

}
