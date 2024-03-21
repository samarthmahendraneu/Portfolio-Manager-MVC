package Model.utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
}
