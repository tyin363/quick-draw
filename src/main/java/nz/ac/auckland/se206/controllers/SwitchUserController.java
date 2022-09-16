package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SwitchUserController implements LoadListener {

  @Inject private UserService UserService;

  @FXML private HBox users;

  @FXML private TextField searchUserText;

  /**
   * Loads user profiles for all the users which are stored as JSON files every time this scene is
   * switched to
   */
  @Override
  public void onLoad() {

    // Clearing previous loaded user profiles
    users.getChildren().clear();
    UserService.setCurrentUser(null);
    for (User user : this.UserService.getUsers()) {

      // Creating user profile
      VBox profile = new VBox();
      File file = new File(user.getProfilePicture());
      Image image = new Image(file.toURI().toString());
      ImageView userImage = new ImageView(image);

      // Setting user profile image dimensions
      userImage.setFitHeight(110);
      userImage.setFitWidth(110);

      // Setting and centering user name
      Label username = new Label(user.getUsername());
      username.setAlignment(Pos.CENTER);
      username.setMaxWidth(Double.MAX_VALUE);

      // Adding profile image and user name to user profile
      profile.getChildren().add(userImage);
      profile.getChildren().add(username);

      // Adding user profile to users
      users.getChildren().add(profile);

      // Setting current user and switching to main menu when user profile is clicked
      profile.setOnMouseClicked(
          event -> {
            UserService.setCurrentUser(user);
            SceneManager.getInstance().switchToView(View.MAIN_MENU);
          });
    }
  }

  /**
   * Switches to the create user scene unless there are already 5 users in which case a warning
   * message is displayed
   */
  @FXML
  private void onAddUser() {

    // Sending a warning message when trying to create more than 5 users
    if (UserService.getUsers().size() > 4) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning (User Limit)");
      alert.setContentText("You can only create up to 5 users!");
      alert.showAndWait();
    } else {

      // ** NEED TO ADD ** Switch to create user page
      // SceneManager.getInstance().switchToView(View.CREATE_USER);
      SceneManager.getInstance().switchToView(View.PROFILE_PAGE);
    }
  }
}
