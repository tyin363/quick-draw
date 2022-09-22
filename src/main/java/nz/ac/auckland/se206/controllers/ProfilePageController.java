package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.fxml.FXML;
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
import nz.ac.auckland.se206.users.Round;
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
  @FXML private TextField usernameTextField;
  @FXML private HBox usernameHbox;
  @FXML private VBox pastWordsVbox;
  @FXML private Text secondsText;

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Logger logger;

  private User user;

  /** Switch to Main menu so user can choose what to do from there */
  @FXML
  private void onSwitchToMenu() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  /** Switch to switch user page so user can change their accounts or add a new one */
  @FXML
  private void onSwitchAccount() {
    this.sceneManager.switchToView(View.SWITCH_USER);
  }

  /** Enables the user's username to be edited. The option to edit the username will be unhidden. */
  @FXML
  private void onEditUsername() {
    this.usernameHbox.setVisible(true);
  }

  /** Prompts the user to select a file to choose a profile picture */
  @FXML
  private void onChangePicture() {

    final FileChooser fileChooser = new FileChooser();
    // Accept png and jpg files
    fileChooser
        .getExtensionFilters()
        .addAll(new ExtensionFilter("PNG", "*.png"), new ExtensionFilter("JPG", "*.jpg"));

    final File file = fileChooser.showOpenDialog(this.sceneManager.getStage());
    if (file != null) {
      try {
        // set chosen file as profile picture
        final Image image = new Image(file.toURI().toString());
        this.user.setProfilePicture(file.getAbsolutePath());
        this.profileImageView.setImage(image);
        this.userService.saveUser(this.user);
      } catch (final SecurityException e) {
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
    if (!this.usernameTextField.getText().isBlank()) {
      this.usernameLabel.setText(this.usernameTextField.getText());
      this.user.setUsername(this.usernameTextField.getText());
      this.usernameHbox.setVisible(false);
      this.userService.saveUser(this.user);

      // Clear text field after use
      this.usernameTextField.clear();
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

    if (this.userService.getCurrentUser() == null) {
      this.user = new User("New user " + (this.userService.getUsers().size() + 1));
      this.userService.saveUser(this.user);
      this.userService.setCurrentUser(this.user);
    } else {
      this.user = this.userService.getCurrentUser();
    }

    // Set labels on GUI
    this.usernameHbox.setVisible(false);
    this.gamesLostLabel.setText(Integer.toString(this.user.getGamesLost()));
    this.gamesWonLabel.setText(Integer.toString(this.user.getGamesWon()));
    this.usernameLabel.setText(this.user.getUsername());

    // Set profile picture
    final File file = new File(this.user.getProfilePicture());
    final Image image = new Image(file.toURI().toString());
    this.profileImageView.setImage(image);

    // If fastest time is 0, display no time
    if (this.user.getFastestTime() == 0) {
      this.secondsText.setVisible(false);
      this.fastestTimeLabel.setText("No Time");
    } else {
      this.secondsText.setVisible(true);
      this.fastestTimeLabel.setText(Integer.toString(this.user.getFastestTime()));
    }

    // Display past words of user
    for (final Round round : this.user.getPastRounds()) {
      final Label pastWord = new Label();
      pastWord.setText(round.getWord());
      this.pastWordsVbox.getChildren().add(pastWord);
    }
  }
}
