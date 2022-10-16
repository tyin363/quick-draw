package nz.ac.auckland.se206.client.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import nz.ac.auckland.se206.client.badges.Badge;
import nz.ac.auckland.se206.client.components.profile.DisplayBadge;
import nz.ac.auckland.se206.client.components.profile.RoundEntry;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.users.User;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.Helpers;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;
import org.slf4j.Logger;

@Singleton
public class ProfilePageController implements LoadListener {

  @FXML private Label winRate;
  @FXML private Label winsCount;
  @FXML private Label lossesCount;
  @FXML private Label fastestTime;
  @FXML private Label roundsPlayed;
  @FXML private Label currentWinStreak;
  @FXML private Label bestWinStreak;
  @FXML private HBox winBarContainer;
  @FXML private Pane winSection;
  @FXML private VBox roundHistoryEntries;
  @FXML private StackPane profilePictureContainer;
  @FXML private ImageView profileImageView;
  @FXML private StackPane changeImageOverlay;
  @FXML private Label username;
  @FXML private TextField usernameTextField;
  @FXML private HBox editUsernameContainer;
  @FXML private Button saveUsernameButton;
  @FXML private Button editUsernameButton;
  @FXML private Pane discardUsernameChanges;
  @FXML private HBox speedBadgesContainer;
  @FXML private HBox streakBadgesContainer;
  @FXML private AnchorPane header;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private Logger logger;
  @Inject private SoundEffect soundEffect;

  private User user;
  private List<DisplayBadge> displayBadges;

  /** Perform once off initialisations for this controller. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
    // Make the win bar scale with the window
    this.winBarContainer
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (this.user != null) {
                final double winWidth = newValue.doubleValue() * this.user.getWinRate();
                this.winSection.setPrefWidth(winWidth);
              }
            });

    // Make the profile picture slightly rounded.
    final Rectangle rect = new Rectangle(220, 220);
    rect.setArcWidth(10);
    rect.setArcHeight(10);
    this.profilePictureContainer.setClip(rect);

    // Add a tooltip to the discard username changes button
    final Tooltip tooltip = new Tooltip("Discard the current changes to the username");
    Tooltip.install(this.discardUsernameChanges, tooltip);

    this.renderBadges();
  }

  /**
   * Current user information is retrieved on load
   *
   * <p>If current user is null, a new user is created and is set to current user
   */
  @Override
  public void onLoad() {
    // Sanity check, this should never be true.
    if (this.userService.getCurrentUser() == null) {
      return;
    }
    this.user = this.userService.getCurrentUser();

    this.renderRoundHistory();
    this.renderCurrentUserStatistics();
    this.renderUserProfilePicture(this.user.getProfileImage());

    // Set the username and hide the edit username elements
    this.username.setText(this.user.getUsername());
    this.setEditUsernameMode(false);
    this.displayBadges.forEach(displayBadge -> displayBadge.update(this.user));
  }

