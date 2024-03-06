package Model;
import java.util.List;

/**
 * class for user interface.
 * User should be able to create portfolio,  get value of portfolio
 * User can create Multiple portfolio.
 * Stocks can be added only while crearion of portfolio.
 *
 * createPortfolio()
 * listPortfolio()
 * loadPortfolio(json file)
 * savePortfolio()
 */
public interface UserInterface {

  boolean createPortfolio();

  List<Stock> getPortfolioComposition();

  List<Portfolio> loadPortfolio(String jsonFile);

  boolean savePortfolio(String jsonFile);


}
