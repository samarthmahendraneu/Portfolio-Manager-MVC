import static org.junit.Assert.assertEquals;


import controller.Payload;
import controller.PortfolioControllerInterface;
import model.PortfolioInterface;
import model.service.StockServiceInterface;
import model.Tradable;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import controller.PortfolioController;
import model.service.StockService;
import model.Portfolio;
import model.Stock;

/**
 * Test class for the PortfolioController class.
 */
public class PortfolioControllerTest {

  private StockServiceInterface stockService;
  private PortfolioControllerInterface portfolioController;

  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioController = new PortfolioController(stockService);
  }

  //Portfolio Creation
  @Test
  public void testCreateNewPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    // convertv to payload
    Portfolio portfolio = (Portfolio) payload.getData();
    assertEquals("Test Portfolio", portfolio.getName());
  }

  // empty portfolio name test
  @Test
  public void testCreateNewPortfolio_EmptyName() {
    Payload payload = portfolioController.createNewPortfolio("");
    assertEquals("Portfolio name cannot be empty", payload.getMessage());
  }

  // duplicate portfolio name test
  @Test
  public void testCreateNewPortfolio_DuplicateName() {
    Object payload1 = portfolioController.createNewPortfolio("Test Portfolio");
    Payload payload2 = portfolioController.createNewPortfolio("Test Portfolio");
    assertEquals("Portfolio already exists: Test Portfolio", payload2.getMessage());
  }

  // //Create a portfolio with a valid set of stocks and shares.
  @Test
  public void testAddStockToPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();

    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals(1, portfolio.getStocks().size());
  }


  // zero quantity test
  @Test
  public void testAddStockToPortfolio_InvalidQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    PortfolioInterface portfolio = (Portfolio) payload.getData();
    Tradable stock = new Stock("AAPL", 0, new BigDecimal("100.00"), LocalDate.now());
    payload = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals("Quantity must be positive: 0", payload.getMessage());

  }

  // negative quantity test
  @Test
  public void testAddStockToPortfolio_NegativeQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    Tradable stock = new Stock("AAPL", -10, new BigDecimal("100.00"), LocalDate.now());
    Payload paylpad = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals("Quantity must be positive: -10", paylpad.getMessage());
  }

  ////Create multiple portfolios with different sets of stocks and shares.
  @Test
  public void testCreateMultiplePortfoliosAndAddStocks() {
    Payload payload1 = portfolioController.createNewPortfolio("Test Portfolio 1");
    Portfolio portfolio1 = (Portfolio) payload1.getData();
    Payload payload2 = portfolioController.createNewPortfolio("Test Portfolio 2");
    Portfolio portfolio2 = (Portfolio) payload2.getData();
    portfolioController.addStockToPortfolio(portfolio1, "AAPL", 10, LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio1, "GOOGL", 5, LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio2, "AAPL", 10, LocalDate.now());
    assertEquals(2, portfolioController.getNumPortfolios());
    PortfolioInterface portfolio3 = portfolioController.getPortfolioService()
        .getPortfolioByName("Test Portfolio 1").get();
    assertEquals(2, portfolio3.getStocks().size());
    PortfolioInterface portfolio4 = portfolioController.getPortfolioService()
        .getPortfolioByName("Test Portfolio 2").get();
    assertEquals(1, portfolio4.getStocks().size());
  }

  // calculate portfolio value test on same day
  @Test
  public void testCalculatePortfolioValue() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-06"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10))
        .add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-06"));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // //Portfolio Value Determination for past date
  @Test
  public void testCalculatePortfolioValue_PastDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-02"));
    assertEquals(BigDecimal.ZERO, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // calculate portfolio value test on future date
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-07"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-07"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10))
        .add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-07"));

    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // examine value on sunday February 4 should be same as February 2
  @Test
  public void testCalculatePortfolioValue_Sunday() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-04"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-04"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-02"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-02"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10))
        .add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-04"));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
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
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10))
        .add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-09"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());

    // value as of February 11
    Payload stock_price_3 = stockService.fetchPreviousClosePrice("MSFT",
        LocalDate.parse("2024-02-11"));
    total = total.add(((BigDecimal) stock_price_3.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-11"));
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

  // save and load portfolio
  @Test
  public void testSaveAndLoadPortfolio() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    portfolioController.savePortfolio("test.csv");
    portfolioController.loadPortfolio("test.csv");
    assertEquals(1, portfolioController.getNumPortfolios());
  }

  // save and load multiple portfolios
  @Test
  public void testSaveAndLoadMultiplePortfolios() {
    Object payload1 = portfolioController.createNewPortfolio("Test Portfolio 1");
    Portfolio portfolio1 = (Portfolio) ((Payload) payload1).getData();
    Object payload2 = portfolioController.createNewPortfolio("Test Portfolio 2");
    Portfolio portfolio2 = (Portfolio) ((Payload) payload2).getData();
    portfolioController.addStockToPortfolio(portfolio1, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio1, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio2, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.savePortfolio("test.csv");
    portfolioController.loadPortfolio("test.csv");
    assertEquals(2, portfolioController.getNumPortfolios());
  }
}

//Determine the total value of a portfolio on a future date (should not be possible or should
// return an error).
//Portfolio Persistence
//Save a portfolio to a file and ensure the data is correctly written in human-readable format.
//Load a portfolio from a file and ensure the data is correctly read and the portfolio is
// accurately reconstructed.
//Attempt to load a portfolio from a corrupted or incorrectly formatted file.
//User Interface/Interactivity
//Interact with the program using valid commands and inputs.
//Test the program's response to invalid commands or inputs (e.g., typos, incorrect data types).
//Ensure that the program does not crash upon receiving unexpected input.