package Model.Service;

import Controller.Payload;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.SortedMap;
import java.util.TreeMap;

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
    parseAndCacheCsvData(apiResponse, symbol);   // Assuming this method parses the CSV and updates the cache
  }

  public SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
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
    } else if (daysBetween <= 540) { // Up to 18 months
      return "monthly";
    } else if (daysBetween <= 1825) { // Up to 5 years
      return "every 3 months";
    } else {
      return "yearly";
    }
  }

  private LocalDate getTargetDateBasedOnResolution(LocalDate date, String resolution, LocalDate endDate) {
    switch (resolution) {
      case "daily":
        return date;
      case "monthly":
        return DateUtils.getLastWorkingDayOfMonth(date);
      case "every 3 months":
        LocalDate endOfQuarter = date.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        return DateUtils.getLastWorkingDayOfMonth(endOfQuarter).isAfter(endDate) ? null : DateUtils.getLastWorkingDayOfMonth(endOfQuarter);
      case "yearly":
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        return DateUtils.getLastWorkingDayOfYear(endOfYear).isAfter(endDate) ? null : DateUtils.getLastWorkingDayOfYear(endOfYear);
      default:
        throw new IllegalArgumentException("Unknown resolution: " + resolution);
    }
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
  public LocalDate findEarliestStockDate(PortfolioInterface portfolio) {
    return portfolio.getStocks().stream()
            .map(Tradable::getPurchaseDate)
            .min(LocalDate::compareTo)
            .orElseThrow(() -> new IllegalStateException("No stocks found in portfolio: " + portfolio.getName()));
  }


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
