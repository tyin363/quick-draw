package nz.ac.auckland.se206.users;

import java.util.Set;

public class User {

  private String username;
  private Set<String> pastWords;

  /**
   * Retrieves the username of the user.
   *
   * @return The username of the user.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username The username of the user.
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * Returns whether the user has previously had to draw the specified word.
   *
   * @param word The word to check.
   * @return Whether the user has previously had to draw the specified word.
   */
  public boolean hasHadWord(final String word) {
    return this.pastWords.contains(word);
  }

  /**
   * Adds a word to the set of words that the user has previously had to draw.
   *
   * @param word The word to add.
   */
  public void addPastWord(final String word) {
    this.pastWords.add(word);
  }

  /**
   * Sets the past words that the user has had to draw.
   *
   * @param pastWords The past words
   */
  public void setPastWords(final Set<String> pastWords) {
    this.pastWords = pastWords;
  }
}
