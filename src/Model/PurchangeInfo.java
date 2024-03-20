package model;
import java.math.BigDecimal;

public class PurchangeInfo {

  /**
   * The quantity of the stock. Purchase price of the stock.
   */
  private int quantity;
  private final BigDecimal purchasePrice;

  /**
   * Constructor for the PurchangeInfo class.
   *
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   */
  public PurchangeInfo(int quantity, BigDecimal purchasePrice) {
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }
}
