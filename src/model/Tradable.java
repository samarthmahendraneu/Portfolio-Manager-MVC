package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import model.service.StockServiceInterface;
import model.transactions.TranactionInfo;

/**
 * Interface for the Tradable class.
 */
public interface Tradable {

  /**
   * Getter for the symbol of the tradable asset.
   *
   * @return The symbol of the tradable asset.
   */
  String getSymbol();

  /**
   * Getter for the quantity of the tradable asset.
   *
   * @return The quantity of the tradable asset.
   */
  float getQuantity();

  /**
   * gets quantity of the tradable asset on a given date.
   *
   * @param date The date to get the quantity for.
   * @return The quantity of the tradable asset on the given date.
   */
  float getQuantity(LocalDate date);


  /**
   * Updates the quantity of the tradable asset.
   *
   * @param quantity     The new quantity of the tradable asset.
   * @param date         The date of the update.
   * @param sellingPrice The price of the sale.
   */
  void sell(float quantity, LocalDate date, BigDecimal sellingPrice);

  /**
   * Buy more of the tradable asset.
   *
   * @param quantity      The quantity to buy.
   * @param date          The date of the purchase.
   * @param purchasePrice The price of the purchase.
   */
  void buy(float quantity, LocalDate date, BigDecimal purchasePrice);

  /**
   * Calculate Money Invested in the tradable asset.
   *
   * @param date The date to calculate the investment for.
   * @return The total investment in the tradable asset.
   */
  BigDecimal calculateInvestment(LocalDate date);


  /**
   * Calculate the value of the tradable asset on a given date.
   *
   * @param stockService The stock service to use to get the stock price.
   * @param date         The date to calculate the value for.
   * @return The value of the tradable asset on the given date.
   */
  BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date);

  /**
   * Getter for activity of the tradable asset.
   *
   * @return The activity log of the tradable asset.
   */
  Map<LocalDate, TranactionInfo> getActivityLog();

  /**
   * to string method for the tradable asset.
   */
  String toString();


}
