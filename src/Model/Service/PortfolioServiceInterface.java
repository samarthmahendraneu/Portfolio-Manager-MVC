package Model.Service;

import Model.PortfolioInterface;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for the PortfolioService class.
 */
public interface PortfolioServiceInterface {

  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The name of the new portfolio.
   *
   * @return The newly created Portfolio object.
   */
  PortfolioInterface createNewPortfolio(String name);

  /**
   * Adds a portfolio to the list of portfolios.
   *
   * @param portfolio The portfolio to add.
   */
  void addPortfolio(PortfolioInterface portfolio);

  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolioName The name of the portfolio to which the stock will be added.
   * @param symbol        The symbol of the stock to be added.
   * @param quantity      The quantity of the stock to be added.
   * @param date          The date on which the stock was purchased.
   *
   * @return The updated portfolio.
   */
  PortfolioInterface addStockToPortfolio(String portfolioName, String symbol, int quantity,
      LocalDate date);

  /**
   * Fetches a portfolio by its name.
   *
   * @param name The name of the portfolio to fetch.
   * @return An Optional containing the portfolio if found, or an empty Optional otherwise.
   */
  Optional<PortfolioInterface> getPortfolioByName(String name);

  /**
   * Calculates the total value of a portfolio on a given date.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the value is to be calculated.
   * @return The total value of the portfolio on the given date.
   */
  Optional<BigDecimal> calculatePortfolioValue(String portfolioName, LocalDate onDate);

  /**
   * Returns a list of all portfolio names.
   *
   * @return A list of all portfolio names.
   */
  List<String> listPortfolioNames();

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path to which the portfolios will be saved.
   * @throws IOException If an error occurs while writing to the file.
   *
   * @return The file path where the portfolios were saved.
   */
  void savePortfoliosToCSV(String filePath) throws IOException;

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @throws IOException If an error occurs while reading from the file.
   *
   * @return The file path from which the portfolios were loaded.
   */
  String loadPortfoliosFromCSV(String filePath) throws IOException;

  /**
   * Checks if a portfolio with the given name exists.
   *
   * @param portfolioName The name of the portfolio to check.
   * @return True if a portfolio with the given name exists, false otherwise.
   */
  boolean portfolioExists(String portfolioName);

  /**
   * get Number of Portfolios.
   *
   * @return number of portfolios.
   */
  int getNumberOfPortfolios();

  /**
   * Retrieves the closing prices of a specified stock symbol
   * over a given period, adjusted to monthly values.
   * This method delegates to fetchMonthlyClosingPricesForPeriod
   * from the StockService to obtain data, which
   * is then returned in a sorted map for easy consumption.
   *
   *
   * @param symbol The stock symbol for which closing prices are to be fetched.
   * @param startDate The start date of the period for which data is required. This is inclusive.
   * @param endDate The end date of the period for which data is required. This is inclusive.
   * @return A SortedMap where keys are LocalDate objects representing the end of each month.
   */

  Map<LocalDate, BigDecimal> fetchValuesForPeriod
          (String identifier, LocalDate startDate, LocalDate endDate);

  /**
   * Plots a performance chart for a given stock or portfolio over a specified time frame.
   * The chart displays the value changes over time, represented by asterisks, where
   * the scale and number of asterisks are dynamically calculated to fit the data range.
   *
   * @param identifier The stock symbol or portfolio name to plot.
   * @param startDate The start date of the period to plot.
   * @param endDate The end date of the period to plot.
   * @throws Exception If any error occurs during the plotting process.
   */
  void plotPerformanceChart(String identifier, LocalDate startDate, LocalDate endDate);
}
