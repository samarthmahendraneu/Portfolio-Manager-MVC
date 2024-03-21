package Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import Model.Service.StockServiceInterface;

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

  /**
   * Updates the quantity of the tradable asset.
   *
   * @param quantity The new quantity of the tradable asset.
   * @param date The date of the update.
   */
  void sell(int quantity, LocalDate date, BigDecimal sellingPrice);

  /**
   * Buy more of the tradable asset.
   */
  void buy(int quantity, LocalDate date, BigDecimal purchasePrice);

  /**
   * Calculate Money Invested in the tradable asset.
   */
  BigDecimal calculateInvestment();


  /**
   * Calculate the value of the tradable asset on a given date.
   */
  BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date);



}
