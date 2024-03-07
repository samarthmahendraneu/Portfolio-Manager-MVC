package Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {
  private final String name;
  private List<Stock> stocks = new ArrayList<>();

  public Portfolio(String name) {
    this.name = name;
  }
  public Portfolio(String name, List<Stock> initialStocks) {
    this.name = name;
    this.stocks = new ArrayList<>(initialStocks);
  }
  public void addStock(Stock stock) {
    stocks.add(stock);
  }

  public BigDecimal calculateTotalValue() {
    return stocks.stream()
            .map(stock -> stock.getPurchasePrice().multiply(BigDecimal.valueOf(stock.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public List<Stock> getStocks() {
    return new ArrayList<>(stocks); // Return a copy to protect internal list
  }
  public String getName() {
    return name;
  }


}
