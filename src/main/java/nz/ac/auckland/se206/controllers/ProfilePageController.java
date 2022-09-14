package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;

@Singleton
public class ProfilePageController implements LoadListener {
  @FXML private Label gamesLostLabel;
  @FXML private Label gamesWonLabel;
  @FXML private Label usernameLabel;
  @FXML private ImageView profileImageView;
  @FXML private Button switchAccountButton;
  @FXML private TextField usernameTextField;
  @FXML private HBox usernameHbox;

  @Inject private User user;

  /** Switch to Main menu so user can choose what to do from there */
  @FXML
  private void onSwitchToMenu() {
    SceneManager.getInstance().switchToView(View.MAIN_MENU);
  }

  @FXML
  private void onEditUsername() {
    usernameLabel.setText(usernameTextField.getText());
    user.setUsername(usernameTextField.getText());
  }

  @Override
  public void onLoad() {
    System.out.println("Profile Page loaded");
  }
}
