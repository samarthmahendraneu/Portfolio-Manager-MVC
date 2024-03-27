package Model.Service;

import Controller.Payload;
import Model.PortfolioInterface;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.math.BigDecimal;

/**
 * Interface for the StockService class.
 */
public interface StockServiceInterface {

  /**
   * Fetches the price of a stock with the given symbol on the given date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which the price is to be fetched.
   * @return The price of the stock on the given date.
   */
  Payload fetchPriceOnDate(String symbol, LocalDate date);

  /**
   * Fetches the closing prices for a given stock symbol over a specified period
   * at a monthly resolution.
   * It dynamically adjusts the resolution based on the start and end date
   * to optimize data representation.
   * The method checks the cache first and updates it with API data if necessary.
   *
   * @param symbol The symbol of the stock.
   * @param startMonth The start Month of the period.
   * @param endMonth The end Month of the period.
   * @return A sorted map where keys are dates (end of the month) and values
   * are the closing prices of the stock.
   */
   SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod
          (String symbol, LocalDate startMonth, LocalDate endMonth);

  /**
   * Finds the earliest stock purchase date in a given portfolio.
   * This can be used to determine the start point for plotting or calculating portfolio values.
   *
   * @param portfolio The portfolio from which to find the earliest stock purchase date.
   * @return The earliest date on which a stock was purchased within the given portfolio.
   * @throws IllegalStateException If the portfolio does not contain any stocks.
   */

   LocalDate findEarliestStockDate(PortfolioInterface portfolio);

  /**
   * Loads stock data into the cache from a previously saved file.
   * This method is used at the start of the application to quickly populate the cache
   * with data that was saved during a previous run, reducing the need for initial API calls.
   *
   * @param filepath The path of the file from which to load the cache.
   * @throws Exception If an error occurs during the loading process.
   */
   void loadCache(String filepath);

  /**
   * Saves the current state of the stock data cache to a file.
   * This allows the cached data to persist beyond the application's runtime,
   * enabling faster data retrieval without the need for repeated API calls.
   *
   * @param filepath The path of the file where the cache should be saved.
   * @throws Exception If an error occurs during the saving process.
   */
  void saveCache(String filepath);

  /**
   * Finds the crossover days for a given stock symbol within a specified date range.
   * A crossover day is a day when the closing price of the stock is higher than the opening price.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  List<LocalDate> findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate);

  /**
   * Finds the moving crossover days for a given stock symbol within a specified date range.
   * A moving crossover day is a day when the closing price of the stock is higher than the moving average.
   *
   * @param symbol       The symbol of the stock to analyze.
   * @param startDate    The start date of the date range.
   * @param endDate      The end date of the date range.
   * @param shortMovingPeriod The number of days to consider for the short moving average.
   * @param longMovingPeriod The number of days to consider for the long moving average.
   * @return A list of dates within the specified range that are moving crossover days.
   */
  Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, int shortMovingPeriod, int longMovingPeriod);
}
