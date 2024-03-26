package Controller;

/**
 * Interface to represent the view for the portfolio.
 */
public interface PortfolioMenuControllerInterface {

  /**
   * Main menu to choose between Flexible and Normal Portfolio.
   */
  void displayMainMenu();

  /**
   * Display the main menu for Flexible Portfolio.
   */
  void displayFlexiblePortfolioMenu();

  /**
   * Display the main menu for Normal Portfolio.
   */
  void displayNormalPortfolioMenu();

  /**
   * Create a new portfolio.
   */
  void createNewPortfolio();

  /**
   * Examine a portfolio.
   */
  void examinePortfolio();

  /**
   * Calculate the portfolio value.
   */
  void calculatePortfolioValue();

  /**
   * Save the portfolio.
   */
  void savePortfolio();

  /**
   * Load the portfolio.
   */
  void loadPortfolio();

  /**
   * Load the stock cache.
   */
  void loadStockCache();


}
