package Controller;

import Model.Service.PortfolioService;
import Model.Service.StockService;
import Model.Portfolio;
import Model.Stock;
import Viewv2.MainFrame;
import Viewv2.PortfolioDetailsEntryWindow;
import Viewv2.PortfolioManagementOptionsWindow;
import Viewv2.PortfolioNameEntryWindow;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PortfolioController {
  private MainFrame mainFrame;
  private PortfolioService portfolioService;
  private StockService stockService;

  public PortfolioController(MainFrame mainFrame, StockService stockService) {
    this.mainFrame = mainFrame;
    this.stockService = stockService;
    this.portfolioService = new PortfolioService(stockService);

    // Assuming MainFrame has these methods to add action listeners
    this.mainFrame.addCreatePortfolioListener(e -> showPortfolioNameEntryWindow());
    this.mainFrame.addLoadPortfolioListener(e -> loadPortfolio());
  }

  private void showPortfolioNameEntryWindow() {
    PortfolioNameEntryWindow nameEntryWindow = new PortfolioNameEntryWindow();
    nameEntryWindow.addConfirmButtonListener(e -> {
      String portfolioName = nameEntryWindow.getPortfolioName();
      if (!portfolioName.isEmpty() || portfolioService.portfolioExists(portfolioName)){
        Portfolio newPortfolio = new Portfolio(portfolioName);
        portfolioService.addPortfolio(newPortfolio);
        nameEntryWindow.dispose(); // Close the window
        showPortfolioDetailsEntryWindow(newPortfolio); // Proceed to add stocks
      } else {
        JOptionPane.showMessageDialog(nameEntryWindow, "Portfolio name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    nameEntryWindow.setVisible(true);
  }

  private void showPortfolioDetailsEntryWindow(Portfolio portfolio) {
    PortfolioDetailsEntryWindow detailsEntryWindow = new PortfolioDetailsEntryWindow();
    detailsEntryWindow.addAddButtonListener(e -> {
      try {
        String tickerSymbol = detailsEntryWindow.getTickerSymbol();
        LocalDate date = LocalDate.parse(detailsEntryWindow.getDate());
        int quantity = Integer.parseInt(detailsEntryWindow.getQuantity());
        BigDecimal price = stockService.fetchPriceOnDate(tickerSymbol, date);
        portfolio.addStock(new Stock(tickerSymbol, quantity, price, date));
        detailsEntryWindow.clearFields();
        JOptionPane.showMessageDialog(detailsEntryWindow, "Stock added.");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(detailsEntryWindow, "Error adding stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    detailsEntryWindow.addEndButtonListener(e -> {
      detailsEntryWindow.dispose(); // Close the window
      showPortfolioManagementOptionsWindow(portfolio); // Show portfolio management options
    });

    detailsEntryWindow.setVisible(true);
  }
  private void createPortfolio() {
    PortfolioNameEntryWindow nameEntryWindow = new PortfolioNameEntryWindow();
    nameEntryWindow.addConfirmButtonListener(e -> {
      String portfolioName = nameEntryWindow.getPortfolioName();
      if (!portfolioName.isEmpty()) {
        Portfolio newPortfolio = portfolioService.getPortfolioByName(portfolioName)
                .orElseGet(() -> {
                  Portfolio portfolio = new Portfolio(portfolioName);
                  portfolioService.addPortfolio(portfolio);
                  return portfolio;
                });
        nameEntryWindow.dispose(); // Close the window
        showPortfolioDetailsEntryWindow(newPortfolio); // Correctly pass the Portfolio object
      } else {
        JOptionPane.showMessageDialog(nameEntryWindow, "Portfolio name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    nameEntryWindow.setVisible(true);
  }
  private void examinePortfolio(Portfolio portfolio) {
    if (portfolio != null) {
      StringBuilder portfolioDetails = new StringBuilder("Portfolio: " + portfolio.getName() + "\n");
      for (Stock stock : portfolio.getStocks()) {
        portfolioDetails.append("Stock: ").append(stock.getSymbol())
                .append(", Quantity: ").append(stock.getQuantity())
                .append(", Purchase Price: ").append(stock.getPurchasePrice())
                .append(", Purchase Date: ").append(stock.getPurchaseDate()).append("\n");
      }
      JOptionPane.showMessageDialog(null, portfolioDetails.toString(), "Portfolio Details", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, "Portfolio not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void calculatePortfolioValue(Portfolio portfolio, LocalDate date) {
    if (portfolio != null) {
      BigDecimal totalValue = BigDecimal.ZERO;
      for (Stock stock : portfolio.getStocks()) {
        BigDecimal priceOnDate = stockService.fetchPriceOnDate(stock.getSymbol(), date);
        BigDecimal value = priceOnDate.multiply(new BigDecimal(stock.getQuantity()));
        totalValue = totalValue.add(value);
      }
      JOptionPane.showMessageDialog(null, "Total Portfolio Value on " + date + ": $" + totalValue, "Portfolio Value", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, "Portfolio not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void savePortfolio(Portfolio portfolio, String filePath) {
    try {
      portfolioService.savePortfoliosToCSV(filePath);
      JOptionPane.showMessageDialog(null, "Portfolio saved to: " + filePath, "Save Successful", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Failed to save portfolio: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }


  private void showPortfolioManagementOptionsWindow(Portfolio portfolio) {
    PortfolioManagementOptionsWindow optionsWindow = new PortfolioManagementOptionsWindow();

    // Examine portfolio listener
    optionsWindow.addExaminePortfolioListener(e -> examinePortfolio(portfolio));

    // Calculate portfolio value listener
    optionsWindow.addCalculateValueListener(e -> {
      String dateString = JOptionPane.showInputDialog(optionsWindow, "Enter the date (YYYY-MM-DD) to calculate the portfolio value:");
      if (dateString != null && !dateString.isEmpty()) {
        try {
          LocalDate date = LocalDate.parse(dateString);
          calculatePortfolioValue(portfolio, date);
        } catch (DateTimeParseException ex) {
          JOptionPane.showMessageDialog(optionsWindow, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    // Save portfolio listener
    optionsWindow.addSavePortfolioListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Specify a file to save");
      int userSelection = fileChooser.showSaveDialog(optionsWindow);
      if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        savePortfolio(portfolio, fileToSave.getAbsolutePath());
      }
    });

    optionsWindow.setVisible(true);
  }

  private void loadPortfolio() {
    // TODO : Implementation for loading a portfolio, similar to previous examples.
  }
}
