package nz.ac.auckland.se206.badges;

import java.util.function.Predicate;
import nz.ac.auckland.se206.users.User;

public enum Badge {
  NONE,
  SPEED_BRONZE(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 30,
      "Win a game in under 30 seconds"),
  SPEED_SILVER(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 15,
      "Win a game in under 15 seconds"),
  SPEED_GOLD(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 5,
      "Win a game in under 5 seconds"),
  SPEED_PLATINUM(
      user -> user.getFastestTime() >= 0 && user.getFastestTime() < 3,
      "Win a game in under 3 seconds"),
  STREAK_BRONZE(user -> user.getBestWinStreak() >= 3, "Win 3 games in a row"),
  STREAK_SILVER(user -> user.getBestWinStreak() >= 10, "Win 10 games in a row"),
  STREAK_GOLD(user -> user.getBestWinStreak() >= 20, "Win 20 games in a row"),
  STREAK_PLATINUM(user -> user.getBestWinStreak() >= 40, "Win 40 games in a row");

  private final Predicate<User> hasAchievedBadge;
  private final String description;
  private final String badgeClassName;

  /** Default constructor for badges that can never be achieved. */
  Badge() {
    this(user -> false, null);
  }

  /**
   * Constructs a new badge with the given predicate that determines if the user has achieved the
   * badge. The class name for this badge is the same as the enum name in lowercase with the
   * underscores replaced with dashes.
   *
   * @param hasAchievedBadge The predicate to use
   * @param description The description of the badge
   */
  Badge(final Predicate<User> hasAchievedBadge, final String description) {
    this.hasAchievedBadge = hasAchievedBadge;
    this.description = description;
    this.badgeClassName = this.name().toLowerCase().replace("_", "-");
  }

  /**
   * Retrieves the bit flag value of this badge. A flag's bit value consists of a number with only 1
   * bit set to 1. This is used to store the badges a user has in a single integer. The bit value of
   * {@link Badge#NONE} is 0.
   *
   * @return The bit flag value of this badge
   */
  public int getBitFlag() {
    if (this.ordinal() == 0) {
      return 0;
    } else {
      return 1 << (this.ordinal() - 1);
    }
  }

  /**
   * Retrieves the class name of this badge. This is used to display the badge in the UI.
   *
   * @return The class name of this badge
   */
  public String getBadgeClassName() {
    return this.badgeClassName;
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
   * Determines if the given user has meet the requirements to achieve this badge.
   *
   * @param user The user to check
   * @return True if the user has achieved this badge, false otherwise
   */
  public boolean hasAchievedBadge(final User user) {
    return this.hasAchievedBadge.test(user);
  }
}
