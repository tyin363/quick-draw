package nz.ac.auckland.se206.util;

import java.net.URL;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;

public class Helpers {

  /**
   * Given the header referenced by {@link nz.ac.auckland.se206.controllers.HeaderController}, it
   * will iterate through the known hierarchy to find the back button.
   *
   * @param header The header root element
   * @return The back button reference
   */
  public static Button getBackButton(final AnchorPane header) {
    // The ordering is: header -> h-box -> anchor pane -> h-box -> back button
    HBox hbox = (HBox) header.getChildren().get(0);
    final AnchorPane anchorPane = (AnchorPane) hbox.getChildren().get(0);
    hbox = (HBox) anchorPane.getChildren().get(0);
    return (Button) hbox.getChildren().get(0);
  }

  /**
   * Loads the sound from the given filename and returns the media object for it.
   *
   * @param filename The file name of the sound
   * @return The media object for the sound
   */
  public static Media loadSound(final String filename) {
    // All sounds are stored in the resources/sounds folder
    final URL url = Helpers.class.getResource("/sounds/" + filename);
    if (url != null) {
      return new Media(url.toExternalForm());
    } else {
      throw new IllegalArgumentException("Couldn't load sound: " + filename);
    }
  }
}
