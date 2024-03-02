package Model;
import java.util.List;

public class Portfolio implements PortfolioInterface {

  // list of stocks
  private List<Stock> stocks;

  /**
   * addStock(Stock stock)
   *
   * @param stock the stock
   */
  @Override
  public void addStock(Stock stock) {
    this.stocks.add(stock);
  }

  /**
   * getPortfolioValue(Date date)
   *
   * @param date the date
   * @return the portfolio value
   */
  @Override
  public double getPortfolioValue(String date) {
    float sum = 0;
    for (Stock stock : this.stocks) {
      sum += stock.getStockPrice(date);
    }
    return sum;

  }

  /**
   * getStocks()
   *
   * @return the stocks
   */
  @Override
  public Stock[] getStocks() {
    return (Stock[]) this.stocks.toArray();
  }
}
