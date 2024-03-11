import static org.junit.Assert.assertEquals;


import Model.Service.PortfolioService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import Controller.PortfolioControllerBasic;
import Model.Service.StockService;
import Model.Portfolio;
import Model.Stock;

public class PortfolioControllerTest {

  private PortfolioService portfolioService;
  private StockService stockService;
  private PortfolioControllerBasic portfolioControllerBasic;

  @Before
  public void setUp() {
    stockService = new StockService("W0M1JOKC82EZEQA8");
    portfolioService = new PortfolioService(stockService);
    portfolioControllerBasic = new PortfolioControllerBasic(stockService);
  }

  @Test
  public void testCreateNewPortfolio() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    assertEquals("Test Portfolio", portfolio.getName());
  }

  // empty portfolio name test
  @Test(expected = Exception.class)
  public void testCreateNewPortfolio_EmptyName() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("");
  }

  // duplicate portfolio name test
  @Test(expected = Exception.class)
  public void testCreateNewPortfolio_DuplicateName() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    Portfolio portfolio2 = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
  }

  @Test
  public void testAddStockToPortfolio() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now());
    portfolioControllerBasic.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
    assertEquals(1, portfolio.getStocks().size());
  }

  // add invalid date test
  @Test(expected = Exception.class)
  public void testAddStockToPortfolio_InvalidDate() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 10, new BigDecimal("100.00"), LocalDate.now().plusDays(1));
    portfolioControllerBasic.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // quantity less than 1 test
  @Test(expected = Exception.class)
  public void testAddStockToPortfolio_InvalidQuantity() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    Stock stock = new Stock("AAPL", 0, new BigDecimal("100.00"), LocalDate.now());
    portfolioControllerBasic.addStockToPortfolio(portfolio, stock.getSymbol(), stock.getQuantity(),
        stock.getPurchaseDate());
  }

  // create multiple portfolios and add stocks to them
  @Test
  public void testCreateMultiplePortfoliosAndAddStocks() {
    Portfolio portfolio1 = portfolioControllerBasic.createNewPortfolio("Test Portfolio 1");
    Portfolio portfolio2 = portfolioControllerBasic.createNewPortfolio("Test Portfolio 2");
    portfolioControllerBasic.addStockToPortfolio(portfolio1, "AAPL", 10, LocalDate.now());
    portfolioControllerBasic.addStockToPortfolio(portfolio1, "GOOGL", 5, LocalDate.now());
    portfolioControllerBasic.addStockToPortfolio(portfolio2, "AAPL", 10, LocalDate.now());
    assertEquals(2, portfolioControllerBasic.getNumPortfolios());
    Portfolio portfolio3 = portfolioControllerBasic.getPortfolioService().getPortfolioByName("Test Portfolio 1").get();
    assertEquals(2, portfolio3.getStocks().size());
    Portfolio portfolio4 = portfolioControllerBasic.getPortfolioService().getPortfolioByName("Test Portfolio 2").get();
    assertEquals(1, portfolio4.getStocks().size());
  }

  // calculate portfolio value test on same day
  @Test
  public void testCalculatePortfolioValue() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    portfolioControllerBasic.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-06"));
    portfolioControllerBasic.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-06"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioControllerBasic.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-06"));
    assertEquals(total, value);
  }

  // calculate portfolio value test on future date
  @Test
  public void testCalculatePortfolioValue_FutureDate() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    portfolioControllerBasic.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioControllerBasic.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-06"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-07"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-07"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioControllerBasic.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-07"));
    assertEquals(total, value);
  }

  // examine value on sunday February 4 should be same as February 2
  @Test
  public void testCalculatePortfolioValue_Sunday() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    portfolioControllerBasic.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-04"));
    portfolioControllerBasic.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-04"));
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-02"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-02"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioControllerBasic.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-04"));
    assertEquals(total, value);
  }

  // examine, add 3 stocks and examine portfolio and examine before buying the last stock
  @Test
  public void testExaminePortfolio() {
    Portfolio portfolio = portfolioControllerBasic.createNewPortfolio("Test Portfolio");
    portfolioControllerBasic.addStockToPortfolio(portfolio, "AAPL", 10, LocalDate.parse("2024-02-06"));
    portfolioControllerBasic.addStockToPortfolio(portfolio, "GOOGL", 5, LocalDate.parse("2024-02-09"));
    portfolioControllerBasic.addStockToPortfolio(portfolio, "MSFT", 5, LocalDate.parse("2024-02-11"));

    // total value as of February 9
    BigDecimal stock_price_1 = stockService.fetchPriceOnDate("AAPL", LocalDate.parse("2024-02-09"));
    BigDecimal stock_price_2 = stockService.fetchPriceOnDate("GOOGL", LocalDate.parse("2024-02-09"));
    BigDecimal total = stock_price_1.multiply(new BigDecimal(10)).add(stock_price_2.multiply(new BigDecimal(5)));
    BigDecimal value = portfolioControllerBasic.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-09"));
    assertEquals(total, value);

    // value as of February 11
    BigDecimal stock_price_3 = stockService.fetchPreviousClosePrice("MSFT", LocalDate.parse("2024-02-11"));
    total = total.add(stock_price_3.multiply(new BigDecimal(5)));
    value = portfolioControllerBasic.calculatePortfolioValue("Test Portfolio", LocalDate.parse("2024-02-11"));
    assertEquals(total, value);
  }


  @Test(expected = Exception.class)
  public void testCalculatePortfolioValue_InvalidPortfolio() {

  }
}
