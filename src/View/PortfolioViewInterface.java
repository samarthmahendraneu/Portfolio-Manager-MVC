package View;

import Controller.Payload;

/**
 * Interface to represent the view for the portfolio.
 */
public interface PortfolioViewInterface {

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
