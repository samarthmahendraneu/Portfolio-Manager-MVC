package Model.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

  /**
   * Returns the last working day of the given month.
   * If the last day of the month is a weekend, it adjusts to the closest Friday.
   *
   * @param date A date within the month of interest.
   * @return The last working day of the month.
   */
  public static LocalDate getLastWorkingDayOfMonth(LocalDate date) {
    LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
    return adjustForWeekend(lastDayOfMonth);
  }

  /**
   * Returns the last working day of the given year.
   * If the last day of the year is a weekend, it adjusts to the closest Friday.
   *
   * @param date A date within the year of interest.
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

  public static String determineResolution(LocalDate startDate, LocalDate endDate) {
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    if (daysBetween <= 30) {
      return "daily";
    } else if (daysBetween <= 540) { // Up to 18 months
      return "monthly";
    } else if (daysBetween <= 1825) { // Up to 5 years
      return "every 3 months";
    } else {
      return "yearly";
    }
  }

  public static LocalDate getTargetDateBasedOnResolution(LocalDate currentDate, String resolution, LocalDate endDate) {
    LocalDate targetDate;
    switch (resolution) {
      case "daily":
        targetDate = currentDate; // For daily, the target date is the current date itself.
        break;
      case "monthly":
        // Calculate the last day of the current month or endDate, whichever is earlier.
        LocalDate endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        targetDate = endOfMonth.isBefore(endDate) ? endOfMonth : endDate;
        break;
      case "every 3 months":
        // Calculate the last day of the current quarter.
        LocalDate endOfQuarter = currentDate.with(TemporalAdjusters.lastDayOfMonth())
                .plusMonths(2) // Move to the last month of the current quarter.
                .with(TemporalAdjusters.lastDayOfMonth());
        targetDate = endOfQuarter.isBefore(endDate) ? endOfQuarter : endDate;
        break;
      case "yearly":
        // Calculate the last day of the current year.
        LocalDate endOfYear = currentDate.with(TemporalAdjusters.lastDayOfYear());
        targetDate = endOfYear.isBefore(endDate) ? endOfYear : endDate;
        break;
      default:
        throw new IllegalArgumentException("Unsupported resolution: " + resolution);
    }
    return targetDate;
  }

}
