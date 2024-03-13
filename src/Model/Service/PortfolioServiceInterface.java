package Model.Service;

import Controller.Payload;
import Model.Portfolio;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface for the PortfolioService class.
 */
public interface PortfolioServiceInterface {

  /**
   * Adds a portfolio to the list of portfolios.
   *
   * @param portfolio The portfolio to add.
   */
  void addPortfolio(Portfolio portfolio);

  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolioName The name of the portfolio to which the stock will be added.
   * @param symbol        The symbol of the stock to be added.
   * @param quantity      The quantity of the stock to be added.
   * @param date          The date on which the stock was purchased.
   */
  String addStockToPortfolio(String portfolioName, String symbol, int quantity, LocalDate date);

  /**
   * Fetches a portfolio by its name.
   *
   * @param name The name of the portfolio to fetch.
   * @return An Optional containing the portfolio if found, or an empty Optional otherwise.
   */
  Optional<Portfolio> getPortfolioByName(String name);

  /**
   * Calculates the total value of a portfolio on a given date.
   *
   * @param portfolioName The name of the portfolio.
   * @param onDate        The date for which the value is to be calculated.
   * @return The total value of the portfolio on the given date.
   */
  Payload calculatePortfolioValue(String portfolioName, LocalDate onDate);

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
   */
  void savePortfoliosToCSV(String filePath) throws IOException;

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @throws IOException If an error occurs while reading from the file.
   */
  void loadPortfoliosFromCSV(String filePath) throws IOException;

  /**
   * Checks if a portfolio with the given name exists.
   *
   * @param portfolioName The name of the portfolio to check.
   * @return True if a portfolio with the given name exists, false otherwise.
   */
  boolean portfolioExists(String portfolioName);
}
