package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a portfolio of stocks.
 */
public class Portfolio implements PortfolioInterface {

  private final String name;
  private List<Tradable> stocks = new ArrayList<>();

  /**
   * Constructor for the Portfolio class.
   *
   * @param name The name of the portfolio.
   */
  public Portfolio(String name) {
    this.name = name;
  }

  public Portfolio(String name, List<Stock> initialStocks) {
    this.name = name;
    this.stocks = new ArrayList<>(initialStocks);
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @param stock The stock to add.
   */
  public void addStock(Tradable stock) {
    stocks.add(stock);
  }

  /**
   * getter for the stocks in the portfolio.
   */
  public List<Tradable> getStocks() {
    return new ArrayList<>(stocks); // Return a copy to protect internal list
  }

  /**
   * Getter for the name of the portfolio.
   *
   * @return The name of the portfolio.
   */
  public String getName() {
    return name;
  }
}
