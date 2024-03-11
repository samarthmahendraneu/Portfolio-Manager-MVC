package View;

import View.PortfolioView;
import java.util.Scanner;


public class Main {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    boolean running = true;
    PortfolioView view = new PortfolioView();
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
  }
}
