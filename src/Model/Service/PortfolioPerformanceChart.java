package Model.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import Controller.Payload;

public class PortfolioPerformanceChart {

  private StockServiceInterface stockService; // Assuming this service can fetch stock/portfolio values.
  private final PortfolioServiceInterface portfolioService;


  public PortfolioPerformanceChart(StockServiceInterface stockService, PortfolioServiceInterface portfolioService) {
    this.stockService = stockService;
    this.portfolioService = portfolioService;
  }

  public void plotPerformanceChart(String identifier, LocalDate startDate, LocalDate endDate) {
    Map<LocalDate, BigDecimal> values = fetchValuesForPeriod(identifier, startDate, endDate);

    BigDecimal minValue = values.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    BigDecimal maxValue = values.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

    // Assume a fixed scale for simplicity; could be dynamic based on minValue and maxValue
    BigDecimal scale = calculateScale(minValue, maxValue);
    int maxAsterisks = 50; // Maximum asterisks per line

    System.out.println("Performance of portfolio " + identifier + " from " + startDate + " to " + endDate + "\n");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
    values.forEach((date, value) -> {
      int asterisks = value.divide(scale, RoundingMode.HALF_UP).intValue();
      System.out.println(formatter.format(date) + ": " + "*".repeat(Math.max(0, asterisks)));
    });

    System.out.println("\nScale: * = " + scale + " dollars");
  }

  public Map<LocalDate, BigDecimal> fetchValuesForPeriod(String identifier, LocalDate startDate, LocalDate endDate) {
    Map<LocalDate, BigDecimal> values = new HashMap<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      // Assuming stockService has a method like fetchClosingPrice or fetchPortfolioValue
      // that returns a Payload containing the value of a stock or portfolio at the end of the day.
      Payload payload;
      if (identifier.equals("SOME_PORTFOLIO_IDENTIFIER")) {
        payload = portfolioService.calculatePortfolioValue(identifier, currentDate);
      } else {
        // Assuming fetchPreviousClosePrice for stocks
        payload = stockService.fetchPreviousClosePrice(identifier, currentDate);
      }

      // Check if payload contains a valid value and is not an error
      if (!payload.isError() && payload.getData() instanceof BigDecimal) {
        BigDecimal value = (BigDecimal) payload.getData();
        values.put(currentDate, value);
      }

      // Increment currentDate by one day. Adjust this if you want monthly or yearly values.
      currentDate = currentDate.plusDays(1);
    }

    return values;
  }

  private BigDecimal calculateScale(BigDecimal minValue, BigDecimal maxValue) {
    // Calculate the range of values
    BigDecimal range = maxValue.subtract(minValue);

    // Determine the maximum number of asterisks we want to display
    int maxAsterisks = 50;

    // Calculate the scale: divide the range by the maximum number of asterisks to find out
    // what value each asterisk represents. We use the ceiling of the division to ensure that
    // we do not exceed 50 asterisks even after rounding.
    BigDecimal scale = range.divide(BigDecimal.valueOf(maxAsterisks), RoundingMode.CEILING);

    // Ensure the scale is at least 1 to avoid a scale of 0 when minValue equals maxValue
    if (scale.compareTo(BigDecimal.ZERO) == 0) {
      scale = BigDecimal.ONE;
    }

    return scale;
  }

}