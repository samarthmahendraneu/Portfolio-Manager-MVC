package Model;

/**
 * class for portfolio interface.
 * addStock(Stock stock)
 * getPortfolioValue(Date date)
 */
public interface PortfolioInterface {

  /**
   * addStock(Stock stock)
   * @param stock the stock
   */
  void addStock(Stock stock);

  /**
   * getPortfolioValue(Date)
   * @param date the date
   * @return the portfolio value
   */
  double getPortfolioValue(String date);


  /**
   * getStocks()
   * @return the stocks
   */
  Stock[] getStocks();

}
