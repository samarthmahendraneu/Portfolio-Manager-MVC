package controller;

import java.util.Scanner;

import model.service.StockService;
import view.GUIViewU;
import view.UnifiedViewInterface;
import view.View;

/**
 * Main class for the Portfolio Management System.
 */
public class Main {

  /**
   * Main method for the Portfolio Management System.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    System.out.println("Select the interface type:");
    System.out.println("1. Graphical User Interface (GUI)");
    System.out.println("2. Textual User Interface (Console)");
    System.out.print("Enter your choice (1/2): ");

    Scanner scanner = new Scanner(System.in);
    int choice = scanner.nextInt();

    UnifiedViewInterface view = null;
    switch (choice) {
      case 1:
        // create a GUI view
        view = new GUIViewU();
        break;
      case 2:
        // create a text view
        view = new View();
        break;
      default:
        System.out.println("Invalid choice. Exiting...");
        System.exit(1);
    }

    PortfolioControllerInterface portfolioController = new PortfolioController(
        new StockService("FIR1DN0VB7SQ4SGD"));
    PortfolioMenuControllerInterface controller = new PortfolioMenuController(portfolioController,
        view);
  }


}
