package Model;

import Model.Transactions.PurchangeInfo;
import Model.Transactions.SaleInfo;
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
  private final BigDecimal purchasePrice;
  private final LocalDate purchaseDate;

  private BigDecimal moneyInvested = BigDecimal.ZERO;

  private final Map<LocalDate, PurchangeInfo> purchaseHistory = new HashMap<>();

  private final Map<LocalDate, SaleInfo> saleHistory = new HashMap<>();

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
    this.purchasePrice = purchasePrice;
    this.purchaseDate = purchaseDate;
    this.purchaseHistory.put(purchaseDate, new PurchangeInfo(quantity, purchasePrice));
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
   * Getter for the purchase price of the stock.
   *
   * @return The purchase price of the stock.
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  /**
   * Getter for the purchase date of the stock.
   *
   * @return The purchase date of the stock.
   */
  public LocalDate getPurchaseDate() {
    return purchaseDate;
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
    this.quantity = quantity;
    SaleInfo saleInfo = new SaleInfo(quantity, sellingPrice);
    this.saleHistory.put(date, saleInfo);
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
    this.purchaseHistory.put(date, purchaseInfo);
  }

  /**
   * Get money invested in this stock from the purchase history on a given date.
   */
  public BigDecimal calculateInvestment(LocalDate date) {
    return purchaseHistory.entrySet().stream().filter(entry -> entry.getKey().isBefore(date))
            .map(entry -> entry.getValue().getPrice())
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
    return ((BigDecimal) stockService.fetchPriceOnDate(this.symbol, date).getData()).multiply(new BigDecimal(this.quantity));
  }

  // Setters, toString(), equals(), and hashCode() methods omitted for brevity
}
