package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SwitchUserController {

  @Inject private UserService User;

  @FXML private HBox users;

  @FXML
  private void onAddUser() {
    User user = new User();

    // Set current user to this user
    // Switch to create user page

    // user.setProfilePicture("src/main/resources/images/defaultUserImage.jpg");
    // user.setUsername("User" + users.getChildren().size());
    //
    // VBox profile = new VBox();
    //
    // File file = new File(user.getProfilePicture());
    // Image image = new Image(file.toURI().toString());
    // ImageView userImage = new ImageView(image);
    // userImage.setFitHeight(100);
    // userImage.setFitWidth(100);
    //
    // Label username = new Label("User" + users.getChildren().size());
    //
    // profile.getChildren().add(userImage);
    // profile.getChildren().add(username);
    //
    // users.getChildren().add(profile);

  }
}
