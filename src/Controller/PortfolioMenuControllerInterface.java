package Controller;

/**
 * Interface to represent the view for the portfolio.
 */
public interface PortfolioMenuControllerInterface {

  /**
   * Display the main menu.
   */
  void displayMainMenu();

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

}
