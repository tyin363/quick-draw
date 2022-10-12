package nz.ac.auckland.se206.client.users;

public class Round {

  private final String word;
  private final int timeTaken;
  private final boolean wasGuessed;

  /** An empty constructor is required to deserialize Round from JSON. */
  public Round() {
    this.word = "";
    this.timeTaken = 0;
    this.wasGuessed = false;
  }

  public Round(final String word, final int timeTaken, final boolean wasGuessed) {
    this.word = word;
    this.timeTaken = timeTaken;
    this.wasGuessed = wasGuessed;
  }

  /**
   * Retrieve the word that was being drawn in this round.
   *
   * @return The word that was being drawn in this round
   */
  public String getWord() {
    return this.word;
  }

  /**
   * Retrieve the time taken in seconds to draw the word in this round. If the word wasn't guessed,
   * this will always be the maximum time.
   *
   * @return The time taken in seconds to draw the word in this round
   */
  public int getTimeTaken() {
    return this.timeTaken;
  }

  /**
   * If the word was guessed in the round.
   *
   * @return If the word was guessed in the round
   */
  public boolean wasGuessed() {
    return this.wasGuessed;
  }

  /**
   * Changed string message for Round object to print word, time taken and if it word was guessed
   */
  @Override
  public String toString() {

    // Formatting Round string message to include word, time taken and if it was guessed
    return "Word: "
        + this.word
        + "; Time Taken: "
        + this.timeTaken
        + "; Was Guessed: "
        + this.wasGuessed;
  }
}
