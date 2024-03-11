package Model.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

import Model.Utilities.StockDataCache;
import Model.Utilities.StockInfo;

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
  public BigDecimal fetchPriceOnDate(String symbol, LocalDate date) {
    if (!cache.hasStockData(symbol, date)) {
      fetchAndCacheStockData(symbol); // Fetch all available data for the symbol and cache it
    }
    StockInfo info = cache.getStockData(symbol, date);
    return info != null ? info.getClose() : BigDecimal.ZERO;
  }

  /**
   * Fetches the closing price of the stock with the given symbol on the previous trading day.
   *
   * @param symbol The symbol of the stock to fetch.
   * @param date   The date for which to fetch the previous close price.
   * @return The closing price of the stock on the previous trading day.
   */
  public BigDecimal fetchPreviousClosePrice(String symbol, LocalDate date) {
    BigDecimal previousClosePrice = this.fetchPriceOnDate(symbol, date);
    int traversecount = 0;
    // traverse 4 days back to get the previous close price
    while (previousClosePrice.equals(BigDecimal.ZERO) && traversecount < 4) {
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
  private void fetchAndCacheStockData(String symbol) {
    String csvData = makeApiRequest(symbol);
    parseAndCacheCsvData(csvData, symbol);
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
    } catch (Exception e) {
      System.out.println("An error occurred while fetching stock data: " + e.getMessage());
    }
    return response.toString();
  }
}
