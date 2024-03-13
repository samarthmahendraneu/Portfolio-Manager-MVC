package model.utilities;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class to represent stock information, including date, open, high, low, close, and volume.
 */
public class StockInfo {

  private final LocalDate date;
  private final BigDecimal open;
  private final BigDecimal high;
  private final BigDecimal low;
  private final BigDecimal close;
  private final long volume;

  /**
   * Constructor for the StockInfo class.
   *
   * @param date   The date of the stock information.
   * @param open   The opening price of the stock.
   * @param high   The highest price of the stock.
   * @param low    The lowest price of the stock.
   * @param close  The closing price of the stock.
   * @param volume The volume of the stock.
   */
  public StockInfo(LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low,
      BigDecimal close, long volume) {
    this.date = date;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }

  /**
   * Getter for the date of the stock information.
   *
   * @return The date of the stock information.
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Getter for the opening price of the stock.
   *
   * @return The opening price of the stock.
   */
  public BigDecimal getOpen() {
    return open;
  }

  /**
   * Getter for the highest price of the stock.
   *
   * @return The highest price of the stock.
   */
  public BigDecimal getHigh() {
    return high;
  }

  /**
   * Getter for the lowest price of the stock.
   *
   * @return The lowest price of the stock.
   */
  public BigDecimal getLow() {
    return low;
  }

  /**
   * Getter for the closing price of the stock.
   *
   * @return The closing price of the stock.
   */
  public BigDecimal getClose() {
    return close;
  }

  /**
   * Getter for the volume of the stock.
   *
   * @return The volume of the stock.
   */
  public long getVolume() {
    return volume;
  }

  /**
   * Returns a string representation of the stock information for debugging purposes.
   *
   * @return A string representation of the stock information.
   */
  @Override
  public String toString() {
    return String.format("Date: %s, Open: %s, High: %s, Low: %s, Close: %s, Volume: %d",
        date, open, high, low, close, volume);
  }
}
