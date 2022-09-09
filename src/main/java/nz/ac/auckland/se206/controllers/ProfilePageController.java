package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;

public class ProfilePageController {
  @FXML private Label gamesLostLabel;
  @FXML private Label gamesWonLabel;
  @FXML private Label usernameLabel;
  @FXML private ImageView profileImageView;
  @FXML private Button mainMenuButtton;

  /** Switch to Main menu so user can choose what to do from there */
  @FXML
  private void onSwitchToMenu() {
    SceneManager.getInstance().switchToView(View.MAIN_MENU);
  }
}
