package controller;


import java.time.LocalDate;

import model.PortfolioInterface;
import model.service.PortfolioServiceInterface;

/**
 * Interface for the Portfolio Management System Controller which acts as Bridge between the
 * portfolio service and menu controller.
 */
public interface PortfolioControllerInterface {

  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The  ` name of the new portfolio.
   * @return The newly created Portfolio object.
   * @throws IllegalArgumentException if the portfolio already exists.
   */
  Payload createNewPortfolio(String name) throws IllegalArgumentException;

  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolio The portfolio to which the stock will be added.
   * @param symbol    The symbol of the stock to be added.
   * @param quantity  The quantity of the stock to be added.
   * @param date      The date on which the stock was purchased.
   * @return The updated portfolio.
   */
  Payload addStockToPortfolio(PortfolioInterface portfolio, String symbol, int quantity,
      LocalDate date);

  /**
   * Calculates the value of the portfolio with the given name on the given date.
   *
   * @param name   The name of the portfolio for which the value will be calculated.
   * @param onDate The date on which the value of the portfolio will be calculated.
   * @return The value of the portfolio on the given date.
   */
  Payload calculatePortfolioValue(String name, LocalDate onDate);

  /**
   * Calculate total investment in a perticular portfolio by a given date.
   *
   * @param name   The name of the portfolio for which the value will be calculated.
   * @param onDate The date on which the value of the portfolio will be calculated.
   * @return The total investment in the portfolio on the given date.
   */
  Payload calculateTotalInvestment(String name, LocalDate onDate);

  /**
   * Buys a stock with the given symbol and quantity on the given date at the given price.
   * @param portfolio The portfolio to which the stock will be added.
   * @param stockSymbol The symbol of the stock to be bought.
   * @param quantity The quantity of the stock to be bought.
   * @param date The date on which the stock was bought.
   * @return The updated portfolio.
   */
  Payload sellStockFromPortfolio(PortfolioInterface portfolio, String stockSymbol, int quantity,
      LocalDate date);

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path where the portfolios will be saved.
   * @param type     The type of the portfolio to save.
   * @return A Payload object indicating success or containing an error message.
   * @throws IllegalArgumentException if there is an error saving the portfolios to the file.
   */
  Payload savePortfolio(String filePath, String type) throws IllegalArgumentException;

  /**
   * Loads the portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @param type     The type of the portfolio to load.
   * @return A Payload object indicating success or containing an error message.
   * @throws IllegalArgumentException if there is an error loading the portfolios from the file.
   */
  Payload loadPortfolio(String filePath, String type) throws IllegalArgumentException;

  /**
   * get Portfolio service.
   *
   * @return PortfolioServiceInterface
   */
  PortfolioServiceInterface getPortfolioService();

  /**
   * getNumPortfolios.
   *
   * @return int
   */
  int getNumPortfolios();

  /**
   * Generates a performance graph for a specified stock or portfolio over a given time frame. The
   * graph represents changes in value with a line of asterisks, where each asterisk's value is
   * dynamically determined based on the range of values within the period.
   *
   * @param identifier The stock symbol or portfolio name.
   * @param startDate  The start date of the period for graph generation.
   * @param endDate    The end date of the period for graph generation.
   * @return A StringBuilder object containing the generated graph.
   */
  StringBuilder genGraph(String identifier, LocalDate startDate, LocalDate endDate);

  /**
   * Saves the current state of the stock data cache to a specified file path. This method allows
   * the persistence of cache data between application sessions, reducing API calls.
   *
   * @param filePath The file path where the cache data will be saved.
   * @return A Payload object indicating success or containing an error message.
   */
  Payload saveCache(String filePath);

  /**
   * Loads stock data cache from a specified file path into the application. This method is useful
   * for initializing the cache with previously saved data upon starting the application.
   *
   * @param filePath The file path from where the cache data will be loaded.
   * @return A Payload object indicating success or containing an error message.
   */
  Payload loadCache(String filePath);

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
  Payload findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      int shortMovingPeriod, int longMovingPeriod);

  /**
   * Finds the crossover days for a given stock symbol within a specified date range. A crossover
   * day is a day when the closing price of the stock is higher than the opening price.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  Payload findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate);

  /**
   * Computes and returns the moving average of a specified stock's closing prices over a defined
   * number of days leading up to a certain date. This average helps to smooth out price data and
   * identify trends.
   *
   * @param symbol  The symbol of the stock.
   * @param endDate The end date for the moving average calculation.
   * @param days    The number of days over which to calculate the moving average.
   * @return A Payload object containing the moving average or an error message.
   */
  Payload computeStockMovingAverage(String symbol, LocalDate endDate, int days);

  /**
   * Inspects the performance of a specified stock on a given date, indicating whether it gained,
   * lost, or remained unchanged. Additionally, it calculates and reports the magnitude of the gain
   * or loss.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date on which to inspect the stock's performance.
   * @return A Payload object with the performance description or an error message.
   */
  Payload inspectStockPerformance(String symbol, LocalDate date);

}
