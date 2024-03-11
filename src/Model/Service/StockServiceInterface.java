package Model.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockServiceInterface {
  BigDecimal fetchPriceOnDate(String symbol, LocalDate date);

  BigDecimal fetchPreviousClosePrice(String symbol, LocalDate date);
}
