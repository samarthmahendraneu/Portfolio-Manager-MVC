package Controller.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Model.Portfolio;
import Model.PortfolioInterface;
import Model.Tradable;

public class CsvFileIO implements FileIO{

  /**
   * Reads the file
   *
   * @param filePath
   * @return
   */
  @Override
  public List<PortfolioInterface> readFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      return null;
    }
    List<PortfolioInterface> loadedPortfolios;
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      reader.readLine(); // Skip header
      Map<String, Portfolio> portfolioMap = new HashMap<>();
      reader.lines().forEach(line -> {
        String[] data = line.split(",");
        Portfolio portfolio = portfolioMap.computeIfAbsent(data[0], Portfolio::new);
        portfolio.addStock(data[1], Integer.parseInt(data[2]), new BigDecimal(data[3]),
            LocalDate.parse(data[4]));
      });
      loadedPortfolios = new ArrayList<>(portfolioMap.values());
    }
    return loadedPortfolios;
  }

  /**
   * Writes to the file
   *
   * @param portfolios
   * @param filePath
   */
  @Override
  public Boolean writeFile(List<PortfolioInterface> portfolios, String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date\n");
      for (PortfolioInterface portfolio : portfolios) {
        for (Tradable stock : portfolio.getStocks()) {
          writer.append(String.join(",", portfolio.getName(), stock.getSymbol(),
              String.valueOf(stock.getQuantity()), stock.getPurchasePrice().toString(),
              stock.getPurchaseDate().toString())).append("\n");
        }
      }
      return true;
    } catch (IOException e) {
      throw new IOException("Error writing to file: " + e.getMessage());
    }
  }
}
