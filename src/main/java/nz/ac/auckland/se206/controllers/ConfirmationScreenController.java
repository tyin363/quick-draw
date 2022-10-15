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
import nz.ac.auckland.se206.statemachine.states.HiddenModeState;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;
  @FXML private AnchorPane wordDefinition;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private HiddenMode hiddenMode;
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
    final boolean isHiddenMode =
        this.stateMachine.getCurrentState().getClass().equals(HiddenModeState.class);
    this.wordDefinition.setVisible(isHiddenMode);
    this.targetWordLabel.setVisible(!isHiddenMode);
    this.targetWordLabel.setText(this.wordService.getTargetWord());
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.hiddenMode.clearDefinitions();
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
