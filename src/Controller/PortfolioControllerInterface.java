package Controller;


import Model.PortfolioInterface;
import Model.Service.PortfolioServiceInterface;
import java.time.LocalDate;

/**
 * Interface for the Portfolio Management System Controller.
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
   *
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
   */
  Payload calculateTotalInvestment(String name, LocalDate onDate);

  /**
   * Buys a stock with the given symbol and quantity on the given date at the given price.
   */
  Payload sellStockFromPortfolio(PortfolioInterface portfolio, String stockSymbol, int quantity,
      LocalDate date);

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path where the portfolios will be saved.
   * @throws IllegalArgumentException if there is an error saving the portfolios to the file.
   *
   * @return The payload containing the portfolios.
   */
  Payload savePortfolio(String filePath) throws IllegalArgumentException;

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @throws IllegalArgumentException if there is an error loading the portfolios from the file.
   *
   * @return The payload containing the portfolios.
   */
  Payload loadPortfolio(String filePath) throws IllegalArgumentException;

  /**
   * get Portfolio Service.
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
   * Generates a performance graph for a specified stock or portfolio over a given time frame.
   * The graph represents changes in value with a line of asterisks, where each asterisk's value
   * is dynamically determined based on the range of values within the period.
   *
   * @param identifier The stock symbol or portfolio name.
   * @param startDate  The start date of the period for graph generation.
   * @param endDate    The end date of the period for graph generation.
   */
  void GenGraph(String identifier, LocalDate startDate, LocalDate endDate);

  /**
   * Saves the current state of the stock data cache to a specified file path. This method
   * allows the persistence of cache data between application sessions, reducing API calls.
   *
   * @param filePath The file path where the cache data will be saved.
   * @return A Payload object indicating success or containing an error message.
   */
  Payload saveCache(String filePath);

  /**
   * Loads stock data cache from a specified file path into the application. This method is
   * useful for initializing the cache with previously saved data upon starting the application.
   *
   * @param filePath The file path from where the cache data will be loaded.
   * @return A Payload object indicating success or containing an error message.
   */
  Payload loadCache(String filePath);

  /**
   * Computes and returns the moving average of a specified stock's closing prices over a defined
   * number of days leading up to a certain date. This average helps to smooth out price data
   * and identify trends.
   *
   * @param symbol   The symbol of the stock.
   * @param endDate  The end date for the moving average calculation.
   * @param days     The number of days over which to calculate the moving average.
   * @return A Payload object containing the moving average or an error message.
   */
  Payload computeStockMovingAverage(String symbol, LocalDate endDate, int days);

  /**
   * Inspects the performance of a specified stock on a given date, indicating whether it gained,
   * lost, or remained unchanged. Additionally, it calculates and reports the magnitude of the
   * gain or loss.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date on which to inspect the stock's performance.
   * @return A Payload object with the performance description or an error message.
   */
  Payload inspectStockPerformance(String symbol, LocalDate date);
}
