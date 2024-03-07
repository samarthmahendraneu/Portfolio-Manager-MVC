package Interface;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockInterface {
  /**
   * Fetches the most recent closing price for a given stock symbol.
   * @param symbol The stock symbol.
   * @return The most recent closing price as a BigDecimal.
   */
  BigDecimal fetchRecentClosePrice(String symbol);

  /**
   * Fetches the closing price of a stock on a specific date.
   * @param symbol The stock symbol.
   * @param date The date for which the price is requested.
   * @return The closing price on the specified date as a BigDecimal, or null if not available.
   */
  BigDecimal fetchPriceOnDate(String symbol, LocalDate date);
}
