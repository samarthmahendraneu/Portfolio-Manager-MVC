package controller;

import model.service.StockService;
import view.GUIViewU;
import view.UnifiedViewInterface;

/**
 * Main class for the Portfolio Management System.
 */
public class Main {

  /**
   * Main method for the Portfolio Management System.
   *
   * @param args The command line arguments.
    */
  public static void main(String[] args) {
    PortfolioControllerInterface portfolioController = new PortfolioController(
        new StockService("FIR1DN0VB7SQ4SGD"));
    UnifiedViewInterface view = new GUIViewU();
    PortfolioMenuControllerInterface controller = new PortfolioMenuController(portfolioController,
        view);

    // interaction with the user
    // [view] <- [PortfolioMenuController] -> [PortfolioController] -> [PortfolioService] -> [model]
    //controller.displayMainMenu();

  }


}
