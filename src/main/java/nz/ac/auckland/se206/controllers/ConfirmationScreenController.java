package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.words.Difficulty;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;

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
}
