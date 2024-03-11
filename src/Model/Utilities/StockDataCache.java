package Model.Utilities;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class StockDataCache {
  private Map<String, Map<LocalDate, StockInfo>> stockDataCache = new HashMap<>();

  public void addStockData(String symbol, LocalDate date, StockInfo stockInfo) {
    stockDataCache.computeIfAbsent(symbol, k -> new HashMap<>()).put(date, stockInfo);
  }

  public StockInfo getStockData(String symbol, LocalDate date) {
    return stockDataCache.getOrDefault(symbol, new HashMap<>()).get(date);
  }

  public boolean hasStockData(String symbol, LocalDate date) {
    return stockDataCache.containsKey(symbol) && stockDataCache.get(symbol).containsKey(date);
  }
}
