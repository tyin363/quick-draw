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

  public GameSettings(String accuracy, String words, int time, String confidence) {
    this.accuracy = accuracy;
    this.words = words;
    this.time = time;
    this.confidence = confidence;
  }

  public String getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(String accuracy) {
    this.accuracy = accuracy;
  }

  public String getWords() {
    return words;
  }

  public void setWords(String words) {
    this.words = words;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public String getConfidence() {
    return confidence;
  }

  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }
}
