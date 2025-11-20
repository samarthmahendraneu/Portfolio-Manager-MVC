package model.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.PortfolioInterface;
import model.Tradable;

/**
 * Interface for the PortfolioService class which provides methods to manage portfolios and stocks.
 */
public interface PortfolioServiceInterface {

  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The name of the new portfolio.
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
   * Sells a stock from a portfolio with the given symbol, quantity, and date.
   *
   * @param portfolioName The name of the portfolio from which the stock will be sold.
   * @param stockSymbol   The symbol of the stock to be sold.
   * @param quantity      The quantity of the stock to be sold.
   * @param date          The date on which the stock was sold.
   * @return True if the stock was successfully sold, false otherwise.
   */
  Boolean sellStockFromPortfolio(String portfolioName, String stockSymbol, int quantity,
      LocalDate date);

  /**
   * Calculates the total investment in a portfolio by a given date.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the investment is to be calculated.
   * @return The total investment in the portfolio on the given date.
   */
  Optional<BigDecimal> calculatePortfolioInvestment(String portfolioName, LocalDate onDate);

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
   * @param type     The type of the portfolio to save.
   * @throws IOException If an error occurs while writing to the file.
   */
  void savePortfoliosToCSV(String filePath, String type) throws IOException;

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @param type     The type of the portfolio to load.
   * @return The file path from which the portfolios were loaded.
   * @throws IOException If an error occurs while reading from the file.
   */
  String loadPortfoliosFromCSV(String filePath, String type) throws IOException;

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
   * Retrieves the closing prices of a specified stock symbol over a given period, adjusted to
   * monthly values. This method delegates to fetchMonthlyClosingPricesForPeriod from the
   * StockService to obtain data, which is then returned in a sorted map for easy consumption.
   *
   * @param identifier The stock symbol for which closing prices are to be fetched.
   * @param startDate  The start date of the period for which data is required. This is inclusive.
   * @param endDate    The end date of the period for which data is required. This is inclusive.
   * @return A SortedMap where keys are LocalDate objects representing the end of each month.
   */

  Map<LocalDate, BigDecimal> fetchValuesForPeriod(
      String identifier, LocalDate startDate, LocalDate endDate);

  /**
   * Plots a performance chart for a given stock or portfolio over a specified time frame. The chart
   * displays the value changes over time, represented by asterisks, where the scale and number of
   * asterisks are dynamically calculated to fit the data range.
   *
   * @param identifier The stock symbol or portfolio name to plot.
   * @param startDate  The start date of the period to plot.
   * @param endDate    The end date of the period to plot.
   */
  StringBuilder plotPerformanceChart(String identifier, LocalDate startDate, LocalDate endDate);

  /**
   * Plots a performance chart for a given stock or portfolio over a specified time frame. The
   * chart
   *
   * @param identifier The stock symbol or portfolio name to plot.
   * @param startDate  The start date of the period to plot.
   * @param endDate    The end date of the period to plot.
   * @return A map of LocalDate to BigDecimal representing the value of the stock or portfolio on
   */
  Map<LocalDate, BigDecimal> plotPerformanceChartGUI(String identifier, LocalDate startDate,
      LocalDate endDate);

  /**
   * Value based investment.
   * @param portfolioName The name of the portfolio to invest in.
   * @param investmentAmount The amount to invest.
   * @param startDate The start date of the investment period.
   * @param stockWeights A map of stock symbols to their respective weights.
   */
  void valueBasedInvestment(String portfolioName, BigDecimal investmentAmount,
      LocalDate startDate, Map<String, Float> stockWeights);

  /**
   * Invests in a portfolio using the dollar cost averaging strategy.
   *
   * @param portfolioName The name of the portfolio to invest in.
   * @param amount        The amount to invest.
   * @param startDate     The start date of the investment period.
   * @param endDate       The end date of the investment period.
   * @param frequency     The frequency of investment.
   */
  void dollarCostAveraging(String portfolioName, BigDecimal amount, LocalDate startDate,
      LocalDate endDate, int frequency, Map<String, Float> stockWeights);

  /**
   * Examine the details of a portfolio on a given date.
   *
   * @param portfolioName The name of the portfolio to examine.
   * @param date          The date to examine the portfolio on.
   * @return A list of Tradable objects representing the stocks in the portfolio.
   */
  List<Tradable> examinePortfolioDetails(String portfolioName, LocalDate date);

}
