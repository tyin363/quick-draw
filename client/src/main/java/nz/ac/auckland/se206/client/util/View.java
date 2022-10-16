package nz.ac.auckland.se206.client.util;

import java.util.Locale;
import nz.ac.auckland.se206.core.scenemanager.FxmlView;

public enum View implements FxmlView {
  MAIN_MENU,
  CONFIRMATION_SCREEN,
  CANVAS,
  PROFILE_PAGE,
  SWITCH_USER,
  SETTINGS;

  private final String fxml;

  /** Constructs a view where the fxml filename is the same as the view name in lowercase. */
  View() {
    this.fxml = this.name().toLowerCase(Locale.ROOT);
  }

  /**
   * Constructs a view where the fxml filename can be manually specified.
   *
   * @param fxml The name of the fxml file
   */
  View(final String fxml) {
    this.fxml = fxml;
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
