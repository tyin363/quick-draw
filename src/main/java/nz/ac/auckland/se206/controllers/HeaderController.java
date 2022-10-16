package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

/**
 * Note: This cannot be annotated with @Singleton as a new instance is created for every view it's
 * used within.
 */
@Controller
public class HeaderController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;

  @FXML private ImageView profilePicture;
  @FXML private Label username;

  private User currentUser;

  /**
   * When the controller is first loaded add a circular clipping to the profile picture so that it's
   * round.
   */
  @FXML
  private void initialize() {
    final double radius = this.profilePicture.getFitWidth() / 2;
    // The first and second parameters are the x and y positions respectively
    final Circle circle = new Circle(radius, radius, radius);
    this.profilePicture.setClip(circle);
    this.userService.addUserSavedListener(this::renderUserProfile);
  }

  /**
   * Everytime the view this is within is switched to check if the user has changed and if it has,
   * then update the profile picture and username.
   */
  @Override
  public void onLoad() {
    final User newCurrentUser = this.userService.getCurrentUser();

    // If the user hasn't changed, don't bother trying to update the profile picture.
    if (newCurrentUser == null || newCurrentUser.equals(this.currentUser)) {
      return;
    }

    this.currentUser = newCurrentUser;
    this.renderUserProfile(this.currentUser);
  }

  /** Update the profile picture and username to match the specified user. */
  private void renderUserProfile(final User user) {
    // Set the current user's information
    this.username.setText(user.getUsername());
    this.profilePicture.setVisible(true);
    this.profilePicture.setImage(user.getProfileImage());
  }

  /** When the user clicks on the profile picture, take them to the profile page view. */
  @FXML
  private void onClickProfile() {
    this.sceneManager.switchToView(View.PROFILE_PAGE);
  }

  /** When the user clicks on the switch user text, take them to the switch user view. */
  @FXML
  private void onSwitchUser() {
    this.sceneManager.switchToView(View.SWITCH_USER);
  }
}
