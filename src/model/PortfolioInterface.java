package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.service.StockServiceInterface;

/**
 * Interface for the Portfolio class that has methods to add and remove stocks from the portfolio.
 */
public interface PortfolioInterface {

  /**
   * Adds a stock to the portfolio.
   *
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   * @param purchaseDate  The date on which the stock was purchased.
   */
  void addStock(String symbol, int quantity, BigDecimal purchasePrice, LocalDate purchaseDate);

  /**
   * getter for the stocks in the portfolio.
   *
   * @return The stocks in the portfolio.
   */
  List<Tradable> getStocks();

  /**
   * Getter for the name of the portfolio.
   *
   * @return The name of the portfolio.
   */
  String getName();

  /**
   * Sell a stock from the portfolio.
   *
   * @param stock        The stock to remove.
   * @param quantity     The quantity of the stock to remove.
   * @param date         The date of the sale.
   * @param sellingPrice The selling price of the stock.
   * @throws IllegalArgumentException if the stock is not in the portfolio.
   */
  void sellStock(String stock, int quantity, LocalDate date, BigDecimal sellingPrice)
      throws IllegalArgumentException;

  /**
   * calculates the total value of the portfolio on a given date.
   *
   * @param stockService The stock service to use.
   * @param date         The date on which to calculate the value.
   * @return The total value of the portfolio on a given date.
   */
  BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date);

  /**
   * Calculate investment in the portfolio.
   *
   * @param date The date to calculate the investment for.
   * @return The total investment in the portfolio.
   */
  BigDecimal calculateInvestment(LocalDate date);

  /**
   * gets the quantity of a stock in the portfolio.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date of the stock.
   * @return The quantity of the stock.
   */
  float getStockQuantity(String symbol, LocalDate date);

  /**
   * Function for Dollar cost averaging strategy.
   *
   * @param amount       amount to invest
   * @param startDate    start date
   * @param endDate      end date
   * @param stockService stock service
   * @param frequency    frequency
   */
  void dollarCostAveraging(BigDecimal amount, LocalDate startDate, LocalDate endDate,
      StockServiceInterface stockService, int frequency, Map<String, Float> stockWeights);

  /**
   * Gets the portfolio on a given date.
   *
   * @param date The date to get the portfolio for.
   * @return The portfolio on the given date with the stocks and their quantities.
   */
  List<Tradable> getPortfolio(LocalDate date);

  /**
   * Invests in a portfolio using the given stock weights.
   *
   * @param investmentAmount The amount to invest.
   * @param date             The  date of the investment.
   * @param stockWeights     A map of stock symbols to their respective weights.
   * @param stockService     The stock service to use.
   */
  void investUsingWeights(Float investmentAmount, LocalDate date,
      StockServiceInterface stockService, Map<String, Float> stockWeights);

}
