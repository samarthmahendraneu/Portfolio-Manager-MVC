package Model.Utilities;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class MonthlyStockDataCache {

  private final Map<String, Map<YearMonth, StockInfo>> monthlyStockDataCache = new HashMap<>();

  public void addMonthlyStockData(String symbol, YearMonth yearMonth, StockInfo stockInfo) {
    monthlyStockDataCache.computeIfAbsent(symbol, k -> new HashMap<>()).put(yearMonth, stockInfo);
  }

  public StockInfo getMonthlyStockData(String symbol, YearMonth yearMonth) {
    return monthlyStockDataCache.getOrDefault(symbol, new HashMap<>()).get(yearMonth);
  }

  public boolean hasMonthlyStockData(String symbol, YearMonth yearMonth) {
    return monthlyStockDataCache.containsKey(symbol) && monthlyStockDataCache.get(symbol).containsKey(yearMonth);
  }
}
