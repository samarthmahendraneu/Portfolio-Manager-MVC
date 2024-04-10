package model.api;

/**
 * Interface for the API source.
 */
public interface ApiSource {

  /**
   * Function to check if a given symbol is valid.
   *
   * @param symbol The symbol to check.
   * @return true if the symbol is valid, false otherwise.
   */
  boolean isValidSymbol(String symbol);

  /**
   * Function to fetch data from the API of the given symbol.
   *
   * @param symbol The symbol to fetch data for.
   * @return A string containing the response from the API.
   */
  String fetchData(String symbol);

}
