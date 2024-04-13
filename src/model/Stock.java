package model;

import model.transactions.PurchangeInfo;
import model.transactions.SaleInfo;
import model.transactions.TranactionInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import model.service.StockServiceInterface;

/**
 * Class to represent a stock in a portfolio.
 */
public class Stock implements Tradable {

  private final String symbol;
  private float quantity;
  private final Map<LocalDate, TranactionInfo> Activity = new HashMap<>();
  private String string;


  /**
   * Constructor for the Stock class.
   *
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   * @param purchaseDate  The date on which the stock was purchased.
   */
  public Stock(String symbol, float quantity, BigDecimal purchasePrice, LocalDate purchaseDate) {
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
  public float getQuantity() {
    return quantity;
  }

  /**
   * get total quantity of the stock on a given date using activity log.
   *
   * @param date date to calculate the quantity
   * @return total quantity of the stock
   */
  public float getQuantity(LocalDate date) {
    float totalQuantity = 0;

    for (Map.Entry<LocalDate, TranactionInfo> entry : this.Activity.entrySet()) {
      if (entry.getKey().isBefore(date) || entry.getKey().isEqual(date)) {
        totalQuantity = totalQuantity + entry.getValue().getQuantity();
      }
    }

    return totalQuantity;
  }


  /**
   * Updates the stock and add stock history.
   *
   * @param quantity     The new quantity of the stock.
   * @param date         The date of the update.
   * @param sellingPrice The price of the sale.
   */
  public void sell(float quantity, LocalDate date, BigDecimal sellingPrice) {
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
   *
   * @param quantity      quantity of the stock to buy
   * @param date          date of the purchase
   * @param purchasePrice price of the stock on the date
   * @throws IllegalArgumentException if quantity is negative
   */
  public void buy(float quantity, LocalDate date, BigDecimal purchasePrice) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative");
    }
    this.quantity += quantity;
    PurchangeInfo purchaseInfo = new PurchangeInfo(quantity, purchasePrice);
    this.Activity.put(date, purchaseInfo);
  }

  /**
   * Get money invested in this stock from the purchase history on a given date.
   *
   * @param date date to calculate the investment
   */
  public BigDecimal calculateInvestment(LocalDate date) {
    return Activity.entrySet().stream()
        .filter(entry -> entry.getKey().isBefore(date))
        .map(entry -> entry.getValue().getPrice()
            .multiply(BigDecimal.valueOf(entry.getValue().getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

  }

  /**
   * Calculate the value of the tradable asset on a given date.
   *
   * @param stockService The stock service to use to fetch stock prices.
   * @param date         The date on which to calculate the value.
   */
  @Override
  public BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date) {
    // calculate total purchase quantity before the date from the activity
    BigDecimal totalQuantity = BigDecimal.ZERO;

    for (Map.Entry<LocalDate, TranactionInfo> entry : this.Activity.entrySet()) {
      if (entry.getKey().isBefore(date) || entry.getKey().isEqual(date)) {
        // value of sale is  already stored as negative quantity
        totalQuantity = totalQuantity.add(new BigDecimal(entry.getValue().getQuantity()));
      }
    }

    // calculate value of the stock on the date
    return ((BigDecimal) stockService.fetchLastClosePrice(this.symbol, date).getData()).multiply(
        totalQuantity);
  }

  /**
   * getter for activity in the stock.
   *
   * @return activity log of the stock
   */
  public Map<LocalDate, TranactionInfo> getActivityLog() {
    return this.Activity;
  }

  /**
   * to string method for the stock.
   */
  @Override
  public String toString() {
    if (string == null) {
      string = "Stock{" + "symbol='" + symbol + '\'' + ", quantity=" + quantity + '}';
    }
    return string;
  }
}
