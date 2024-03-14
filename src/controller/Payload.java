package controller;

/**
 * this class has two variables acts as a payload data - type class of any type message - type.
 * string
 */
public class Payload {

  private Object data;
  private String message;

  /**
   * constructor for the Payload class.
   *
   * @param data    - type class of any type
   * @param message - type string
   */
  public Payload(Object data, String message) {
    this.data = data;
    this.message = message;
  }

  /**
   * getter for the data variable.
   *
   * @return data
   */
  public Object getData() {
    return data;
  }

  /**
   * getter for the message variable.
   *
   * @return - type string
   */
  public String getMessage() {
    return message;
  }

  /**
   * isError method checks if the message is empty or not.
   */
  public boolean isError() {
    return !message.isEmpty();
  }

  /**
   * isSuccessful method checks if the message is empty or not.
   */
  public boolean isSuccess() {
    return message.isEmpty();
  }
}
