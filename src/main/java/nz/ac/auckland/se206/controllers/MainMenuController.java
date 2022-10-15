package nz.ac.auckland.se206.controllers;

import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.statemachine.states.DefaultCanvasState;
import nz.ac.auckland.se206.statemachine.states.HiddenModeState;
import nz.ac.auckland.se206.statemachine.states.ZenModeState;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.util.SoundEffect;

@Singleton
public class MainMenuController implements LoadListener, TerminationListener {

  private final Random random = new Random();
  private final String[] messages = {
    "Speed is key! Make sure to focus on features that are most identifiable",
    "\"The object of art is not to reproduce reality, but to create a reality of the same"
        + " intensity\"  – Alberto Giacometti",
    "\"The art of the artist is the art of the tools\"  – Pablo Picasso",
    "Made with love by Team 9 :)"
  };

  @FXML private Label messageLabel;
  @FXML private AnchorPane header;
  @Inject private SceneManager sceneManager;
  @Inject private SoundEffect soundEffect;
  @Inject private CanvasStateMachine stateMachine;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** Everytime this scene is switched to select a new random message. */
  @Override
  public void onLoad() {
    this.messageLabel.setText(this.messages[this.random.nextInt(this.messages.length)]);
  }

  /**
   * Switch to the settings screen, where the user will have time to think about the word before the
   * timer starts.
   */
  @FXML
  private void onStartGame() {
    // Play click sound effect
    this.soundEffect.playClickSound();
    this.stateMachine.switchState(DefaultCanvasState.class);
    this.sceneManager.switchToView(View.SETTINGS);
  }

  /**
   * Sets the canvas state machine to the zen mode state and switches to the confirmation screen
   * where users will have time to think about the word before the timer starts.
   */
  @FXML
  private void onStartZenMode() {
    // Play click sound effect
    this.soundEffect.playClickSound();
    this.stateMachine.switchState(ZenModeState.class);
    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
  }

  /**
   * Switch to the confirmation screen, where the user will have time to think about the word before
   * the timer starts.
   */
  @FXML
  private void onStartHidden() {
    // Play click sound effect
    this.soundEffect.playClickSound();
    this.stateMachine.switchState(HiddenModeState.class);
    this.sceneManager.switchToView(View.SETTINGS);
  }

  /**
   * When the user clicks the back button, take them back to the switch user page, unless they were
   * just at the profile page, in which case go back to that page.
   */
  private void onSwitchBack() {
    // Play click sound effect
    this.soundEffect.playClickSound();

    final View previousView = this.sceneManager.getPreviousView();
    // Only switch back to the profile page if they were just on it
    if (previousView == View.PROFILE_PAGE) {
      this.sceneManager.switchToView(View.PROFILE_PAGE);
    } else {
      this.sceneManager.switchToView(View.SWITCH_USER);
    }
  }

  @Override
  public void onTerminate() {
    this.soundEffect.terminate();
  }
}
