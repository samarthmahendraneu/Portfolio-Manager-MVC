package Mock;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import Controller.Payload;
import Model.PortfolioInterface;
import Model.Service.StockServiceInterface;
import Model.utilities.StockInfo;

public  class MockStockService implements StockServiceInterface {
  private static final Map<String, Map<LocalDate, StockInfo>> MOCK_DATA = new HashMap<>();

  static {
    // Initialize mock data for AAPL with more diverse scenarios
    Map<LocalDate, StockInfo> aaplData = new HashMap<>();
    aaplData.put(LocalDate.of(2024, 2, 1), new StockInfo(LocalDate.of(2024, 2, 1), new BigDecimal("150"), new BigDecimal("155"), new BigDecimal("149"), new BigDecimal("152"), 100000));
    aaplData.put(LocalDate.of(2024, 2, 2), new StockInfo(LocalDate.of(2024, 2, 2), new BigDecimal("152"), new BigDecimal("156"), new BigDecimal("151"), new BigDecimal("154"), 100000));
    aaplData.put(LocalDate.of(2024, 2, 3), new StockInfo(LocalDate.of(2024, 2, 3), new BigDecimal("154"), new BigDecimal("158"), new BigDecimal("153"), new BigDecimal("156"), 100000));
    // Adding a day with a loss
    aaplData.put(LocalDate.of(2024, 2, 4), new StockInfo(LocalDate.of(2024, 2, 4), new BigDecimal("156"), new BigDecimal("157"), new BigDecimal("150"), new BigDecimal("151"), 100000));
    // Adding a day with unchanged stock price
    aaplData.put(LocalDate.of(2024, 2, 5), new StockInfo(LocalDate.of(2024, 2, 5), new BigDecimal("151"), new BigDecimal("152"), new BigDecimal("150"), new BigDecimal("151"), 100000));
    // Missing data for 2024-02-06 to simulate a gap in data
    // Adding a day with minimal movement, testing near-zero average
    aaplData.put(LocalDate.of(2024, 2, 7), new StockInfo(LocalDate.of(2024, 2, 7), new BigDecimal("151"), new BigDecimal("151.01"), new BigDecimal("150.99"), new BigDecimal("151"), 100000));

    MOCK_DATA.put("AAPL", aaplData);
  }

  public BigDecimal computeXDayMovingAverage(String symbol, LocalDate endDate, int days) {
    Map<LocalDate, StockInfo> stockData = MOCK_DATA.getOrDefault(symbol, new HashMap<>());
    LocalDate startDate = endDate.minusDays(days - 1);

    BigDecimal sum = stockData.entrySet().stream()
            .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
            .map(entry -> entry.getValue().getClose())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    long validDays = stockData.keySet().stream()
            .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
            .count();

    return validDays > 0 ? sum.divide(BigDecimal.valueOf(validDays), BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
  }

  public String inspectStockGainOrLoss(String symbol, LocalDate date) {
    StockInfo stockInfo = MOCK_DATA.getOrDefault(symbol, new HashMap<>()).get(date);
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

  public boolean isValidDate(LocalDate date) {
    if (date.isAfter(LocalDate.now())) {
      System.out.println("Date cannot be in the future: " + date);
      return false;
    }

    DayOfWeek dayOfWeek = date.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
      System.out.println("Date must be on a weekday: " + date);
      return false;
    }

    return true;
  }

  @Override
  public Payload fetchPriceOnDate(String symbol, LocalDate date) {
    return null;
  }

  @Override
  public SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod(String symbol, LocalDate startMonth, LocalDate endMonth) {
    return null;
  }

  @Override
  public LocalDate findEarliestStockDate(PortfolioInterface portfolio) {
    return null;
  }

  @Override
  public void loadCache(String filepath) {

  }

  @Override
  public void saveCache(String filepath) {

  }

  @Override
  public List<LocalDate> findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate) {
    return null;
  }

  @Override
  public Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, int shortMovingPeriod, int longMovingPeriod) {
    return null;
  }

}


