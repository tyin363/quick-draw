package nz.ac.auckland.se206.controllers;

import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.statemachine.states.HiddenModeState;
import nz.ac.auckland.se206.statemachine.states.NormalCanvasState;
import nz.ac.auckland.se206.statemachine.states.ZenModeState;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.util.Sound;
import nz.ac.auckland.se206.util.SoundEffect;

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
  @FXML private AnchorPane header;
  @FXML private HBox normalModeBox;
  @FXML private HBox hiddenModeBox;
  @FXML private HBox zenModeBox;
  @Inject private SceneManager sceneManager;
  @Inject private SoundEffect soundEffect;
  @Inject private CanvasStateMachine stateMachine;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    this.addToolTips();
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
  private void onStartNormal() {
    this.soundEffect.playSound(Sound.CLICK);
    this.stateMachine.switchState(NormalCanvasState.class);
    this.sceneManager.switchToView(View.SETTINGS);
  }

  /**
   * Sets the canvas state machine to the zen mode state and switches to the confirmation screen
   * where users will have time to think about the word before the timer starts.
   */
  @FXML
  private void onStartZenMode() {
    // Play click sound effect
    this.soundEffect.playSound(Sound.CLICK);
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
    this.soundEffect.playSound(Sound.CLICK);
    this.stateMachine.switchState(HiddenModeState.class);
    this.sceneManager.switchToView(View.SETTINGS);
  }

  /**
   * When the user clicks the back button, take them back to the switch user page, unless they were
   * just at the profile page, in which case go back to that page.
   */
  private void onSwitchBack() {
    // Play click sound effect
    this.soundEffect.playSound(Sound.CLICK);

    final View previousView = this.sceneManager.getPreviousView();
    // Only switch back to the profile page if they were just on it
    if (previousView == View.PROFILE_PAGE) {
      this.sceneManager.switchToView(View.PROFILE_PAGE);
    } else {
      this.sceneManager.switchToView(View.SWITCH_USER);
    }
  }

  /** Helper method to add tool tips to main menu buttons */
  private void addToolTips() {
    // Creating main menu button tool tips
    Tooltip normalModeTip = new Tooltip("Timed speed drawing for a given word");
    Tooltip hiddenModeTip = new Tooltip("Timed speed drawing for a given definition");
    Tooltip zenModeTip = new Tooltip("Endless drawing with color!");

    // Setting main menu button tool tips
    Tooltip.install(this.normalModeBox, normalModeTip);
    Tooltip.install(this.hiddenModeBox, hiddenModeTip);
    Tooltip.install(this.zenModeBox, zenModeTip);
  }
}
