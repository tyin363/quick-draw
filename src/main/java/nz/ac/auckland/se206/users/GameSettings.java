package nz.ac.auckland.se206.users;

public class GameSettings {
  public String accuracy;
  public String words;
  public int time;
  public String confidence;

  /** An empty constructor is required to deserialize Round from JSON. */
  public GameSettings() {
    this.accuracy = "";
    this.time = 0;
    this.words = "";
    this.confidence = "";
  }

  /**
   * A constructor for the user's game settings
   *
   * @param accuracy
   * @param words
   * @param time
   * @param confidence
   */
  public GameSettings(String accuracy, String words, int time, String confidence) {
    this.accuracy = accuracy;
    this.words = words;
    this.time = time;
    this.confidence = confidence;
  }

  /**
   * Returns the user's current accuracy setting
   *
   * @return accuracy setting
   */
  public String getAccuracy() {
    return accuracy;
  }

  /**
   * Sets the user's current accuracy setting
   *
   * @return accuracy setting
   */
  public void setAccuracy(String accuracy) {
    this.accuracy = accuracy;
  }

  /**
   * Returns the user's current words setting
   *
   * @return words setting
   */
  public String getWords() {
    return words;
  }

  /**
   * Sets the user's current words setting
   *
   * @return words setting
   */
  public void setWords(String words) {
    this.words = words;
  }

  /**
   * Gets the user's current time setting
   *
   * @return time setting
   */
  public int getTime() {
    return time;
  }

  /**
   * Sets the user's current time setting
   *
   * @return time setting
   */
  public void setTime(int time) {
    this.time = time;
  }

  /**
   * Returns the user's current confidence setting
   *
   * @return confidence setting
   */
  public String getConfidence() {
    return confidence;
  }

  /**
   * Sets the user's confidence words setting
   *
   * @return confidence setting
   */
  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }
}
