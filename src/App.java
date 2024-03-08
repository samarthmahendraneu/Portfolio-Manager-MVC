
import Controller.PortfolioController;
import Service.StockService;
import View.MainFrame;
import javax.swing.SwingUtilities;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame("Portfolio Management System");
      StockService stockService = new StockService("8WGFWEOZ5SVHAF75"); // Assuming you have a default constructor or another way to initialize
      new PortfolioController(mainFrame, stockService);
      mainFrame.setVisible(true);
    });
  }
}
