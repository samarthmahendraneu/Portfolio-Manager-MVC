package model.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import controller.Payload;
import controller.fileio.CsvFileIO;
import controller.fileio.FileIO;
import model.Portfolio;
import model.PortfolioInterface;
import model.Tradable;
import model.utilities.DateUtils;


/**
 * service class for managing portfolios.
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

  /**
   * Validates the name of a portfolio.
   *
   * @param name The name of the portfolio.
   */
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
   * @return The updated portfolio.
   * @throws IllegalArgumentException If stock already exists, quantity is not positive, or date is
   *                                  in the future.
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

  /**
   * Validates the input for adding a stock to a portfolio.
   *
   * @param portfolioName The name of the portfolio.
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   * @param date          The date of the stock.
   */
  private void validateStockInput(String portfolioName, String symbol, int quantity,
      LocalDate date) {
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));

    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive: " + quantity);
    } else if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future: " + date);
    }
    // date cannot be in weekend
    if (date.getDayOfWeek().getValue() > 5) {
      throw new IllegalArgumentException("Date cannot be on a weekend: " + date);
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
  public Boolean sellStockFromPortfolio(String portfolioName, String stockSymbol, int quantity,
      LocalDate date) {
    getPortfolioByName(portfolioName).ifPresent(portfolio -> {
      portfolio.sellStock(stockSymbol, quantity, date,
          (BigDecimal) stockService.fetchPriceOnDate(stockSymbol, date).getData());
    });
    return true;
  }

  /**
   * Examine the details of a portfolio on a particular date.
   * @param portfolioName The name of the portfolio to examine.
   * @param date The date to examine the portfolio on.
   * @return A list of Tradable objects representing the stocks in the portfolio.
   */
  public List<Tradable> examinePortfolioDetails(String portfolioName, LocalDate date) {
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseThrow(() -> new IllegalArgumentException("Portfolio not found: " + portfolioName));
    return portfolio.getPortfolio(date);

  }

  /**
   * Value based investment strategy for a portfolio.
   * portfolioService.valueBasedInvestment(name, investmentAmount, startDate, stockWeights)
   * @param portfolioName   The name of the portfolio to invest in.
   * @param investmentAmount The amount to invest.
   * @param startDate       The start date of the investment period.
   * @param stockWeights    A map of stock symbols to their respective weights.
   */
  public void valueBasedInvestment(String portfolioName, BigDecimal investmentAmount,
      LocalDate startDate, Map<String, Float> stockWeights) {
    // check if portfolio exists if not create a new one
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseGet(() -> createNewPortfolio(portfolioName));
    portfolio.investUsingWeights(investmentAmount.floatValue(), startDate, stockService,
        stockWeights);
    return;
  }

  /**
   * dollar cost averaging for a portfolio.
   * @param portfolioName The name of the portfolio to invest in.
   * @param amount The amount to invest.
   * @param startDate The start date of the investment period.
   * @param endDate The end date of the investment period.
   * @param frequency The frequency of investment.
   */
  public void dollarCostAveraging(String portfolioName, BigDecimal amount, LocalDate startDate,
      LocalDate endDate, int frequency, Map<String, Float> stockWeights) {
    // check if portfolio exists if not create a new one
    PortfolioInterface portfolio = getPortfolioByName(portfolioName)
        .orElseGet(() -> createNewPortfolio(portfolioName));
    portfolio.dollarCostAveraging(amount, startDate, endDate, stockService, frequency,
        stockWeights);
    return;
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

  /**
   * Calculates the total investment in a portfolio on a given date.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the investment is to be calculated.
   * @return The total investment in the portfolio on the given date.
   * @throws IllegalArgumentException If date is in the future or portfolio not found.
   */
  public Optional<BigDecimal> calculatePortfolioInvestment(String portfolioName, LocalDate onDate) {
    validatePortfolioValueInput(portfolioName, onDate);
    return getPortfolioByName(portfolioName).map(
        p -> p.calculateInvestment(onDate));
  }

  /**
   * Validates the input for calculating the value of a portfolio.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the value is to be calculated.
   */
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

  /**
   * Retrieves the closing prices of a specified stock symbol over a given period, adjusted to
   * monthly values. This method delegates to fetchMonthlyClosingPricesForPeriod from the
   * StockService to obtain data, which is then returned in a sorted map for easy consumption.
   *
   * @param symbol    The stock symbol for which closing prices are to be fetched.
   * @param startDate The start date of the period for which data is required. This is inclusive.
   * @param endDate   The end date of the period for which data is required. This is inclusive.
   * @return A SortedMap where keys are LocalDate objects representing the end of each month.
   */

  @Override
  public SortedMap<LocalDate, BigDecimal> fetchValuesForPeriod(String symbol, LocalDate startDate,
      LocalDate endDate) {
    YearMonth startMonth = YearMonth.from(startDate);
    YearMonth endMonth = YearMonth.from(endDate);
    SortedMap<LocalDate, BigDecimal> monthlyValues = new TreeMap<>();

    // Directly use fetchMonthlyClosingPricesForPeriod from StockService.
    SortedMap<LocalDate, BigDecimal> fetchedData = stockService.fetchMonthlyClosingPricesForPeriod(
        symbol, startDate, endDate);

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
   */
  public void savePortfoliosToCSV(String filePath, String type) {
    FileIO fileio = new CsvFileIO();
    try {
      fileio.writeFile(portfolios, filePath, type);
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
  public String loadPortfoliosFromCSV(String filePath, String type) throws IOException {
    FileIO fileio = new CsvFileIO();
    List<PortfolioInterface> loadedPortfolios = fileio.readFile(filePath, type);
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

  /**
   * Fetches the total value of a portfolio over a specified period. The values are aggregated based
   * on a resolution determined by the start and end dates to ensure the chart has at least 5 lines
   * but no more than 30 lines.
   *
   * @param portfolioName The name of the portfolio.
   * @param startDate     The start date of the period.
   * @param endDate       The end date of the period.
   * @return A sorted map with dates as keys and total portfolio values as values.
   */

  public SortedMap<LocalDate, BigDecimal> fetchPortfolioValuesForPeriod(String portfolioName,
      LocalDate startDate, LocalDate endDate) {
    String resolution = DateUtils.determineResolution(startDate, endDate);
    SortedMap<LocalDate, BigDecimal> portfolioValues = new TreeMap<>();
    PortfolioInterface portfolio = getPortfolioByName(portfolioName).orElse(null);
    LocalDate earliestStockDate = stockService.findEarliestStockDate(portfolio);

    // Adjust the start date if it's before the earliest stock addition date
    LocalDate currentDate = startDate.isBefore(earliestStockDate) ? earliestStockDate : startDate;
    while (!currentDate.isAfter(endDate)) {
      LocalDate targetDate = DateUtils.getTargetDateBasedOnResolution(currentDate, resolution,
          endDate);

      // Use calculatePortfolioValue for the target date
      BigDecimal portfolioValue = calculatePortfolioValue(portfolioName, targetDate)
          .orElse(null);
      portfolioValues.put(targetDate, portfolioValue);

      // Increment the date based on the resolution
      currentDate = incrementDateByResolution(currentDate, resolution);
    }

    return portfolioValues;
  }

  /**
   * Increments a date by a specified resolution.
   *
   * @param date       The date to increment.
   * @param resolution The resolution by which to increment the date.
   * @return The incremented date.
   */
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

  /**
   * Plots a performance chart for a given stock or portfolio over a specified time frame. The chart
   * displays the value changes over time, represented by asterisks, where the scale and number of
   * asterisks are dynamically calculated to fit the data range.
   *
   * @param identifier The stock symbol or portfolio name to plot.
   * @param startDate  The start date of the period to plot.
   * @param endDate    The end date of the period to plot.
   */

  public StringBuilder plotPerformanceChart(String identifier, LocalDate startDate,
      LocalDate endDate) {
    StringBuilder chartBuilder = new StringBuilder();
    try {
      // Determine whether the identifier is for a stock or a portfolio
      Map<LocalDate, BigDecimal> values = portfolioExists(identifier)
          ? fetchPortfolioValuesForPeriod(identifier, startDate, endDate) :
          fetchValuesForPeriod(identifier, startDate, endDate);

      if (values.isEmpty()) {
        System.out.println(
            "No data available for " + identifier + " from " + startDate + " to " + endDate);
        return null;
      }

      BigDecimal minValue = values.values().stream().min(BigDecimal::compareTo)
          .orElse(BigDecimal.ZERO);
      BigDecimal maxValue = values.values().stream().max(BigDecimal::compareTo)
          .orElse(BigDecimal.ZERO);

      AtomicReference<String> scaleType = new AtomicReference<>("absolute");
      BigDecimal scale = calculateScale(minValue, maxValue, scaleType);

      // Construct the performance chart

      // Construct the performance chart
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuu");
      values.forEach((date, value) -> {
        int asterisksCount = value.divide(scale, 2, RoundingMode.HALF_UP)
            .intValue(); // Added rounding
        chartBuilder.append(formatter.format(date))
            .append(": ")
            .append("*".repeat(Math.max(0, asterisksCount)))
            .append("\n");
      });

      // Append scale info
      chartBuilder.append("\nScale: * = ")
          .append(scale)
          .append(" dollars (")
          .append(scaleType.get())
          .append(")");
      return chartBuilder;
    } catch (Exception e) {
      System.err.println("An error occurred while plotting performance chart: " + e.getMessage());
    }
    return chartBuilder;
  }


  /**
   * Plots a performance chart for a given stock or portfolio over a specified time frame. The chart
   * @param identifier The stock symbol or portfolio name to plot.
   * @param startDate The start date of the period to plot.
   * @param endDate The end date of the period to plot.
   * @return A map of dates to total values for the given identifier.
   */
  public Map<LocalDate, BigDecimal> plotPerformanceChartGUI(String identifier, LocalDate startDate,
      LocalDate endDate) {
    Map<LocalDate, BigDecimal> values = portfolioExists(identifier)
        ? fetchPortfolioValuesForPeriod(identifier, startDate, endDate) :
        fetchValuesForPeriod(identifier, startDate, endDate);

    if (values.isEmpty()) {
      System.out.println(
          "No data available for " + identifier + " from " + startDate + " to " + endDate);
      return null;
    }

    return values;
  }

  /**
   * Calculates the scale for the performance chart based on the range of values.
   *
   * @param minValue  The minimum value in the data set.
   * @param maxValue  The maximum value in the data set.
   * @param scaleType The type of scale used for the chart.
   * @return The scale value to be used for the performance chart.
   */
  private BigDecimal calculateScale(
      BigDecimal minValue, BigDecimal maxValue, AtomicReference<String> scaleType) {
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
      // For moderate ranges, adjust the scale to have meaningful but not
      // overwhelming representation
      scale = range.divide(BigDecimal.valueOf(maxAsterisks / 2), RoundingMode.UP);
      scaleType.set("absolute");
    } else {
      // For smaller ranges, use a finer scale to highlight differences
      scale = range.divide(BigDecimal.valueOf(maxAsterisks), RoundingMode.UP);
      scaleType.set("relative");
    }

    // Adjust the scale to ensure it's practical and avoids producing overly long bars
    while (maxValue.divide(scale, RoundingMode.HALF_UP).intValue() > maxAsterisks) {
      scale = scale.multiply(
          BigDecimal.valueOf(2)); // Double the scale to reduce the number of asterisks
    }

    return scale;
  }

}
