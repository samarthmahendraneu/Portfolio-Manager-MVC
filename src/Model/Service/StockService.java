package Model.Service;

import Controller.Payload;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import Model.PortfolioInterface;
import Model.utilities.DateUtils;
import Model.utilities.StockDataCache;
import Model.utilities.StockInfo;
import Model.Tradable;

/**
 * Service class for fetching stock data and calculating stock prices.
 */
public class StockService implements StockServiceInterface {

  private final StockDataCache cache = new StockDataCache(); // Instance of your caching class

  private final String apiKey;

  /**
   * Constructor for the StockService class.
   *
   * @param apiKey The API key to be used for fetching stock data.
   */
  public StockService(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Fetches the closing price of the stock with the given symbol on the given date.
   *
   * @param symbol The symbol of the stock to fetch.
   * @param date   The date for which to fetch the stock price.
   * @return The closing price of the stock on the given date.
   */
  public Payload fetchPriceOnDate(String symbol, LocalDate date) {
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
    } while (traverseCount < 4);

    return new Payload(BigDecimal.ZERO, "");
  }

  private StockInfo fetchCompleteStockDataOnDate(String symbol, LocalDate date) {
    StockInfo info;
    String message;

    if (!cache.hasStockData(symbol, date)) {
      message = fetchAndCacheStockData(symbol);
      if (message != null) {
        return null;
      }
    }
    info = cache.getStockData(symbol, date);
    return info;
  }


