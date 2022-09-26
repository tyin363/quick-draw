package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import org.slf4j.Logger;

@Controller
public class HeaderController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Logger logger;

  @FXML private ImageView profilePicture;
  @FXML private Label username;

  private User currentUser;

  @FXML
  private void initialize() {
    final Circle circle = new Circle(this.profilePicture.getFitWidth() / 2);
    this.profilePicture.setClip(circle);
  }

  @Override
  public void onLoad() {
    // If the user hasn't changed, don't bother trying to update the profile picture.
    if (Objects.equals(this.currentUser, this.userService.getCurrentUser())) {
      return;
    }

    this.currentUser = this.userService.getCurrentUser();
    if (this.currentUser == null) {
      return;
    }

    // Set the current user's information
    this.username.setText(this.currentUser.getUsername());
    this.profilePicture.setVisible(true);
    final File file = new File(this.currentUser.getProfilePicture());

    // Check that the profile picture exists
    if (file.exists()) {
      final Image image = new Image(file.toURI().toString());
      this.profilePicture.setImage(image);
      return;
    }

    // If the user doesn't have a profile picture, or it doesn't exist, hide the image.
    this.profilePicture.setVisible(false);
  }

  @FXML
  private void onClickProfile() {
    this.sceneManager.switchToView(View.PROFILE_PAGE);
  }

  @FXML
  private void onSwitchUser() {
    this.sceneManager.switchToView(View.SWITCH_USER);
  }
}
