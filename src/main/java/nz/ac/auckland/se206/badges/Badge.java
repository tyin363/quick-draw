package nz.ac.auckland.se206.badges;

import java.util.function.Predicate;
import nz.ac.auckland.se206.users.User;

public enum Badge {
  SPEED_BRONZE(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 30,
      "Win a game in under 30 seconds",
      "Speed I"),
  SPEED_SILVER(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 15,
      "Win a game in under 15 seconds",
      "Speed II"),
  SPEED_GOLD(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 5,
      "Win a game in under 5 seconds",
      "Speed III"),
  SPEED_PLATINUM(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 2,
      "Win a game in 1 second",
      "Speed IV"),
  STREAK_BRONZE(user -> user.getBestWinStreak() >= 3, "Win 3 games in a row", "Streak I"),
  STREAK_SILVER(user -> user.getBestWinStreak() >= 10, "Win 10 games in a row", "Streak II"),
  STREAK_GOLD(user -> user.getBestWinStreak() >= 20, "Win 20 games in a row", "Streak III"),
  STREAK_PLATINUM(user -> user.getBestWinStreak() >= 40, "Win 40 games in a row", "Streak IV");

  private final Predicate<User> hasAchievedBadge;
  private final String description;
  private final String displayName;

  /**
   * Constructs a new badge with the given predicate that determines if the user has achieved the
   * badge.
   *
   * @param hasAchievedBadge The predicate to use
   * @param description The description of the badge
   */
  Badge(
      final Predicate<User> hasAchievedBadge, final String description, final String displayName) {
    this.hasAchievedBadge = hasAchievedBadge;
    this.description = description;
    this.displayName = displayName;
  }

  /**
   * Retrieves the bit flag value of this badge. A flag's bit value consists of a number with only 1
   * bit set to 1. This is used to store the badges a user has in a single integer.
   *
   * @return The bit flag value of this badge
   */
  public int getBitFlag() {
    return 1 << this.ordinal();
  }

  /**
   * Retrieves the description of this badge.
   *
   * @return The description of this badge
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Retrieves the display name of this badge.
   *
   * @return The display name of this badge
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * Determines if the given user has meet the requirements to achieve this badge.
   *
   * @param user The user to check
   * @return True if the user has achieved this badge, false otherwise
   */
  public boolean hasAchievedBadge(final User user) {
    return this.hasAchievedBadge.test(user);
  }

  /**
   * Retrieves the badge's group. This is the first part of the enum name before the underscore in
   * lowercase.
   *
   * @return The badge's group
   */
  public String getBadgeGroup() {
    return this.name().split("_")[0].toLowerCase();
  }

  /**
   * Retrieves the badge's tier. This is the second part of the enum name after the underscore in
   * lowercase.
   *
   * @return The badge's tier
   */
  public String getBadgeTier() {
    return this.name().split("_")[1].toLowerCase();
  }
}
