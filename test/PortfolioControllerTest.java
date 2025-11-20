import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import controller.Payload;
import controller.PortfolioController;
import controller.PortfolioControllerInterface;
import mock.MockStockService;
import model.Portfolio;
import model.PortfolioInterface;
import model.Stock;
import model.Tradable;
import model.service.StockService;
import model.service.StockServiceInterface;
import view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the PortfolioController class.
 */
public class PortfolioControllerTest {

  private StockServiceInterface stockService;
  private PortfolioControllerInterface portfolioController;
  private MockStockService mockStockService;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private ByteArrayInputStream inContent;

  /**
   * Sets up the test environment by creating a new StockService and PortfolioController.
   */
  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioController = new PortfolioController(stockService);
    View view = new View();
    mockStockService = new MockStockService();

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

    Stock stock = new Stock("AAPL", 10.0F, new BigDecimal("100.00"),
        LocalDate.now());
    portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(), (int) stock.getQuantity(),
        LocalDate.now());
    assertEquals(0, portfolio.getStocks().size());
  }


  /**
   * Tests adding a stock to a portfolio with a quantity of 0.
   */
  @Test
  public void testAddStockToPortfolio_InvalidQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    PortfolioInterface portfolio = (Portfolio) payload.getData();
    Tradable stock = new Stock("AAPL", 0, new BigDecimal("100.00"),
        LocalDate.now());
    payload = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        (int) stock.getQuantity(),
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
    Tradable stock = new Stock("AAPL", -10, new BigDecimal("100.00"),
        LocalDate.now());
    Payload paylpad = portfolioController.addStockToPortfolio(portfolio, stock.getSymbol(),
        (int) stock.getQuantity(),
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
    assertEquals(0, portfolio3.getStocks().size());
    PortfolioInterface portfolio4 = portfolioController.getPortfolioService()
        .getPortfolioByName("Test Portfolio 2").get();
    assertEquals(0, portfolio4.getStocks().size());
  }

  /**
   * Tests calculating the value of a portfolio on the same day.
   */
  @Test
  public void testCalculatePortfolioValue() {
    // calculate portfolio value test on same day
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL",
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-06"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL",
        LocalDate.parse("2024-02-06"));
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
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-06"));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-02"));
    BigDecimal bigDecimal1 = new BigDecimal("0.00");
    assertEquals(0,
        bigDecimal1.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }

  /**
   * Tests calculating the value of a portfolio on a future date.
   */
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    // calculate portfolio value test on future date
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-06"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL",
        LocalDate.parse("2024-02-07"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL",
        LocalDate.parse("2024-02-07"));
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
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-02"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-02"));
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL",
        LocalDate.parse("2024-02-02"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL",
        LocalDate.parse("2024-02-02"));
    BigDecimal total = ((BigDecimal) stock_price_1.getData()).multiply(new BigDecimal(10))
        .add(((BigDecimal) stock_price_2.getData()).multiply(new BigDecimal(5)));
    payload = portfolioController.calculatePortfolioValue("Test Portfolio",
        LocalDate.parse("2024-02-04"));
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
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-08"));
    portfolioController.addStockToPortfolio(portfolio, "MSFT", 5,
        LocalDate.parse("2024-02-11"));

    // total value as of February 9
    Payload stock_price_1 = stockService.fetchPriceOnDate("AAPL",
        LocalDate.parse("2024-02-09"));
    Payload stock_price_2 = stockService.fetchPriceOnDate("GOOGL",
        LocalDate.parse("2024-02-09"));
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
    payload = portfolioController.addStockToPortfolio(portfolio, "INVALID", 10,
        LocalDate.now());
    assertEquals("Date cannot be on a weekend: 2024-04-13",
        ((Payload) payload).getMessage());
  }

  /**
   * Tests saving and loading a portfolio.
   */
  @Test
  public void testSaveAndLoadPortfolio() {
    Object payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) ((Payload) payload).getData();
    portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio, "GOOGL", 5,
        LocalDate.parse("2024-02-06"));
    portfolioController.savePortfolio("test.csv", "Normal");
    portfolioController.loadPortfolio("test.csv", "Normal");
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
    portfolioController.addStockToPortfolio(portfolio1, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio1, "GOOGL", 5,
        LocalDate.parse("2024-02-06"));
    portfolioController.addStockToPortfolio(portfolio2, "AAPL", 10,
        LocalDate.parse("2024-02-06"));
    portfolioController.savePortfolio("test.csv", "Normal");
    portfolioController.loadPortfolio("test.csv", "Normal");
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
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", -10,
        LocalDate.now());
    String message = payload.getMessage();
    assertEquals("Quantity must be positive: -10", message);
  }

  @Test
  public void testAddStockWithFutureDate() {
    String portfolioName = "FutureDatePortfolio";
    Payload payload = portfolioController.createNewPortfolio(portfolioName);
    Portfolio portfolio = (Portfolio) payload.getData();
    LocalDate futureDate = LocalDate.now().plusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        futureDate);
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
    assertEquals("Portfolio already exists: " + portfolioName,
        resultDuplicate.getMessage());
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
    assertEquals("Expected number of portfolios does not match",
        numberOfPortfolios, portfolioNames.size());

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
    LocalDate validDate = LocalDate.now().minusDays(1); // Assuming this is a
    // valid past date
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
    Payload result = portfolioController.loadPortfolio(filePath, "Normal");
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
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL",
          5, purchaseDate);
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
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL",
          5, purchaseDate);
    }
    Payload result = portfolioController.savePortfolio("test.csv", "Normal");
    assertTrue(result.isNotError());
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
      portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL",
          5, purchaseDate);
    }
    portfolioController.savePortfolio("test.csv", "Normal");
    portfolioController.loadPortfolio("test.csv", "Normal");
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
    payload = portfolioController.addStockToPortfolio(portfolio, "INVALID", 10,
        LocalDate.of(2024, 04, 04));
    assertEquals("Invalid stock symbol", payload.getMessage());
  }

  // testAddStockToPortfolio_InvalidQuantity
  @Test
  public void testAddStockToPortfolioInvalidQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 0,
        LocalDate.now());
    assertEquals("Quantity must be positive: 0", payload.getMessage());
  }

  // testAddStockToPortfolio_FutureDate
  @Test
  public void testAddStockToPortfolio_FutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    LocalDate futureDate = LocalDate.now().plusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        futureDate);
    assertEquals("Date cannot be in the future: " + futureDate, payload.getMessage());
  }

  // testAddStockToPortfolio_PortfolioNotFound expect null pointer exception
  @Test(expected = NullPointerException.class)
  public void testAddStockToPortfolio_PortfolioNotFound() {
    Payload payload = portfolioController.addStockToPortfolio(null, "AAPL",
        10, LocalDate.now());
    assertEquals("Portfolio not found: null", payload.getMessage());
  }

  // add same stock twice to the portfolio

  /**
   * Tests adding the same stock twice to a portfolio.
   */
  @Test
  public void testAddSameStockTwiceToPortfolio() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.now());
    assertEquals(0, portfolio.getStocks().size());
    // add a new date - 1 of current date
    LocalDate newDate = LocalDate.now().minusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 5, newDate);
    assertEquals(5, (int) portfolio.getStockQuantity("AAPL", LocalDate.now()));
  }

  // add same stock thrice to the portfolio
  // validate on different dates

  /**
   * Tests adding the same stock thrice to a portfolio on different dates.
   */
  @Test
  public void testAddSameStockTwiceToPortfolioValidateDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 10,
        LocalDate.now());
    assertEquals(0, portfolio.getStocks().size());
    // add a new date - 1 of current date
    LocalDate newDate = LocalDate.now().minusDays(1);
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL", 5, newDate);
    assertEquals(5, (int) portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(5, (int) portfolio.getStockQuantity("AAPL", newDate));

  }


  // 1. sell stock from a portfolio that does not exist
  @Test(expected = NullPointerException.class)
  public void testSellStockFromPortfolio_PortfolioNotFound() {
    Payload payload = portfolioController.sellStockFromPortfolio(null, "AAPL",
        10, LocalDate.now());
  }

  // 2. sell stock from a portfolio that has no stocks
  @Test
  public void testSellStockFromPortfolio_PortfolioNoStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL",
        10, LocalDate.now());
    assertEquals("Stock not found", payload.getMessage());
  }

  // 3. sell stock from a portfolio that has the stock but not the quantity
  @Test
  public void testSellStockFromPortfolio_PortfolioNoQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(portfolio, "AAPL",
        10, LocalDate.now());
    payload = portfolioController.sellStockFromPortfolio(portfolio, "AAPL",
        15, LocalDate.now());
    assertEquals("Stock not found", payload.getMessage());
  }

  // 4. sell stock from a portfolio that has the stock and the quantity
  @Test
  public void testSellStockFromPortfolio_PortfolioValid() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    // buy 2 days back
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(2));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now());
    assertEquals(5, (int) portfolio.getStockQuantity("AAPL", LocalDate.now()));
  }

  // 5. sell stock from a portfolio that has the stock and the quantity but on a different date
  @Test
  public void testSellStockFromPortfolio_PortfolioDifferentDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    assertEquals(5, (int) portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(10, (int) portfolio.getStockQuantity(
        "AAPL", LocalDate.now().minusDays(2)));
  }

  // 6. sell stock from a portfolio that has the stock and the quantity but on a future date
  @Test
  public void testSellStockFromPortfolio_PortfolioFutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now().plusDays(1));
    assertEquals("Cannot sell stock in the future", payload.getMessage());
  }

  // 7. sell stock on different days and validate quantity on different days
  @Test
  public void testSellStockFromPortfolio_PortfolioDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 3, LocalDate.now().minusDays(2));
    assertEquals(2, (int) portfolio.getStockQuantity("AAPL", LocalDate.now()));
    assertEquals(2, (int) portfolio.getStockQuantity("AAPL",
        LocalDate.now().minusDays(1)));
    assertEquals(7, (int) portfolio.getStockQuantity("AAPL",
        LocalDate.now().minusDays(2)));
    assertEquals(10, (int) portfolio.getStockQuantity("AAPL",
        LocalDate.now().minusDays(3)));
  }

  // 8. try to sell stock more than the quantity available
  @Test
  public void testSellStockFromPortfolio_PortfolioMoreQuantity() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 15, LocalDate.now().minusDays(12));
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
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL",
        LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }

  // 4. calculate investment for a portfolio that has stocks but on a future date
  @Test
  public void testCalculateInvestment_PortfolioFutureDate() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().plusDays(1));
    assertEquals("Date cannot be in the future: " + LocalDate.now().plusDays(
        1), payload.getMessage());
  }

  // 5. calculate investment for a portfolio that has stocks and on the same date
  @Test
  public void testCalculateInvestment_PortfolioValid() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate(
        "AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now());
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }


  // 6 investment should remain same if no stocks are sold
  @Test
  public void testCalculateInvestment_PortfolioNoStocksSold() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate(
        "AAPL", LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now());
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }

  // 6. calculate investment for a portfolio that has stocks and on different dates
  @Test
  public void testCalculateInvestment_PortfolioDifferentDays() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL",
        LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(2));
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
  }

  @Test
  public void testCalculateInvestment_PortfolioDifferentDaysSellStocks() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(12));
    // get price of stock on the date
    Payload stock_price = stockService.fetchPriceOnDate("AAPL",
        LocalDate.now().minusDays(12));
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    payload = portfolioController.calculateTotalInvestment("Test Portfolio", LocalDate.now());
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(1));
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(2));
    assertEquals(0, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now().minusDays(1));
    payload = portfolioController.calculateTotalInvestment(
        "Test Portfolio", LocalDate.now().minusDays(1));

    BigDecimal newTotal = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(10));
    assertEquals(0, newTotal.compareTo(((Optional<BigDecimal>) payload.getData()).get()));

    // total investment remains same
  }

  @Test
  public void testInspectStockGain() {
    LocalDate date = LocalDate.of(2024, 2, 6); // Example fixed date
    String symbol = "AAPL";
    // Assuming setup for cache or API to return specific open and close prices
    Payload result = portfolioController.inspectStockPerformance(symbol, date);
    assertFalse(result.isError());
    assertTrue(((String) result.getData()).contains("Gained by"));
  }

  // 1. add stock to portfolio, sell stock from portfolio and then calculate value

  /**
   * Tests adding a stock to a portfolio, selling the stock, and then calculating the portfolio
   * value.
   */
  @Test
  public void testAddStockSellStockCalculateValue() {
    Payload payload = portfolioController.createNewPortfolio("Test Portfolio");
    Portfolio portfolio = (Portfolio) payload.getData();
    // buy 2 days back
    payload = portfolioController.addStockToPortfolio(
        portfolio, "AAPL", 10, LocalDate.now().minusDays(2));
    payload = portfolioController.sellStockFromPortfolio(
        portfolio, "AAPL", 5, LocalDate.now());
    payload = portfolioController.calculatePortfolioValue("Test Portfolio", LocalDate.now());
    // fetch stock price
    Payload stock_price = stockService.fetchPriceOnDate("AAPL", LocalDate.now());
    BigDecimal total = ((BigDecimal) stock_price.getData()).multiply(new BigDecimal(5));
    assertEquals(-1, total.compareTo(((Optional<BigDecimal>) payload.getData()).get()));
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


  @Test
  public void testInspectStockGainOrLoss_DataNotAvailable() {
    MockStockService mockService = new MockStockService() {
    };
    String result = mockService.inspectStockGainOrLoss(
        "AAPL", LocalDate.of(2024, 1, 14));
    assertTrue(result.contains("Stock data not available"));
  }

  @Test
  public void testComputeXDayMovingAverage_ValidInput() {
    BigDecimal average = mockStockService.computeXDayMovingAverage(
        "AAPL", LocalDate.of(2024, 2, 2), 2);
    assertNotNull(average);
    assertTrue(average.compareTo(new BigDecimal("153")) == 0);
  }

  @Test
  public void testInspectStockGainOrLoss_Gainby2() {
    String result = mockStockService.inspectStockGainOrLoss(
        "AAPL", LocalDate.of(2024, 2, 3));
    assertEquals("Gained by 2", result);
  }


  @Test
  public void testMovingAverageWithMissingData() {
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int days = 5;
    BigDecimal expectedAverage = new BigDecimal("152");
    BigDecimal actualAverage = mockStockService.computeXDayMovingAverage(
        "AAPL", endDate, days);
    assertEquals(expectedAverage, actualAverage.stripTrailingZeros());
  }

  @Test
  public void testMovingAverageWithMinimalMovement() {
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int days = 1; // Only includes the day with minimal movement
    BigDecimal expectedAverage = new BigDecimal("151");
    BigDecimal actualAverage = mockStockService.computeXDayMovingAverage("AAPL",
        endDate, days);
    assertEquals(expectedAverage, actualAverage.stripTrailingZeros());
  }


  @Test
  public void testStockPerformanceGain() {
    LocalDate date = LocalDate.of(2024, 2, 3); // Day with a gain
    String expectedPerformance = "Gained by 2"; // Open at 154, Close at 156
    String actualPerformance = mockStockService.inspectStockGainOrLoss("AAPL", date);
    assertEquals(expectedPerformance, actualPerformance);
  }

  @Test
  public void testStockPerformanceLoss() {
    LocalDate date = LocalDate.of(2024, 2, 4); // Day with a loss
    String expectedPerformance = "Lost by 5"; // Open at 156, Close at 151
    String actualPerformance = mockStockService.inspectStockGainOrLoss("AAPL", date);
    assertEquals(expectedPerformance, actualPerformance);
  }

  @Test
  public void testStockPerformanceUnchanged() {
    LocalDate date = LocalDate.of(2024, 2, 5); // Day unchanged
    String expectedPerformance = "Unchanged";
    String actualPerformance = mockStockService.inspectStockGainOrLoss("AAPL", date);
    assertEquals(expectedPerformance, actualPerformance);
  }

  @Test
  public void testDateValidationWithInvalidDate() {
    MockStockService mockService = new MockStockService();
    LocalDate futureDate = LocalDate.now().plusDays(10); // Future date
    assertFalse("Future date should not be valid", mockService.isValidDate(futureDate));
  }

  @Test
  public void testDateValidationWithWeekendDate() {
    MockStockService mockService = new MockStockService();
    LocalDate weekendDate = LocalDate.of(2023, 10, 8);
    assertFalse("Weekend date should not be valid", mockService.isValidDate(weekendDate));
  }

  @Test
  public void testDateValidationWithValidDate() {
    MockStockService mockService = new MockStockService();
    LocalDate validDate = LocalDate.now().minusDays(1); // Previous day
    assertTrue("Previous day should be valid", mockService.isValidDate(validDate));
  }

  @Test
  public void testPlotPerformanceChartIntegration() {

    StringBuilder expectedOutput = new StringBuilder();
    expectedOutput.append("30 Jun 2020: ***************************\n");
    expectedOutput.append("30 Sept 2020: ***************************\n");
    expectedOutput.append("31 Dec 2020: ****************************\n");
    expectedOutput.append("31 Mar 2021: *****************************\n");
    expectedOutput.append("30 Jun 2021: ********************************\n");
    expectedOutput.append("30 Sept 2021: *******************************\n");
    expectedOutput.append("31 Dec 2021: ******************************\n");
    expectedOutput.append("31 Mar 2022: *****************************\n");
    expectedOutput.append("30 Jun 2022: *******************************\n");
    expectedOutput.append("30 Sept 2022: **************************\n");
    expectedOutput.append("30 Dec 2022: *******************************\n");
    expectedOutput.append("\nScale: * = 4.4448 dollars (relative)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "IBM", LocalDate.of(2020, 4, 3),
        LocalDate.of(2023, 3, 3));

    assertEquals(expectedOutput.toString().trim(), actualOutput.toString().trim());
  }

  @Test
  public void testPlotPerformanceChartForSpecificDateRange() {

    StringBuilder expectedOutput = new StringBuilder();
    expectedOutput.append("6 Mar 2023: *****************************\n");
    expectedOutput.append("7 Mar 2023: *****************************\n");
    expectedOutput.append("8 Mar 2023: ****************************\n");
    expectedOutput.append("9 Mar 2023: ****************************\n");
    expectedOutput.append("10 Mar 2023: ****************************\n");
    expectedOutput.append("13 Mar 2023: ****************************\n");
    expectedOutput.append("14 Mar 2023: ****************************\n");
    expectedOutput.append("15 Mar 2023: ***************************\n");
    expectedOutput.append("16 Mar 2023: ****************************\n");
    expectedOutput.append("17 Mar 2023: ***************************\n");
    expectedOutput.append("20 Mar 2023: ****************************\n");
    expectedOutput.append("21 Mar 2023: ****************************\n");
    expectedOutput.append("22 Mar 2023: ****************************\n");
    expectedOutput.append("23 Mar 2023: ***************************\n");
    expectedOutput.append("24 Mar 2023: ****************************\n");
    expectedOutput.append("\nScale: * = 4.4224 dollars (relative)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "IBM", LocalDate.of(2023, 3, 6),
        LocalDate.of(2023, 3, 24));

    assertEquals(expectedOutput.toString().trim(), actualOutput.toString().trim());
  }


  @Test
  public void testFindCrossoverDays_ValidInput() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    // [2024-02-01, 2024-02-02, 2024-02-06]
    List<LocalDate> expectedDates = Arrays.asList(
        LocalDate.of(2024, 2, 1),
        LocalDate.of(2024, 2, 5)
    );
    Payload result = portfolioController.findCrossoverDays("AAPL", startDate, endDate);
    assertFalse(result.isError());
    assertEquals(expectedDates, result.getData());
  }


  // 2. invalid symbol, valid start date and end date
  @Test
  public void testFindCrossoverDays_InvalidSymbol() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    Payload result = portfolioController.findCrossoverDays("INVALID", startDate, endDate);
    assertEquals("Invalid stock symbol: " + "INVALID", result.getMessage());
  }

  // 3. valid symbol, invalid start date and end date
  @Test
  public void testFindCrossoverDays_InvalidStartDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 8); // Future date
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    Payload result = portfolioController.findCrossoverDays("AAPL", startDate, endDate);
    assertEquals("Start date should be before end date", result.getMessage());
  }

  // 4. valid symbol, start date and invalid end date
  @Test
  public void testFindCrossoverDays_InvalidEndDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    // end date as today +1
    LocalDate endDate = LocalDate.now().plusDays(1);
    Payload result = portfolioController.findCrossoverDays("AAPL", startDate, endDate);
    assertEquals("Date cannot be in the future", result.getMessage());
  }

  // 5. valid symbol, start date after end date
  @Test
  public void testFindCrossoverDays_StartDateAfterEndDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 7);
    LocalDate endDate = LocalDate.of(2024, 2, 1);
    Payload result = portfolioController.findCrossoverDays("AAPL", startDate, endDate);
    assertEquals("Start date should be before end date", result.getMessage());
  }


  // 3. valid symbol and invalid date
  @Test
  public void testInspectStockPerformance_InvalidDate() {
    LocalDate date = LocalDate.now().plusDays(1); // Future date
    Payload result = portfolioController.inspectStockPerformance("AAPL", date);
    assertEquals("Date cannot be in the future", result.getMessage());
  }

  // 5. valid symbol and future date
  @Test
  public void testInspectStockPerformance_FutureDate() {
    LocalDate date = LocalDate.now().plusDays(10); // Future date
    Payload result = portfolioController.inspectStockPerformance("AAPL", date);
    assertEquals("Date cannot be in the future", result.getMessage());
  }


  // 1. valid symbol, start date, end date, short moving period and long moving period
  @Test
  public void testFindMovingCrossoverDays_ValidInput() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int shortMovingPeriod = 2;
    int longMovingPeriod = 5;
    // [2024-02-01, 2024-02-02, 2024-02-06]
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    Map<String, Object> data = (Map<String, Object>) result.getData();
    List<LocalDate> res = (List<LocalDate>) data.get("movingCrossoverDays");
    // expected [2024-02-02, 2024-02-04, 2024-02-05]
    List<LocalDate> expectedDates = Arrays.asList(
        LocalDate.of(2024, 2, 1),
        LocalDate.of(2024, 2, 2),
        LocalDate.of(2024, 2, 6)
    );
    assertEquals(expectedDates, res);
  }


  // 3. valid symbol, invalid start date, end date, short moving period and long moving period
  @Test
  public void testFindMovingCrossoverDays_InvalidStartDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 8); // Future date
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int shortMovingPeriod = 2;
    int longMovingPeriod = 5;
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    assertEquals("Start date should be before end date", result.getMessage());
  }

  // 4. valid symbol, start date, invalid end date, short moving period and long moving period
  @Test
  public void testFindMovingCrossoverDays_InvalidEndDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    // end date as today +1
    LocalDate endDate = LocalDate.now().plusDays(1);
    int shortMovingPeriod = 2;
    int longMovingPeriod = 5;
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    assertEquals("Date cannot be in the future", result.getMessage());
  }

  // 5. valid symbol, start date, end date, invalid short moving period and long moving period
  @Test
  public void testFindMovingCrossoverDays_InvalidShortMovingPeriod() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int shortMovingPeriod = 0;
    int longMovingPeriod = 5;
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    assertEquals("Short moving period should be greater than 0", result.getMessage());
  }

  // 6. valid symbol, start date, end date, short moving period and invalid long moving period
  @Test
  public void testFindMovingCrossoverDays_InvalidLongMovingPeriod() {
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 2, 7);
    int shortMovingPeriod = 2;
    int longMovingPeriod = 0;
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    assertEquals("Short moving period should be less than long moving period",
        result.getMessage());
  }

  // 7. valid symbol, start date after end date, short moving period and long moving period
  @Test
  public void testFindMovingCrossoverDays_StartDateAfterEndDate() {
    LocalDate startDate = LocalDate.of(2024, 2, 7);
    LocalDate endDate = LocalDate.of(2024, 2, 1);
    int shortMovingPeriod = 2;
    int longMovingPeriod = 5;
    Payload result = portfolioController.findMovingCrossoverDays(
        "AAPL", startDate, endDate, shortMovingPeriod, longMovingPeriod);
    assertEquals("Start date should be before end date", result.getMessage());
  }


  /**
   * Test for graph generation with valid input.
   */
  @Test
  public void testPlotPerformanceChartForAAPL() {
    StringBuilder expectedOutput = new StringBuilder();
    expectedOutput.append("31 Dec 2014: **************\n");
    expectedOutput.append("31 Dec 2015: *************\n");
    expectedOutput.append("30 Dec 2016: ***************\n");
    expectedOutput.append("29 Dec 2017: **********************\n");
    expectedOutput.append("31 Dec 2018: ********************\n");
    expectedOutput.append("31 Dec 2019: **************************************\n");
    expectedOutput.append("31 Dec 2020: *****************\n");
    expectedOutput.append("31 Dec 2021: ***********************\n");
    expectedOutput.append("30 Dec 2022: *****************\n");
    expectedOutput.append("29 Dec 2023: *************************\n");
    expectedOutput.append("\nScale: * = 7.5356 dollars (absolute)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "AAPL", LocalDate.of(2014, 3, 5),
        LocalDate.of(2024, 3, 25));

    assertEquals(expectedOutput.toString().trim(), actualOutput.toString().trim());
  }

  /**
   * Plot performance chart for GOOG.
   */
  @Test
  public void testPlotPerformanceChartForGOOG() {

    StringBuilder expectedOutput = new StringBuilder();
    expectedOutput.append("3 Jan 2024: **************************\n");
    expectedOutput.append("23 Jan 2024: ***************************\n");
    expectedOutput.append("2 Feb 2024: **************************\n");
    expectedOutput.append("12 Feb 2024: ***************************\n");
    expectedOutput.append("22 Feb 2024: ***************************\n");
    expectedOutput.append("13 Mar 2024: **************************\n");
    expectedOutput.append("\nScale: * = 5.3568 dollars (relative)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "GOOG", LocalDate.of(2024, 1, 3),
        LocalDate.of(2024, 3, 25));

    assertEquals(expectedOutput.toString().trim(), actualOutput.toString().trim());
  }

  /**
   * Test for graph generation with valid input.
   */
  @Test
  public void testCreatePortfolioAndGenerateGraph() {
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Portfolio portfolio = (Portfolio) createPayload.getData();

    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 20, LocalDate.of(2016, 3, 4));
    assertFalse(addIbmPayload.isError());

    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 13, LocalDate.of(2021, 3, 4));
    assertFalse(addAaplPayload.isError());

    StringBuilder expectedGraph = new StringBuilder();
    expectedGraph.append("31 Dec 2018: **\n");
    expectedGraph.append("31 Dec 2019: **\n");
    expectedGraph.append("31 Dec 2020: **\n");
    expectedGraph.append("31 Dec 2021: ****\n");
    expectedGraph.append("30 Dec 2022: ****\n");
    expectedGraph.append("29 Dec 2023: *****\n");
    expectedGraph.append("\nScale: * = 1000 dollars (absolute)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "Test", LocalDate.of(2018, 3, 5),
        LocalDate.of(2024, 3, 4));
    assertEquals(expectedGraph.toString(), actualOutput.toString());
  }

  /**
   * Test for year wise graph generation.
   */
  @Test
  public void testCreatePortfolioAndGenerateGraphYearly() {
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Portfolio portfolio = (Portfolio) createPayload.getData();

    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 20, LocalDate.of(2016, 3, 4));
    assertFalse(addIbmPayload.isError());

    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 13, LocalDate.of(2021, 3, 4));
    assertFalse(addAaplPayload.isError());

    StringBuilder expectedGraph = new StringBuilder();
    expectedGraph.append("5 Mar 2024: ****************************************\n");
    expectedGraph.append("6 Mar 2024: *****************************************\n");
    expectedGraph.append("7 Mar 2024: *****************************************\n");
    expectedGraph.append("8 Mar 2024: *****************************************\n");
    expectedGraph.append("9 Mar 2024: *****************************************\n");
    expectedGraph.append("10 Mar 2024: *****************************************\n");
    expectedGraph.append("11 Mar 2024: *****************************************\n");
    expectedGraph.append("12 Mar 2024: *****************************************\n");
    expectedGraph.append("13 Mar 2024: *****************************************\n");
    expectedGraph.append("14 Mar 2024: *****************************************\n");
    expectedGraph.append("15 Mar 2024: ****************************************\n");
    expectedGraph.append("16 Mar 2024: ****************************************\n");
    expectedGraph.append("17 Mar 2024: ****************************************\n");
    expectedGraph.append("18 Mar 2024: *****************************************\n");
    expectedGraph.append("19 Mar 2024: *****************************************\n");
    expectedGraph.append("20 Mar 2024: *****************************************\n");
    expectedGraph.append("21 Mar 2024: ****************************************\n");
    expectedGraph.append("22 Mar 2024: ****************************************\n");
    expectedGraph.append("23 Mar 2024: ****************************************\n");
    expectedGraph.append("24 Mar 2024: ****************************************\n");
    expectedGraph.append("25 Mar 2024: ****************************************\n");
    expectedGraph.append("26 Mar 2024: ****************************************\n");
    expectedGraph.append("\nScale: * = 148.0704 dollars (absolute)");

    StringBuilder actualOutput = portfolioController.genGraph(
        "Test", LocalDate.of(2024, 3, 5),
        LocalDate.of(2024, 3, 26));
    assertEquals(expectedGraph.toString(), actualOutput.toString());
  }


  /**
   * Test for dollarCostAveraging with invalid investmentAmount.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDollarCostAveraging_InvalidInvestmentAmount() {
    // create a new portfolio with name "Test"
    // add IBM and AAPL stocks to the portfolio 10, 20
    // call dollarCostAveraging with name "Test", investmentAmount 1000, startDate 2021-03-04,
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 0.5f); // 50% of the investment
    stockWeights.put("AAPL", 0.5f); // 50% of the investment
    Portfolio portfolio = (Portfolio) createPayload.getData();
    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 10, LocalDate.of(2023, 1, 9));
    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 20, LocalDate.of(2023, 3, 9));
    portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(-1000);
    portfolioController.getPortfolioService().dollarCostAveraging("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4),
        LocalDate.of(2024, 3, 9), 3, stockWeights);
  }

  /**
   * Test for dollarCostAveraging with invalid startDate.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDollarCostAveraging_InvalidStartDate() {
    // create a new portfolio with name "Test"
    // add IBM and AAPL stocks to the portfolio 10, 20
    // call dollarCostAveraging with name "Test", investmentAmount 1000, startDate 2021-03-04,
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 0.5f); // 50% of the investment
    stockWeights.put("AAPL", 0.5f); // 50% of the investment
    Portfolio portfolio = (Portfolio) createPayload.getData();
    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 10, LocalDate.of(2023, 1, 9));
    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 20, LocalDate.of(2023, 3, 9));
    portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(1000);
    portfolioController.getPortfolioService().dollarCostAveraging("Test",
        investmentAmount,
        LocalDate.of(2024, 9, 4),
        LocalDate.of(2024, 3, 9), 3, stockWeights);
  }

  /**
   * Test for dollarCostAveraging with invalid endDate.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDollarCostAveraging_InvalidEndDate() {
    // create a new portfolio with name "Test"
    // add IBM and AAPL stocks to the portfolio 10, 20
    // call dollarCostAveraging with name "Test", investmentAmount 1000, startDate 2021-03-04,
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 0.5f); // 50% of the investment
    stockWeights.put("AAPL", 0.5f); // 50% of the investment
    Portfolio portfolio = (Portfolio) createPayload.getData();
    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 10, LocalDate.of(2023, 1, 9));
    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 20, LocalDate.of(2023, 3, 9));
    portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(1000);
    portfolioController.getPortfolioService().dollarCostAveraging("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4), LocalDate.of(2024, 9,
            9), 3, stockWeights);
  }

  /**
   * Test for dollarCostAveraging with invalid frequency.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDollarCostAveraging_InvalidFrequency() {
    // create a new portfolio with name "Test"
    // add IBM and AAPL stocks to the portfolio 10, 20
    // call dollarCostAveraging with name "Test", investmentAmount 1000, startDate 2021-03-04,
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 0.5f); // 50% of the investment
    stockWeights.put("AAPL", 0.5f); // 50% of the investment
    Portfolio portfolio = (Portfolio) createPayload.getData();
    Payload addIbmPayload = portfolioController.addStockToPortfolio(portfolio,
        "IBM", 10, LocalDate.of(2023, 1, 9));
    Payload addAaplPayload = portfolioController.addStockToPortfolio(portfolio,
        "AAPL", 20, LocalDate.of(2023, 3, 9));
    portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(1000);
    portfolioController.getPortfolioService().dollarCostAveraging("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4), LocalDate.of(2024, 3,
            9), 0, stockWeights);
  }

  @Test
  public void testValueBasedInvestment_ValidInput() {
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Portfolio portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(1000);
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 50.0f);
    stockWeights.put("AAPL", 50.0f);
    portfolioController.getPortfolioService().valueBasedInvestment("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4), stockWeights);
    Payload result = portfolioController.calculatePortfolioValue("Test",
        LocalDate.of(2024, 3, 9));
    assertEquals(0, 0, ((Optional<BigDecimal>) result.getData()).get().intValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueBasedInvestment_InvalidInvestmentAmount() {
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Portfolio portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(-1000);
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 50.0f);
    stockWeights.put("AAPL", 50.0f);
    portfolioController.getPortfolioService().valueBasedInvestment("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4), stockWeights);
  }

  // invalid weights
  @Test(expected = IllegalArgumentException.class)
  public void testValueBasedInvestment_InvalidWeights() {
    Payload createPayload = portfolioController.createNewPortfolio("Test");
    assertNotNull(createPayload.getData());
    Portfolio portfolio = (Portfolio) createPayload.getData();
    BigDecimal investmentAmount = new BigDecimal(1000);
    Map<String, Float> stockWeights = new HashMap<>();
    stockWeights.put("IBM", 50.0f);
    stockWeights.put("AAPL", 60.0f);
    portfolioController.getPortfolioService().valueBasedInvestment("Test",
        investmentAmount,
        LocalDate.of(2023, 9, 4), stockWeights);
  }


}

