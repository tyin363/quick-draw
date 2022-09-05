package nz.ac.auckland.se206.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum ColourType {
  PRIMARY_MAIN,
  PRIMARY_LIGHT,
  PRIMARY_DARK;

  // The value of 1 prevents the hashmap from resizing
  private static final Map<String, ColourType> colourKeys = new HashMap<>(values().length, 1);

  static {
    for (final ColourType colourType : values()) {
      colourKeys.put(colourType.getKey(), colourType);
    }
  }

  /**
   * Retrieve the ColourType from the key if it exists, or an empty optional if it doesn't.
   *
   * @param key The key to retrieve the colour type of
   * @return An optional containing the colour type
   */
  public static Optional<ColourType> fromKey(final String key) {
    return Optional.ofNullable(colourKeys.get(key));
  }

  private final String key;

  /**
   * Constructs a new colour type where the key is the lowered name with all the '_' characters
   * replaced with '-' and prefixed with '-fx-'. E.g: PRIMARY_MAIN's key would be -fx-primary-main.
   */
  ColourType() {
    this.key = "-fx-" + this.name().toLowerCase().replaceAll("_", "-");
  }

  /**
   * Retrieves the key for this colour type.
   *
   * @return The key for this colour type.
   */
  public String getKey() {
    return this.key;
  }
}
