package controller;

import model.PortfolioInterface;
import model.service.PortfolioService;
import model.service.PortfolioServiceInterface;
import model.service.StockServiceInterface;
import java.time.LocalDate;

/**
 * Controller class for the Portfolio Management System. This class provides basic functionality for
 * managing portfolios. It interacts with the PortfolioService and StockService classes to perform
 * operations on portfolios and stocks.
 */
public class PortfolioController implements PortfolioControllerInterface {

  private final PortfolioServiceInterface portfolioService;

  /**
   * Constructor for the PortfolioControllerBasic class.
   *
   * @param stockService The StockService model object to be used by the controller.
   */
  public PortfolioController(StockServiceInterface stockService) {
    this.portfolioService = new PortfolioService(stockService);
  }

  /**
   * Getter for the PortfolioService object.
   *
   * @return The PortfolioService object used by the controller.
   */
  public PortfolioServiceInterface getPortfolioService() {
    return portfolioService;
  }

  /**
   * Creates a new portfolio with the given name.
   *
   * @param name The name of the new portfolio.
   * @return The newly created Portfolio object.
   */
  public Payload createNewPortfolio(String name) {
    try {
      this.portfolioService.createNewPortfolio(name);
    } catch (IllegalArgumentException e) {
      return new Payload(null, e.getMessage());
    }
    return new Payload(null, "");
  }

  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolio The portfolio to which the stock will be added.
   * @param symbol    The symbol of the stock to be added.
   * @param quantity  The quantity of the stock to be added.
   * @param date      The date on which the stock was purchased.
   */
  public Payload addStockToPortfolio(PortfolioInterface portfolio, String symbol, int quantity,
      LocalDate date) {
    try {
      this.portfolioService.addStockToPortfolio(portfolio.getName(), symbol, quantity, date);
    } catch (IllegalArgumentException e) {
      return new Payload(null, e.getMessage());
    }
    return new Payload(null, "");
  }

  /**
   * Calculates the value of the portfolio with the given name on the given date.
   *
   * @param name   The name of the portfolio for which the value will be calculated.
   * @param onDate The date on which the value of the portfolio will be calculated.
   * @return The value of the portfolio on the given date.
   */
  public Payload calculatePortfolioValue(String name, LocalDate onDate) {
    try {
      return new Payload(this.portfolioService.calculatePortfolioValue(name, onDate), "");
    } catch (IllegalArgumentException e) {
      return new Payload(null, e.getMessage());
    }
  }

  /**
   * Saves the portfolios to a CSV file at the given file path.
   *
   * @param filePath The file path where the portfolios will be saved.
   * @throws IllegalArgumentException if there is an error saving the portfolios to the file.
   */
  public Payload savePortfolio(String filePath) throws IllegalArgumentException {
    try {
      this.portfolioService.savePortfoliosToCSV(filePath);
      return new Payload(null, "");
    } catch (Exception e) {
      return new Payload(null, e.getMessage());
    }
  }

  /**
   * Loads portfolios from a CSV file at the given file path.
   *
   * @param filePath The file path from which the portfolios will be loaded.
   * @throws IllegalArgumentException if there is an error loading the portfolios from the file.
   */
  public Payload loadPortfolio(String filePath) throws IllegalArgumentException {
    try {
      return new Payload(null, this.portfolioService.loadPortfoliosFromCSV(filePath));
    } catch (Exception e) {
      return new Payload(null, e.getMessage());
    }
  }

  /**
   * get number of portfolios.
   * @return number of portfolios
   */
  public int getNumPortfolios() {
    return this.portfolioService.getNumberOfPortfolios();
  }
}
