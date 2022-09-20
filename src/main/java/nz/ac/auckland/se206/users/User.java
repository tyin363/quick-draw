package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {

  private final UUID id;
  private final List<Round> pastRounds;
  private String username;
  private String profilePicture;
  private int fastestTime;
  private int gamesWon;
  private int gamesLost;
  private int currentWinStreak;
  private int bestWinStreak;

  /** An empty constructor is required to deserialize the user from JSON. */
  public User() {
    this(null);
  }

  /**
   * Creates a new user with the given username and automatically generates a random UUID for them.
   *
   * @param username The username of the user
   */
  public User(final String username) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.profilePicture = "src/main/resources/images/defaultUserImage.jpg";
    this.pastRounds = new ArrayList<>();
  }

  /**
   * Returns whether the user has previously had to draw the specified word.
   *
   * @param word The word to check.
   * @return Whether the user has previously had to draw the specified word.
   */
  public boolean hasHadWord(final String word) {
    return this.pastRounds.stream().anyMatch(round -> round.getWord().equals(word));
  }

  /**
   * Adds a round to the list of rounds that the user has previously played.
   *
   * @param round The round to add.
   */
  public void addPastRound(final Round round) {
    this.pastRounds.add(round);
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
   * Retrieves the fastest time of the user.
   *
   * @return The fastest time of the user.
   */
  public int getFastestTime() {
    return this.fastestTime;
  }

  /**
   * Sets the fastest time of the user.
   *
   * @param fastestTime The fastest time of the user.
   */
  public void setFastestTime(final int fastestTime) {
    this.fastestTime = fastestTime;
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
   * Retrieves a list containing the rounds previously played by the user.
   *
   * @return A list containing the rounds previously played by the user.
   */
  public List<Round> getPastRounds() {
    return this.pastRounds;
  }

  /**
   * Retrieves the path to the profile picture of the user or null if the user doesn't have a
   * profile picture.
   *
   * @return The path to the profile picture of the user.
   */
  public String getProfilePicture() {
    return this.profilePicture;
  }

  /**
   * Sets the path to the profile picture of the user. To set no profile picture, pass null.
   *
   * @param profilePicture The path to the profile picture of the user.
   */
  public void setProfilePicture(final String profilePicture) {
    this.profilePicture = profilePicture;
  }

  /**
   * Retrieves the number of games the user has won.
   *
   * @return The number of games the user has won.
   */
  public int getGamesWon() {
    return this.gamesWon;
  }

  /**
   * Increases the number of games the user has won by 1 and increments the user's current win
   * streak by 1. If the new current win streak is greater than the best win streak, then it will
   * also update the best win streak
   */
  public void incrementGamesWon() {
    this.gamesWon++;
    this.currentWinStreak++;
    if (this.currentWinStreak > this.bestWinStreak) {
      this.bestWinStreak = this.currentWinStreak;
    }
  }

  /**
   * Retrieves the number of games the user has lost.
   *
   * @return The number of games the user has lost.
   */
  public int getGamesLost() {
    return this.gamesLost;
  }

  /**
   * Increases the number of games the user has lost by 1. If the user was on a win streak then this
   * will also set the current win streak to 0.
   */
  public void incrementGamesLost() {
    this.gamesLost++;
    this.currentWinStreak = 0;
  }

  /**
   * Retrieves the total number of games the user has played. This is the sum of the games this user
   * has won and the games lost.
   *
   * @return The total number of games the user has played.
   */
  @JsonIgnore
  public int getTotalGames() {
    return this.gamesWon + this.gamesLost;
  }

  /**
   * Retrieves the current win streak of the user.
   *
   * @return The current win streak of the user
   */
  public int getCurrentWinStreak() {
    return this.currentWinStreak;
  }

  /**
   * Retrieves the best win streak of the user.
   *
   * @return The best win streak of the user
   */
  public int getBestWinStreak() {
    return this.bestWinStreak;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.id,
        this.username,
        this.pastRounds,
        this.profilePicture,
        this.gamesWon,
        this.gamesLost);
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
        && Objects.equals(this.pastRounds, user.pastRounds)
        && Objects.equals(this.profilePicture, user.profilePicture)
        && this.gamesWon == user.gamesWon
        && this.gamesLost == user.gamesLost;
  }

  @Override
  public String toString() {
    return String.format(
        "User{id=%s, username='%s', pastWords='%s', profilePicture='%s', "
            + "gamesWon=%d, gamesLost=%d}",
        this.id,
        this.username,
        this.pastRounds,
        this.profilePicture,
        this.gamesWon,
        this.gamesLost);
  }
}
