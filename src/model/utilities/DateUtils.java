package model.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Utility class for date-related operations.
 */
public class DateUtils {

  /**
   * Returns the last working day of the given month. If the last day of the month is a weekend, it
   * adjusts to the closest Friday.
   *
   * @param date The date for which to find the last working day of the month.
   * @return The last working day of the month.
   */
  public static LocalDate getLastWorkingDayOfMonth(LocalDate date) {
    LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
    return adjustForWeekend(lastDayOfMonth);
  }

  /**
   * Returns the last working day of the given year. If the last day of the year is a weekend, it
   * adjusts to the closest Friday.
   *
   * @param date The date for which to find the last working day of the year.
   * @return The last working day of the year.
   */
  public static LocalDate getLastWorkingDayOfYear(LocalDate date) {
    LocalDate lastDayOfYear = LocalDate.of(date.getYear(), 12, 31);
    return adjustForWeekend(lastDayOfYear);
  }

  /**
   * Adjusts the given date to the closest previous working day if it falls on a weekend.
   *
   * @param date The date to adjust.
   * @return The adjusted date.
   */
  private static LocalDate adjustForWeekend(LocalDate date) {
    if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
      return date.minusDays(1); // Move to Friday
    } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
      return date.minusDays(2); // Move to Friday
    }
    return date; // No adjustment needed for weekdays
  }

  /**
   * Determines the recommended resolution for a chart based on the timespan of the data.
   *
   * @param startDate The start date of the data.
   * @param endDate   The end date of the data.
   * @return The recommended resolution for the chart.
   */
  public static String determineResolution(LocalDate startDate, LocalDate endDate) {
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    if (daysBetween <= 30) {
      return "daily";
    } else if (daysBetween <= 150) {
      return "every 10 days";
    } else if (daysBetween <= 540) { // Up to 18 months
      return "monthly";
    } else if (daysBetween <= 1825) { // Up to 5 years
      return "every 3 months";
    } else {
      return "yearly";
    }
  }

  /**
   * Returns the target date based on the resolution.
   *
   * @param currentDate The current date.
   * @param resolution  The resolution for the chart.
   * @param endDate     The end date of the data.
   * @return The target date based on the resolution.
   */
  public static LocalDate getTargetDateBasedOnResolution(LocalDate currentDate, String resolution,
      LocalDate endDate) {
    LocalDate targetDate;
    switch (resolution) {
      case "daily":
        return currentDate;
      case "every 10 days":
        return currentDate;
      case "monthly":
        return DateUtils.getLastWorkingDayOfMonth(currentDate);
      case "every 3 months":
        LocalDate endOfQuarter
            = currentDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        return DateUtils.getLastWorkingDayOfMonth(endOfQuarter).isAfter(endDate)
            ? null : DateUtils.getLastWorkingDayOfMonth(endOfQuarter);
      case "yearly":
        LocalDate endOfYear = currentDate.with(TemporalAdjusters.lastDayOfYear());
        return DateUtils.getLastWorkingDayOfYear(endOfYear).isAfter(endDate)
            ? null : DateUtils.getLastWorkingDayOfYear(endOfYear);
      default:
        throw new IllegalArgumentException("Unsupported resolution: " + resolution);
    }
  }

}
