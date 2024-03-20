package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import model.service.StockServiceInterface;

/**
 * Interface for the Portfolio class.
 */
public interface PortfolioInterface {

  /**
   * Adds a stock to the portfolio.
   *
   * @param symbol The symbol of the stock.
   * @param quantity The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   * @param purchaseDate The date on which the stock was purchased.
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
   * @param stock The stock to remove.
   * @param quantity The quantity of the stock to remove.
   * @param date The date of the sale.
   * @throws IllegalArgumentException if the stock is not in the portfolio.
   */
  void sellStock(String stock, int quantity, LocalDate date, BigDecimal sellingPrice) throws IllegalArgumentException;

  /**
   * calculates the total value of the portfolio on a given date.
   */
  BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date);

  /**
   * Calculate investment in the portfolio.
   * @return The total investment in the portfolio.
   */
  BigDecimal calculateInvestment();
}
