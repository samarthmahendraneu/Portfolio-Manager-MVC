package Model;

import java.util.List;

/**
 * Interface for the Portfolio class.
 */
public interface PortfolioInterface {

  /**
   * Adds a stock to the portfolio.
   *
   * @param stock The stock to add.
   */
  void addStock(Stock stock);

  /**
   * getter for the stocks in the portfolio
   */
  List<Stock> getStocks();

  /**
   * Getter for the name of the portfolio.
   *
   * @return The name of the portfolio.
   */
  String getName();
}
