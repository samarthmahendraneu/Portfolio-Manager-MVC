package model.service;

import controller.Payload;
import model.PortfolioInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.math.BigDecimal;

/**
 * Interface for the StockService class that has methods to fetch stock data and perform analysis on
 * stock prices.
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
   * Fetches the closing price of the stock with the given symbol on the previous trading day.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which the price is to be fetched.
   * @return The price of the stock on the given date.
   */
  Payload fetchLastClosePrice(String symbol, LocalDate date);

  /**
   * Fetches the closing prices for a given stock symbol over a specified period at a monthly
   * resolution. It dynamically adjusts the resolution based on the start and end date to optimize
   * data representation. The method checks the cache first and updates it with API data if
   * necessary.
   *
   * @param symbol     The symbol of the stock.
   * @param startMonth The start Month of the period.
   * @param endMonth   The end Month of the period.
   * @return A sorted map with keys as dates (end of the month) and values as the closing prices
   */
  SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod(String symbol,
      LocalDate startMonth, LocalDate endMonth);

  /**
   * Finds the earliest stock purchase date in a given portfolio. This can be used to determine the
   * start point for plotting or calculating portfolio values.
   *
   * @param portfolio The portfolio from which to find the earliest stock purchase date.
   * @return The earliest date on which a stock was purchased within the given portfolio.
   * @throws IllegalStateException If the portfolio does not contain any stocks.
   */

  LocalDate findEarliestStockDate(PortfolioInterface portfolio);

  /**
   * Loads stock data into the cache from a previously saved file. This method is used at the start
   * of the application to quickly populate the cache with data that was saved during a previous
   * run, reducing the need for initial API calls.
   *
   * @param filepath The path of the file from which to load the cache.
   */
  void loadCache(String filepath);

  /**
   * Saves the current state of the stock data cache to a file. This allows the cached data to
   * persist beyond the application's runtime, enabling faster data retrieval without the need for
   * repeated API calls.
   *
   * @param filepath The path of the file where the cache should be saved.
   */
  void saveCache(String filepath);

  /**
   * Finds the crossover days for a given stock symbol within a specified date range. A crossover
   * day is a day when the closing price of the stock is higher than the opening price.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  List<LocalDate> findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate);

  /**
   * Finds the moving crossover days for a given stock symbol within a specified date range. A
   * moving crossover day is a day when the closing price of the stock is higher than the moving
   * average.
   *
   * @param symbol            The symbol of the stock to analyze.
   * @param startDate         The start date of the date range.
   * @param endDate           The end date of the date range.
   * @param shortMovingPeriod The number of days to consider for the short moving average.
   * @param longMovingPeriod  The number of days to consider for the long moving average.
   * @return A list of dates within the specified range that are moving crossover days.
   */
  Map<String, Object> findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      int shortMovingPeriod, int longMovingPeriod);

  /**
   * Calculates the x-day moving average for a stock's closing prices over a specified period. This
   * average is a technical analysis tool that smooths out price data to create a constantly updated
   * average price.
   *
   * @param symbol  The stock symbol for calculation.
   * @param endDate End date for the period.
   * @param days    Number of days for the moving average.
   * @return BigDecimal representing the x-day moving average over the specified period.
   * @throws IllegalArgumentException If parameters are invalid or the period is too short.
   */
  BigDecimal computeXDayMovingAverage(String symbol, LocalDate endDate, int days);

  /**
   * Inspects the performance change of a stock on a given day. It calculates the difference between
   * these prices.
   *
   * @param symbol object containing stock data for the day.
   * @param date   The date for which the performance is to be inspected.
   * @return A string message indicating the stock's performance.
   */
  String inspectStockGainOrLoss(String symbol, LocalDate date);

}
