package nz.ac.auckland.se206.core.words;

import java.util.HashMap;
import java.util.Map;

public enum Difficulty {
  EASY("E"),
  MEDIUM("M"),
  HARD("H");

  private static final Map<String, Difficulty> ALIAS_MAPPING = new HashMap<>();

  static {
    for (final Difficulty difficulty : values()) {
      ALIAS_MAPPING.put(difficulty.getAlias(), difficulty);
    }
  }

  /**
   * Retrieves the {@link Difficulty} from the specified alias.
   *
   * @param alias The alias of the difficulty. This must be capitalised.
   * @return The matching difficulty or null if there is no matching one.
   */
  public static Difficulty fromAlias(final String alias) {
    return ALIAS_MAPPING.get(alias);
  }

  private final String alias;

  /**
   * Constructors a new difficulty with the specified alias. This alias can be used to retrieve the
   * difficulty using {@link #fromAlias(String)}.
   *
   * @param alias The alias of the difficulty.
   */
  Difficulty(final String alias) {
    this.alias = alias;
  }

  /**
   * Get the alias for this difficulty.
   *
   * @return This difficulty's alias
   */
  public String getAlias() {
    return this.alias;
  }
}
