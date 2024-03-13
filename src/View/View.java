package View;


import Model.Tradable;
import java.io.IOException;
import java.util.List;
import Model.Portfolio;
import Model.Tradable;

public class View {

  /**
   * Display the main menu options.
   */
  public void displayMainMenu(Appendable out) throws IOException {
    out.append("\nPortfolio Management System:\n");
    out.append("1. Create a new portfolio\n");
    out.append("2. Examine a portfolio\n");
    out.append("3. Calculate portfolio value\n");
    out.append("4. Save portfolio\n");
    out.append("5. Load portfolio\n");
    out.append("6. Exit\n");
    out.append("Select an option: ");
  }

  /**
   * Display available portfolios.
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
   */
  public void displayPortfolioValue(String name, String date, String value, Appendable out)
      throws IOException {
    out.append("Value of the portfolio '").append(name).append("' on ").append(date).append(": ")
        .append(value).append("\n");
  }

  /**
   * Display a success message for saving portfolios.
   */
  public void displaySaveSuccess(String filePath, Appendable out) throws IOException {
    out.append("Portfolios have been saved successfully to ").append(filePath).append("\n");
  }

  /**
   * Display a success message for loading portfolios.
   */
  public void displayLoadSuccess(Appendable out) throws IOException {
    out.append("Portfolios have been loaded successfully.\n");
  }

  /**
   * Display an error message.
   */
  public void displayError(String errorMessage, Appendable out) throws IOException {
    out.append("Error: ").append(errorMessage).append("\n");
  }
}
