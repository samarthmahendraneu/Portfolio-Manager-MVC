package Model.Transactions;
import java.math.BigDecimal;

public interface TranactionInfo {


  /**
   * Get the quantity of the stock.
   *
   * @return The quantity of the stock.
   */
  int getQuantity();

  /**
   * Get the purchase price of the stock.
   *
   * @return The purchase price of the stock.
   */
  BigDecimal getPrice();

}
