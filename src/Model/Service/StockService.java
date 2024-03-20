package Model.Service;

import Controller.Payload;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import Model.Utilities.MonthlyStockDataCache;
import Model.Utilities.StockDataCache;
import Model.Utilities.StockInfo;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Service class for fetching stock data and calculating stock prices.
 */
public class StockService implements StockServiceInterface {

  private final StockDataCache cache = new StockDataCache(); // Instance of your caching class
  private final MonthlyStockDataCache monthlyCache = new MonthlyStockDataCache(); // Instance of your caching class

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
    String message = "";
    if (!cache.hasStockData(symbol, date)) {
      message = fetchAndCacheStockData(
          symbol); // Fetch all available data for the symbol and cache it
      if (message != null) {
        return new Payload(null, message);
      }
    }
    StockInfo info = cache.getStockData(symbol, date);
    return info != null ? new Payload(info.getClose(), "") : new Payload(BigDecimal.ZERO, "");
  }

  /**
   * Fetches the closing price of the stock with the given symbol on the previous trading day.
   *
   * @param symbol The symbol of the stock to fetch.
   * @param date   The date for which to fetch the previous close price.
   * @return The closing price of the stock on the previous trading day.
   */
  public Payload fetchPreviousClosePrice(String symbol, LocalDate date) {
    // BigDecimal previousClosePrice = this.fetchPriceOnDate(symbol, date);
    Payload previousClosePrice = this.fetchPriceOnDate(symbol, date);
    int traversecount = 0;
    // traverse 4 days back to get the previous close price
    while (previousClosePrice.getData().equals(BigDecimal.ZERO) && traversecount < 4) {
      date = date.minusDays(1);
      previousClosePrice = this.fetchPriceOnDate(symbol, date);
      traversecount++;
    }
    return previousClosePrice;
  }

  /**
   * Fetches stock data for the given symbol from the API and caches it.
   *
   * @param symbol The symbol of the stock to fetch.
   */
  private <Optional> String fetchAndCacheStockData(String symbol) {
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
    try (Scanner scanner = new Scanner(csvData)) {
      scanner.nextLine(); // Skip header
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] values = line.split(",");
        LocalDate date = LocalDate.parse(values[0]);
        BigDecimal open = new BigDecimal(values[1]);
        BigDecimal high = new BigDecimal(values[2]);
        BigDecimal low = new BigDecimal(values[3]);
        BigDecimal close = new BigDecimal(values[4]);
        long volume = Long.parseLong(values[5]);
        StockInfo stockInfo = new StockInfo(date, open, high, low, close, volume);
        cache.addStockData(symbol, date, stockInfo);
      }
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
          "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&datatype=csv&apikey=%s",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line).append("\n");
      }
      reader.close();

      // Check if the response contains an error message
      if (response.toString().contains("Error Message")) {
        System.out.println("Invalid stock symbol: " + symbol);
        return "Invalid stock symbol: " + symbol;
      }

    } catch (Exception e) {
      System.out.println("An error occurred while fetching stock data: " + e.getMessage());
    }
    return response.toString();
  }


  public Payload fetchPriceOnMonth(String symbol, YearMonth date) {
    String message = "";
    if (!monthlyCache.hasMonthlyStockData(symbol, date)) {
      message = fetchAndCacheMonthlyData(
              symbol); // Fetch all available data for the symbol and cache it
      if (message != null) {
        return new Payload(null, message);
      }
    }
    StockInfo info = monthlyCache.getMonthlyStockData(symbol, date);
    return info != null ? new Payload(info, "") :
            new Payload(null, "");
  }
  public <Optional> String fetchAndCacheMonthlyData(String symbol) {
    String csvData = makeApiRequestMonthly(symbol);
    if (csvData.contains("Invalid stock symbol")) {
      return "Invalid stock symbol:";
    }
    parseAndStoreMonthlyData(csvData, symbol);
    return null;
  }

  private String makeApiRequestMonthly(String symbol) {
    // but fetching monthly data instead of daily.
    // Assuming it returns CSV formatted data for the stock's monthly performance.
    StringBuilder response = new StringBuilder();
    try {
      // Adjust the URL according to how the API provides monthly data
      String urlString = String.format(
              "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&datatype=csv&apikey=%s",
              symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(
              new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line).append("\n");
      }
      reader.close();
    } catch (Exception e) {
      System.out.println("An error occurred while fetching monthly stock data: " + e.getMessage());
      return "Error";
    }
    return response.toString();
  }
  public void parseAndStoreMonthlyData(String csvData, String symbol) {
    // Assuming the first line is a header and skipping it
    String[] lines = csvData.split("\n");

    for (int i = 1; i < lines.length; i++) {
      String[] data = lines[i].split(",");
      LocalDate date = LocalDate.parse(data[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      BigDecimal open = new BigDecimal(data[1]);
      BigDecimal high = new BigDecimal(data[2]);
      BigDecimal low = new BigDecimal(data[3]);
      BigDecimal close = new BigDecimal(data[4]);
      long volume = Long.parseLong(data[5]);

      // Create a StockInfo object
      StockInfo stockInfo = new StockInfo(date, open, high, low, close, volume);

      // Extract YearMonth from the date
      YearMonth yearMonth = YearMonth.from(date);

      // Add to the monthly cache
      monthlyCache.addMonthlyStockData(symbol, yearMonth, stockInfo);
    }
  }

  public SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod
          (String symbol, YearMonth startMonth, YearMonth endMonth) {
    SortedMap<LocalDate, BigDecimal> monthlyClosingPrices = new TreeMap<>();

    YearMonth currentMonth = startMonth;
    while (!currentMonth.isAfter(endMonth)) {
      Payload info = fetchPriceOnMonth(symbol, currentMonth);
      if (info != null) {
        StockInfo stockInfo = (StockInfo) info.getData();
        // Assuming the date in StockInfo is the last trading day of the month
        monthlyClosingPrices.put(stockInfo.getDate(), stockInfo.getClose());
      }
      currentMonth = currentMonth.plusMonths(1);
    }

    return monthlyClosingPrices;
  }
}
