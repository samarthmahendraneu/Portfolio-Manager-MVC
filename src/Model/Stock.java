package Model;

/**
 * getStockSymbol()
 * getStockPrice(Date date)
 * getStockQuantity()
 */
public class Stock implements StockInterface{


  private String stockSymbol;
  private String stockName;
  private double stockPrice;
  private int stockQuantity;
  /**
   * getStockSymbol()
   *
   * @return the stock symbol
   */
  @Override
  public String getStockSymbol() {
    return this.stockSymbol;
  }

  /**
   * getStockPrice(Date date)
   *
   * @param date the date
   * @return the stock price
   */
  @Override
  public double getStockPrice(String date) {
    // TODO integrate with API
    return 0;
  }

  /**
   * getStockQuantity()
   *
   * @return the stock quantity
   */
  @Override
  public int getStockQuantity() {
    return this.stockQuantity;
  }

  /**
   * getStockName()
   *
   * @return the stock name
   */
  @Override
  public String getStockName() {
    return this.stockName;
  }

}
