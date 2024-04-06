package Controller;

import Model.Service.StockService;
import View.View;

/**
 * Main class for the Portfolio Management System.
 */
public class Main {

  /**
   * Main method for the Portfolio Management System.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    PortfolioControllerInterface portfolioController = new PortfolioController(
        new StockService("FIR1DN0VB7SQ4SGD"));
    View view = new View();
    PortfolioMenuControllerInterface controller = new PortfolioMenuController(portfolioController,
        view);
    // interaction with the user
    // [View] <- [PortfolioMenuController] -> [PortfolioController] -> [PortfolioService] -> [Model]
    controller.displayMainMenu();

  }
}
