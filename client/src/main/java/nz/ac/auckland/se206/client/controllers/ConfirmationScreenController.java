package nz.ac.auckland.se206.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.client.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.client.statemachine.states.ZenModeState;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.Helpers;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;
  @FXML private AnchorPane wordDefinition;
  @FXML private Label timeLimitLabel;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private SoundEffect soundEffect;
  @Inject private HiddenMode hiddenMode;
  @Inject private UserService userService;
  @Inject private CanvasStateMachine stateMachine;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** When the user confirms they are ready, switch to the canvas view. */
  @FXML
  private void onConfirmSwitch() {
    // Play click sound effect
    this.soundEffect.playSound(Sound.SPECIAL_CLICK);
    this.sceneManager.switchToView(View.CANVAS);
  }

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {

    // Checking if selected mode is hidden and get the definition instead if it is
    final boolean isHiddenMode = this.hiddenMode.isHiddenMode();
    this.wordDefinition.setVisible(isHiddenMode);
    this.targetWordLabel.setVisible(!this.hiddenMode.isHiddenMode());

    // Choosing and setting a random word depending on the word difficulty setting
    this.wordService.selectRandomTarget(
        this.userService.getCurrentUser().getGameSettings().getWords());
    this.targetWordLabel.setText(this.wordService.getTargetWord());

    // Change time label depending on if zen or normal mode is selected
    if (this.stateMachine.getCurrentState().getClass() == ZenModeState.class) {
      this.timeLimitLabel.setText("You have unlimited time to draw:");
    } else {
      this.timeLimitLabel.setText(
          "You have "
              + this.userService.getCurrentUser().getGameSettings().getTime()
              + " seconds to draw:");
    }
  }

  /**
   * When the user clicks the back button, take them back to the main menu if in zen mode or
   * settings otherwise.
   */
  private void onSwitchBack() {

    // Play click sound effect
    this.soundEffect.playSound(Sound.CLICK);
    this.hiddenMode.clearDefinitions();

    // Switching to previous screen depending on the current mode
    this.sceneManager.switchToView(View.MAIN_MENU);

    // Return to previous screen depending on current mode
    if (this.stateMachine.getCurrentState().getClass() == ZenModeState.class) {
      this.sceneManager.switchToView(View.MAIN_MENU);
    } else {
      this.sceneManager.switchToView(View.SETTINGS);
    }
  }
}
