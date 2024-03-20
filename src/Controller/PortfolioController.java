package Controller;

import Model.Service.PortfolioService;
import Model.Service.PortfolioServiceInterface;
import Model.Portfolio;
import Model.Service.StockServiceInterface;
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
    return this.portfolioService.createNewPortfolio(name);
  }

  /**
   * Adds a stock to the given portfolio with the given symbol, quantity, and date.
   *
   * @param portfolio The portfolio to which the stock will be added.
   * @param symbol    The symbol of the stock to be added.
   * @param quantity  The quantity of the stock to be added.
   * @param date      The date on which the stock was purchased.
   */
  public Payload addStockToPortfolio(Portfolio portfolio, String symbol, int quantity,
      LocalDate date) {
    return new Payload(null,
        this.portfolioService.addStockToPortfolio(portfolio.getName(), symbol, quantity, date));
  }

  /**
   * Calculates the value of the portfolio with the given name on the given date.
   *
   * @param name   The name of the portfolio for which the value will be calculated.
   * @param onDate The date on which the value of the portfolio will be calculated.
   * @return The value of the portfolio on the given date.
   */
  public Payload calculatePortfolioValue(String name, LocalDate onDate) {
    return this.portfolioService.calculatePortfolioValue(name, onDate);
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
      Payload payload = new Payload(null, this.portfolioService.loadPortfoliosFromCSV(filePath));
      return payload;
    } catch (Exception e) {
      return new Payload(null, e.getMessage());
    }
  }

  /**
   * get number of portfolios
   */
  public int getNumPortfolios() {
    return this.portfolioService.getNumberOfPortfolios();
  }


  public void GenGraph(String identifier, LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
    try {
      this.portfolioService.plotPerformanceChart(identifier,startDate,endDate);
    } catch (Exception e) {

      System.out.println(e.getMessage());   }
  }

}
