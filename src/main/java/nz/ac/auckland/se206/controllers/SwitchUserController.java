package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SwitchUserController implements LoadListener {

  @Inject private UserService UserService;

  @FXML private HBox users;

  @Override
  public void onLoad() {
    for (User user : this.UserService.getUsers()) {

      VBox profile = new VBox();
      File file = new File(user.getProfilePicture());
      Image image = new Image(file.toURI().toString());
      ImageView userImage = new ImageView(image);
      userImage.setFitHeight(100);
      userImage.setFitWidth(100);
      Label username = new Label(user.getUsername());
      profile.getChildren().add(userImage);
      profile.getChildren().add(username);
      users.getChildren().add(profile);
    }
  }

  @FXML
  private void onAddUser() {}
}
