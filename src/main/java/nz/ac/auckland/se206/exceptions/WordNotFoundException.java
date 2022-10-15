package nz.ac.auckland.se206.exceptions;

public class WordNotFoundException extends Exception {

  private String word;
  private String subMessage;

  /**
   * Constructs a new exception for a word that was not found in the dictionary.
   *
   * @param word The word that was not found
   * @param message The message
   * @param subMessage The sub message
   */
  public WordNotFoundException(final String word, final String message, final String subMessage) {
    super(message);
    this.word = word;
    this.subMessage = subMessage;
  }

  /**
   * This method retrieves the required word
   *
   * @return The word
   */
  public String getWord() {
    return this.word;
  }

  /**
   * This method retrieves the required sub message
   *
   * @return The sub message
   */
  public String getSubMessage() {
    return this.subMessage;
  }
}
