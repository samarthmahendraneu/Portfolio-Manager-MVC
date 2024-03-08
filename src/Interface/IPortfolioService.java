package Interface;

import Model.Portfolio;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPortfolioService {
  void addPortfolio(Portfolio portfolio);

  void addStockToPortfolio(String portfolioName, String symbol, int quantity, LocalDate date);

  Optional<Portfolio> getPortfolioByName(String name);

  BigDecimal calculatePortfolioValue(String portfolioName, LocalDate onDate);

  List<String> listPortfolioNames();

  void savePortfoliosToCSV(String filePath) throws IOException;

  void loadPortfoliosFromCSV(String filePath) throws IOException;

  boolean portfolioExists(String portfolioName);
}
