package nz.ac.auckland.se206.client.controllers;

import java.util.Comparator;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import nz.ac.auckland.se206.client.components.profile.UserProfile;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.users.User;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.Config;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;

@Singleton
public class SwitchUserController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Config config;
  @Inject private SoundEffect soundEffect;

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
    users.sort(Comparator.comparing(User::getCreated));

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
            this.soundEffect.playSound(Sound.CLICK);
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
   * Creates a new user and then switches to the user profile scene. This method can only even be
   * invoked if there is less than the maximum allowed number of users.
   */
  @FXML
  private void onAddUser() {
    this.soundEffect.playSound(Sound.CLICK);

    final List<User> users = this.userService.getUsers();
    int newUserValue = 1;

    while (true) {
      final String username = "New user " + newUserValue;
      // Check that no user already exists with this default username
      if (users.stream().noneMatch(user -> user.getUsername().equals(username))) {
        final User newUser = new User(username);
        this.userService.saveUser(newUser);
        this.userService.setCurrentUser(newUser);
        this.sceneManager.switchToView(View.PROFILE_PAGE);
        break;
      }

      // Increment the new user value until a unique username is found
      newUserValue++;
    }
  }
}
