package Model.Utilities;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to cache stock data.
 * This class is used to store stock data in memory to avoid making repeated API calls to fetch the same data.
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
   * @param symbol symbol of the stock.
   * @param date date for which the stock data is to be fetched.
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
}
