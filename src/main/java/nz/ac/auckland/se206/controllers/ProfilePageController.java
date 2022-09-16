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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import org.slf4j.Logger;

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
  @Inject private Logger logger;

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

  /** Enables the user's username to be edited. The option to edit the username will be unhidden. */
  @FXML
  private void onEditUsername() {
    usernameHbox.setVisible(true);
  }

  /** Prompts the user to select a file to choose a profile picture */
  @FXML
  private void onChangePicture() {

    final FileChooser fileChooser = new FileChooser();
    // Accept png and jpg files
    fileChooser
        .getExtensionFilters()
        .addAll(new ExtensionFilter("PNG", "*.png"), new ExtensionFilter("JPG", "*.jpg"));

    final File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      try {
        // set chosen file as profile picture
        Image image = new Image(file.toURI().toString());
        user.setProfilePicture(file.toURI().toString().replace("file:", ""));
        profileImageView.setImage(image);
        userService.saveUser(user);
      } catch (final Exception e) {
        this.logger.error("Error saving image", e);
      }
    }
  }

  /**
   * Sets the username of the user. When the username is set, the option to set the username will be
   * hidden.
   */
  @FXML
  private void onSetUsername() {
    // Do not allow null to be a username
    if (!usernameTextField.getText().isBlank()) {
      usernameLabel.setText(usernameTextField.getText());
      user.setUsername(usernameTextField.getText());
      usernameHbox.setVisible(false);
      userService.saveUser(user);
    }
  }

  /**
   * Current user information is retrieved on load
   *
   * <p>If current user is null, a new user is created and is set to current user
   */
  @Override
  public void onLoad() {
    // Clear past words
    this.pastWordsVbox.getChildren().clear();

    if (userService.getCurrentUser() == null) {
      this.user = new User("New user " + Integer.toString(userService.getUsers().size() + 1));
      userService.saveUser(this.user);
      userService.setCurrentUser(this.user);
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

    // Display past words of user
    for (String word : user.getPastWords()) {
      Label pastWord = new Label();
      pastWord.setText(word);
      this.pastWordsVbox.getChildren().add(pastWord);
    }
  }
}
