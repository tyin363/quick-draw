package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;
  @FXML private Label timeLimitLabel;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;

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
    this.wordService.selectRandomTarget(
        this.userService.getCurrentUser().getGameSettings().getWords());
    this.targetWordLabel.setText(this.wordService.getTargetWord());
    this.timeLimitLabel.setText(
        "You have "
            + this.userService.getCurrentUser().getGameSettings().getTime()
            + " seconds to draw:");
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
