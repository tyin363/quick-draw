package nz.ac.auckland.se206.controllers.scenemanager;

import java.util.Locale;

public enum View {
  MAIN_MENU,
  CONFIRMATION_SCREEN,
  CANVAS;

  private final String fxml;

  /** Constructs a view where the fxml filename is the same as the view name in lowercase. */
  View() {
    this.fxml = this.name().toLowerCase(Locale.ROOT);
  }

  /**
   * Retrieves the fxml filename for the view.
   *
   * @return The fxml filename for the view.
   */
  public String getFxml() {
    return this.fxml;
  }
}
