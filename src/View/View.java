package View;


import Model.Tradable;
import java.io.IOException;
import java.util.List;

/**
 * View class for the Portfolio Management System.
 */
public class View {

  /**
   * Display the main menu options.
   *
   * @param out The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayMainMenu(Appendable out) throws IOException {
    out.append("\nPortfolio Management System:\n");
    out.append("1. Create a new portfolio\n");
    out.append("2. Examine a portfolio\n");
    out.append("3. Calculate portfolio value\n");
    out.append("4. Save portfolio\n");
    out.append("5. Load portfolio\n");
    out.append("6. Exit\n");
    out.append("7. Graph\n");
    out.append("8. Save Cache\n");
    out.append("9. Load Cache\n");
    out.append("6. Sell stocks from \n");
    out.append("Select an option: ");
  }

  /**
   * Display available portfolios.
   *
   * @param portfolioNames The names of the available portfolios.
   * @param out            The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayAvailablePortfolios(List<String> portfolioNames, Appendable out)
      throws IOException {
    out.append("Available portfolios:\n");
    for (String name : portfolioNames) {
      out.append(name).append("\n");
    }
  }

  /**
   * Display portfolio details.
   *
   * @param name   The name of the portfolio.
   * @param stocks The stocks in the portfolio.
   * @param out    The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayPortfolioDetails(String name, List<Tradable> stocks, Appendable out)
      throws IOException {
    out.append("Stocks in ").append(name).append(":\n");
    for (Tradable stock : stocks) {
      out.append(stock.getSymbol()).append(" - Quantity: ")
          .append(String.valueOf(stock.getQuantity()))
          .append(", Purchase Price: ").append(String.valueOf(stock.getPurchasePrice()))
          .append(", Purchase Date: ").append(stock.getPurchaseDate().toString()).append("\n");
    }
  }

  /**
   * Display portfolio value.
   *
   * @param name  The name of the portfolio.
   * @param date  The date for which the value is to be calculated.
   * @param value The value of the portfolio.
   * @param out   The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayPortfolioValue(String name, String date, String value, Appendable out)
      throws IOException {
    out.append("Value of the portfolio '").append(name).append("' on ").append(date).append(": ")
        .append(value).append("\n");
  }

  /**
   * Display a success message for saving portfolios.
   *
   * @param filePath The file path where the portfolios were saved.
   * @param out      The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displaySaveSuccess(String filePath, Appendable out) throws IOException {
    out.append("Portfolios have been saved successfully to ").append(filePath).append("\n");
  }

  /**
   * Display a success message for loading portfolios.
   *
   * @param out The output to write to.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayLoadSuccess(Appendable out) throws IOException {
    out.append("Portfolios have been loaded successfully.\n");
  }

  /**
   * Display an error message.
   *
   * @param out          The output to write to.
   * @param errorMessage The error message to display.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayError(String errorMessage, Appendable out) throws IOException {
    out.append("Error: ").append(errorMessage).append("\n");
  }
}
