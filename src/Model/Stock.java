package Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class to represent a stock in a portfolio.
 */
public class Stock implements Tradable {

  private final String symbol;
  private final int quantity;
  private final BigDecimal purchasePrice;
  private final LocalDate purchaseDate;

  /**
   * Constructor for the Stock class.
   *
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   * @param purchaseDate  The date on which the stock was purchased.
   */
  public Stock(String symbol, int quantity, BigDecimal purchasePrice, LocalDate purchaseDate) {
    this.symbol = symbol;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
    this.purchaseDate = purchaseDate;
  }

  /**
   * Getter for the symbol of the stock.
   *
   * @return The symbol of the stock.
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Getter for the quantity of the stock.
   *
   * @return The quantity of the stock.
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Getter for the purchase price of the stock.
   *
   * @return The purchase price of the stock.
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  /**
   * Getter for the purchase date of the stock.
   *
   * @return The purchase date of the stock.
   */
  public LocalDate getPurchaseDate() {
    return purchaseDate;
  }

  // Setters, toString(), equals(), and hashCode() methods omitted for brevity
}
