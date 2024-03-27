import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import Controller.Payload;
import Controller.PortfolioControllerInterface;
import Model.PortfolioInterface;
import Model.Service.StockServiceInterface;
import Model.Tradable;

import View.View;
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

/**
 * Test class for the PortfolioController class.
 */
public class PortfolioControllerTest {

  private StockServiceInterface stockService;
  private PortfolioControllerInterface portfolioController;
  private View view;

  /**
   * Sets up the test environment by creating a new StockService and PortfolioController.
   */
  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioController = new PortfolioController(stockService);
    view = new View();
  }

  /**
   * Tests creating a new portfolio with a valid name.
   */
  @Test
  public void testCreateNewPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    assertEquals("Test Portfolio", portfolio.getName());
  }

  /**
   * Tests creating a portfolio with an empty name.
   */
  @Test
  public void testCreateNewPortfolio_EmptyName() {
    // empty portfolio name test
    Payload payload = portfolioController.createNewPortfolio("");
    assertEquals("Portfolio name cannot be empty", payload.getMessage());
  }

  /**
   * Tests creating a portfolio with a duplicate name.
   */
  @Test
  public void testCreateNewPortfolio_DuplicateName() {
    // duplicate portfolio name test
    Object payload1 = portfolioController.createNewPortfolio("Test Portfolio");
    Payload payload2 = portfolioController.createNewPortfolio("Test Portfolio");
    assertEquals("Portfolio already exists: Test Portfolio", payload2.getMessage());
  }

  /**
   * Tests adding a stock to a portfolio with a valid set of stocks and shares.
   */
  @Test
  public void testAddStockToPortfolio() {
    //Create a portfolio with a valid set of stocks and shares.
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();

    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        LocalDate.now());
    assertEquals(1, portfolio.getStocks().size());
  }


  /**
   * Tests adding a stock to a portfolio with a quantity of 0.
   */
  @Test
  public void testAddStockToPortfolio_InvalidQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    PortfolioInterface portfolio = (Portfolio) payload.getData();
    Tradable stock = new Stock("AAPL", 0, new BigDecimal("100.00"), LocalDate.now());
    payload = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        stock.getQuantity(),
        LocalDate.now());
    assertEquals("Quantity must be positive: 0", payload.getMessage());

  }

  /**
   * Tests adding a stock to a portfolio with a negative quantity.
   */
  @Test
  public void testAddStockToPortfolio_NegativeQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    Tradable stock = new Stock("AAPL", -10, new BigDecimal("100.00"), LocalDate.now());
    Payload paylpad = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        stock.getQuantity(),
        LocalDate.now());
    assertEquals("Quantity must be positive: -10", paylpad.getMessage());
  }

  /**
   * Tests adding a stock to a portfolio with a future purchase date.
   */
  @Test
  public void testCreateMultiplePortfoliosAndAddStocks() {
    //Create multiple portfolios with different sets of stocks and shares.
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

  /**
   * Tests calculating the value of a portfolio on the same day.
   */
  @Test
  public void testCalculatePortfolioValue() {
    // calculate portfolio value test on same day
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

  /**
   * Tests calculating the value of a portfolio on a past date.
   */
  @Test
  public void testCalculatePortfolioValue_PastDate() {
    // Portfolio Value Determination for past date
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-02"));
    BigDecimal bigDecimal1 = new BigDecimal("0.00");
    assertEquals(0, bigDecimal1.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }

  /**
   * Tests calculating the value of a portfolio on a future date.
   */
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    // calculate portfolio value test on future date
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

  /**
   * Tests examining a portfolio on a Sunday.
   */
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
        LocalDate.parse("2024-02-02"));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }


  /**
   * Tests examining a portfolio.
   */
  @Test
  public void testExaminePortfolio() {
    // examine, add 3 stocks and examine portfolio and examine before buying the last stock
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-08"));
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
    Payload stock_price_3 = stockService.fetchPriceOnDate("MSFT",
        LocalDate.parse("2024-02-11"));
    total = total.add(((BigDecimal) stock_price_3.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-11"));
    assertEquals(total, ((Optional<BigDecimal>) ((Payload) payload).getData()).get());
  }

  /**
   * Tests adding an invalid stock to a portfolio.
   */
  @Test
  public void testAddInvalidStockToPortfolio() {
    // Portfolio portfolio = portfolioController.createNewPortfolio("Test Portfolio");
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "INVALID", 10, LocalDate.now());
    assertEquals("Invalid stock symbol", ((Payload) payload).getMessage());
  }

  /**
   * Tests saving and loading a portfolio.
   */
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

  /**
   * Tests saving and loading multiple portfolios.
   */
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
  @Test
  public void testListPortfolioNames() {
    portfolioController.createNewPortfolio("FirstPortfolio");
    portfolioController.createNewPortfolio("SecondPortfolio");

    List<String> portfolioNames = this.portfolioController.getPortfolioService()
        .listPortfolioNames();
    assertEquals(2, portfolioNames.size());
    assertTrue(portfolioNames.contains("FirstPortfolio"));
    assertTrue(portfolioNames.contains("SecondPortfolio"));
  }

  @Test
  public void testAddStockWithNegativeQuantity() {
    String portfolioName = "MyPortfolio";
    Payload payload = portfolioController.createNewPortfolio(portfolioName);
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", -10, LocalDate.now());
    String message = payload.getMessage();
    assertEquals("Quantity must be positive: -10", message);
  }

  @Test
  public void testAddStockWithFutureDate() {
    String portfolioName = "FutureDatePortfolio";
    Payload payload = portfolioController.createNewPortfolio(portfolioName);
    Portfolio portfolio = (Portfolio) payload.getData();
    LocalDate futureDate = LocalDate.now().plusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, futureDate);
    String message = payload.getMessage();
    assertEquals("Date cannot be in the future: " + futureDate, message);
  }
  @Test
  public void testCreatePortfolioWithEmptyName() {
    Payload result = portfolioController.createNewPortfolio("");
    assertTrue(result.isError());
    assertEquals("Portfolio name cannot be empty", result.getMessage());
  }

  @Test
  public void testCreatePortfolioWithDuplicateName() {
    String portfolioName = "DuplicateName";
    portfolioController.createNewPortfolio(portfolioName);
    Payload resultDuplicate = portfolioController.createNewPortfolio(portfolioName);
    assertTrue(resultDuplicate.isError());
    assertEquals("Portfolio already exists: " + portfolioName, resultDuplicate.getMessage());
  }

  @Test
  public void testCreatingAndListing25Portfolios() {
    int numberOfPortfolios = 25;
    // Create 25 portfolios with unique names
    IntStream.rangeClosed(1, numberOfPortfolios).forEach(i -> {
      String portfolioName = "Portfolio" + i;
      Payload result = portfolioController.createNewPortfolio(portfolioName);
      assertFalse("Creation failed for portfolio: " + portfolioName, result.isError());
    });

    // List all portfolio names and verify
    List<String> portfolioNames = portfolioController.getPortfolioService().listPortfolioNames();
    assertEquals("Expected number of portfolios does not match", numberOfPortfolios, portfolioNames.size());

    // Verify each portfolio name is correctly listed
    IntStream.rangeClosed(1, numberOfPortfolios).forEach(i -> {
      String expectedPortfolioName = "Portfolio" + i;
      assertTrue("Expected portfolio name not found: " + expectedPortfolioName,
              portfolioNames.contains(expectedPortfolioName));
    });
  }

  /**
   * Verifies that attempting to calculate the portfolio value on a future date results in an
   * error.
   */
  @Test
  public void testCalculatePortfolioValueOnFutureDate() {
    String portfolioName = "InvestmentPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate futureDate = LocalDate.now().plusDays(10);
    Payload result = portfolioController.calculatePortfolioValue(portfolioName, futureDate);

    assertTrue(result.isError());
    assertEquals("Date cannot be in the future: " + futureDate, result.getMessage());
  }




  /**
   * Test to AddStockWithInvalidSymbol.
   */
  @Test
  public void testAddStockWithInvalidSymbol() {
    String portfolioName = "MyPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate validDate = LocalDate.now().minusDays(1); // Assuming this is a valid past date
    Payload result = portfolioController.addStockToPortfolio(new Portfolio(portfolioName),
        "INVALID", 5, validDate);

    assertTrue(result.isError());
    assertTrue(result.getMessage().contains("Invalid stock symbol"));
  }

  /**
   * Test to calculate empty portfolio value.
   */
  @Test
  public void testCalculateEmptyPortfolioValue() {
    String portfolioName = "EmptyPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate validDate = LocalDate.now();
    Payload result = portfolioController.calculatePortfolioValue(portfolioName, validDate);

    assertTrue(result.getData() instanceof Optional);
    assertEquals(BigDecimal.ZERO,
        ((Optional<BigDecimal>) result.getData()).orElse(BigDecimal.valueOf(-1)));
  }


  /**
   * Test reading from a non existent file.
   */
  @Test
  public void testLoadPortfolioFromNonexistentFile() {
    String filePath = "nonexistent_file.csv";
    Payload result = portfolioController.loadPortfolio(filePath);
    assertTrue(result.isError());
  }

  /**
   * add more than 5 different stocks to a portfolio.
   */
  @Test
  public void testAddMoreThan5StocksToPortfolio() {
    String portfolioName = "ManyStocksPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate purchaseDate = LocalDate.now().minusDays(5);

    Payload result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "GOOGL", 5, purchaseDate);
    result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "AAPL", 5, purchaseDate);
    result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "MSFT", 5, purchaseDate);
    result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "TSLA", 5, purchaseDate);
    result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "AMZN", 5, purchaseDate);
    PortfolioInterface portfolio = (Portfolio) result.getData();
    assertTrue(portfolio.getStocks().size() == 5);
  }

  /**
   * more than 5 portfolios with different names and atleast 1 stock in each.
   */
  @Test
  public void testCreateMoreThan5PortfoliosWithStocks() {
    for (int i = 1; i <= 5; i++) {
      String portfolioName = "Portfolio" + i;
      portfolioController.createNewPortfolio(portfolioName);
      LocalDate purchaseDate = LocalDate.now().minusDays(5);
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL", 5,
          purchaseDate);
    }
    assertEquals(5, portfolioController.getNumPortfolios());
  }

  /**
   * save more than 2 portfolios to a file.
   */
  @Test
  public void testSaveMoreThan2Portfolios() {
    for (int i = 1; i <= 3; i++) {
      String portfolioName = "Portfolio" + i;
      portfolioController.createNewPortfolio(portfolioName);
      LocalDate purchaseDate = LocalDate.now().minusDays(5);
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL", 5,
          purchaseDate);
    }
    Payload result = portfolioController.savePortfolio("test.csv");
    assertTrue(result.IsNotError());
  }


  /**
   * load more than 2 portfolios from a file and then calculate the value of each portfolio.
   */
  @Test
  public void testLoadMoreThan2PortfoliosAndCalculateValue() {
    for (int i = 1; i <= 3; i++) {
      String portfolioName = "Portfolio" + i;
      portfolioController.createNewPortfolio(portfolioName);
      LocalDate purchaseDate = LocalDate.now().minusDays(5);
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL", 5,
          purchaseDate);
    }
    portfolioController.savePortfolio("test.csv");
    portfolioController.loadPortfolio("test.csv");
    for (int i = 1; i <= 3; i++) {
      String portfolioName = "Portfolio" + i;
      Payload result = portfolioController.calculatePortfolioValue(portfolioName, LocalDate.now());
      assertTrue(result.getData() instanceof Optional);
    }
  }

  @Test
  public void testSaveAndLoadCache() {
    String testFilePath = "testCache.csv";

    // Assuming saveCache() and loadCache() modify and read an actual file or data structure.
    Payload saveResult = portfolioController.saveCache(testFilePath);
    assertFalse("Cache saving should succeed.", saveResult.isError());

    Payload loadResult = portfolioController.loadCache(testFilePath);
    assertFalse("Cache loading should succeed.", loadResult.isError());
  }

  @Test
  public void testComputeStockMovingAverage() {
    // Example assumes your service layer can handle these calls directly.
    String symbol = "AAPL";
    LocalDate endDate = LocalDate.now().minusDays(1); // Use a fixed date or a mock date provider
    int days = 30;

    Payload result = portfolioController.computeStockMovingAverage(symbol, endDate, days);
    assertFalse("Should compute moving average without errors.", result.isError());
    assertEquals(result.getData(), "Moving average should not be null.");
  }

  @Test
  public void testInspectStockPerformance() {
    String symbol = "AAPL";
    LocalDate date = LocalDate.now().minusDays(1); // Use a fixed date or a mock date provider

    Payload result = portfolioController.inspectStockPerformance(symbol, date);
    assertFalse("Should inspect stock performance without errors.", result.isError());
    assertEquals(result.getData(), "Stock performance description should not be null.");
  }




  @Test
  public void testAdditionalAddStockToPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now());
    assertEquals(1, portfolio.getStocks().size());
    // add one more stock to the portfolio
    payload = portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.now());
    assertEquals(2, portfolio.getStocks().size());
  }

  //testAddStockToPortfolio_EmptyPortfolioName
  @Test
  public void testAddStockToPortfolio_EmptyPortfolioName() {
    Payload payload = portfolioController.createNewPortfolio("");
    assertEquals("Portfolio name cannot be empty", payload.getMessage());
  }

  // testAddStockToPortfolio_InvalidStockSymbol
  @Test
  public void testAddStockToPortfolio_InvalidStockSymbol() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "INVALID", 10, LocalDate.now());
    assertEquals("Invalid stock symbol", payload.getMessage());
  }

  // testAddStockToPortfolio_InvalidQuantity
  @Test
  public void testAddStockToPortfolioInvalidQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 0, LocalDate.now());
    assertEquals("Quantity must be positive: 0", payload.getMessage());
  }

  // testAddStockToPortfolio_FutureDate
  @Test
  public void testAddStockToPortfolio_FutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    LocalDate futureDate = LocalDate.now().plusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, futureDate);
    assertEquals("Date cannot be in the future: " + futureDate, payload.getMessage());
  }

  // testAddStockToPortfolio_PortfolioNotFound expect null pointer exception
  @Test(expected = NullPointerException.class)
  public void testAddStockToPortfolio_PortfolioNotFound() {
    Payload payload = portfolioController.addStockToPortfolio(null, "AAPL", 10, LocalDate.now());
    assertEquals("Portfolio not found: null", payload.getMessage());
  }

  // add same stock twice to the portfolio
  @Test
  public void testAddSameStockTwiceToPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now());
    assertEquals(1, portfolio.getStocks().size());
    // add a new date - 1 of current date
    LocalDate newDate = LocalDate.now().minusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 5, newDate);
    assertEquals(15, portfolio.getStockQuantity("AAPL", LocalDate.now()));
  }

  // add same stock thrice to the portfolio
  // validate on different dates
  @Test
  public void testAddSameStockTwiceToPortfolioValidateDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now());
    assertEquals(1, portfolio.getStocks().size());
    // add a new date - 1 of current date
    LocalDate newDate = LocalDate.now().minusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 5, newDate);
    assertEquals(15, portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(5, portfolio.getStockQuantity("AAPL", newDate));

  }





  // 1. sell stock from a portfolio that does not exist
  @Test(expected = NullPointerException.class)
  public void testSellStockFromPortfolio_PortfolioNotFound() {
    Payload payload = portfolioController.sellStockFromPortfolio(null, "AAPL", 10, LocalDate.now());
  }

  // 2. sell stock from a portfolio that has no stocks
  @Test
  public void testSellStockFromPortfolio_PortfolioNoStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 10, LocalDate.now());
    assertEquals("Stock not found", payload.getMessage());
  }

  // 3. sell stock from a portfolio that has the stock but not the quantity
  @Test
  public void testSellStockFromPortfolio_PortfolioNoQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now());
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 15, LocalDate.now());
    assertEquals("Not enough stock to sell", payload.getMessage());
  }

  // 4. sell stock from a portfolio that has the stock and the quantity
  @Test
  public void testSellStockFromPortfolio_PortfolioValid() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    // buy 2 days back
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(2));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now());
    assertEquals(5, portfolio.getStockQuantity("AAPL", LocalDate.now()));
  }

  // 5. sell stock from a portfolio that has the stock and the quantity but on a different date
  @Test
  public void testSellStockFromPortfolio_PortfolioDifferentDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    assertEquals(5, portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(10, portfolio.getStockQuantity("AAPL", LocalDate.now().minusDays(2)));
  }

  // 6. sell stock from a portfolio that has the stock and the quantity but on a future date
  @Test
  public void testSellStockFromPortfolio_PortfolioFutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now().plusDays(1));
    assertEquals("Cannot sell stock in the future", payload.getMessage());
  }

  // 7. sell stock on different days and validate quantity on different days
  @Test
  public void testSellStockFromPortfolio_PortfolioDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 3, LocalDate.now().minusDays(2));
    assertEquals(2, portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(2, portfolio.getStockQuantity("AAPL", LocalDate.now().minusDays(1)));
    assertEquals(7, portfolio.getStockQuantity("AAPL", LocalDate.now().minusDays(2)));
    assertEquals(10, portfolio.getStockQuantity("AAPL", LocalDate.now().minusDays(3)));
  }

  // 8. try to sell stock more than the quantity available
  @Test
  public void testSellStockFromPortfolio_PortfolioMoreQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 15, LocalDate.now().minusDays(12));
    assertEquals("Not enough stock to sell", payload.getMessage());
  }


  // 1. calculate investment for a portfolio that does not exist
  @Test
  public void testCalculateInvestment_PortfolioNotFound() {
    Payload payload = portfolioController.calculateTotalInvestment(null, LocalDate.now());
    assertEquals("Portfolio not found: null", payload.getMessage());
  }

  // 2. calculate investment for a portfolio that has no stocks
  @Test
  public void testCalculateInvestment_PortfolioNoStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(0, ((Optional<BigDecimal>) payload.getData()).get().intValue());
  }

  // 3. calculate investment for a portfolio that has stocks but on a different date
  @Test
  public void testCalculateInvestment_PortfolioDifferentDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // 4. calculate investment for a portfolio that has stocks but on a future date
  @Test
  public void testCalculateInvestment_PortfolioFutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().plusDays(1));
    assertEquals("Date cannot be in the future: " + LocalDate.now().plusDays(1), payload.getMessage());
  }

  // 5. calculate investment for a portfolio that has stocks and on the same date
  @Test
  public void testCalculateInvestment_PortfolioValid() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }


  // 6 investment should remain same if no stocks are sold
  @Test
  public void testCalculateInvestment_PortfolioNoStocksSold() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // 6. calculate investment for a portfolio that has stocks and on different dates
  @Test
  public void testCalculateInvestment_PortfolioDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(2));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // 7. calculate investment for a portfolio that has stocks and on different dates and sell stocks on different dates
  @Test
  public void testCalculateInvestment_PortfolioDifferentDaysSellStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(2));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now().minusDays(1));

    BigDecimal newTotal = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    assertEquals(newTotal, ((Optional<BigDecimal>) payload.getData()).get());

    // total investment remains same
  }




  // 1. add stock to portfolio, sell stock from portfolio and then calculate value
  @Test
  public void testAddStockSellStockCalculateValue() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    // buy 2 days back
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(2));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now());
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now());
    // fetch stock price
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now());
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(5));
    assertEquals(total, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // 2. add stock to portfolio, sell stock from portfolio and then calculate value on different dates
  @Test
  public void testAddStockSellStockCalculateValueDifferentDates() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    // buy 2 days back
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.now().minusDays(2));
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL", 5, LocalDate.now());
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now());
    // fetch stock price
    Payload stock_price_today = stockService.fetchPriceOnDate("AAPL", LocalDate.now());
    BigDecimal total_today = ((BigDecimal) stock_price_today.getData()).multiply(new BigDecimal(5));
    Payload stock_price_yesterday = stockService.fetchPriceOnDate("AAPL", LocalDate.now().minusDays(1));
    assertEquals(total_today, ((Optional<BigDecimal>) payload.getData()).get());
    BigDecimal total_yesterday = ((BigDecimal) stock_price_yesterday.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(total_yesterday, ((Optional<BigDecimal>) payload.getData()).get());
  }

  // 1. calculate value for a portfolio that does not exist
  @Test
  public void testCalculatePortfolioValue_PortfolioNotFound() {
    Payload payload = portfolioController.calculatePortfolioValue(null, LocalDate.now());
    assertEquals("Portfolio not found: null", payload.getMessage());
  }

  // 2. calculate value for a portfolio that has no stocks
  @Test
  public void testCalculatePortfolioValue_PortfolioNoStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now());
    assertEquals(0, ((Optional<BigDecimal>) payload.getData()).get().intValue());
  }

  // 3 calculate value before buying the stock
  @Test
  public void testCalculatePortfolioValue_PortfolioBeforeBuyingStock() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now());
    assertEquals(0, ((Optional<BigDecimal>) payload.getData()).get().intValue());
  }



















}