  /** Render all the badges that can be achieved. */
  private void renderBadges() {
    // Create all the display badges and group them by their badge group (Either speed or streak)
    final Map<String, List<DisplayBadge>> badges =
        Arrays.stream(Badge.values())
            .map(DisplayBadge::new)
            .collect(Collectors.groupingBy(display -> display.getBadge().getBadgeGroup()));
    this.speedBadgesContainer.getChildren().addAll(badges.get("speed"));
    this.streakBadgesContainer.getChildren().addAll(badges.get("streak"));

    // Store all the display badges in a list for easy access later
    this.displayBadges = new ArrayList<>();
    this.displayBadges.addAll(badges.get("speed"));
    this.displayBadges.addAll(badges.get("streak"));
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.soundEffect.playSound(Sound.CLICK);

    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  /** Delete the current user and then take them back to the switch user view. */
  @FXML
  private void onDeleteProfile() {
    this.soundEffect.playSound(Sound.CANCEL);
    this.userService.deleteUser(this.user);
    this.sceneManager.switchToView(View.SWITCH_USER);
  }

  /** Discards the current changes to the username. */
  @FXML
  private void onDiscardUsernameChanges() {
    this.soundEffect.playSound(Sound.CANCEL);
    this.setEditUsernameMode(false);
  }

  /** Enables the user's username to be edited. The option to edit the username will be unhidden. */
  @FXML
  private void onEditUsername() {
    this.soundEffect.playSound(Sound.CLICK);
    this.usernameTextField.setText(this.user.getUsername());
    this.setEditUsernameMode(true);
    // Move the cursor to the text field. This can only be done if the text field is visible.
    this.usernameTextField.requestFocus();
  }

  /**
   * Whenever the user types in the username text field, check if the username is valid. If it
   * isn't, then disable the save button.
   */
  @FXML
  private void onChangeUsername() {
    final String newUsername = this.usernameTextField.getText();
    this.saveUsernameButton.setDisable(newUsername.isBlank());
  }

  /** Prompts the user to select a file to choose a profile picture. */
  @FXML
  private void onChangeProfilePicture() {
    // Play click sound effect
    this.soundEffect.playSound(Sound.CLICK);

    final FileChooser fileChooser = new FileChooser();

    // Accept only png and jpeg files
    final List<String> extensions = new ArrayList<>();
    extensions.add("*.jpg");
    extensions.add("*.png");
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", extensions));

    final File file = fileChooser.showOpenDialog(this.sceneManager.getStage());
    if (file != null) {
      // Set the chosen file as profile picture
      final String absolutePath = file.getAbsolutePath();
      this.renderUserProfilePicture(new Image(absolutePath));
      this.user.setProfilePicture(absolutePath);
      this.userService.saveUser(this.user);
    }
  }

  /**
   * Sets the username of the user. When the username is set, the text-field and save button will be
   * hidden.
   */
  @FXML
  private void onSaveUsername() {
    // Do not allow them to set an empty username
    if (!this.usernameTextField.getText().isBlank()) {
      this.soundEffect.playSound(Sound.SETTINGS_CLICK);
      this.username.setText(this.usernameTextField.getText());
      this.user.setUsername(this.usernameTextField.getText());
      this.userService.saveUser(this.user);

      // Hide edit username elements
      this.setEditUsernameMode(false);
    }
  }

  /**
   * This method will display the change image text prompt on top of the profile image of the user.
   */
  @FXML
  private void onEnterImage() {
    this.changeImageOverlay.setVisible(true);
  }

  /** This method will hide the change image text prompt on top of the profile image of the user. */
  @FXML
  private void onExitImage() {
    this.changeImageOverlay.setVisible(false);
  }

  /**
   * This sets the visibility of elements concerned with editing the username.
   *
   * @param isEditing Whether the username is being edited
   */
  private void setEditUsernameMode(final boolean isEditing) {
    // Visibility of default elements are visible
    this.editUsernameButton.setVisible(!isEditing);
    this.username.setVisible(!isEditing);

    // Visibility of edit username elements are opposite of default
    this.saveUsernameButton.setVisible(isEditing);
    this.editUsernameContainer.setVisible(isEditing);
    // this.cancelButton.setVisible(isEditing);
  }

  /** Renders the current users statistics and updates the win/loss bar. */
  private void renderCurrentUserStatistics() {
    final double winRate = this.user.getWinRate();
    final int totalGames = this.user.getTotalGames();

    // If the user hasn't won any games, display '–%' instead
    if (totalGames == 0) {
      this.winRate.setText("–%");
    } else {
      this.winRate.setText(Math.round(100 * winRate) + "%");
    }

    // Render all the user statistics
    this.winsCount.setText(Integer.toString(this.user.getGamesWon()));
    this.lossesCount.setText(Integer.toString(this.user.getGamesLost()));
    this.roundsPlayed.setText(Integer.toString(totalGames));
    this.currentWinStreak.setText(Integer.toString(this.user.getCurrentWinStreak()));
    this.bestWinStreak.setText(Integer.toString(this.user.getBestWinStreak()));

    // If the user doesn't have a fastest time, display '–' instead.
    final int fastestTime = this.user.getFastestTime();
    if (fastestTime == -1) {
      this.fastestTime.setText("–");
    } else {
      this.fastestTime.setText(Integer.toString(fastestTime));
    }

    // Render the win/loss bar
    final double winWidth = this.winBarContainer.getWidth() * winRate;
    this.winSection.setPrefWidth(winWidth);
  }

  /**
   * Renders the current users past rounds. This will automatically clear any previously rendered
   * rounds first.
   */
  private void renderRoundHistory() {
    // Clear any previous round history
    this.roundHistoryEntries.getChildren().clear();

    // Convert the users past rounds into round entry components
    final List<RoundEntry> entries =
        this.user.getPastRounds().stream().map(RoundEntry::new).collect(Collectors.toList());

    // Display most recent rounds at the top
    Collections.reverse(entries);

    this.roundHistoryEntries.getChildren().addAll(entries);
  }

  /**
   * Renders the users profile picture from the given image.
   *
   * @param image The image to render
   */
  private void renderUserProfilePicture(final Image image) {
    this.profileImageView.setImage(image);
    this.changeImageOverlay.setVisible(false);
  }
}
