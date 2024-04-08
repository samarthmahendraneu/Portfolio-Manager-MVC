package controller;

import java.awt.event.ActionEvent;
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
    // Continue initializing listeners for other buttons...
  }



  private void createNewPortfolio() {
    String name = JOptionPane.showInputDialog(view, "Enter new portfolio name:");
    if (name == null || name.trim().isEmpty()) {
      view.showMessageDialog( "Portfolio name cannot be empty.");
      return;
    }

    try {
      PortfolioInterface newPortfolio = portfolioService.createNewPortfolio(name);

      boolean addMoreStocks = true;
      while (addMoreStocks) {
        String symbol = view.showInputDialog("Enter stock symbol to add to the portfolio, or cancel to finish:");
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
            String dateString = JOptionPane.showInputDialog(view, "Enter the purchase date (YYYY-MM-DD):");
            date = LocalDate.parse(dateString);
          } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Please enter a valid date in the format YYYY-MM-DD.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            continue;
          }
        }

        // Add stock to the newly created portfolio
        portfolioService.addStockToPortfolio(name, symbol, quantity, date);
        view.showMessageDialog("Stock has been added successfully");

      }

      JOptionPane.showMessageDialog(view, "Portfolio '" + name + "' has been created and populated.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(view, "Error creating portfolio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void examinePortfolio() {
    String name = JOptionPane.showInputDialog(view, "Enter the name of the portfolio to examine:");
    if (name != null && !name.isEmpty()) {
      PortfolioInterface portfolio = portfolioService.getPortfolioByName(name).orElse(null);

      if (portfolio != null) {
        // For simplicity, just showing the names and quantities of stocks in a message dialog
        StringBuilder details = new StringBuilder("Stocks in ").append(name).append(":\n");
        portfolio.getStocks().forEach(stock -> details.append(stock.getSymbol()).append(" - Quantity: ").append(stock.getQuantity()).append("\n"));
        JOptionPane.showMessageDialog(view, details.toString());
      } else {
        JOptionPane.showMessageDialog(view, "Portfolio not found.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void calculatePortfolioValue() {
    String name = JOptionPane.showInputDialog(view, "Enter the name of the portfolio:");
    if (name != null && !name.isEmpty()) {
      String dateInput = JOptionPane.showInputDialog(view, "Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      if (dateInput != null && !dateInput.isEmpty()) {
        try {
          LocalDate date = LocalDate.parse(dateInput);
          Optional<BigDecimal> value = portfolioService.calculatePortfolioValue(name, date);
          if (!value.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Value of the portfolio '" + name + "' on " + dateInput + ": " + value.toString());
          } else {
            JOptionPane.showMessageDialog(view, "Error: ", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(view, "Error calculating portfolio value: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private void addStockToPortfolio(ActionEvent e){
      // This is simplified and assumes methods for adding stock are present in PortfolioServiceInterface
      String portfolioName = JOptionPane.showInputDialog(view, "Enter the portfolio name:");
      if (portfolioName != null && !portfolioName.isEmpty()) {
        String symbol = JOptionPane.showInputDialog(view, "Enter the stock symbol:");
        if (symbol != null && !symbol.isEmpty()) {
          int quantity = Integer.parseInt(JOptionPane.showInputDialog(view, "Enter the quantity:"));
          // Simplified: using current date. You should provide a way to input a date.
          LocalDate date = LocalDate.now();
          PortfolioInterface success = portfolioService.addStockToPortfolio(portfolioName, symbol, quantity, date);
          if (success != null) {
            view.showMessage("Stock added successfully to the portfolio.");
          } else {
            view.showMessage("Failed to add stock to the portfolio.");
          }
        }
      }
    }

  private void sellStockFromPortfolio(ActionEvent e) {
    // Implement similar to addStockToPortfolio, but for selling stocks
    String portfolioName = JOptionPane.showInputDialog(view, "Enter the portfolio name to sell stock from:");
    if (portfolioName != null && !portfolioName.isEmpty()) {
      String symbol = JOptionPane.showInputDialog(view, "Enter the stock symbol to sell:");
      if (symbol != null && !symbol.isEmpty()) {
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(view, "Enter the quantity to sell:"));
        LocalDate date = LocalDate.now(); // Simplified: using current date.
        boolean success = portfolioService.sellStockFromPortfolio(portfolioName, symbol, quantity, date);
        if (success) {
          view.showMessage("Stock sold successfully from the portfolio.");
        } else {
          view.showMessage("Failed to sell stock from the portfolio.");
        }
      }
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
