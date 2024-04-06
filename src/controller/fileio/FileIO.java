package Controller.fileio;

import java.io.IOException;
import java.util.List;
import Model.PortfolioInterface;

public interface FileIO {

  /**
   * Reads the file
   *
   * @return
   */
  List<PortfolioInterface> readFile(String filePath) throws IOException;

  /**
   * Writes to the file
   */
  Boolean writeFile(List<PortfolioInterface> portfolio, String filePath) throws IOException;
}
