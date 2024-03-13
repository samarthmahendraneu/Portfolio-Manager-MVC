package View;

import Model.Service.StockService;
import java.util.Scanner;
import Controller.PortfolioController;

/**
 * Main class for the Portfolio Management System.
 */
public class Main {

  private static final Scanner scanner = new Scanner(System.in);

  /**
   * Main method for the Portfolio Management System.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    boolean running = true;
    PortfolioController portfolioController = new PortfolioController(
        new StockService("W0M1JOKC82EZEQA8"));
    PortfolioView view = new PortfolioView(portfolioController);

    while (running) {
      System.out.println("\nPortfolio Management System:");
      System.out.println("1. Create a new portfolio");
      System.out.println("2. Examine a portfolio");
      System.out.println("3. Calculate portfolio value");
      System.out.println("4. Save portfolio");
      System.out.println("5. Load portfolio");
      System.out.println("6. Exit");
      System.out.print("Select an option: ");

      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          view.createNewPortfolio();
          break;
        case 2:
          view.examinePortfolio();
          break;
        case 3:
          view.calculatePortfolioValue();
          break;
        case 4:
          view.savePortfolio();
          break;
        case 5:
          view.loadPortfolio();
          break;
        case 6:
          System.out.println("Exiting...");
          running = false;
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    }
    scanner.close(); // Close the scanner to prevent resource leak
  }
}
