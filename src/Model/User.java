package Model;

import java.util.List;

import Interface.UserInterface;

public class User implements UserInterface {


  // list of portfolio
  private List<Portfolio> portfolioList;

  @Override
  public boolean createPortfolio() {
    return false;
  }

  @Override
  public List<Stock> getPortfolioComposition() {
    return null;
  }

  @Override
  public List<Portfolio> loadPortfolio(String jsonFile) {
    return null;
  }

  @Override
  public boolean savePortfolio(String jsonFile) {
    return false;
  }
}
