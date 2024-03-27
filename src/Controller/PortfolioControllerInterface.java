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

  void GenGraph(String identifier, LocalDate startDate, LocalDate endDate);

  Payload saveCache(String filePath);
  Payload loadCache(String filePath);

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
  Payload findMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      int shortMovingPeriod, int longMovingPeriod);

  /**
   * Finds the crossover days for a given stock symbol within a specified date range.
   * A crossover day is a day when the closing price of the stock is higher than the opening price.
   *
   * @param symbol    The symbol of the stock to analyze.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @return A list of dates within the specified range that are crossover days.
   */
  Payload findCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate);
}
