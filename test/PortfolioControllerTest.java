import static org.junit.Assert.assertEquals;


import Model.Service.PortfolioService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import Controller.PortfolioController;
import Model.Service.StockService;
import Model.Portfolio;
import Model.Stock;

public class PortfolioControllerTest {

  private PortfolioService portfolioService;
  private StockService stockService;
  private PortfolioController portfolioController;

  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioService = new PortfolioService(stockService);
    portfolioController = new PortfolioController(stockService);
  }

  //Portfolio Creation
  @Test
  public void testCreateNewPortfolio() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    assertEquals("Test Portfolio", portfolio.getName());
  }

  // empty portfolio name test
  @Test(expected = Exception.class)
  public void testCreateNewPortfolio_EmptyName() {
    Portfolio portfolio = portfolioController.createNewPortfolio("");
  }

  // duplicate portfolio name test
  @Test(expected = Exception.class)
  public void testCreateNewPortfolio_DuplicateName() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio2 = portfolioController.createNewPortfolio("Test Portfolio");
  }

  // //Create a portfolio with a valid set of stocks and shares.
  @Test
  public void testAddStockToPortfolio() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals(1, portfolio.getStocks().size());
  }

  // add invalid date test
  @Test
  public void testAddStockToPortfolio_InvalidDate() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now().plusDays(1));
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // quantity less than 1 test
  @Test
  public void testAddStockToPortfolio_InvalidQuantity() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 0, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // create multiple portfolios and add stocks to them
  @Test
  public void testCreateMultiplePortfoliosAndAddStocks() {
    Portfolio portfolio1 = portfolioController.createNewPortfolio("Test Portfolio 1");
    Portfolio portfolio2 = portfolioController.createNewPortfolio("Test Portfolio 2");
    portfolioController.addStockToPortfolio(portfolio1, "AAPL", 10, LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio1, "GOOGL", 5, LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio2, "AAPL", 10, LocalDate.now());
    assertEquals(2, portfolioController.getNumPortfolios());
    Portfolio portfolio3 = portfolioController.getPortfolioService().getPortfolioByName("Test Portfolio 1").get();
    assertEquals(2, portfolio3.getStocks().size());
    Portfolio portfolio4 = portfolioController.getPortfolioService().getPortfolioByName("Test Portfolio 2").get();
    assertEquals(1, portfolio4.getStocks().size());
  }

  // calculate portfolio value test on same day
  @Test
  public void testCalculatePortfolioValue() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-06"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-06"));
    assertEquals(total, value);
  }

  // calculate portfolio value test on future date
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-07"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-07"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-07"));
    assertEquals(total, value);
  }

  // examine value on sunday February 4 should be same as February 2
  @Test
  public void testCalculatePortfolioValue_Sunday() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-04"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-04"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-02"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-02"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-04"));
    assertEquals(total, value);
  }

  // examine, add 3 stocks and examine portfolio and examine before buying the last stock
  @Test
  public void testExaminePortfolio() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-09"));
    portfolioController.addStockToPortfolio(portfolio, "MSFT", 5, LocalDate.parse("2024-02-11"));

    // total value as of February 9
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-09"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-09"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-09"));
    assertEquals(total, value);

    // value as of February 11
    BigDecimal stock_price_3 = stockService.fetchPreviousClosePrice("MSFT", LocalDate.parse("2024-02-11"));
    total = total.add(stock_price_3.multiply(new BigDecimal(5)));
    value = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-11"));
    assertEquals(total, value);
  }


  //Attempt to create a portfolio with invalid or non-existent stock tickers.
  @Test
  public void testAddInvalidStockToPortfolio() {
    Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    portfolioController.addStockToPortfolio(portfolio, "INVALID", 10, LocalDate.now());
    // prints "Invalid stock symbol: INVALID"


  }

}



// Functional Test Cases

//Attempt to create a portfolio with negative or zero shares.
//Create multiple portfolios with different sets of stocks and shares.
//Portfolio Composition Examination
//Examine the composition of a newly created portfolio.
//Examine the composition of a portfolio after the market has closed.
//Examine the composition of an empty portfolio (if allowed by the program).
//Portfolio Value Determination
//Determine the total value of a portfolio on a current date with market data available.
//Determine the total value of a portfolio on a past date with historical market data.
//Determine the total value of a portfolio on a future date (should not be possible or should return an error).
//Portfolio Persistence
//Save a portfolio to a file and ensure the data is correctly written in human-readable format.
//Load a portfolio from a file and ensure the data is correctly read and the portfolio is accurately reconstructed.
//Attempt to load a portfolio from a corrupted or incorrectly formatted file.
//User Interface/Interactivity
//Interact with the program using valid commands and inputs.
//Test the program's response to invalid commands or inputs (e.g., typos, incorrect data types).
//Ensure that the program does not crash upon receiving unexpected input.