package Model;

import java.util.List;

public interface PortfolioInterface {
  void addStock(Stock stock);

  List<Stock> getStocks();

  String getName();
}
