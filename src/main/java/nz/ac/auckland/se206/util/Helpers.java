package nz.ac.auckland.se206.util;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class Helpers {

  /**
   * Given the header referenced by {@link nz.ac.auckland.se206.controllers.HeaderController}, it
   * will iterate through the known hierarchy to find the back button.
   *
   * @param header The header root element
   * @return The back button reference
   */
  public static Button getBackButton(final AnchorPane header) {
    HBox hbox = (HBox) header.getChildren().get(0);
    final AnchorPane anchorPane = (AnchorPane) hbox.getChildren().get(0);
    hbox = (HBox) anchorPane.getChildren().get(0);
    return (Button) hbox.getChildren().get(0);
  }
}
