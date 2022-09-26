package nz.ac.auckland.se206.util;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class Helpers {

  public static Button getBackButton(final AnchorPane header) {
    HBox hbox = (HBox) header.getChildren().get(0);
    final AnchorPane anchorPane = (AnchorPane) hbox.getChildren().get(0);
    hbox = (HBox) anchorPane.getChildren().get(0);
    return (Button) hbox.getChildren().get(0);
  }
}
