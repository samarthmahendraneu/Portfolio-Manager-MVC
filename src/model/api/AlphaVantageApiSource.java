package model.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to represent an API source for the Alpha Vantage API.
 */
public class AlphaVantageApiSource implements ApiSource {


  private final String apiKey;

  /**
   * Constructor for the AlphaVantageApiSource class.
   *
   * @param apiKey The API key to use for the requests.
   */
  public AlphaVantageApiSource(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Function to check if a given symbol is valid.
   *
   * @param symbol The symbol to check.
   * @return true if the symbol is valid, false otherwise.
   */
  @Override
  public boolean isValidSymbol(String symbol) {
    try {
      String urlString = String.format(
          "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=%s&apikey=%s",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      StringBuilder response = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line).append("\n");
        }
      }

      return !response.toString().contains("Error Message");

    } catch (Exception e) {
      System.out.println("An error occurred while validating the stock symbol: " + e.getMessage());
      return false;
    }
  }

  /**
   * Makes an API request to fetch stock data for the given symbol.
   *
   * @param symbol The symbol of the stock to fetch data for.
   * @return A string containing the response from the API.
   */
  @Override
  public String fetchData(String symbol) {
    StringBuilder response = new StringBuilder();
    try {
      String urlString = String.format(
          "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol"
              + "=%s&datatype=csv&apikey=%s&outputsize=full",
          symbol, this.apiKey);
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line).append("\n");
        }
      }

      if (response.toString().contains("Error Message")) {
        System.out.println("Invalid stock symbol: " + symbol);
        return "Invalid stock symbol: " + symbol;
      }

    } catch (Exception e) {
      System.out.println("An error occurred while fetching stock data: " + e.getMessage());
    }
    return response.toString();
  }
}
