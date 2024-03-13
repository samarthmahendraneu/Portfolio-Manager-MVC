package Controller;

import Model.Portfolio;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import View.View;

public class PortfolioMenuController implements PortfolioMenuControllerInterface {

  private static final Scanner scanner = new Scanner(System.in);
  private final PortfolioControllerInterface portfolioController;
  private final View view;

  public PortfolioMenuController(PortfolioControllerInterface portfolioController, View view) {
    this.portfolioController = portfolioController;
    this.view = view;
  }

  /**
   * Display the main menu.
   */
  public void displayMainMenu() {
    boolean running = true;
    while (running) {
      try {
        view.displayMainMenu(System.out);
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
          case 1:
            createNewPortfolio();
            break;
          case 2:
            examinePortfolio();
            break;
          case 3:
            calculatePortfolioValue();
            break;
          case 4:
            savePortfolio();
            break;
          case 5:
            loadPortfolio();
            break;
          case 6:
            System.out.println("Exiting...");
            running = false;
            break;
          default:
            System.out.println("Invalid option. Please try again.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        scanner.nextLine(); // Consume newline
      }
    }
  }

  /**
   * Create a new portfolio.
   */
  public void createNewPortfolio() {
    try {
      System.out.println("Enter new portfolio name:");
      String name = scanner.nextLine().trim();
      Payload payload = portfolioController.createNewPortfolio(name);
      if (this.printIfError(payload)) {
        return;
      }
      Portfolio newPortfolio = (Portfolio) payload.getData();
      boolean flag = true;
      System.out.println("Enter the stocks you want to add to the portfolio");

      while (flag) {
        System.out.println("Enter the stock symbol:");
        String symbol = scanner.nextLine().trim();
        System.out.println("Enter the quantity of the stock:");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        LocalDate date;
        while (true) {
          System.out.println("Enter the purchase date (YYYY-MM-DD):");
          String dateString = scanner.nextLine().trim();
          try {
            date = LocalDate.parse(dateString);
          } catch (Exception e) {
            System.out.println("Invalid date format. Please try again.");
            continue;
          }
          date = LocalDate.parse(dateString);
          if (!date.isBefore(LocalDate.now())) {
            System.out.println("Date must be before today. Please try again.");
            continue;
          }
          DayOfWeek dayOfWeek = date.getDayOfWeek();
          if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            System.out.println("Date must be on a weekday. Please try again.");
            continue;
          }
          break;
        }
        payload = portfolioController.addStockToPortfolio(newPortfolio, symbol, quantity, date);
        if (this.printIfError(payload)) {
          return;
        }
        System.out.println("Press q to exit, Press n to go on");
        String exitChar = scanner.nextLine().trim();
        if (exitChar.equals("q")) {
          flag = false;
        }
      }
      System.out.println("Portfolio '" + name + "' has been created and populated.");
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Examine a portfolio.
   */
  public void examinePortfolio() {
    try {
      view.displayAvailablePortfolios(portfolioController.getPortfolioService().listPortfolioNames(), System.out);
      System.out.println("Enter the name of the portfolio to examine:");
      String name = scanner.nextLine().trim();
      portfolioController.getPortfolioService().getPortfolioByName(name)
          .ifPresentOrElse(
              portfolio -> {
                try {
                  view.displayPortfolioDetails(name, portfolio.getStocks(), System.out);
                } catch (IOException e) {
                  System.out.println("Error displaying portfolio details: " + e.getMessage());
                }
              },
              () -> System.out.println("Portfolio not found.")
          );
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }


  /**
   * Calculate the portfolio value.
   */
  public void calculatePortfolioValue() {
    try {
      System.out.println("Enter the name of the portfolio:");
      String name = scanner.nextLine().trim();
      System.out.println("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      String dateInput = scanner.nextLine().trim();
      Payload payload = portfolioController.calculatePortfolioValue(name, LocalDate.parse(dateInput));
      if (this.printIfError(payload)) {
        return;
      }

      Optional<BigDecimal> portfolioValue = (Optional<BigDecimal>) payload.getData();
      if (portfolioValue.isPresent()) {
        BigDecimal value = portfolioValue.get();
        view.displayPortfolioValue(name, dateInput, value.toString(), System.out);
      } else {
        System.out.println("No value found for the portfolio '" + name + "' on " + dateInput);
      }
    } catch (Exception e) {
      System.out.println("Error calculating portfolio value: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Save the portfolio.
   */
  public void savePortfolio() {
    try {
      System.out.println("Enter the file path to save the portfolio:");
      String filePath = scanner.nextLine().trim();
      Object payload = portfolioController.savePortfolio(filePath);
      if (((Payload)payload).isError()) {
        System.out.println("Error: " + ((Payload) payload).getMessage());
        return;
      }
      view.displaySaveSuccess(filePath, System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  /**
   * Load the portfolio.
   */
  public void loadPortfolio() {
    try {
      System.out.println("Enter the file path to load portfolios from:");
      String filePath = scanner.nextLine().trim();
      Object payload = portfolioController.loadPortfolio(filePath);
      if (Objects.nonNull(payload) && ((Payload) payload).isError()) {
        System.out.println("Error: " + payload);
        return;
      }
      view.displayLoadSuccess(System.out);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      scanner.nextLine(); // Consume newline
    }
  }

  public boolean printIfError(Payload payload) {
    if (payload.isError()) {
      try{
        view.displayError(payload.getMessage(), System.out);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
      return true;
    }
    return false;
  }
}
