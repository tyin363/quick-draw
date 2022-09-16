package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class ProfilePageController implements LoadListener {
  @FXML private Label gamesLostLabel;
  @FXML private Label gamesWonLabel;
  @FXML private Label usernameLabel;
  @FXML private Label fastestTimeLabel;
  @FXML private ImageView profileImageView;
  @FXML private Button switchAccountButton;
  @FXML private TextField usernameTextField;
  @FXML private HBox usernameHbox;
  @FXML private VBox pastWordsVbox;
  @FXML private Text secondsText;
  @Inject private UserService userService;

  private User user;

  /** Switch to Main menu so user can choose what to do from there */
  @FXML
  private void onSwitchToMenu() {
    SceneManager.getInstance().switchToView(View.MAIN_MENU);
  }

  /** Switch to switch user page so user can change their accounts or add a new one */
  @FXML
  private void onSwitchAccount() {
    SceneManager.getInstance().switchToView(View.SWITCH_USER);
  }

  @FXML
  private void onEditUsername() {
    usernameHbox.setVisible(true);
  }

  @FXML
  private void onSetUsername() {
    // Do not allow null to be a username
    if (usernameTextField.getText() != "") {
      usernameLabel.setText(usernameTextField.getText());
      user.setUsername(usernameTextField.getText());
      usernameHbox.setVisible(false);
      userService.saveUser(user);
    }
  }

  @Override
  public void onLoad() {
    System.out.println("Profile Page loaded");
    /*
     * Temporary method to get user from the saved users. The first user in the list will be
     * retrieved.
     *
     * If there are no users, a new user is created
     *
     * A way to get the 'current' user is to be implemented in the future
     */
    System.out.println(userService.getUsers());
    if (userService.getCurrentUser() == null) {
      User newUser = new User("New user " + Integer.toString(userService.getUsers().size() + 1));
      userService.saveUser(newUser);
      userService.setCurrentUser(newUser);
      user = newUser;
    } else {
      user = userService.getCurrentUser();
    }

    // Set labels on GUI
    usernameHbox.setVisible(false);
    gamesLostLabel.setText(Integer.toString(user.getGamesLost()));
    gamesWonLabel.setText(Integer.toString(user.getGamesWon()));
    usernameLabel.setText(user.getUsername());

    // Set profile picture
    File file = new File(user.getProfilePicture());
    Image image = new Image(file.toURI().toString());

    profileImageView.setImage(image);

    // If fastest time is 0, display no time
    if (user.getFastestTime() == 0) {
      secondsText.setVisible(false);
      fastestTimeLabel.setText("No Time");
    } else {
      secondsText.setVisible(true);
      fastestTimeLabel.setText(Integer.toString(user.getFastestTime()));
    }

    if (!user.getPastWords().isEmpty()) {
      for (String word : user.getPastWords()) {
        Label pastWord = new Label();
        pastWord.setText(word);
        this.pastWordsVbox.getChildren().add(pastWord);
      }
    }
  }
}
