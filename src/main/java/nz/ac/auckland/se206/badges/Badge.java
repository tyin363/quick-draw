package nz.ac.auckland.se206.badges;

public enum Badge {
  NONE,
  SPEED_BRONZE,
  SPEED_SILVER,
  SPEED_GOLD,
  SPEED_PLATINUM,
  STREAK_BRONZE,
  STREAK_SILVER,
  STREAK_GOLD,
  STREAK_PLATINUM;

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
}
