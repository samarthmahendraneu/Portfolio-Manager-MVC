package model.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A class to cache stock data. This class is used to store stock data in memory to avoid making
 * repeated API calls to fetch the same data.
 */
public class StockDataCache {

  private final Map<String, Map<LocalDate, StockInfo>> stockDataCache = new HashMap<>();

  /**
   * Adds stock data to the cache.
   *
   * @param symbol    The symbol of the stock.
   * @param date      The date for which the stock data is to be added.
   * @param stockInfo The stock data to be added.
   */
  public void addStockData(String symbol, LocalDate date, StockInfo stockInfo) {
    stockDataCache.computeIfAbsent(symbol, k -> new HashMap<>()).put(date, stockInfo);
  }

  /**
   * Fetches stock data from the cache.
   *
   * @param symbol symbol of the stock.
   * @param date   date for which the stock data is to be fetched.
   * @return stock data for the given symbol and date.
   */
  public StockInfo getStockData(String symbol, LocalDate date) {
    return stockDataCache.getOrDefault(symbol, new HashMap<>()).get(date);
  }

  /**
   * Checks if the cache contains stock data for the given symbol and date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which to check for stock data.
   * @return true if the cache contains stock data for the given symbol and date, false otherwise.
   */
  public boolean hasStockData(String symbol, LocalDate date) {
    return stockDataCache.containsKey(symbol) && stockDataCache.get(symbol).containsKey(date);
  }

  /**
   * Saves the cache to a file.
   *
   * @param filePath The path to the file where the cache is to be saved.
   */
  public void saveCacheToFile(String filePath) {
    try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
      out.println("Symbol,Date,Open,High,Low,Close,Volume"); // CSV header
      for (Map.Entry<String, Map<LocalDate, StockInfo>> entry : stockDataCache.entrySet()) {
        String symbol = entry.getKey();
        for (Map.Entry<LocalDate, StockInfo> dateEntry : entry.getValue().entrySet()) {
          LocalDate date = dateEntry.getKey();
          StockInfo info = dateEntry.getValue();
          out.printf("%s,%s,%f,%f,%f,%f,%d%n", symbol, date, info.getOpen(), info.getHigh(),
              info.getLow(), info.getClose(), info.getVolume());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the cache from a file.
   *
   * @param filePath The path to the file from which the cache is to be loaded.
   */
  public void loadCacheFromFile(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath))) {
      if (scanner.hasNextLine()) { // Skip header
        scanner.nextLine();
      }

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(",");
        String symbol = parts[0];
        LocalDate date = LocalDate.parse(parts[1]);
        BigDecimal open = new BigDecimal(parts[2]);
        BigDecimal high = new BigDecimal(parts[3]);
        BigDecimal low = new BigDecimal(parts[4]);
        BigDecimal close = new BigDecimal(parts[5]);
        long volume = Long.parseLong(parts[6]);

        StockInfo stockInfo = new StockInfo(date, open, high, low, close, volume);
        addStockData(symbol, date, stockInfo);
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + filePath);
      throw new IllegalArgumentException("File not found: " + filePath);
    } catch (Exception e) {
      System.out.println("Error reading from file: " + e.getMessage());
      throw new IllegalArgumentException("Error reading from file: " + e.getMessage());
    }
  }

}
