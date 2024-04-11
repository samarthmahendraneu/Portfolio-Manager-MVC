package model.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This class used to display a bar chart in a JPanel.
 */
public class BarChartPanel extends JPanel {

  private Map<LocalDate, BigDecimal> data;
  private static final int MAX_BAR_WIDTH = 500;
  private static final int MAX_LABEL_WIDTH = 100;
  private static final int BAR_HEIGHT = 50;
  private static final int GAP = 26;
  private static final int MARGIN = 50;
  private BigDecimal maxScaleValue;

  /**
   * Constructor for the BarChartPanel class.
   *
   * @param data The data to be displayed in the bar chart.
   */
  public BarChartPanel(Map<LocalDate, BigDecimal> data) {
    this.data = data;
    this.maxScaleValue = data.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
    setPreferredSize(
        new Dimension(MAX_BAR_WIDTH + MARGIN * 2, (BAR_HEIGHT + GAP) * data.size() + MARGIN));
  }

  /**
   * Paints the bar chart on the panel.
   *
   * @param g The Graphics object to paint on.
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int x = MARGIN;
    int barWidth = getWidth() - MARGIN * 2; // Width for each bar
    int barHeight = (getHeight() - (MARGIN * 2) - (GAP * (data.size() - 1)))
        / data.size(); // Height for each bar

    int maxBarLength = barWidth - MAX_LABEL_WIDTH; // Adjust bar length for label

    int y = MARGIN;
    for (Entry<LocalDate, BigDecimal> entry : data.entrySet()) {
      int barLength = (int) ((entry.getValue().doubleValue() / maxScaleValue.doubleValue())
          * maxBarLength);
      g2d.setColor(Color.BLUE);
      g2d.fillRect(x + MAX_LABEL_WIDTH, y, barLength, barHeight);
      g2d.setColor(Color.BLACK);

      // Draw the date on the left of the bar
      String dateString = entry.getKey().toString();
      int stringWidth = g.getFontMetrics().stringWidth(dateString);
      int dateX = x + (MAX_LABEL_WIDTH - stringWidth) / 2; // Center the date in the label area
      g2d.drawString(dateString, dateX, y + barHeight / 2);

      // Draw the value on the right of the bar
      String valueString = entry.getValue().toString();
      g2d.drawString(valueString, x + MAX_LABEL_WIDTH + barLength + 5, y + barHeight / 2);

      y += barHeight + GAP;
    }


  }

  /**
   * Sets the data to be displayed in the bar chart.
   *
   * @param data The data to be displayed in the bar chart.
   */
  public void setData(Map<LocalDate, BigDecimal> data) {
    this.data = data;
    revalidate();
    repaint();
  }

  /**
   * Displays the bar chart in a window.
   */
  public void displayInWindow() {
    JFrame frame = new JFrame("Performance Chart");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * Main method to test the BarChartPanel class.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Bar Chart Test");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Map<LocalDate, BigDecimal> sampleData = new LinkedHashMap<>();
      sampleData.put(LocalDate.now().minusDays(4), new BigDecimal("50"));
      sampleData.put(LocalDate.now().minusDays(3), new BigDecimal("80"));
      // Add more data points...

      BarChartPanel barChartPanel = new BarChartPanel(sampleData);
      frame.add(barChartPanel, BorderLayout.CENTER);
      frame.setSize(400, 300);
      frame.setVisible(true);
    });
  }
}
