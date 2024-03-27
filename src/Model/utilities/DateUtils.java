package Model.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

  /**
   * Returns the last working day of the given month.
   * If the last day of the month is a weekend, it adjusts to the closest Friday.
   */
  public static LocalDate getLastWorkingDayOfMonth(LocalDate date) {
    LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
    return adjustForWeekend(lastDayOfMonth);
  }

  /**
   * Returns the last working day of the given year.
   * If the last day of the year is a weekend, it adjusts to the closest Friday.
   */
  public static LocalDate getLastWorkingDayOfYear(LocalDate date) {
    LocalDate lastDayOfYear = LocalDate.of(date.getYear(), 12, 31);
    return adjustForWeekend(lastDayOfYear);
  }

  /**
   * Adjusts the given date to the closest previous working day if it falls on a weekend.
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
    } else if(daysBetween <= 150){
      return "every 10 days";
    }
    else if (daysBetween <= 540) { // Up to 18 months
      return "monthly";
    } else if (daysBetween <= 1825) { // Up to 5 years
      return "every 3 months";
    } else {
      return "yearly";
    }
  }

  public static LocalDate getTargetDateBasedOnResolution
          (LocalDate date, String resolution, LocalDate endDate) {
    switch (resolution) {
      case "daily", "every 10 days":
        return date;
      case "monthly":
        return DateUtils.getLastWorkingDayOfMonth(date);
      case "every 3 months":
        LocalDate endOfQuarter
                = date.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        return DateUtils.getLastWorkingDayOfMonth(endOfQuarter).isAfter(endDate)
                ? null : DateUtils.getLastWorkingDayOfMonth(endOfQuarter);
      case "yearly":
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        return DateUtils.getLastWorkingDayOfYear(endOfYear).isAfter(endDate)
                ? null : DateUtils.getLastWorkingDayOfYear(endOfYear);
      default:
        throw new IllegalArgumentException("Unknown resolution: " + resolution);
    }
  }



}
