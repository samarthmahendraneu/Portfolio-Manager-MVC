package Model;

import Model.Transactions.PurchangeInfo;
import Model.Transactions.SaleInfo;
import Model.Transactions.TranactionInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import Model.Service.StockServiceInterface;

/**
 * Class to represent a stock in a portfolio.
 */
public class Stock implements Tradable {

  private final String symbol;
  private int quantity;
  private final Map<LocalDate, TranactionInfo> Activity = new HashMap<>();

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
    this.Activity.put(purchaseDate, new PurchangeInfo(quantity, purchasePrice));
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
   * Updates the stock and add stock history.
   */
  public void sell(int quantity, LocalDate date, BigDecimal sellingPrice) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative");
    }
    if (quantity > this.quantity) {
      throw new IllegalArgumentException("Quantity cannot be greater than the current quantity");
    }
    SaleInfo saleInfo = new SaleInfo(-quantity, sellingPrice);
    this.Activity.put(date, saleInfo);
    this.quantity = this.quantity - quantity;
  }

  /**
   * Buy extra stock and add stock history.
   */
  public void buy(int quantity, LocalDate date, BigDecimal purchasePrice) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative");
    }
    this.quantity += quantity;
    PurchangeInfo purchaseInfo = new PurchangeInfo(quantity, purchasePrice);
    this.Activity.put(date, purchaseInfo);
  }

  /**
   * Get money invested in this stock from the purchase history on a given date.
   */
  public BigDecimal calculateInvestment(LocalDate date) {
    return Activity.entrySet().stream()
        .filter(entry -> entry.getKey().isBefore(date))
        .map(entry -> entry.getValue().getPrice().multiply(BigDecimal.valueOf(entry.getValue().getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

  }

  /**
   * Calculate the value of the tradable asset on a given date.
   *
   * @param stockService
   * @param date
   */
  @Override
  public BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date) {
    // return ((BigDecimal) stockService.fetchPriceOnDate(this.symbol, date).getData()).multiply(new BigDecimal(this.quantity));

    // calculate total purchase quantity before the date from the activity
    BigDecimal totalQuantity = BigDecimal.ZERO;

    for(Map.Entry<LocalDate, TranactionInfo> entry : this.Activity.entrySet()) {
      if(entry.getKey().isBefore(date) || entry.getKey().isEqual(date)) {
        if (entry.getValue() instanceof PurchangeInfo) {
          totalQuantity = totalQuantity.add(new BigDecimal(entry.getValue().getQuantity()));
        } else {
          // value of sale is  already stored as negative quantity
          totalQuantity = totalQuantity.add(new BigDecimal(entry.getValue().getQuantity()));
        }
      }
    }

    // calculate value of the stock on the date
    return ((BigDecimal) stockService.fetchPriceOnDate(this.symbol, date).getData()).multiply(totalQuantity);
  }

  /**
   * get total quantity of the stock on a given date using activity log.
   */
  public int getQuantity(LocalDate date) {
    int totalQuantity = 0;

    for (Map.Entry<LocalDate, TranactionInfo> entry : this.Activity.entrySet()) {
      if (entry.getKey().isBefore(date) || entry.getKey().isEqual(date)) {
          totalQuantity = totalQuantity + entry.getValue().getQuantity();
      }
    }

    return totalQuantity;
  }

  /**
   * getter for activity in the stock.
   */
  public Map<LocalDate, TranactionInfo> getActivityLog() {
    return this.Activity;
  }

}
