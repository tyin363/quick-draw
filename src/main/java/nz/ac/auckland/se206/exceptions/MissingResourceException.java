package nz.ac.auckland.se206.exceptions;

public class MissingResourceException extends RuntimeException {

  /**
   * Constructs a new runtime exception with the specified message detailing the nature of the
   * missing resource.
   *
   * @param message The message detailing the nature of the missing resource
   */
  public MissingResourceException(final String message) {
    super(message);
  }
}
