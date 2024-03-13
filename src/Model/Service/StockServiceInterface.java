package Model.Service;

import Controller.Payload;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface for the StockService class.
 */
public interface StockServiceInterface {

  /**
   * Fetches the price of a stock with the given symbol on the given date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date for which the price is to be fetched.
   * @return The price of the stock on the given date.
   */
  Payload fetchPriceOnDate(String symbol, LocalDate date);


  /**
   * Fetches the closing price of the stock with the given symbol on the previous trading day.
   *
   * @param symbol The symbol of the stock to fetch.
   * @param date   The date for which to fetch the previous close price.
   * @return The closing price of the stock on the previous trading day.
   */
  Payload fetchPreviousClosePrice(String symbol, LocalDate date);
}
