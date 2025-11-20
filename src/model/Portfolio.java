package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.service.StockServiceInterface;

/**
 * Class to represent a portfolio of stocks.
 */
public class Portfolio implements PortfolioInterface {

  private final String name;
  private List<Tradable> stocks = new ArrayList<>();

  /**
   * Constructor for the Portfolio class.
   *
   * @param name The name of the portfolio.
   */
  public Portfolio(String name) {
    this.name = name;
  }

  /**
   * Constructor for the Portfolio class.
   *
   * @param name          The name of the portfolio.
   * @param initialStocks The initial stocks in the portfolio.
   */
  public Portfolio(String name, List<Stock> initialStocks) {
    this.name = name;
    this.stocks = new ArrayList<>(initialStocks);
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   * @param purchasePrice The purchase price of the stock.
   * @param purchaseDate  The date on which the stock was purchased.
   */
  public void addStock(String symbol, int quantity, BigDecimal purchasePrice,
      LocalDate purchaseDate) {
    // check if the stock is already in the portfolio - > s.buy else new Stock
    this.stocks.stream().filter(s -> s.getSymbol().equals(symbol)).findFirst()
        .ifPresentOrElse(s -> s.buy(quantity, purchaseDate, purchasePrice),
            () -> this.stocks.add(new Stock(symbol, quantity, purchasePrice, purchaseDate)));
  }

  /**
   * Invest in the portfolio using weights.
   *
   * @param investmentAmount The amount to invest.
   * @param date             The  date of the investment.
   * @param stockService     The stock service to use.
   * @param stockWeights     A map of stock symbols to their respective weights.
   */
  public void investUsingWeights(Float investmentAmount, LocalDate date,
      StockServiceInterface stockService, Map<String, Float> stockWeights) {
    // check if the investment amount is positive
    if (investmentAmount <= 0) {
      throw new IllegalArgumentException("Invalid investment amount");
    }
    // check if the date is in the future
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Cannot invest in the future");
    }
    // check if the stock weights sum to 100
    if (stockWeights.values().stream().mapToDouble(Float::doubleValue).sum() != 100) {
      throw new IllegalArgumentException("Invalid stock weights");
    }
    for (Map.Entry<String, Float> entry : stockWeights.entrySet()) {
      String symbol = entry.getKey();
      Float weight = entry.getValue();
      BigDecimal stockInvestment = new BigDecimal(investmentAmount * (weight / 100));
      float quantity =
          stockInvestment.floatValue() / ((BigDecimal) stockService.fetchLastClosePrice(
              symbol, date).getData()).floatValue();
      // check if the stock is already in the portfolio - > s.buy else new Stock\
      // if not create a new stock
      this.stocks.stream().filter(s -> s.getSymbol().equals(symbol)).findFirst()
          .ifPresentOrElse(s -> s.buy(quantity, date, (BigDecimal) stockService
                  .fetchLastClosePrice(symbol, date).getData()),
              () -> this.stocks.add(new Stock(symbol, quantity,
                  (BigDecimal) stockService.fetchLastClosePrice(symbol, date).getData(), date)));

    }
  }

  /**
   * function that implements dollar cost averaging using helper function invest.
   *
   * @param amount       amount to invest
   * @param startDate    start date
   * @param endDate      end date
   * @param stockService stock service
   * @param frequency    frequency
   * @param stockWeights stock weights
   */
  public void dollarCostAveraging(BigDecimal amount, LocalDate startDate, LocalDate endDate,
      StockServiceInterface stockService, int frequency, Map<String, Float> stockWeights) {

    // start date should be before end date and start date should be before today
    if (startDate.isAfter(endDate) || startDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Invalid start date");
    }

    // frequency should be between 1 and 4
    if (frequency < 1 || frequency > 4) {
      throw new IllegalArgumentException("Invalid frequency");
    }

    // end date should be before today
    if (endDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Invalid end date");
    }

    // frequency 1 for daily, 2 for weekly, 3 for monthly, 4 for yearly
    LocalDate date = startDate;
    while (date.isBefore(endDate)) {
      this.investUsingWeights(amount.floatValue(), date, stockService, stockWeights);
      if (frequency == 1) {
        date = date.plusDays(1);
      } else if (frequency == 2) {
        date = date.plusWeeks(1);
      } else if (frequency == 3) {
        date = date.plusMonths(1);
      } else {
        date = date.plusYears(1);
      }
    }
  }


  /**
   * Sell a stock from the portfolio.
   *
   * @param stock    The stock to remove.
   * @param quantity The quantity of the stock to remove.
   * @param date     The date of the sale.
   * @throws IllegalArgumentException if the stock is not in the portfolio.
   */
  public void sellStock(String stock, int quantity, LocalDate date, BigDecimal sellingPrice)
      throws IllegalArgumentException {
    // throw an if date is in the future
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Cannot sell stock in the future");
    }

    // stream through the stocks in the portfolio and find the stock to sell
    this.getStocks().stream()
        .filter(s -> s.getSymbol().equals(stock))
        .findFirst()
        .ifPresentOrElse(
            s -> {
              if (s.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock to sell");
              }
              s.sell(quantity, date, sellingPrice);
            },
            () -> {
              throw new IllegalArgumentException("Stock not found");
            });
  }


  /**
   * calculates the total value of the portfolio on a given date.
   *
   * @param stockService The stock service to use to fetch stock prices.
   * @param date         The date on which to calculate the value.
   */
  @Override
  public BigDecimal calculateValue(StockServiceInterface stockService, LocalDate date) {
    return this.stocks.stream()
        .map(s -> s.calculateValue(stockService, date))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculate investment in the portfolio.
   *
   * @param date The date on which to calculate the investment.
   * @return The total investment in the portfolio.
   */
  public BigDecimal calculateInvestment(LocalDate date) {
    BigDecimal investment = BigDecimal.ZERO;
    for (Tradable stock : this.stocks) {
      investment = investment.add(stock.calculateInvestment(date));
    }
    return investment;
  }

  /**
   * Adds a stock to the portfolio.
   *
   * @return The stocks in the portfolio.
   */
  public List<Tradable> getStocks() {
    return new ArrayList<>(stocks); // Return a copy to protect internal list
  }

  /**
   * Getter for the name of the portfolio.
   *
   * @return The name of the portfolio.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the quantity of a stock on a given date.
   *
   * @param symbol The symbol of the stock.
   * @param date   The date of the stock.
   * @return The quantity of the stock.
   */
  public float getStockQuantity(String symbol, LocalDate date) {
    return this.stocks.stream().filter(s -> s.getSymbol().equals(symbol)).findFirst()
        .map(s -> s.getQuantity(date)).orElse(0.0f);
  }

  /**
   * Get the portfolio details on a given date.
   */
  public List<Tradable> getPortfolio(LocalDate date) {
    List<Tradable> portfolioDetails = new ArrayList<>();
    for (Tradable stock : this.stocks) {
      if (stock.getQuantity(date) > 0) {
        portfolioDetails.add(new Stock(stock.getSymbol(), stock.getQuantity(date),
            null, null));
      }
    }
    return portfolioDetails;
  }


}
