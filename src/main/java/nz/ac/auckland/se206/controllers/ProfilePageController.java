package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

  @FXML
  private void onEditUsername() {
    usernameHbox.setVisible(true);
  }

  @FXML
  private void onSetUsername() {
    if (usernameTextField.getText() != "") {
      usernameLabel.setText(usernameTextField.getText());
      user.setUsername(usernameTextField.getText());
      usernameHbox.setVisible(false);
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
    if (userService.getUsers().isEmpty()) {
      User newUser = new User("New user");
      userService.saveUser(newUser);
      user = newUser;
    } else {
      user = userService.getUsers().get(0);
    }

    // Set labels on GUI
    usernameHbox.setVisible(false);
    gamesLostLabel.setText(Integer.toString(user.getGamesLost()));
    gamesWonLabel.setText(Integer.toString(user.getGamesWon()));
    usernameLabel.setText(user.getUsername());

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
