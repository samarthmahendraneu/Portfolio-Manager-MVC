package view;

import java.awt.event.ActionListener;

public interface GUIInterface extends UnifiedViewInterface{

  void setCreatePortfolioAction(ActionListener actionListener);
  void setExaminePortfolioButtonListener(ActionListener listener);
  void setCalculatePortfolioValueButtonListener(ActionListener listener);
  void setSavePortfolioButtonListener(ActionListener listener);
  void setLoadPortfolioButtonListener(ActionListener listener);
  void setGraphButtonListener(ActionListener listener);
  void setInspectStockPerformanceButtonListener(ActionListener listener);
  void setAddButtonListener(ActionListener listener);
  void setSellButtonListener(ActionListener listener);
}
