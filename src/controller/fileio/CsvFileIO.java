package controller.fileio;

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
import model.Portfolio;
import model.PortfolioInterface;
import model.Tradable;
import model.transactions.TranactionInfo;

/**
 * Class to read and write to a CSV file.
 */
public class CsvFileIO implements FileIO {

  /**
   * Reads the file.
   *
   * @param filePath The path of the file to read.
   * @param type     The type of the portfolio to read.
   * @return List of portfolios
   */
  @Override
  public List<PortfolioInterface> readFile(String filePath, String type) throws IOException {
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
        if (!type.equals(data[5])) {
          throw new IllegalArgumentException("Invalid Portfolio Type");
        }
        if (Float.parseFloat(data[2]) < 0) {
          portfolio.sellStock(data[1], (int) Float.parseFloat(data[2]) * -1,
              LocalDate.parse(data[4]), new BigDecimal(data[3]));
        } else {
          portfolio.addStock(data[1], (int) Float.parseFloat(data[2]), new BigDecimal(data[3]),
              LocalDate.parse(data[4]));
        }
      });
      loadedPortfolios = new ArrayList<>(portfolioMap.values());
    }
    return loadedPortfolios;
  }

  /**
   * Writes to the file.
   *
   * @param portfolios portfolios to write to the file
   * @param filePath   path of the file to write
   * @param type       type of the portfolio to write
   * @return true if the file was written successfully
   * @throws IOException if there was an error writing to the file
   */
  @Override
  public Boolean writeFile(List<PortfolioInterface> portfolios, String filePath, String type)
      throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append(
          "Portfolio Name,Stock Symbol,Quantity,Purchase Price,Purchase Date,Portfolio Type\n");
      for (PortfolioInterface portfolio : portfolios) {
        for (Tradable stock : portfolio.getStocks()) {
          for (Map.Entry<LocalDate, TranactionInfo> entry : stock.getActivityLog().entrySet()) {
            LocalDate date = entry.getKey();
            TranactionInfo info = entry.getValue();
            BigDecimal price = info.getPrice();
            Float quantity = info.getQuantity();
            writer.append(String.join(",", portfolio.getName(), stock.getSymbol(),
                String.valueOf(quantity), price.toString(), date.toString(), type));
            writer.append("\n");
          }

        }
      }
      return true;
    } catch (IOException e) {
      throw new IOException("Error writing to file: " + e.getMessage());
    }
  }
}
