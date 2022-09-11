package nz.ac.auckland.se206.users;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {

  private final UUID id;
  private String username;
  private Set<String> pastWords;

  /** An empty constructor is required to deserialize the user from JSON. */
  public User() {
    this.id = UUID.randomUUID();
  }

  public User(final String username) {
    this.id = UUID.randomUUID();
    this.username = username;
  }

  /**
   * Retrieves the unique-user-id (UUID) for the user.
   *
   * @return The UUID for the user.
   */
  public UUID getId() {
    return this.id;
  }

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

  @Override
  public String toString() {
    return "User{id=%s, username='%s'}".formatted(this.id, this.username);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    } else if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final User user = (User) o;
    // Check that all the fields are equal
    return this.id.equals(user.id)
        && Objects.equals(this.username, user.username)
        && Objects.equals(this.pastWords, user.pastWords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.username, this.pastWords);
  }
}
