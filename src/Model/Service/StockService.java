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

public class StockService {
  private final StockDataCache cache = new StockDataCache(); // Instance of your caching class

  private final String apiKey;

  public StockService(String apiKey) {
    this.apiKey = apiKey;
  }

  public BigDecimal fetchPriceOnDate(String symbol, LocalDate date) {
    if (!cache.hasStockData(symbol, date)) {
      fetchAndCacheStockData(symbol); // Fetch all available data for the symbol and cache it
    }
    StockInfo info = cache.getStockData(symbol, date);
    return info != null ? info.getClose() : BigDecimal.ZERO;
  }

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

  private void fetchAndCacheStockData(String symbol) {
    String csvData = makeApiRequest(symbol);
    parseAndCacheCsvData(csvData, symbol);
  }

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

  private String makeApiRequest(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
              "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&datatype=csv&apikey=%s",
              symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
