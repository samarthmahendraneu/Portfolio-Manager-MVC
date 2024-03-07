package Utilities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockInfo {
  private LocalDate date;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private long volume;

  public StockInfo(LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, long volume) {
    this.date = date;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
  }

  // Getters
  public LocalDate getDate() { return date; }
  public BigDecimal getOpen() { return open; }
  public BigDecimal getHigh() { return high; }
  public BigDecimal getLow() { return low; }
  public BigDecimal getClose() { return close; }
  public long getVolume() { return volume; }

  // You might add toString() for easy logging/debugging
  @Override
  public String toString() {
    return String.format("Date: %s, Open: %s, High: %s, Low: %s, Close: %s, Volume: %d",
            date, open, high, low, close, volume);
  }
}
