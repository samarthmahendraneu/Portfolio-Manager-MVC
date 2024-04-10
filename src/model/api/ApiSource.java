package model.api;

public interface ApiSource {

  /**
   * Function to check if a given symbol is valid.
   */
  boolean isValidSymbol(String symbol);

  /**
   * Function to fetch data from the API of the given symbol.
   */
  String fetchData(String symbol);

}
