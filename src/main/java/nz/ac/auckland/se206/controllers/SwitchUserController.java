package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.components.UserProfile;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SwitchUserController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;

  @FXML private HBox users;
  @FXML private Button newUser;

  /**
   * Loads user profiles for all the users which are stored as JSON files every time this scene is
   * switched to
   */
  @Override
  public void onLoad() {

    // Clearing previous loaded user profiles
    this.users.getChildren().clear();
    this.userService.setCurrentUser(null);

    for (final User user : this.userService.getUsers()) {
      final UserProfile profile = new UserProfile(user);

      // Adding user profile to users
      this.users.getChildren().add(profile);

      // Setting current user and switching to main menu when user profile is clicked
      profile.setOnMouseClicked(
          event -> {
            this.userService.setCurrentUser(user);
            this.sceneManager.switchToView(View.MAIN_MENU);
          });
    }

    // Make sure that the new user button is always displayed last
    this.users.getChildren().add(this.newUser);
  }

  /**
   * Switches to the create user scene unless there are already 5 users in which case a warning
   * message is displayed
   */
  @FXML
  private void onAddUser() {
    // Sending a warning message when trying to create more than 5 users
    if (this.userService.getUsers().size() > 4) {
      final Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning (User Limit)");
      alert.setContentText("You can only create up to 5 users!");
      alert.showAndWait();
    } else {

      this.sceneManager.switchToView(View.PROFILE_PAGE);
    }
  }
}
