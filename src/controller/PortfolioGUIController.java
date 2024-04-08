package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;

import View.GUIView;
import model.PortfolioInterface;
import model.service.PortfolioService;
import model.service.PortfolioServiceInterface;
import model.service.StockService;
import model.service.StockServiceInterface;

/**
 * GUI controller for managing the interaction between GUI view and portfolio operations.
 */
public class PortfolioGUIController {
  private final PortfolioServiceInterface portfolioService;

  private static StockServiceInterface stockService = new StockService("FIR1DN0VB7SQ4SGD");

  private final GUIView view;

  public PortfolioGUIController(PortfolioServiceInterface portfolioService, StockServiceInterface stockService, GUIView view) {
    this.portfolioService = portfolioService;
    this.stockService = stockService;
    this.view = view;
    initViewListeners();
  }

  private void initViewListeners() {
    view.setCreatePortfolioAction(e -> createNewPortfolio());
    view.setExaminePortfolioButtonListener(e->examinePortfolio());
    view.setCalculatePortfolioValueButtonListener(e->calculatePortfolioValue());
    view.setSavePortfolioButtonListener(e->savePortfolio());
    view.setLoadPortfolioButtonListener(e->loadPortfolio());
    view.setGraphButtonListener(e->calculateGraph());
    view.setInspectStockPerformanceButtonListener(e->inspectStockPerformance());
    view.setAddButtonListener(e->addStockToPortfolio());
    view.setSellButtonListener(e->sellStockFromPortfolio());
    // Continue initializing listeners for other buttons...
  }



  private void createNewPortfolio() {
    String name = view.showInputDialog("Enter new portfolio name:");
    if (name == null || name.trim().isEmpty()) {
      view.showMessageDialog( "Portfolio name cannot be empty.");
      return;
    }

    try {
      PortfolioInterface newPortfolio = portfolioService.createNewPortfolio(name);

      boolean addMoreStocks = true;
      while (addMoreStocks) {
        String symbol = view.showInputDialog("Enter stock symbol to add to the portfolio," +
                " or cancel to finish:");
        if (symbol == null || symbol.trim().isEmpty()) {
          addMoreStocks = false;
          continue;
        }

        Integer quantity = null;
        while (quantity == null) {
          try {
            String quantityString = view.showInputDialog( "Enter quantity of the stock:");
            quantity = Integer.parseInt(quantityString);
            if (quantity <= 0) throw new NumberFormatException();
          } catch (NumberFormatException e) {
            view.showMessageDialog("Please enter a valid quantity greater than 0.");
            continue;
          }
        }

        LocalDate date = null;
        while (date == null) {
          try {
            String dateString = view.showInputDialog( "Enter the purchase date " +
                    "(YYYY-MM-DD):");
            date = LocalDate.parse(dateString);
          } catch (DateTimeParseException e) {
            view.showMessageDialog( "Please enter a valid date in the format YYYY-MM-DD.");
            continue;
          }
        }

        // Add stock to the newly created portfolio
        portfolioService.addStockToPortfolio(name, symbol, quantity, date);
        view.showMessageDialog("Stock has been added successfully");

      }

      view.showMessageDialog( "Portfolio '" + name + "' has been created and populated.");
    } catch (Exception e) {
      view.showMessageDialog( "Error creating portfolio, Please try again");
    }
  }

  public void examinePortfolio() {
    String name = view.showInputDialog("Enter the name of the portfolio to examine:");
    if (name != null && !name.isEmpty()) {
      PortfolioInterface portfolio = portfolioService.getPortfolioByName(name).orElse(null);

      if (portfolio != null) {
        // For simplicity, just showing the names and quantities of stocks in a message dialog
        StringBuilder details = new StringBuilder("Stocks in ").append(name).append(":\n");
        portfolio.getStocks().forEach(stock ->
                details.append(stock.getSymbol()).append(" - Quantity: ")
                       .append(stock.getQuantity()).append("\n"));
        view.showMessageDialog(details.toString());
      } else {
        view.showMessageDialog( "Portfolio not found.");
      }
    }
  }

  public void calculatePortfolioValue() {
    String name = view.showInputDialog( "Enter the name of the portfolio:");
    if (name != null && !name.isEmpty()) {
      String dateInput = view.showInputDialog("Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      if (dateInput != null && !dateInput.isEmpty()) {
        try {
          LocalDate date = LocalDate.parse(dateInput);
          Optional<BigDecimal> value = portfolioService.calculatePortfolioValue(name, date);
          if (!value.isEmpty()) {
            view.showMessageDialog("Value of the portfolio '" + name + "' on " + dateInput + ": " + value.toString());
          } else {
            view.showMessageDialog( "Error: Please try again");
          }
        } catch (Exception e) {
          view.showMessageDialog("Error calculating portfolio value: ");
        }
      }
    }
  }

  public void savePortfolio() {
    String filePath = view.showInputDialog("Enter the file path to save the portfolio (.csv):");
    if (filePath == null || filePath.trim().isEmpty()) {
      view.showMessageDialog("File path cannot be empty.");
      return;
    }

    try {
       portfolioService.savePortfoliosToCSV(filePath.trim());
       view.showMessageDialog("Portfolio has been saved to path"+filePath);

    }
     catch (Exception e) {
      view.showMessageDialog("Error: Invalid Path");
    }
  }

