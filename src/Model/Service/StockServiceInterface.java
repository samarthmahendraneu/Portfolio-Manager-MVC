package Model.Service;

import Controller.Payload;
import Model.PortfolioInterface;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.SortedMap;
import java.math.BigDecimal;

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



   SortedMap<LocalDate, BigDecimal> fetchMonthlyClosingPricesForPeriod
          (String symbol, LocalDate startMonth, LocalDate endMonth);

   LocalDate findEarliestStockDate(PortfolioInterface portfolio);
   void loadCache(String filepath);

   void saveCache(String filepath);

}
