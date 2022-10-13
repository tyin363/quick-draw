package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
import nz.ac.auckland.se206.util.Helpers;
import org.slf4j.Logger;

@Singleton
public class ProfilePageController implements LoadListener {

  @FXML private Label gamesLostLabel;
  @FXML private Label gamesWonLabel;
  @FXML private Label usernameLabel;
  @FXML private Label fastestTimeLabel;
  @FXML private ImageView profileImageView;
  @FXML private TextField usernameTextField;
  @FXML private VBox pastWordsVbox;
  @FXML private Label secondsLabel;
  @FXML private Label currentWinstreakLabel;
  @FXML private Label bestWinstreakLabel;
  @FXML private StackPane fireStackPane;
  @FXML private AnchorPane header;
  @FXML private Button setUsernameButton;
  @FXML private Button editUsernameButton;
  @FXML private Button cancelButton;
  @FXML private StackPane hoverImageStackPane;
  @FXML private StackPane usernameStackPane;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Logger logger;

  private double usernameTextFieldWidth = 250;
  private User user;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  /** Delete the current user and then take them back to the switch user view. */
  @FXML
  private void onDeleteUser() {
    this.userService.deleteUser(this.user);
    this.sceneManager.switchToView(View.SWITCH_USER);
  }

  /** Delete the current user and then take them back to the switch user view. */
  @FXML
  private void onCancelEdit() {
    setEditUsernameMode(false);
  }

  /** Enables the user's username to be edited. The option to edit the username will be unhidden. */
  @FXML
  private void onEditUsername() {
    this.usernameStackPane.setPrefWidth(usernameTextFieldWidth);
    setEditUsernameMode(true);
  }

  /** Prompts the user to select a file to choose a profile picture */
  @FXML
  private void onChangePicture() {

    final FileChooser fileChooser = new FileChooser();

    // Accept only png and jpeg files
    final List<String> extensions = new ArrayList<>();
    extensions.add("*.jpg");
    extensions.add("*.png");
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", extensions));

    final File file = fileChooser.showOpenDialog(this.sceneManager.getStage());
    if (file != null) {
      try {
        // set chosen file as profile picture
        final Image image = new Image(file.toURI().toString());
        this.user.setProfilePicture(file.getAbsolutePath());
        this.profileImageView.setImage(image);
        this.addBorderToImage(this.profileImageView, image, 20);
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
      this.userService.saveUser(this.user);

      // Clear text field after use
      this.usernameTextField.clear();

      // Set username elements
      setEditUsernameMode(false);
      setUsernameWidth();
    }
  }

  /**
   * This method will display the change image text prompt on top of the profile image of the user
   */
  @FXML
  private void onEnterImage() {
    this.hoverImageStackPane.setVisible(true);
  }

  /**
   * This method will display the change image text prompt on top of the profile image of the user
   */
  @FXML
  private void onExitImage() {
    this.hoverImageStackPane.setVisible(false);
  }

  /** This method sets the width of the stackpane which the username label is in. */
  private void setUsernameWidth() {
    Text tmpText = new Text(this.user.getUsername());
    double textWidth;
    tmpText.setFont(this.usernameLabel.getFont());

    // Add extra 10 size to be sure
    textWidth = tmpText.getLayoutBounds().getWidth() + 10;
    this.usernameStackPane.setPrefWidth(textWidth);
  }

  /**
   * This sets the visibility of elements concerned with editing the username
   *
   * @param isEdit The boolean value that will determines visibility of elements
   */
  private void setEditUsernameMode(boolean isEdit) {
    // Visibility of default elements are visible
    this.editUsernameButton.setVisible(!isEdit);
    this.usernameLabel.setVisible(!isEdit);

    // Visibility of edit username elements are opposite of default
    this.setUsernameButton.setVisible(isEdit);
    this.usernameTextField.setVisible(isEdit);
    this.cancelButton.setVisible(isEdit);
  }

  /**
   * Current user information is retrieved on load
   *
   * <p>If current user is null, a new user is created and is set to current user
   */
  @Override
  public void onLoad() {

    // Set visible and invisible initial nodes
    this.editUsernameButton.setVisible(true);
    this.usernameLabel.setVisible(true);
    this.setUsernameButton.setVisible(false);
    this.usernameTextField.setVisible(false);
    this.cancelButton.setVisible(false);

    // Clear past words
    this.pastWordsVbox.getChildren().clear();

    // Sanity check, this should never be true.
    if (this.userService.getCurrentUser() == null) {
      return;
    }
    this.user = this.userService.getCurrentUser();

    // Set fire to current win streak if 1 or above
    this.fireStackPane.setVisible(this.user.getCurrentWinStreak() > 0);

    // Set labels on GUI
    this.gamesLostLabel.setText(Integer.toString(this.user.getGamesLost()));
    this.gamesWonLabel.setText(Integer.toString(this.user.getGamesWon()));
    this.usernameLabel.setText(this.user.getUsername());
    setUsernameWidth();

    this.currentWinstreakLabel.setText(Integer.toString(this.user.getCurrentWinStreak()));
    this.bestWinstreakLabel.setText(Integer.toString(this.user.getBestWinStreak()));

    // Set profile picture
    final File file = new File(this.user.getProfilePicture());
    final Image image = new Image(file.toURI().toString());
    this.profileImageView.setImage(image);
    this.addBorderToImage(this.profileImageView, image, 20);
    this.hoverImageStackPane.setVisible(false);

    // If fastest time is -1 (hasn't played a game yet), display no time
    if (this.user.getFastestTime() == -1) {
      this.secondsLabel.setVisible(false);
      this.fastestTimeLabel.setText("No Time");
    } else {
      this.secondsLabel.setVisible(true);
      this.fastestTimeLabel.setText(Integer.toString(this.user.getFastestTime()));
    }

    // Display past words of user
    for (final Round round : this.user.getPastRounds()) {
      final Label pastWord = new Label();
      pastWord.setText(round.getWord());
      // Add colour to word
      pastWord.getStyleClass().add("text-default");
      pastWord.setStyle("-fx-font-size: 25px;");
      this.pastWordsVbox.getChildren().add(pastWord);
    }
  }

  /**
   * This will make the ImageView have rounded corners. This gives a similar effect as the border
   * radius effect on a button.
   *
   * @param imageView The ImageView displayed on fxml
   * @param image The original image
   * @param borderRadius The border radius of the image
   */
  private void addBorderToImage(
      final ImageView imageView, final Image image, final int borderRadius) {
    // Get height and width of image
    final double aspectRatio = image.getWidth() / image.getHeight();
    final double realWidth =
        Math.min(imageView.getFitWidth(), imageView.getFitHeight() * aspectRatio);
    final double realHeight =
        Math.min(imageView.getFitHeight(), imageView.getFitWidth() / aspectRatio);

    // Clip the imageView to a rectangle with rounded borders
    final Rectangle clip = new Rectangle();
    clip.setWidth(realWidth);
    clip.setHeight(realHeight);
    clip.setArcHeight(borderRadius);
    clip.setArcWidth(borderRadius);
    imageView.setClip(clip);

    // Snapshot the clipped image and store it as the ImageView
    final SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    final WritableImage writableImage = imageView.snapshot(parameters, null);
    imageView.setImage(writableImage);

    // Remove previous clip from ImageView
    imageView.setClip(null);
  }
}
