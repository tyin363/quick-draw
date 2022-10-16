package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
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
  @FXML private Slider musicVolumeSlider;
  @FXML private Slider soundEffectVolumeSlider;
  @FXML private Pane profilePicturePane;
  @FXML private Button backButton;
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
    // If the user hasn't changed, don't bother trying to update the profile picture.
    if (newCurrentUser == null || newCurrentUser.equals(this.currentUser)) {
      return;
    }
    // Set audio volume
    soundEffect.getBackgroundMediaPlayer().setVolume(newCurrentUser.getMusicVolume());
    soundEffect.getMediaPlayer().setVolume(newCurrentUser.getSoundEffectVolume());

    this.currentUser = newCurrentUser;
    this.renderUserProfile(this.currentUser);

    // Add sound effect volume slider functionality
    this.soundEffectVolumeSlider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                soundEffect
                    .getMediaPlayer()
                    .setVolume(soundEffectVolumeSlider.getValue() / (500 / 3));
              }
            });

    // Save volume preference of user
    this.soundEffectVolumeSlider.setOnMouseReleased(
        event -> {
          setVolumePreference(soundEffectVolumeSlider, this.soundEffect.getMediaPlayer(), 500 / 3);
        });

    // Add music volume slider functionality
    this.musicVolumeSlider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                soundEffect
                    .getBackgroundMediaPlayer()
                    .setVolume(musicVolumeSlider.getValue() / 500);
              }
            });
    // Save volume preference of user
    this.musicVolumeSlider.setOnMouseReleased(
        event -> {
          setVolumePreference(musicVolumeSlider, this.soundEffect.getBackgroundMediaPlayer(), 500);
        });
  }

  /**
   * Sets the user volume preference and changes the volume
   *
   * @param slider The volume slider
   * @param player The media player responsible for playing sound
   * @param factor The conversion rate of sound
   */
  private void setVolumePreference(Slider slider, MediaPlayer player, double factor) {
    // Set user volume according to its slider
    if (slider == musicVolumeSlider) {
      this.currentUser.setMusicVolume(slider.getValue() / factor);
    } else {
      this.currentUser.setSoundEffectVolume(slider.getValue() / factor);
    }

    // Set the media player volume
    player.setVolume(slider.getValue() / factor);
    this.userService.saveUser(this.currentUser);
  }

  /** Update the profile picture and username to match the specified user. */
  private void renderUserProfile(final User user) {
    // Set the current user's information
    this.musicVolumeSlider.setValue(user.getMusicVolume() * 500);
    this.soundEffectVolumeSlider.setValue(user.getSoundEffectVolume() * (500 / 3));
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
    this.profilePicturePane.setMouseTransparent(true);
    this.switchUserLabel.setVisible(false);
    this.backButton.setVisible(false);
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
