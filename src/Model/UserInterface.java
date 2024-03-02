package Model;
import java.util.List;
import Model.Stock;

/**
 * class for user interface.
 * User should be able to create portfolio,  get value of portfolio
 * User can create Multiple portfolio.
 * Stocks can be added only while crearion of portfolio.
 */
public interface UserInterface {

  boolean createPortfolio();

  float getPortfolioValue();

  boolean addStockToPortfolio();

  boolean removeStockFromPortfolio();

  List<Stock> getPortfolioComposition();


}
