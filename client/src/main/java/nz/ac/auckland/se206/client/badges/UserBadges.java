package nz.ac.auckland.se206.client.badges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Arrays;
import java.util.List;
import nz.ac.auckland.se206.client.badges.serialization.UserBadgesDeserializer;
import nz.ac.auckland.se206.client.badges.serialization.UserBadgesSerializer;

@JsonSerialize(using = UserBadgesSerializer.class)
@JsonDeserialize(using = UserBadgesDeserializer.class)
public class UserBadges {

  private int bitFlag;

  /** Creates a new instance of the User Badges class with no badges. */
  public UserBadges() {
    this.bitFlag = 0;
  }

  /**
   * Constructs a new instance of the User Badges class with the given bit flag.
   *
   * @param bitFlag The bit flag to use
   */
  public UserBadges(final int bitFlag) {
    this.bitFlag = bitFlag;
  }

  /**
   * Determines if the user has the given badge by checking if the badges bit flag is set.
   *
   * @param badge The badge to check for
   * @return True if the user has the badge, false otherwise
   */
  public boolean hasBadge(final Badge badge) {
    return (this.bitFlag & badge.getBitFlag()) != 0;
  }

  /**
   * Adds the given badge to user's badges.
   *
   * @param badge The badge to add
   */
  public void addBadge(final Badge badge) {
    this.bitFlag |= badge.getBitFlag();
  }

  /**
   * Retrieves an immutable list of all the badges that this user has. If the user has no badges
   * then an empty list will be returned.
   *
   * @return The list of badges
   */
  public List<Badge> getBadges() {
    return Arrays.stream(Badge.values()).filter(this::hasBadge).toList();
  }

  /**
   * Retrieves the underlying bit flag for this user's badges.
   *
   * @return The bit flag
   */
  public int getBitFlag() {
    return this.bitFlag;
  }

  /**
   * Gets the hashcode of this object.
   *
   * @return The hashcode of this object
   */
  @Override
  public int hashCode() {
    return this.bitFlag;
  }

  /**
   * Checks the equality of this object with either another {@link UserBadges} object or an {@link
   * Badge} by checking if the bit flag is equal.
   *
   * @param obj The object to check equality with
   * @return Whether the objects are equal
   */
  @Override
  public boolean equals(final Object obj) {
    // Check if the bit flags are equal
    if (obj.getClass() == UserBadges.class) {
      return this.bitFlag == ((UserBadges) obj).bitFlag;
    } else if (obj.getClass() == Badge.class) {
      return this.bitFlag == ((Badge) obj).getBitFlag();
    }
    return false;
  }
}
