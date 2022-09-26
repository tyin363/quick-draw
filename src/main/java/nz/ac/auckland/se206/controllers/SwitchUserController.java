package nz.ac.auckland.se206.controllers;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.components.UserProfile;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Config;

@Singleton
public class SwitchUserController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Config config;

  @FXML private GridPane userGrid;
  @FXML private Button newUser;

  /**
   * Loads user profiles for all the users which are stored as JSON files every time this scene is
   * switched to
   */
  @Override
  public void onLoad() {

    // Clearing previous loaded user profiles
    this.userGrid.getChildren().clear();
    this.userService.setCurrentUser(null);

    final List<User> users = this.userService.getUsers();

    int index = 0;
    for (final User user : users) {
      final UserProfile profile = new UserProfile(user);

      // Calculate the position of the user profile in the grid
      final int row = index / 3;
      final int column = index % 3;
      this.userGrid.add(profile, column, row);

      // Setting current user and switching to main menu when user profile is clicked
      profile.setOnMouseClicked(
          event -> {
            this.userService.setCurrentUser(user);
            this.sceneManager.switchToView(View.MAIN_MENU);
          });
      index++;
    }

    // Only render the new user button if there isn't the maximum number of users
    if (users.size() < this.config.getMaxUserCount()) {
      // Make sure that the new user button is always displayed last
      final int row = index / 3;
      final int column = index % 3;
      this.userGrid.add(this.newUser, column, row);
    }
  }

  /**
   * Switches to the create user scene. This can only even be invoked if there is less tha maximum
   * allowed number of users.
   */
  @FXML
  private void onAddUser() {
    this.sceneManager.switchToView(View.PROFILE_PAGE);
  }
}
