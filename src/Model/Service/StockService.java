package Model.Service;

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

import Controller.Payload;
import Model.PortfolioInterface;
import Model.Tradable;
import Model.utilities.StockDataCache;
import Model.utilities.StockInfo;

import static Model.utilities.DateUtils.determineResolution;
import static Model.utilities.DateUtils.getTargetDateBasedOnResolution;

/**
 * Service class for fetching stock data and calculating stock prices.
 */
public class StockService implements StockServiceInterface {

  private final StockDataCache cache = new StockDataCache();

  private final String apiKey;

  /**
   * Constructor for the StockService class.
   *
   */
  public StockService(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Fetches the closing price of the stock with the given symbol on the given date.
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
    } while (traverseCount < 4);

    return new Payload(BigDecimal.ZERO, "");
  }

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
   * Fetches stock data for the given symbol from the API and caches it.
   */
  private String fetchAndCacheStockData(String symbol) {
    String csvData = makeApiRequest(symbol);
    if (csvData.contains("Invalid stock symbol")) {
      return "Invalid stock symbol";
    }
    parseAndCacheCsvData(csvData, symbol);
    return null;
  }


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
    for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      StockInfo info =  this.fetchCompleteStockDataOnDate(symbol, date);
      if (info != null && info.getClose().compareTo(info.getOpen()) > 0) {
        crossoverDays.add(date);
      }
    }
    return crossoverDays;
    }


    public Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, int shortMovingPeriod, int longMovingPeriod) {
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
        throw new IllegalArgumentException("Short moving period should be less than long moving period");
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



  public void saveCache(String filepath)
  {
    cache.saveCacheToFile(filepath);
  }


  public void loadCache(String filepath)
  {
    cache.loadCacheFromFile(filepath);
  }


  public String inspectStockGainOrLoss(String symbol, LocalDate date) {

    // date should be in the past
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future");
    }
    boolean isDataFullyAvailable = isDataAvailableInCache(symbol, date.minusDays(1), date);

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

      StockInfo stockInfo = cache.getStockData(symbol, date);      if (stockInfo != null) {
        sum = sum.add(stockInfo.getClose());
        count++;
      }
    }

    return count > 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) :
            BigDecimal.ZERO;
  }

}
