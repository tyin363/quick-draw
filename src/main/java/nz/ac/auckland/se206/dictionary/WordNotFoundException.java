package nz.ac.auckland.se206.dictionary;

public class WordNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;
  private String word;
  private String subMessage;

  public WordNotFoundException(String word, String message, String subMessage) {
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
    return word;
  }

  /**
   * This method retrieves the required sub message
   *
   * @return The sub message
   */
  public String getSubMessage() {
    return subMessage;
  }
}
