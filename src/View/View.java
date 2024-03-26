package View;


import Model.Tradable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

/**
 * View class for the Portfolio Management System.
 */
public class View {
  
  private final Appendable out;
  private final Readable in;
  public View() {
    this.out = System.out;
    this.in = new InputStreamReader(System.in);
  }


  /**
   * This method writes a message to the appendable.
   *
   * @param message the message to write.
   * @throws IllegalStateException if there is an error in writing.
   */
  public void writeMessage(String message) throws IllegalStateException {
    try {
      this.out.append(message);

    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * This method is used for inputStream
   */
  public String readLine() {
    Scanner scanner = new Scanner(this.in);
    String line = scanner.nextLine();
    return line;
  }

  /**
   * This method is used for inputStream
   */
  public Integer readInt() {
    Scanner scanner = new Scanner(this.in);
    Integer res = scanner.nextInt();
    scanner.nextLine();
    return res;

  }

  /**
   * Display the main menu options.
   *
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayMainMenu() throws IOException {
    this.out.append("\nPortfolio Management System:\n");
    this.out.append("1. Create a new portfolio\n");
    this.out.append("2. Examine a portfolio\n");
    this.out.append("3. Calculate portfolio value\n");
    this.out.append("4. Save portfolio\n");
    this.out.append("5. Load portfolio\n");
    this.out.append("6. Exit\n");
    this.out.append("7. Graph\n");
    this.out.append("8. Save Cache\n");
    this.out.append("9. Load Cache\n");
    this.out.append("10. Add Stock to Portfolio\n");
    this.out.append("11. Sell Stock from Portfolio\n");
    this.out.append("12. Calculate Investment\n");
    this.out.append("Select an option: \n");
  }

  /**
   * Display available portfolios.
   *
   * @param portfolioNames The names of the available portfolios.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayAvailablePortfolios(List<String> portfolioNames)
      throws IOException {
    this.out.append("Available portfolios:\n");
    for (String name : portfolioNames) {
      this.out.append(name).append("\n");
    }
  }

  /**
   * Display portfolio details.
   *
   * @param name   The name of the portfolio.
   * @param stocks The stocks in the portfolio.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayPortfolioDetails(String name, List<Tradable> stocks)
      throws IOException {
    this.out.append("Stocks in ").append(name).append(":\n");
    for (Tradable stock : stocks) {
      // display stock name and quantity
      this.out.append(stock.getSymbol()).append(" - Quantity: ")
          .append(String.valueOf(stock.getQuantity()));
      // print new line
      this.out.append("\n");
    }
  }

  /**
   * Display portfolio value.
   *
   * @param name  The name of the portfolio.
   * @param date  The date for which the value is to be calculated.
   * @param value The value of the portfolio.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayPortfolioValue(String name, String date, String value)
      throws IOException {
    this.out.append("Value of the portfolio '").append(name).append("' on ").append(date).append(": ")
        .append(value).append("\n");
  }

  /**
   * Display portfolio investment on a given date.
   *
   * @param name  The name of the portfolio.
   * @param date  The date for which the value is to be calculated.
   * @param value The value of the portfolio.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayPortfolioInvestment(String name, String date, String value)
      throws IOException {
    this.out.append("Investment of the portfolio '").append(name).append("' on ").append(date).append(": ")
        .append(value).append("\n");
  }

  /**
   * Display a success message for saving portfolios.
   *
   * @param filePath The file path where the portfolios were saved.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displaySaveSuccess(String filePath, Appendable out) throws IOException {
    this.out.append("Portfolios have been saved successfully to ").append(filePath).append("\n");
  }

  /**
   * Display a success message for loading portfolios.
   *
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayLoadSuccess() throws IOException {
    this.out.append("Portfolios have been loaded successfully.\n");
  }

  /**
   * Displace displayStockAdded
   */
  public void displayStockAdded(String portfolioName, String stockSymbol, int quantity) throws IOException {
    this.out.append("Stock ").append(stockSymbol).append(" with quantity ")
        .append(String.valueOf(quantity))
        .append(" added to portfolio ").append(portfolioName).append("\n");
  }

  /**
   * Display a success message for selling stocks.
   */
  public void displayStockSold(String portfolioName, String symbol, int quantity) throws IOException {
    this.out.append("Stock ").append(symbol).append(" with quantity ")
        .append(String.valueOf(quantity))
        .append(" sold from portfolio ").append(portfolioName).append("\n");
  }

  /**
   * Display an error message.
   *
   * @param errorMessage The error message to display.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayError(String errorMessage) throws IOException {
    this.out.append("Error: ").append(errorMessage).append("\n");
  }
}