  /**
   * Fetches stock data for the given symbol from the API and caches it.
   *
   * @param symbol The symbol of the stock to fetch.
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
   * Parses the given CSV data and caches it.
   *
   * @param csvData The CSV data to parse and cache.
   * @param symbol  The symbol of the stock for which the data is being cached.
   */
  private void parseAndCacheCsvData(String csvData, String symbol) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvData.getBytes())))) {
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
   * @param symbol The symbol of the stock to fetch.
   * @return The response from the API as a string.
   */
  private String makeApiRequest(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
          "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&datatype=csv&apikey=%s&outputsize=full",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
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


  private String makeApiRequestMonthly(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
          "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&datatype=csv&apikey=%s",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line).append("\n");
        }
      }

    } catch (Exception e) {
      System.out.println("An error occurred while fetching monthly stock data: " + e.getMessage());
      return "Error";
    }
    return response.toString();
  }


  /**
   * Finds the crossover days for a given stock symbol within a specified date range.
   * A crossover day is a day when the closing price of the stock is higher than the opening price.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  public List<LocalDate> findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate) {
    List<LocalDate> crossoverDays = new ArrayList<>();
    String csvData = makeApiRequest(symbol);
    if (csvData.contains("Invalid stock symbol")) {
      return crossoverDays;
    }
    // loop through dates in the range
    for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      StockInfo info =  this.fetchCompleteStockDataOnDate(symbol, date);
      if (info != null && info.getClose().compareTo(info.getOpen()) > 0) {
        crossoverDays.add(date);
      }
    }
    return crossoverDays;
    }

    /**
     * Finds the moving crossover days for a given stock symbol within a specified date range.
     * A moving crossover day is a day when the closing price of the stock is higher than the moving average.
     *
     * @param symbol       The symbol of the stock to analyze.
     * @param startDate    The start date of the date range.
     * @param endDate      The end date of the date range.
     * @param shortMovingPeriod The number of days to consider for the short moving average.
     * @param longMovingPeriod The number of days to consider for the long moving average.
     * @return A list of dates within the specified range that are moving crossover days.
     */
    public Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, int shortMovingPeriod, int longMovingPeriod) {
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
        boolean crossedAbove = shortMovingAvg > longMovingAvg && shortMovingAvg < prevShortMovingAvg;
        boolean crossedBelow = shortMovingAvg < longMovingAvg && shortMovingAvg > prevShortMovingAvg;

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

  private List<BigDecimal> getHistoricalData(String symbol, LocalDate startDate, LocalDate endDate) {
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

  private double calculateMovingAverage(List<BigDecimal> prices, int period) {
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < period && i < prices.size(); i++) {
      sum = sum.add(prices.get(i));
    }
    return (Double) sum.divide(BigDecimal.valueOf(period)).doubleValue();
    }

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

  private void updateCacheWithApiData(String symbol) {
    String apiResponse = makeApiRequest(symbol); // Your method to fetch data
    parseAndCacheCsvData(apiResponse, symbol);
  }

  /**
   * Fetches the closing prices for a given stock symbol over a specified period
   * at a monthly resolution.
   * It dynamically adjusts the resolution based on the start and end date
   * to optimize data representation.
   * The method checks the cache first and updates it with API data if necessary.
   *
   * @param symbol The symbol of the stock.
   * @param startDate The start date of the period.
   * @param endDate The end date of the period.
   * @return A sorted map where keys are dates (end of the month) and values
   * are the closing prices of the stock.
   */

  public SortedMap<LocalDate, BigDecimal>
  fetchMonthlyClosingPricesForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
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

  private String determineResolution(LocalDate startDate, LocalDate endDate) {
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    if (daysBetween <= 30) {
      return "daily";
    } else if(daysBetween <= 150){
      return "every 10 days";
    }
    else if (daysBetween <= 540) { // Up to 18 months
      return "monthly";
    } else if (daysBetween <= 1825) { // Up to 5 years
      return "every 3 months";
    } else {
      return "yearly";
    }
  }

  private LocalDate getTargetDateBasedOnResolution
          (LocalDate date, String resolution, LocalDate endDate) {
    switch (resolution) {
      case "daily", "every 10 days":
        return date;
      case "monthly":
        return DateUtils.getLastWorkingDayOfMonth(date);
      case "every 3 months":
        LocalDate endOfQuarter
                = date.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        return DateUtils.getLastWorkingDayOfMonth(endOfQuarter).isAfter(endDate)
                ? null : DateUtils.getLastWorkingDayOfMonth(endOfQuarter);
      case "yearly":
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        return DateUtils.getLastWorkingDayOfYear(endOfYear).isAfter(endDate)
                ? null : DateUtils.getLastWorkingDayOfYear(endOfYear);
      default:
        throw new IllegalArgumentException("Unknown resolution: " + resolution);
    }
  }

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
   * This can be used to determine the start point for plotting or calculating portfolio values.
   *
   * @param portfolio The portfolio from which to find the earliest stock purchase date.
   * @return The earliest date on which a stock was purchased within the given portfolio.
   * @throws IllegalStateException If the portfolio does not contain any stocks.
   */

  public LocalDate findEarliestStockDate(PortfolioInterface portfolio) {
    LocalDate earliestDate = null;
    for (Tradable stock : portfolio.getStocks()) {
      LocalDate stockDate = stock.getActivityLog().keySet().stream().min(LocalDate::compareTo).orElse(null);
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
   * Saves the current state of the stock data cache to a file.
   * This allows the cached data to persist beyond the application's runtime,
   * enabling faster data retrieval without the need for repeated API calls.
   *
   * @param filepath The path of the file where the cache should be saved.
   * @throws Exception If an error occurs during the saving process.
   */

  public void saveCache(String filepath)
  {
    try {
      cache.saveCacheToFile(filepath);
    }
    catch (Exception e)
    {
    throw e;
    }
  }

  /**
   * Loads stock data into the cache from a previously saved file.
   * This method is used at the start of the application to quickly populate the cache
   * with data that was saved during a previous run, reducing the need for initial API calls.
   *
   * @param filepath The path of the file from which to load the cache.
   * @throws Exception If an error occurs during the loading process.
   */

  public void loadCache(String filepath)
  {
    try {
      cache.loadCacheFromFile(filepath);
    }
    catch (Exception e)
    {
      throw e;
    }
  }

}
