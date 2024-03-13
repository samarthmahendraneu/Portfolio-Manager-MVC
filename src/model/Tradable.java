package model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
  int getQuantity();

  /**
   * Getter for the purchase price of the tradable asset.
   *
   * @return The purchase price of the tradable asset.
   */
  BigDecimal getPurchasePrice();

  /**
   * Getter for the purchase date of the tradable asset.
   *
   * @return The purchase date of the tradable asset.
   */
  LocalDate getPurchaseDate();
}
