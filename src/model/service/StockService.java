package model.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import controller.Payload;
import model.PortfolioInterface;
import model.Tradable;
import model.utilities.StockDataCache;
import model.utilities.StockInfo;

import static model.utilities.DateUtils.determineResolution;
import static model.utilities.DateUtils.getTargetDateBasedOnResolution;

/**
 * service class for fetching stock data and calculating stock prices.
 */
public class StockService implements StockServiceInterface {

  private final StockDataCache cache = new StockDataCache();

  private final String apiKey;

  /**
   * Constructor for the StockService class.
   *
   * @param apiKey The API key to use for fetching stock data.
   */
  public StockService(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Fetches the price of a stock with the given symbol on the given date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which the price is to be fetched.
   * @return The price of the stock on the given date.
   */
  public Payload fetchPriceOnDate(String symbol, LocalDate date) {
    String message;

    if (!cache.hasStockData(symbol, date)) {
      message = fetchAndCacheStockData(symbol);
      if (message != null) {
        return new Payload(null, message);
      }
    }

    StockInfo info = cache.getStockData(symbol, date);
    if (info != null) {
      return new Payload(info.getClose(), "");
    }

    return new Payload(BigDecimal.ZERO, "");
  }

  /**
   * Fetches the closing price of the stock with the given symbol on the previous trading day.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which to fetch the previous close price.
   * @return The closing price of the stock on the previous trading day.
   */
  public Payload fetchLastClosePrice(String symbol, LocalDate date) {
    int traverseCount = 0;
    String message;

    do {
      if (!cache.hasStockData(symbol, date)) {
        message = fetchAndCacheStockData(symbol);
        if (message != null) {
          return new Payload(null, message);
        }
      }

      StockInfo info = cache.getStockData(symbol, date);
      if (info != null) {
        return new Payload(info.getClose(), "");
      }

      date = date.minusDays(1);
      traverseCount++;
    }
    while (traverseCount < 4);

    return new Payload(BigDecimal.ZERO, "");
  }

  /**
   * Fetches the complete stock data for the given symbol on the given date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which to fetch the stock data.
   * @return The stock data for the given symbol on the given date.
   */
  private StockInfo fetchCompleteStockDataOnDate(String symbol, LocalDate date) {
    StockInfo info;
    String message;

    if (!cache.hasStockData(symbol, date)) {
      message = fetchAndCacheStockData(symbol);
      if (message != null) {
        throw new IllegalArgumentException(message);
      }
    }
    info = cache.getStockData(symbol, date);
    return info;
  }


  /**
   * Fetches and caches stock data for the given symbol.
   *
   * @param symbol The symbol of the stock to fetch data for.
   * @return A string containing an error message if the symbol is invalid, or null otherwise.
   */
  private String fetchAndCacheStockData(String symbol) {
    String csvData = makeApiRequest(symbol);
    if (csvData.contains("Invalid stock symbol")) {
      return "Invalid stock symbol";
    }
    parseAndCacheCsvData(csvData, symbol);
    return null;
  }

  /**
   * Parses the CSV data and caches it.
   *
   * @param csvData The CSV data to parse.
   * @param symbol  The symbol of the stock.
   */
  private void parseAndCacheCsvData(String csvData, String symbol) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(csvData.getBytes())))) {
      reader.lines()
          .skip(1) // Skip header
          .map(line -> line.split(","))
          .forEach(values -> {
            LocalDate date = LocalDate.parse(values[0]);
            BigDecimal open = new BigDecimal(values[1]);
            BigDecimal high = new BigDecimal(values[2]);
            BigDecimal low = new BigDecimal(values[3]);
            BigDecimal close = new BigDecimal(values[4]);
            long volume = Long.parseLong(values[5]);
            StockInfo stockInfo = new StockInfo(date, open, high, low, close, volume);
            cache.addStockData(symbol, date, stockInfo);
          });
    } catch (Exception e) {
      System.out.println("An error occurred while parsing and caching CSV data: " + e.getMessage());
    }
  }

  /**
   * Makes an API request to fetch stock data for the given symbol.
   *
   * @param symbol The symbol of the stock to fetch data for.
   * @return A string containing the response from the API.
   */

  private String makeApiRequest(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
          "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol"
              + "=%s&datatype=csv&apikey=%s&outputsize=full",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line).append("\n");
        }
      }

      if (response.toString().contains("Error Message")) {
        System.out.println("Invalid stock symbol: " + symbol);
        return "Invalid stock symbol: " + symbol;
      }

    } catch (Exception e) {
      System.out.println("An error occurred while fetching stock data: " + e.getMessage());
    }
    return response.toString();
  }


  /**
   * Finds the crossover days for a given stock symbol within a specified date range.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  public List<LocalDate> findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate) {
    // both start and end dates cant be in the future
    if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future");
    }
    // start date should be before end date
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date should be before end date");
    }
    // start != end
    if (startDate.isEqual(endDate)) {
      throw new IllegalArgumentException("Start date should not be equal to end date");
    }
    List<LocalDate> crossoverDays = new ArrayList<>();
    String csvData = makeApiRequest(symbol);
    if (csvData.contains("Invalid stock symbol")) {
      throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
    }
    // loop through dates in the range
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      StockInfo info = this.fetchCompleteStockDataOnDate(symbol, date);
      if (info != null && info.getClose().compareTo(info.getOpen()) > 0) {
        crossoverDays.add(date);
      }
    }
    return crossoverDays;
  }

  /**
   * Finds the moving crossover days for a given stock symbol within a specified date range.
   *
   * @param symbol            The symbol of the stock to analyze.
   * @param startDate         The start date of the date range.
   * @param endDate           The end date of the date range.
   * @param shortMovingPeriod The number of days to consider for the short moving average.
   * @param longMovingPeriod  The number of days to consider for the long moving average.
   * @return A map containing lists of golden crosses, death crosses, and moving crossover days.
   */
  public Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate,
      LocalDate endDate, int shortMovingPeriod, int longMovingPeriod) {
    // start date should be before end date
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date should be before end date");
    }
    // start != end
    if (startDate.isEqual(endDate)) {
      throw new IllegalArgumentException("Start date should not be equal to end date");
    }
    // both start and end dates cant be in the future
    if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future");
    }
    // short moving period should be less than long moving period
    if (shortMovingPeriod >= longMovingPeriod) {
      throw new IllegalArgumentException(
          "Short moving period should be less than long moving period");
    }
    // short moving period should be greater than 0
    if (shortMovingPeriod <= 0) {
      throw new IllegalArgumentException("Short moving period should be greater than 0");
    }

    // long moving period should be greater than 0
    if (longMovingPeriod <= 0) {
      throw new IllegalArgumentException("Long moving period should be greater than 0");
    }

    List<LocalDate> goldenCrosses = new ArrayList<>();
    List<LocalDate> deathCrosses = new ArrayList<>();
    List<LocalDate> movingCrossoverDays = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      LocalDate endShortWindow = currentDate.plusDays(shortMovingPeriod - 1);
      LocalDate endLongWindow = currentDate.plusDays(longMovingPeriod - 1);

      // Retrieve historical stock data for the current window
      List<BigDecimal> closingPrices = getHistoricalData(symbol, currentDate, endShortWindow);
      List<BigDecimal> longClosingPrices = getHistoricalData(symbol, currentDate, endLongWindow);

      // Calculate the moving averages
      double shortMovingAvg = calculateMovingAverage(closingPrices, shortMovingPeriod);
      double longMovingAvg = calculateMovingAverage(longClosingPrices, longMovingPeriod);
      double prevShortMovingAvg = getPreviousShortMovingAvg(closingPrices, shortMovingPeriod);

      // Check for crossover
      boolean crossedAbove =
          shortMovingAvg > longMovingAvg && shortMovingAvg < prevShortMovingAvg;
      boolean crossedBelow =
          shortMovingAvg < longMovingAvg && shortMovingAvg > prevShortMovingAvg;

      if (crossedAbove) {
        goldenCrosses.add(currentDate);
        movingCrossoverDays.add(currentDate);
      } else if (crossedBelow) {
        deathCrosses.add(currentDate);
        movingCrossoverDays.add(currentDate);
      }

      currentDate = currentDate.plusDays(1);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("goldenCrosses", goldenCrosses);
    result.put("deathCrosses", deathCrosses);
    result.put("movingCrossoverDays", movingCrossoverDays);

    return result;
  }

  /**
   * Fetches historical data for a stock symbol within a given date range.
   *
   * @param symbol    The stock symbol.
   * @param startDate The start date of the period.
   * @param endDate   The end date of the period.
   * @return A list of closing prices for the stock within the specified date range.
   */
  private List<BigDecimal> getHistoricalData(String symbol, LocalDate startDate,
      LocalDate endDate) {
    List<BigDecimal> closingPrices = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      StockInfo info = fetchCompleteStockDataOnDate(symbol, currentDate);
      if (info != null) {
        closingPrices.add(info.getClose());
      }
      currentDate = currentDate.plusDays(1);
    }

    return closingPrices;
  }

  /**
   * Calculates the moving average for a given period.
   *
   * @param prices The list of closing prices.
   * @param period The period for which to calculate the moving average.
   * @return The moving average.
   */
  private double calculateMovingAverage(List<BigDecimal> prices, int period) {
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < period && i < prices.size(); i++) {
      sum = sum.add(prices.get(i));
    }
    return (Double) sum.divide(BigDecimal.valueOf(period)).doubleValue();
  }

  /**
   * Calculates the previous short moving average for a given period.
   *
   * @param prices The list of closing prices.
   * @param period The period for which to calculate the moving average.
   * @return The previous short moving average.
   */
  private double getPreviousShortMovingAvg(List<BigDecimal> prices, int period) {
    if (prices.size() < period + 1) {
      return 0; // Not enough data to calculate previous moving average
    }
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 1; i <= period; i++) {
      sum = sum.add(prices.get(i));
    }
    return (Double) sum.divide(BigDecimal.valueOf(period)).doubleValue();
  }


  /**
   * Checks if the stock data for the given symbol is available in the cache.
   *
   * @param symbol    The symbol of the stock.
   * @param startDate The start date of the period.
   * @param endDate   The end date of the period.
   * @return True if the data is available in the cache, false otherwise.
   */
  private boolean isDataAvailableInCache(String symbol, LocalDate startDate, LocalDate endDate) {
    // Check if the cache contains data for all days in the requested range.
    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      if (!cache.hasStockData(symbol, currentDate)) {
        return false;
      }
      currentDate = currentDate.plusDays(1);
    }
    return true;
  }

  /**
   * Updates the cache with the latest stock data from the API.
   *
   * @param symbol The symbol of the stock to update.
   */
  private void updateCacheWithApiData(String symbol) {
    String apiResponse = makeApiRequest(symbol); // Your method to fetch data
    parseAndCacheCsvData(apiResponse, symbol);
  }


  /**
   * Fetches the monthly closing prices of a stock for a given period.
   *
   * @param symbol    The symbol of the stock.
   * @param startDate The start Month of the period.
   * @param endDate   The end Month of the period.
   * @return A sorted map where keys are dates (end of the month) and values are the closing prices
   */
  public SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod(
      String symbol, LocalDate startDate, LocalDate endDate) {
    // Determine resolution based on the period
    String resolution = determineResolution(startDate, endDate);

    SortedMap<LocalDate, BigDecimal> values = new TreeMap<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      LocalDate targetDate = getTargetDateBasedOnResolution(currentDate, resolution, endDate);

      if (targetDate != null) {
        boolean isDataFullyAvailable = isDataAvailableInCache(symbol, startDate, endDate);

        if (!isDataFullyAvailable) {
          // Step 2: Fetch from API and update cache
          updateCacheWithApiData(symbol);
        }

        StockInfo stockInfo = cache.getStockData(symbol, targetDate);
        if (stockInfo != null) {
          values.put(targetDate, stockInfo.getClose());
        }
      }

      // Move to next period based on resolution
      currentDate = incrementDateByResolution(currentDate, resolution);
    }

    return values;
  }

  /**
   * Increments the date based on the resolution.
   *
   * @param date       The date to increment.
   * @param resolution The resolution to use for incrementing the date.
   * @return The incremented date.
   */
  private LocalDate incrementDateByResolution(LocalDate date, String resolution) {
    switch (resolution) {
      case "daily":
        return date.plusDays(1);
      case "every 10 days":
        return date.plusDays(10);
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
   * Finds the earliest stock purchase date in a given portfolio.
   *
   * @param portfolio The portfolio from which to find the earliest stock purchase date.
   * @return The earliest date on which a stock was purchased within the given portfolio.
   */
  public LocalDate findEarliestStockDate(PortfolioInterface portfolio) {
    LocalDate earliestDate = null;
    for (Tradable stock : portfolio.getStocks()) {
      LocalDate stockDate = stock.getActivityLog().keySet().stream().min(LocalDate::compareTo)
          .orElse(null);
      if (earliestDate == null || (stockDate != null && stockDate.isBefore(earliestDate))) {
        earliestDate = stockDate;
      }
    }
    if (earliestDate == null) {
      throw new IllegalStateException("Portfolio does not contain any stocks.");
    }
    return earliestDate;
  }


  /**
   * Saves the stock data cache to a file.
   *
   * @param filepath The path of the file to save the cache to.
   */
  public void saveCache(String filepath) {
    cache.saveCacheToFile(filepath);
  }

  /**
   * Loads stock data into the cache from a previously saved file.
   *
   * @param filepath The path of the file from which to load the cache.
   */
  public void loadCache(String filepath) {
    cache.loadCacheFromFile(filepath);
  }

  /**
   * Inspects the gain or loss of a stock on a given date.
   *
   * @param symbol object containing stock data for the day.
   * @param date   The date to inspect.
   * @return A string indicating the gain or loss of the stock on the given date.
   */
  public String inspectStockGainOrLoss(String symbol, LocalDate date) {

    // date should be in the past
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future");
    }
    boolean isDataFullyAvailable = isDataAvailableInCache(symbol, date.minusDays(1),
        date);

    if (!isDataFullyAvailable) {
      // Step 2: Fetch from API and update cache
      updateCacheWithApiData(symbol);
    }

    StockInfo stockInfo = cache.getStockData(symbol, date);
    if (stockInfo == null) {
      return "Stock data not available for " + symbol + " on " + date;
    }
    BigDecimal gainLossAmount = stockInfo.getClose().subtract(stockInfo.getOpen());
    if (gainLossAmount.compareTo(BigDecimal.ZERO) > 0) {
      return "Gained by " + gainLossAmount;
    } else if (gainLossAmount.compareTo(BigDecimal.ZERO) < 0) {
      return "Lost by " + gainLossAmount.abs();
    } else {
      return "Unchanged";
    }
  }

  /**
   * Computes the X-day moving average for a given stock symbol and end date.
   *
   * @param symbol  The stock symbol for calculation.
   * @param endDate End date for the period.
   * @param days    Number of days for the moving average.
   * @return BigDecimal representing the X-day moving average over the specified period.
   */
  public BigDecimal computeXDayMovingAverage(String symbol, LocalDate endDate, int days) {
    LocalDate startDate = endDate.minusDays(days);
    BigDecimal sum = BigDecimal.ZERO;
    int count = 0;

    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      boolean isDataFullyAvailable =
          isDataAvailableInCache(symbol, endDate.minusDays(days), endDate);

      if (!isDataFullyAvailable) {
        // Step 2: Fetch from API and update cache
        updateCacheWithApiData(symbol);
      }

      StockInfo stockInfo = cache.getStockData(symbol, date);
      if (stockInfo != null) {
        sum = sum.add(stockInfo.getClose());
        count++;
      }
    }

    return count > 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) :
        BigDecimal.ZERO;
  }

}
