package view;


import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Tradable;

/**
 * view class for the Portfolio Management System.
 */
public class View implements UnifiedViewInterface {

  private final Appendable out;
  private Readable in;

  /**
   * Constructor for the view class.
   */
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
  public void inputMessage(String message) throws IllegalStateException {
    try {
      this.out.append(message);

    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * This method is used for inputStream.
   *
   * @return the line read from the input stream.
   */
  public String readLine() {
    Scanner scanner = new Scanner(this.in);
    return scanner.nextLine();
  }

  /**
   * This method is used for inputStream.
   *
   * @return the integer read from the input stream.
   */
  public Integer readInt() {
    Scanner scanner = new Scanner(this.in);
    Integer res = scanner.nextInt();
    scanner.nextLine();
    return res;

  }

  /**
   * This method is used for input.
   *
   * @param prompt The prompt to display to the user.
   * @return the user's input.
   */
  @Override
  public String requestInput(String prompt) {
    Scanner scanner = new Scanner(this.in);
    System.out.println(prompt);
    return scanner.nextLine().trim();
  }

  /**
   * set the input stream.
   *
   * @param in the input stream.
   */
  public void setStreamableInput(Readable in) {
    this.in = in;
  }

  /**
   * get the output stream.
   *
   * @return the output stream.
   */
  public Appendable getStreamableOutput() {
    return this.out;
  }


  /**
   * display main menu to choose between flexible and normal portfolio management.
   */
  public void displayMainMenu() throws IOException {
    this.out.append("\nPortfolio Management System:\n");
    this.out.append("1.Normal Portfolio Management \n");
    this.out.append("2.Flexible Portfolio Management\n");
    this.out.append("3. Exit\n");
    this.out.append("Select an option: \n");
  }

  /**
   * Display the main menu options for flexible portfolio management.
   *
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayFlexiblePortfolioMenu() throws IOException {
    this.out.append("\nPortfolio Management System:\n");
    this.out.append("1. Create a new portfolio\n");
    this.out.append("2. Examine a portfolio\n");
    this.out.append("3. Calculate portfolio value\n");
    this.out.append("4. Add Stock to Portfolio\n");
    this.out.append("5. Sell Stock from Portfolio\n");
    this.out.append("6. Calculate Investment\n");
    this.out.append("7. Save portfolio\n");
    this.out.append("8. Load portfolio\n");
    this.out.append("9. Graph\n");
    // cross over days
    this.out.append("10. Inspect Stock performance\n");
    // moving average crossover days
    this.out.append("11. Calculate X-Day Moving Average\n");
    this.out.append("12. Crossover Days\n");
    this.out.append("13. Moving Crossover Days\n");
    this.out.append("14. Dollar cost Averaging\n");
    this.out.append("15. Value based investment\n");
    this.out.append("16. Exit\n");
    this.out.append("Select an option: \n");
  }


  /**
   * Display the main menu options for Normal portfolio management.
   *
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayNormalPortfolioMenu() throws IOException {
    this.out.append("\nPortfolio Management System:\n");
    this.out.append("1. Create a new portfolio\n");
    this.out.append("2. Examine a portfolio\n");
    this.out.append("3. Calculate portfolio value\n");
    this.out.append("4. Save portfolio\n");
    this.out.append("5. Load portfolio\n");
    this.out.append("6. Graph\n");
    this.out.append("7. Inspect Stock performance\n");
    this.out.append("8. Calculate X-Day Moving Average\n");
    this.out.append("9. Crossover Days\n");
    this.out.append("10. Moving Crossover Days\n");
    this.out.append("11. Dollar cost Averaging\n");
    this.out.append("12. Exit\n");
    this.out.append("Select an option: \n");
  }



  /**
   * Display available portfolios.
   *
   * @param portfolioNames The names of the available portfolios.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displayAvailablePortfolios(List<String> portfolioNames) throws IOException {
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
  public void displayPortfolioDetails(String name, List<Tradable> stocks) throws IOException {
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
  public void displayPortfolioValue(String name, String date, String value) throws IOException {
    this.out.append("Value of the portfolio '").append(name).append("' on ").append(date)
        .append(": ").append(value).append("\n");
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
    this.out.append("Investment of the portfolio '").append(name).append("' on ").append(date)
        .append(": ").append(value).append("\n");
  }

  /**
   * Display a success message for saving portfolios.
   *
   * @param filePath The file path where the portfolios were saved.
   * @throws IOException If an error occurs while writing to the output.
   */
  public void displaySaveSuccess(String filePath) throws IOException {
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
   * Displace a success message for creating a new portfolio.
   */
  public void displayStockAdded(String portfolioName, String stockSymbol, int quantity)
      throws IOException {
    this.out.append("Stock ").append(stockSymbol).append(" with quantity ")
        .append(String.valueOf(quantity)).append(" added to portfolio ").append(portfolioName)
        .append("\n");
  }

  /**
   * Display a success message for selling stocks.
   *
   * @param portfolioName The name of the portfolio.
   * @param symbol        The symbol of the stock.
   * @param quantity      The quantity of the stock.
   */
  public void displayStockSold(String portfolioName, String symbol, int quantity)
      throws IOException {
    this.out.append("Stock ").append(symbol).append(" with quantity ")
        .append(String.valueOf(quantity)).append(" sold from portfolio ").append(portfolioName)
        .append("\n");
  }

  /**
   * Display a message.
   *
   * @param message The message to display.
   */
  @Override
  public void displayMessage(String message) {
    System.out.println(message);
  }

  /**
   * Display an error message.
   *
   * @param errorMessage The error message to display.
   */
  public void displayError(String errorMessage) {
    inputMessage("Error: " + errorMessage);
  }

  /**
   * Display the crossover days for the given stock symbol.
   *
   * @param symbol    The stock symbol.
   * @param startDate The start date of the date range.
   * @param endDate   The end date of the date range.
   * @param dates     The crossover days.
   */
  public void displayCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      List<LocalDate> dates) throws IOException {
    this.out.append("Crossover (Buy) days for stock ").append(symbol).append(" between ")
        .append(startDate.toString()).append(" and ").append(endDate.toString()).append(":\n");
    for (LocalDate date : dates) {
      this.out.append(date.toString()).append("\n");
    }
  }

  /**
   * Display the moving crossover days for the given stock symbol.
   *
   * @param symbol            The stock symbol.
   * @param startDate         The start date of the date range.
   * @param endDate           The end date of the date range.
   * @param shortMovingPeriod The number of days to consider for the short moving average.
   * @param longMovingPeriod  The number of days to consider for the long moving average.
   * @param result            The moving crossover days.
   */
  public void displayMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate,
      int shortMovingPeriod, int longMovingPeriod, Map<String, Object> result) throws IOException {
    List<LocalDate> goldenCrosses = (List<LocalDate>) result.get("goldenCrosses");
    List<LocalDate> deathCrosses = (List<LocalDate>) result.get("deathCrosses");
    List<LocalDate> movingCrossoverDays = (List<LocalDate>) result.get("movingCrossoverDays");

    this.out.append("Moving crossover days for stock ").append(symbol).append(" between ")
        .append(startDate.toString()).append(" and ").append(endDate.toString()).append(":\n")
        .append("Short moving period: ").append(String.valueOf(shortMovingPeriod))
        .append(", Long moving period: ").append(String.valueOf(longMovingPeriod)).append("\n");
    this.out.append("Golden Crosses / Upward trend:\n");
    for (LocalDate goldenDate : goldenCrosses) {
      this.out.append(goldenDate.toString()).append("\n");
    }
    // death crosses
    this.out.append("Death Crosses / downward trend:\n");
    for (LocalDate deathDate : deathCrosses) {
      this.out.append(deathDate.toString()).append("\n");
    }
    // moving crossover days
    this.out.append("Moving Crossover Days:\n");
    for (LocalDate movingDate : movingCrossoverDays) {
      this.out.append(movingDate.toString()).append("\n");
    }

  }
}
