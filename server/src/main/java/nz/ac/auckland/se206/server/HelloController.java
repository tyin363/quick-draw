package nz.ac.auckland.se206.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

  @FXML
  private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    this.welcomeText.setText("Welcome to JavaFX Application!");
  }
}