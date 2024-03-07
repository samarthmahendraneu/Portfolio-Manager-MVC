package Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

import Interface.StockInterface;
import Utilities.StockDataCache;
import Utilities.StockInfo;

public class StockService implements StockInterface {
  private StockDataCache cache = new StockDataCache(); // Instance of your caching class

  private final String apiKey;

  public StockService(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public BigDecimal fetchRecentClosePrice(String symbol) {
    String csvData = makeApiRequest(symbol);
    return parseCsvForClosePrice(csvData);
  }


  @Override
  public BigDecimal fetchPriceOnDate(String symbol, LocalDate date) {
    if (!cache.hasStockData(symbol, date)) {
      fetchAndCacheStockData(symbol); // Fetch all available data for the symbol and cache it
    }
    StockInfo info = cache.getStockData(symbol, date);
    return info != null ? info.getClose() : BigDecimal.ZERO;
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
        long volume = Long.parseLong(values[6]);
        StockInfo stockInfo = new StockInfo(date, open, high, low, close, volume);
        cache.addStockData(symbol, date, stockInfo);
      }
    }
  }

  private String makeApiRequest(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
              "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=%s&datatype=csv&apikey=%s",
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

  private BigDecimal parseCsvForClosePrice(String csvData) {
    try (Scanner scanner = new Scanner(csvData)) {
      scanner.nextLine(); // Skip header
      if (scanner.hasNextLine()) {
        String[] values = scanner.nextLine().split(",");
        return new BigDecimal(values[4]); // Assuming close price is in the 5th column
      }
    }
    return BigDecimal.ZERO;
  }

  private BigDecimal parseCsvForSpecificDate(String csvData, LocalDate date) {
    try (Scanner scanner = new Scanner(csvData)) {
      scanner.nextLine(); // Skip header
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] values = line.split(",");
        LocalDate lineDate = LocalDate.parse(values[0]);
        if (date.equals(lineDate)) {
          return new BigDecimal(values[4]); // Assuming close price is in the 5th column
        }
      }
    } catch (Exception e) {
      System.out.println("An error occurred while parsing the CSV data: " + e.getMessage());
    }
    System.out.println("No data found for the specified date: " + date);
    return BigDecimal.ZERO;
  }

  public static void main(String[] args) {
    StockService service = new StockService("W0M1JOKC82EZEQA8");
    BigDecimal recentClosePrice = service.fetchRecentClosePrice("IBM");
    System.out.println("Most Recent Close Price: " + recentClosePrice);

    LocalDate date = LocalDate.of(2024, 3, 6); // Example date
    BigDecimal priceOnDate = service.fetchPriceOnDate("IBM", date);
    System.out.println("Price on " + date + ": " + priceOnDate);
  }
}
