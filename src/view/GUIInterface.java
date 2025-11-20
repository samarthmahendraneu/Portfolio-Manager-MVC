package view;

import java.awt.event.ActionListener;

/**
 * This interface defines methods for setting action listeners for various GUI components in a
 * financial portfolio management application.
 */
public interface GUIInterface extends UnifiedViewInterface {

  /**
   * Sets an action listener for the "Create Portfolio" button.
   *
   * @param actionListener The action listener to be set.
   */
  void setCreatePortfolioAction(ActionListener actionListener);

  /**
   * Sets an action listener for the "Examine Portfolio" button.
   *
   * @param listener The action listener to be set.
   */
  void setExaminePortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Calculate Portfolio Value" button.
   *
   * @param listener The action listener to be set.
   */
  void setCalculatePortfolioValueButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Save Portfolio" button.
   *
   * @param listener The action listener to be set.
   */
  void setSavePortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Load Portfolio" button.
   *
   * @param listener The action listener to be set.
   */
  void setLoadPortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Graph" button.
   *
   * @param listener The action listener to be set.
   */
  void setGraphButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Inspect Stock Performance" button.
   *
   * @param listener The action listener to be set.
   */
  void setInspectStockPerformanceButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Add" button.
   *
   * @param listener The action listener to be set.
   */
  void setAddButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Sell" button.
   *
   * @param listener The action listener to be set.
   */
  void setSellButtonListener(ActionListener listener);

  // Normal mode listeners

  /**
   * Sets an action listener for the "Create Portfolio" button in normal mode.
   *
   * @param actionListener The action listener to be set.
   */
  void setnormalCreatePortfolioAction(ActionListener actionListener);

  /**
   * Sets an action listener for the "Examine Portfolio" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalExaminePortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Calculate Portfolio Value" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalCalculatePortfolioValueButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Save Portfolio" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalSavePortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Load Portfolio" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalLoadPortfolioButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Graph" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalGraphButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Inspect Stock Performance" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalInspectStockPerformanceButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Calculate X-Day Moving Average" button in normal mode.
   *
   * @param listener The action listener to be set.
   */
  void setnormalCalculateXDayMovingAverageButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Crossover" button.
   *
   * @param listener The action listener to be set.
   */
  void setNormalCrossoverButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Moving Crossover" button.
   *
   * @param listener The action listener to be set.
   */
  void setNormalMovingCrossoverButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Dollar Cost" button.
   *
   * @param listener The action listener to be set.
   */
  void setNormalDollarCostButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Crossover" button.
   *
   * @param listener The action listener to be set.
   */
  void setCrossoverButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Moving Crossover" button.
   *
   * @param listener The action listener to be set.
   */
  void setMovingCrossoverButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Dollar Cost" button.
   *
   * @param listener The action listener to be set.
   */
  void setDollarCostButtonListener(ActionListener listener);


  /**
   * Sets an action listener for the "Investment" button.
   *
   * @param listener The action listener to be set.
   */
  void setValueBasedInvestmentButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Investment" button.
   *
   * @param listener The action listener to be set.
   */
  void setNormalValueBasedInvestmentButtonListener(ActionListener listener);


  /**
   * Sets an action listener for the "Investment" button.
   *
   * @param listener The action listener to be set.
   */
  void setInvestmentButtonListener(ActionListener listener);

  /**
   * Sets an action listener for the "Calculate X-Day Moving Average" button.
   *
   * @param listener The action listener to be set.
   */
  void setCalculateXDayMovingAverageButtonListener(ActionListener listener);
}

