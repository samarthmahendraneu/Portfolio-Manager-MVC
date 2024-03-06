package Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Stock implements Serializable {
  private String symbol;
  private int quantity;
  private BigDecimal purchasePrice;
  private LocalDate purchaseDate;

  public Stock(String symbol, int quantity, BigDecimal purchasePrice, LocalDate purchaseDate) {
    this.symbol = symbol;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
    this.purchaseDate = purchaseDate;
  }

  // Getters
  public String getSymbol() {
    return symbol;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  public LocalDate getPurchaseDate() {
    return purchaseDate;
  }

  // Setters, toString(), equals(), and hashCode() methods omitted for brevity
}
