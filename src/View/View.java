package View;
import java.io.IOException;


public class View {

  /**
   * display the main menu
   *
   */
  public void displayMainMenu(Appendable out) throws IOException {
    out.append("\nPortfolio Management System:\n");
    out.append("1. Create a new portfolio\n");
    out.append("2. Examine a portfolio\n");
    out.append("3. Calculate portfolio value\n");
    out.append("4. Save portfolio\n");
    out.append("5. Load portfolio\n");
    out.append("6. Exit\n");
    out.append("Select an option: ");
  }

}
