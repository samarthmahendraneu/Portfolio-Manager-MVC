package view;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.Tradable;

public interface UnifiedViewInterface {
  void displayMessage(String message);
  void displayError(String message);
  String requestInput(String prompt);
  void inputMessage(String message);
  String readLine();
  LocalDate requestDate(String prompt);
  void displayPerformanceChart(Map<LocalDate, BigDecimal> data);

  void displayMainMenu() throws IOException;

  Integer readInt();

  void displayFlexiblePortfolioMenu()throws IOException;

  void displayCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, List<LocalDate> data)throws IOException;

  void displayNormalPortfolioMenu()throws IOException;

  void displayMovingCrossoverDays(String symbol, LocalDate startDate, LocalDate endDate, int shortMovingPeriod, int longMovingPeriod, Map<String, Object> data)throws IOException;

  void displayAvailablePortfolios(List<String> strings)throws IOException;

  void displayStockAdded(String portfolioName, String symbol, int quantity)throws IOException;

  void displayPortfolioInvestment(String name, String dateInput, String string)throws IOException;

  void displayStockSold(String portfolioName, String symbol, int quantity)throws IOException;

  void displayPortfolioDetails(String name, List<Tradable> stocks)throws IOException;

  void displaySaveSuccess(String filePath)throws IOException;

  void displayLoadSuccess()throws IOException;

  void displayPortfolioValue(String name, String dateInput, String string)throws IOException;
}
