import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import Controller.Payload;
import Model.Service.PortfolioService;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.IntStream;

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
    Object payload  = portfolioController.createNewPortfolio("Test Portfolio");
    // convertv to payload
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    assertEquals("Test Portfolio", portfolio.getName());
  }

  // empty portfolio name test
  @Test
  public void testCreateNewPortfolio_EmptyName() {
    Object payload = portfolioController.createNewPortfolio("");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    assertEquals("Portfolio name cannot be empty", ((Payload) payload).getMessage());
  }

  // duplicate portfolio name test
  @Test
  public void testCreateNewPortfolio_DuplicateName() {
    Object payload1 = portfolioController.createNewPortfolio("Test Portfolio");
    Object payload2 = portfolioController.createNewPortfolio("Test Portfolio");
    assertEquals("Portfolio already exists: Test Portfolio", ((Payload) payload2).getMessage());
  }

  // //Create a portfolio with a valid set of stocks and shares.
  @Test
  public void testAddStockToPortfolio() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();

    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals(1, portfolio.getStocks().size());
  }

  // add invalid date test
  @Test
  public void testAddStockToPortfolio_InvalidDate() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now().plusDays(1));
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // quantity less than 1 test
  @Test
  public void testAddStockToPortfolio_InvalidQuantity() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    Stock stock = new Stock("AAPL", 0, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // create multiple portfolios and add stocks to them
  @Test
  public void testCreateMultiplePortfoliosAndAddStocks() {
    Object payload1 = portfolioController.createNewPortfolio("Test Portfolio 1");
    Portfolio portfolio1 = (Portfolio) ((Payload) payload1).getData();
    Object payload2 = portfolioController.createNewPortfolio("Test Portfolio 2");
    Portfolio portfolio2 = (Portfolio) ((Payload) payload2).getData();
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
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-06"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10)).add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-06"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());
  }

  // calculate portfolio value test on future date
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-07"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-07"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10)).add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-07"));

    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());
  }

  // examine value on sunday February 4 should be same as February 2
  @Test
  public void testCalculatePortfolioValue_Sunday() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-04"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-04"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-02"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-02"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10)).add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-04"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());
  }

  // examine, add 3 stocks and examine portfolio and examine before buying the last stock
  @Test
  public void testExaminePortfolio() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-09"));
    portfolioController.addStockToPortfolio(portfolio, "MSFT", 5, LocalDate.parse("2024-02-11"));

    // total value as of February 9
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-09"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-09"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10)).add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-09"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());

    // value as of February 11
    Payload stock_price_3 = stockService.fetchPreviousClosePrice("MSFT", LocalDate.parse("2024-02-11"));
    total = total.add(((BigDecimal) stock_price_3.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-11"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());
  }


  //Attempt to create a portfolio with invalid or non-existent stock tickers.
  @Test
  public void testAddInvalidStockToPortfolio() {
    // Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "INVALID", 10, LocalDate.now());
    assertEquals("Invalid stock symbol", ((Payload) payload).getMessage());


  }
  @Test
  public void testListPortfolioNames() {
    portfolioService.createNewPortfolio("FirstPortfolio");
    portfolioService.createNewPortfolio("SecondPortfolio");

    List<String> portfolioNames = portfolioService.listPortfolioNames();
    assertEquals(2, portfolioNames.size());
    assertTrue(portfolioNames.contains("FirstPortfolio"));
    assertTrue(portfolioNames.contains("SecondPortfolio"));
  }
  @Test
  public void testCalculatePortfolioValueOnFutureDate() {
    String portfolioName = "InvestmentPortfolio";
    portfolioService.createNewPortfolio(portfolioName);
    LocalDate futureDate = LocalDate.now().plusDays(10);
    Payload result = portfolioService.calculatePortfolioValue(portfolioName, futureDate);

    assertTrue(result.isError());
    assertEquals("Date cannot be in the future: " + futureDate, result.getMessage());
  }
  @Test
  public void testAddStockWithNegativeQuantity() {
    String portfolioName = "MyPortfolio";
    portfolioService.createNewPortfolio(portfolioName);
    String message = portfolioService.addStockToPortfolio(portfolioName, "AAPL", -10, LocalDate.now());

    assertEquals("Quantity must be positive: -10", message);
  }

  @Test
  public void testAddStockWithFutureDate() {
    String portfolioName = "FutureDatePortfolio";
    portfolioService.createNewPortfolio(portfolioName);
    LocalDate futureDate = LocalDate.now().plusDays(1);
    String message = portfolioService.addStockToPortfolio(portfolioName, "AAPL", 10, futureDate);

    assertEquals("Date cannot be in the future: " + futureDate, message);
  }
  @Test
  public void testCreatePortfolioWithEmptyName() {
    Payload result = portfolioService.createNewPortfolio("");
    assertTrue(result.isError());
    assertEquals("Portfolio name cannot be empty", result.getMessage());
  }

  @Test
  public void testCreatePortfolioWithDuplicateName() {
    String portfolioName = "DuplicateName";
    portfolioService.createNewPortfolio(portfolioName);
    Payload resultDuplicate = portfolioService.createNewPortfolio(portfolioName);
    assertTrue(resultDuplicate.isError());
    assertEquals("Portfolio already exists: " + portfolioName, resultDuplicate.getMessage());
  }

  @Test
  public void testCreatingAndListing25Portfolios() {
    int numberOfPortfolios = 25;
    // Create 25 portfolios with unique names
    IntStream.rangeClosed(1, numberOfPortfolios).forEach(i -> {
      String portfolioName = "Portfolio" + i;
      Payload result = portfolioService.createNewPortfolio(portfolioName);
      assertFalse("Creation failed for portfolio: " + portfolioName, result.isError());
    });

    // List all portfolio names and verify
    List<String> portfolioNames = portfolioService.listPortfolioNames();
    assertEquals("Expected number of portfolios does not match", numberOfPortfolios, portfolioNames.size());

    // Verify each portfolio name is correctly listed
    IntStream.rangeClosed(1, numberOfPortfolios).forEach(i -> {
      String expectedPortfolioName = "Portfolio" + i;
      assertTrue("Expected portfolio name not found: " + expectedPortfolioName,
              portfolioNames.contains(expectedPortfolioName));
    });
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