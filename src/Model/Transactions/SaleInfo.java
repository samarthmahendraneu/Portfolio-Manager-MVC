package Model.Transactions;

import java.math.BigDecimal;

public class SaleInfo {

  /**
   * The quantity of the stock. Sell price of the stock.
   */
  private int quantity;
  private final BigDecimal sellPrice;

  /**
   * Constructor for the SaleInfo class.
   *
   * @param quantity  The quantity of the stock.
   * @param sellPrice The sell price of the stock.
   */
  public SaleInfo(int quantity, BigDecimal sellPrice) {
    this.quantity = quantity;
    this.sellPrice = sellPrice;

  }

}