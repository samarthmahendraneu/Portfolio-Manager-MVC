package Model.utilities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Class to calculate the recommended resolution for a chart based on the timespan of the data.
 */
public class ChartTimespanCalculator {

  /**
   * Determines the recommended resolution for a chart based on the timespan of the data.
   *
   * @param startDate The start date of the data.
   * @param endDate   The end date of the data.
   * @return The recommended resolution for the chart.
   */
  public static String determineResolution(LocalDate startDate, LocalDate endDate) {
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    long yearsBetween = ChronoUnit.YEARS.between(startDate, endDate);

    // Daily resolution for very short periods (less than a month)
    if (daysBetween <= 30) {
      return "daily";
    }
    // Monthly resolution for periods up to 1.5 years to aim for a detailed yet not
    // overcrowded chart
    else if (yearsBetween <= 1.5) {
      return "monthly";
    }
    // Every 3 months resolution for periods up to 5 years for a balanced overview
    else if (yearsBetween <= 5) {
      return "every 3 months";
    }
    // Yearly resolution for periods over 5 years for a clear long-term perspective
    else {
      return "yearly";
    }
  }

  /**
   * Main method to test the determineResolution method.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    LocalDate startDate = LocalDate.of(2020, 3, 3);
    LocalDate endDate = LocalDate.of(2022, 3, 18);
    String resolution = determineResolution(startDate, endDate);
    System.out.println("Recommended resolution: " + resolution);
  }
}
