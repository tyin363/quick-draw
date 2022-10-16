package nz.ac.auckland.se206.core.scenemanager;

public interface FxmlView {

  /**
   * Retrieves the fxml filename associated with this view. By default, if this is an enum it will
   * be the enum name in lowercase.
   *
   * @return The fxml filename
   * @throws UnsupportedOperationException If this method is not overridden, and it's not an enum.
   */
  default String getFxml() {
    // If this an enum, use the enum name in lowercase
    if (this instanceof Enum<?> enumView) {
      return enumView.name().toLowerCase();
    }
    throw new UnsupportedOperationException(
        "You must implement this method if you do not use an enum");
  }
}
