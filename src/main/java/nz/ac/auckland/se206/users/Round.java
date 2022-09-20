package nz.ac.auckland.se206.users;

public class Round {

  private final String word;
  private final int timeTaken;
  private final boolean wasGuessed;

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
  public boolean isWasGuessed() {
    return this.wasGuessed;
  }
}
