package controller;

/**
 * Interface that controls the Portfolio Menu with view.
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
   * @param type The type of the portfolio to save
   */
  void savePortfolio(String type);

  /**
   * Load the portfolio.
   * @param type The type of the portfolio to load
   */
  void loadPortfolio(String type);

  /**
   * Load the stock cache.
   */
  void loadStockCache();

  /**
   * Prompts the user to enter a stock symbol and a specific date, then inspects and reports the
   * stock's performance for that day. It calculates the difference between the closing and opening
   * prices to determine whether the stock gained, lost, or remained unchanged, along with the
   * magnitude of change. The method interacts with the user through the view to gather input and
   * display the outcome. The performance change is represented as "Gained by [amount]", "Lost by
   * [amount]", or "Unchanged", where [amount] signifies the difference between closing and opening
   * prices.
   */
  void inspectStockPerformance();

  /**
   * Prompts the user to enter a stock symbol, start and end dates, and the number of days for
   * calculating the moving average. This method computes the moving average of the stock's closing
   * prices over the specified period and displays the result. The x-day moving average is a
   * technical indicator that smooths out price data by creating a constantly updated average price
   * over x days. It serves as a tool to identify the direction of the stock's trend. The method
   * uses the view to interact with the user for inputs and to display the computed moving average.
   * It handles validation of user inputs and calculation errors gracefully, providing appropriate
   * feedback through the view.
   */
  void computeStockMovingAverage();


  /**
   * adding a stock to the portfolio.
   */
  void addStockToPortfolio();


}
