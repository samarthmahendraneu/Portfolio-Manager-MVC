package Model;

/**
 * /**
 *  * getStockSymbol()
 *  * getStockPrice(Date date)
 *  * getStockQuantity()
 * */
public interface StockInterface {


  /**
   * getStockSymbol()
   * @return the stock symbol
   */
  String getStockSymbol();

  /**
   * getStockPrice(Date date)
   * @param date the date
   * @return the stock price
   */
  double getStockPrice(String date);

  /**
   * getStockQuantity()
   * @return the stock quantity
   */
  int getStockQuantity();

  /**
   * getStockName()
   * @return the stock name
   */
  String getStockName();



}
