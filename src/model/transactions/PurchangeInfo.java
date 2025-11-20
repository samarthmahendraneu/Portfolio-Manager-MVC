package model.transactions;

import java.math.BigDecimal;

/**
 * Class to represent the purchase of a stock.
 */
public class PurchangeInfo implements TranactionInfo {

  private final float quantity;
  private final BigDecimal purchasePrice;

  /**
   * Constructor for the PurchangeInfo class.
   *
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   */
  public PurchangeInfo(float quantity, BigDecimal purchasePrice) {
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

  /**
   * Getter for the quantity of the stock.
   *
   * @return The quantity of the stock.
   */
  public float getQuantity() {
    return quantity;
  }

  /**
   * Get the purchase price of the stock.
   *
   * @return The purchase price of the stock.
   */
  public BigDecimal getPrice() {
    return purchasePrice;
  }
}
