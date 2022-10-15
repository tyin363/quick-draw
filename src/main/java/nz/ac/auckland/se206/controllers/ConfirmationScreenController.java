package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.statemachine.states.ZenModeState;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;
  @FXML private AnchorPane wordDefinition;
  @FXML private Label timeLimitLabel;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
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
    this.sceneManager.switchToView(View.CANVAS);
  }

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {
    final boolean isHiddenMode = this.hiddenMode.isHiddenMode();
    this.wordDefinition.setVisible(isHiddenMode);
    this.targetWordLabel.setVisible(!this.hiddenMode.isHiddenMode());
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
    this.hiddenMode.clearDefinitions();
    this.sceneManager.switchToView(View.MAIN_MENU);
    if (this.stateMachine.getCurrentState().getClass() == ZenModeState.class) {
      this.sceneManager.switchToView(View.MAIN_MENU);
    } else {
      this.sceneManager.switchToView(View.SETTINGS);
    }
  }
}
