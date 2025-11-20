package model.transactions;

import java.math.BigDecimal;

/**
 * Interface for the TransactionInfo class that has methods to get the quantity and price of a
 * stock.
 */
public interface TranactionInfo {


  /**
   * Get the quantity of the stock.
   *
   * @return The quantity of the stock.
   */
  float getQuantity();

  /**
   * Get the purchase price of the stock.
   *
   * @return The purchase price of the stock.
   */
  BigDecimal getPrice();

}
