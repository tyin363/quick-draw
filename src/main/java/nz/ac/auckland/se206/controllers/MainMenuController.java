package nz.ac.auckland.se206.controllers;

import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class MainMenuController implements LoadListener {

  private final Random random = new Random();
  private final String[] messages = {
    "Speed is key! Make sure to focus on features that are most identifiable",
    "\"The object of art is not to reproduce reality, but to create a reality of the same"
        + " intensity\"  – Alberto Giacometti",
    "\"The art of the artist is the art of the tools\"  – Pablo Picasso",
    "Made with love by Team 9 :)"
  };

  @FXML private Label messageLabel;
  @Inject private UserService userService; // Cause the onEnable method to be called
  @Inject private SceneManager sceneManager;

  /**
   * Switch to the confirmation screen, where the user will have time to think about the word before
   * the timer starts.
   */
  @FXML
  private void onStartGame() {
    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
  }

  /** Switch to the profile page, where the user can check their statistics. */
  @FXML
  private void onSwitchToProfile() {
    SceneManager.getInstance().switchToView(View.PROFILE_PAGE);
  }

  /** Switch to switch user page so user can change their accounts or add a new one */
  @FXML
  private void onSwitchAccount() {
    SceneManager.getInstance().switchToView(View.SWITCH_USER);
  }

  /** Everytime this scene is switched to select a new random message. */
  @Override
  public void onLoad() {
    this.messageLabel.setText(this.messages[this.random.nextInt(this.messages.length)]);
  }
}
