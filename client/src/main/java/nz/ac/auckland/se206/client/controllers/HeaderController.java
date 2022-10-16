package nz.ac.auckland.se206.client.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.client.sockets.DrawingSessionService;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.users.User;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.core.annotations.Controller;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;

/**
 * Note: This cannot be annotated with @Singleton as a new instance is created for every view it's
 * used within.
 */
@Controller
public class HeaderController implements LoadListener {

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private DrawingSessionService drawingSessionService;
  @Inject private SoundEffect soundEffect;

  @FXML private ImageView profilePicture;
  @FXML private Label username;
  @FXML private Label switchUserLabel;
  @FXML private Slider musicVolumeSlider;
  @FXML private Slider soundEffectVolumeSlider;
  @FXML private Pane profilePicturePane;
  @FXML private Button backButton;
  @FXML private HBox drawingSessionPopup;
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
    this.drawingSessionService.addDrawingSessionListener(this::onDrawingSession);
    this.drawingSessionService.addRespondSessionListener(
        () -> this.drawingSessionPopup.setVisible(false));
    this.drawingSessionPopup.setVisible(false);
  }

  /**
   * When a drawing session is started, log the request. Note that this will be called on a
   * different thread.
   *
   * @param request The request that was sent from the server
   */
  private void onDrawingSession(final DrawingSessionRequest request) {
    Platform.runLater(() -> this.drawingSessionPopup.setVisible(true));
  }

  /** Accept the current drawing session request. */
  @FXML
  private void onAcceptSession() {
    this.drawingSessionService.acceptSession();
    this.drawingSessionPopup.setVisible(false);
  }

  /** Reject the current drawing session request. */
  @FXML
  private void onDeclineSession() {
    this.drawingSessionService.declineSession();
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
    this.soundEffect.getBackgroundMediaPlayer().setVolume(newCurrentUser.getMusicVolume());
    this.soundEffect.getMediaPlayer().setVolume(newCurrentUser.getSoundEffectVolume());

    this.currentUser = newCurrentUser;
    this.renderUserProfile(this.currentUser);

    // Add sound effect volume slider functionality
    this.soundEffectVolumeSlider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  final ObservableValue<? extends Number> observable,
                  final Number oldValue,
                  final Number newValue) {
                HeaderController.this
                    .soundEffect
                    .getMediaPlayer()
                    .setVolume(
                        HeaderController.this.soundEffectVolumeSlider.getValue() / (500 / 3));
              }
            });

    // Save volume preference of user
    this.soundEffectVolumeSlider.setOnMouseReleased(
        event -> {
          this.setVolumePreference(
              this.soundEffectVolumeSlider, this.soundEffect.getMediaPlayer(), 500 / 3);
        });

    // Add music volume slider functionality
    this.musicVolumeSlider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  final ObservableValue<? extends Number> observable,
                  final Number oldValue,
                  final Number newValue) {
                HeaderController.this
                    .soundEffect
                    .getBackgroundMediaPlayer()
                    .setVolume(HeaderController.this.musicVolumeSlider.getValue() / 500);
              }
            });
    // Save volume preference of user
    this.musicVolumeSlider.setOnMouseReleased(
        event -> {
          this.setVolumePreference(
              this.musicVolumeSlider, this.soundEffect.getBackgroundMediaPlayer(), 500);
        });
  }

  /**
   * Sets the user volume preference and changes the volume
   *
   * @param slider The volume slider
   * @param player The media player responsible for playing sound
   * @param factor The conversion rate of sound
   */
  private void setVolumePreference(
      final Slider slider, final MediaPlayer player, final double factor) {
    // Set user volume according to its slider
    if (slider == this.musicVolumeSlider) {
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
    this.profilePicture.setImage(user.getProfileImage());
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
    this.soundEffect.playSound(Sound.CLICK);
    this.sceneManager.switchToView(View.PROFILE_PAGE);
  }

  /** When the user clicks on the switch user text, take them to the switch user view. */
  @FXML
  private void onSwitchUser() {
    this.soundEffect.playSound(Sound.CLICK);
    this.sceneManager.switchToView(View.SWITCH_USER);
  }
}
