package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.SoundEffect;

/**
 * Note: This cannot be annotated with @Singleton as a new instance is created for every view it's
 * used within.
 */
@Controller
public class HeaderController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private SoundEffect soundEffect;

  @FXML private ImageView profilePicture;
  @FXML private Label username;
  @FXML private Label switchUserLabel;
  @FXML private Slider volumeSlider;

  private User currentUser;

  /**
   * When the controller is first loaded add a circular clipping to the profile picture so that it's
   * round.
   */
  @FXML
  private void initialize() {
    final double radius = this.profilePicture.getFitWidth() / 2;
    // The first and second parameters are the x and y positions respectively
    final Circle circle = new Circle(radius, radius, radius);
    this.profilePicture.setClip(circle);
    this.userService.addUserSavedListener(this::renderUserProfile);
  }

  /**
   * Everytime the view this is within is switched to check if the user has changed and if it has,
   * then update the profile picture and username.
   */
  @Override
  public void onLoad() {
    final User newCurrentUser = this.userService.getCurrentUser();
    soundEffect.getBackgroundMediaPlayer().setVolume(newCurrentUser.getMusicVolume());
    // If the user hasn't changed, don't bother trying to update the profile picture.
    if (newCurrentUser == null || newCurrentUser.equals(this.currentUser)) {
      return;
    }

    this.currentUser = newCurrentUser;
    this.renderUserProfile(this.currentUser);

    // Add volume slider functionality
    this.volumeSlider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                soundEffect.getBackgroundMediaPlayer().setVolume(volumeSlider.getValue() / 500);
              }
            });

    // Save volume preference of user
    this.volumeSlider.setOnMouseReleased(
        event -> {
          this.currentUser.setMusicVolume(volumeSlider.getValue() / 500);
          this.userService.saveUser(this.currentUser);
        });
  }

  /** Update the profile picture and username to match the specified user. */
  private void renderUserProfile(final User user) {
    // Set the current user's information
    this.volumeSlider.setValue(user.getMusicVolume() * 500);
    this.username.setText(user.getUsername());
    this.profilePicture.setVisible(true);
    final File file = new File(user.getProfilePicture());

    // Check that the profile picture exists
    if (file.exists()) {
      final Image image = new Image(file.toURI().toString());
      this.profilePicture.setImage(image);
      return;
    }

    // If the user doesn't have a profile picture, or it doesn't exist, hide the image.
    this.profilePicture.setVisible(false);
  }

  /** Buttons are disabled and will not be able to be clicked */
  public void disableButtons() {
    this.profilePicture.setMouseTransparent(true);
    this.switchUserLabel.setMouseTransparent(true);
  }

  /** When the user clicks on the profile picture, take them to the profile page view. */
  @FXML
  private void onClickProfile() {
    this.soundEffect.playClickSound();
    this.sceneManager.switchToView(View.PROFILE_PAGE);
  }

  /** When the user clicks on the switch user text, take them to the switch user view. */
  @FXML
  private void onSwitchUser() {
    this.soundEffect.playClickSound();
    this.sceneManager.switchToView(View.SWITCH_USER);
  }
}
