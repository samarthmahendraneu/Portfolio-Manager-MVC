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

   void setnormalCreatePortfolioAction(ActionListener actionListener);

   void setnormalExaminePortfolioButtonListener(ActionListener listener) ;

   void setnormalCalculatePortfolioValueButtonListener(ActionListener listener);

   void setnormalSavePortfolioButtonListener(ActionListener listener);

   void setnormalLoadPortfolioButtonListener(ActionListener listener);

   void setnormalGraphButtonListener(ActionListener listener);
  void setnormalInspectStockPerformanceButtonListener(ActionListener listener);
   void setnormalCalculateXDayMovingAverageButtonListener(ActionListener listener);
  void setCrossoverButtonListener(ActionListener listener);
  void setMovingCrossoverButtonListener(ActionListener listener);
  void setDollarCostButtonListener(ActionListener listener);
  void setInvestmentButtonListener(ActionListener listener);

  void setCalculateXDayMovingAverageButtonListener(ActionListener listener);
}
