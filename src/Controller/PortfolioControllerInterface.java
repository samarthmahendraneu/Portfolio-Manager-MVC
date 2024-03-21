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
}
