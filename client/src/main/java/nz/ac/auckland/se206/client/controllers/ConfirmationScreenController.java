package nz.ac.auckland.se206.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.client.annotations.Inject;
import nz.ac.auckland.se206.client.annotations.Singleton;
import nz.ac.auckland.se206.client.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.client.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.client.controllers.scenemanager.View;
import nz.ac.auckland.se206.client.util.Helpers;
import nz.ac.auckland.se206.client.words.Difficulty;
import nz.ac.auckland.se206.client.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;

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
    this.wordService.selectRandomTarget(Difficulty.EASY);
    this.targetWordLabel.setText(this.wordService.getTargetWord());
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
