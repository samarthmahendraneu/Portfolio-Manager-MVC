import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

  /**
   * Sets up the test environment by creating a new StockService and PortfolioController.
   */
  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioController = new PortfolioController(stockService);
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
        stock.getPurchaseDate());
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
        stock.getPurchaseDate());
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
        stock.getPurchaseDate());
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
    assertEquals(BigDecimal.ZERO, ((Optional<BigDecimal>) payload.getData()).get());
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
    // examine value on sunday February 4 should be same as February 2
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
    Payload stock_price_3 = stockService.fetchPreviousClosePrice("MSFT",
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

  /**
   * Tests creating portfolios with valid names and verifies they are correctly listed.
   */
  @Test
  public void testListPortfolioNames() {
    portfolioController.createNewPortfolio("FirstPortfolio");
    portfolioController.createNewPortfolio("SecondPortfolio");

    int numberOfPortfolios = portfolioController.getNumPortfolios();
    assertEquals(2, numberOfPortfolios);
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
   * Tests that adding a stock with a negative quantity results in an appropriate error message.
   */
  @Test
  public void testAddStockWithNegativeQuantity() {
    String portfolioName = "MyPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    Payload result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "AAPL", -10, LocalDate.now());

    assertTrue(result.isError());
    assertEquals("Quantity must be positive: -10", result.getMessage());
  }

  /**
   * Checks that adding a stock with a future purchase date results in an error message.
   */
  @Test
  public void testAddStockWithFutureDate() {
    String portfolioName = "FutureDatePortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate futureDate = LocalDate.now().plusDays(1);
    Payload result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "AAPL", 10, futureDate);

    assertTrue(result.isError());
    assertEquals("Date cannot be in the future: " + futureDate, result.getMessage());
  }

  /**
   * Validates that creating a portfolio with an empty name results in an error.
   */
  @Test
  public void testCreatePortfolioWithEmptyName() {
    Payload result = portfolioController.createNewPortfolio("");
    assertTrue(result.isError());
    assertEquals("Portfolio name cannot be empty", result.getMessage());
  }

  /**
   * Confirms creating portfolios with duplicate names is not allowed and results in an error.
   */
  @Test
  public void testCreatePortfolioWithDuplicateName() {
    String portfolioName = "DuplicateName";
    portfolioController.createNewPortfolio(portfolioName);
    Payload resultDuplicate = portfolioController.createNewPortfolio(portfolioName);
    assertTrue(resultDuplicate.isError());
    assertEquals("Portfolio already exists: " + portfolioName, resultDuplicate.getMessage());
  }

  /**
   * Tests creating and listing 25 unique portfolios, verifying all are correctly listed.
   */
  @Test
  public void testCreatingAndListing25Portfolios() {
    int numberOfPortfolios = 25;
    for (int i = 1; i <= numberOfPortfolios; i++) {
      String portfolioName = "Portfolio" + i;
      portfolioController.createNewPortfolio(portfolioName);
    }

    assertEquals(" portfolios does not match", numberOfPortfolios,
        portfolioController.getNumPortfolios());
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
   * Adding a stock which already exists.
   */
  @Test
  public void testAddDuplicateStockForSameDate() {
    String portfolioName = "DupStockPortfolio";
    portfolioController.createNewPortfolio(portfolioName);
    LocalDate purchaseDate = LocalDate.now().minusDays(5);
    portfolioController.addStockToPortfolio(new Portfolio(portfolioName), "GOOGL", 5, purchaseDate);
    Payload result = portfolioController.addStockToPortfolio(
        new Portfolio(portfolioName), "GOOGL", 5, purchaseDate);

    assertTrue(result.isError());
    assertTrue(result.getMessage().contains("Stock already exists"));
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
    assertTrue(result.isSuccess());
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


}