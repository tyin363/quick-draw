package nz.ac.auckland.se206.client.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javafx.scene.image.Image;
import nz.ac.auckland.se206.client.badges.UserBadges;

public class User {

  private final UUID id;
  private final Date created;
  private final List<Round> pastRounds;
  private String username;
  private String profilePicture;
  private int fastestTime = -1;
  private int gamesWon;
  private int gamesLost;
  private int currentWinStreak;
  private int bestWinStreak;
  private double musicVolume = 0.1;
  private double soundEffectVolume = 0.3;
  private GameSettings gameSettings;
  private UserBadges badges;

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
    this.created = new Date();
    this.username = username;
    this.pastRounds = new ArrayList<>();
    this.gameSettings = new GameSettings();
    this.badges = new UserBadges();
    this.profilePicture = "images/defaultUserImage.jpg";
  }

  /**
   * Retrieves the users music volume preference
   *
   * @return Users music volume
   */
  public double getMusicVolume() {
    return musicVolume;
  }

  /**
   * Sets the users music volume preference
   *
   * @param musicVolume The music volume to set
   */
  public void setMusicVolume(double musicVolume) {
    this.musicVolume = musicVolume;
  }

  /**
   * Get the users sound effect volume preference
   *
   * @return Users sound effect volume
   */
  public double getSoundEffectVolume() {
    return soundEffectVolume;
  }

  /**
   * Sets the users sound effect volume preference
   *
   * @param soundEffectVolume The sound effect volume to set
   */
  public void setSoundEffectVolume(double soundEffectVolume) {
    this.soundEffectVolume = soundEffectVolume;
  }

  /**
   * Retrieves the game settings for this user.
   *
   * @return The game settings for this user
   */
  public GameSettings getGameSettings() {
    return this.gameSettings;
  }

  /**
   * Retrieves the badges that this user has achieved.
   *
   * @return The user's badges
   */
  public UserBadges getBadges() {
    return this.badges;
  }

  /**
   * Adds a round to the list of rounds that the user has previously played. If the user won the
   * round, it will increment their current win streak and update their best win streak if
   * necessary. It will also check if this is a new fastest time for completing a round
   * Alternatively, if the user lost the round, it will increment the number of games lost and reset
   * their current win streak.
   *
   * @param round The round to add.
   */
  public void addPastRound(final Round round) {
    // Keep track of the past rounds they've played for the stats page
    this.pastRounds.add(round);
    if (round.wasGuessed()) {
      this.gamesWon++;
      this.currentWinStreak++;
      if (this.currentWinStreak > this.bestWinStreak) {
        this.bestWinStreak = this.currentWinStreak;
      }

      // Check if this is a new fastest time or if there is not currently a fastest time
      if (round.timeTaken() < this.fastestTime || this.fastestTime == -1) {
        this.fastestTime = round.timeTaken();
      }
    } else {
      this.gamesLost++;
      // Reset their current win streak if they lose
      this.currentWinStreak = 0;
    }
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
   * Retrieves the date that this user was created.
   *
   * @return The date that this user was created.
   */
  public Date getCreated() {
    return this.created;
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
   * Retrieves the users profile picture as an Image object. If the profile picture is invalid or
   * doesn't exist then it will return the default profile picture.
   *
   * @return The users profile picture as an Image object
   */
  @JsonIgnore
  public Image getProfileImage() {
    try {
      return new Image(this.profilePicture);
    } catch (final IllegalArgumentException e) {
      // If the path is invalid or the file doesn't exist, use the default profile picture.
      return new Image("images/defaultUserImage.jpg");
    }
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
   * Retrieves the number of games the user has lost.
   *
   * @return The number of games the user has lost.
   */
  public int getGamesLost() {
    return this.gamesLost;
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
   * Retrieves the percentage of games that the user has won in the range 0-1. If they haven't
   * played any games then this will return 1.
   *
   * @return The win rate of the user in the range 0-1.
   */
  @JsonIgnore
  public double getWinRate() {
    if (this.getTotalGames() == 0) {
      return 1;
    }
    return (double) this.gamesWon / this.getTotalGames();
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

  /** Retrieves the user's ID hash code */
  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  /**
   * Equals method for checking the equality of two users
   *
   * @param o The object to check
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    } else if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final User user = (User) o;
    // Equality is determined by whether they have the same id
    return this.id.equals(user.id);
  }

  /** ToString method for formatting the string of User class */
  @Override
  public String toString() {
    return String.format("User{id=%s, username='%s'}", this.id, this.username);
  }
}
