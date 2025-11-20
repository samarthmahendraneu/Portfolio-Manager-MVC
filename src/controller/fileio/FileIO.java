package controller.fileio;

import java.io.IOException;
import java.util.List;
import model.PortfolioInterface;

/**
 * Interface for the FileIO class.
 */
public interface FileIO {

  /**
   * Reads the file and returns a list of portfolios.
   *
   * @param filePath The path of the file to read
   * @param type     The type of the portfolio to read
   * @return List of portfolios
   * @throws IOException If the file cannot be read
   *
   */
  List<PortfolioInterface> readFile(String filePath, String type) throws IOException;

  /**
   * Writes to the file.
   *
   * @param portfolio The list of portfolios to write to the file
   * @param filePath  The path of the file to write the portfolios to
   * @param type      The type of the portfolio to write
   * @return True if the write was successful, false otherwise
   * @throws IOException If the file cannot be written
   */
  Boolean writeFile(List<PortfolioInterface> portfolio, String filePath, String type)
      throws IOException;
}