  public void loadPortfolio() {
    String filePath = view.showInputDialog("Enter the file path to load the portfolio (.csv):");
    if (filePath == null || filePath.trim().isEmpty()) {
      view.showMessageDialog("File path cannot be empty.");
      return;
    }

    try {
      portfolioService.loadPortfoliosFromCSV(filePath.trim());
      view.showMessageDialog("Portfolio has been loaded from path"+filePath);

    }
    catch (Exception e) {
      view.showMessageDialog("Error: Invalid Path");
    }
  }

  public void calculateGraph() {
    // Prompt for Stock or Portfolio name
    String name = view.showInputDialog("Enter Stock or Portfolio name:");
    if (name == null || name.isEmpty()) {
      view.showMessage("Operation cancelled or no name entered.");
      return;
    }

    // Prompt for Start Date
    LocalDate startDate = view.promptForDate("Enter Start Date (YYYY-MM-DD):");
    if (startDate == null) return; // Error message handled within validateAndParseDate

    // Prompt for End Date
    LocalDate endDate = view.promptForDate("Enter End Date (YYYY-MM-DD):");
    if (endDate == null) return; // Error message handled within validateAndParseDate

    // Generate the graph panel using the method from earlier example
    Map<LocalDate, BigDecimal> values =
            portfolioService.plotPerformanceChartGUI(name, startDate, endDate); // Adapt this method for your GUI

    // Display the chart

    // Now, tell the view to display this chart
    view.displayPerformanceChart(values);  }

  private void addStockToPortfolio(){
    // Prompt for the portfolio name
    String portfolioName = JOptionPane.showInputDialog(view, "Enter the portfolio name:");
    if (portfolioName == null || portfolioName.isEmpty()) {
      view.showMessage("Portfolio name cannot be empty.");
      return;
    }

    // Prompt for the stock symbol
    String symbol = JOptionPane.showInputDialog(view, "Enter the stock symbol:");
    if (symbol == null || symbol.isEmpty()) {
      view.showMessage("Stock symbol cannot be empty.");
      return;
    }

    // Prompt for the quantity of stock to be added
    int quantity;
    try {
      String quantityStr = view.showInputDialog("Enter the quantity:");
      if (quantityStr == null || quantityStr.isEmpty()) {
        view.showMessage("Quantity cannot be empty.");
        return;
      }
      quantity = Integer.parseInt(quantityStr);
      if (quantity <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException ex) {
      view.showMessage("Invalid quantity. Please enter a positive integer.");
      return;
    }

    LocalDate date = view.promptForDate("Enter Date (YYYY-MM-DD):");;

    try {
      PortfolioInterface  success = portfolioService.addStockToPortfolio(
              portfolioName, symbol, quantity, date);
      if (success!=null) {
        view.showMessage("Stock added successfully to the portfolio.");
      } else {
        view.showMessage("Failed to add stock to the portfolio.");
      }
    } catch (Exception ex) {
      view.showMessage("Error: " + ex.getMessage());
    }
  }

  private void sellStockFromPortfolio() {
    String portfolioName = view.showInputDialog("Enter the portfolio name to sell stock from:");
    if (portfolioName == null || portfolioName.trim().isEmpty()) {
      view.showMessage("Portfolio name cannot be empty.");
      return;
    }

    String symbol = view.showInputDialog("Enter the stock symbol to sell:");
    if (symbol == null || symbol.trim().isEmpty()) {
      view.showMessage("Stock symbol cannot be empty.");
      return;
    }

    int quantity;
    try {
      String quantityStr = view.showInputDialog("Enter the quantity to sell:");
      if (quantityStr == null || quantityStr.trim().isEmpty()) {
        view.showMessage("Quantity cannot be empty.");
        return;
      }
      quantity = Integer.parseInt(quantityStr);
      if (quantity <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException ex) {
      view.showMessage("Invalid quantity. Please enter a positive integer.");
      return;
    }

    LocalDate date = view.promptForDate("Enter date of purchase");
    boolean success = portfolioService.sellStockFromPortfolio(portfolioName, symbol, quantity, date);
    if (success) {
      view.showMessage("Stock sold successfully from the portfolio.");
    } else {
      view.showMessage("Failed to sell stock from the portfolio.");
    }
  }

  /**
   * Inspect the stock performance for a given stock symbol on a specified date.
   */
  public void inspectStockPerformance() {
    String symbol = this.view.showInputDialog("Enter the stock symbol:");

    LocalDate date = view.promptForDate("Enter the date (YYYY-MM-DD) to inspect the stock performance:");
    if (date == null) return; // Error message handled within validateAndParseDate

    String result = stockService.inspectStockGainOrLoss(symbol, date);

    if (!result.isEmpty()) {
      this.view.showMessage("Stock Performance on " + date + ": " + result);
    } else {
      this.view.showMessage("Error: showing stock gain/loss. Please try again in sometime");
    }
  }

  public static void main(String[] args) {
    // Example main method to run the GUI
    SwingUtilities.invokeLater(() -> {

      PortfolioServiceInterface portfolioService = new PortfolioService( new StockService("FIR1DN0VB7SQ4SGD")); // Initialize your portfolio service here
      GUIView view = new GUIView();
      new PortfolioGUIController(portfolioService, stockService, view);
    });
  }
}
