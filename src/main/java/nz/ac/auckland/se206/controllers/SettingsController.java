package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class SettingsController implements LoadListener {

  @FXML private AnchorPane header;
  @FXML private RadioButton easyAccuracyButton, mediumAccuracyButton, hardAccuracyButton;
  @FXML private RadioButton easyWordsButton, mediumWordsButton, hardWordsButton, masterWordsButton;
  @FXML private RadioButton easyTimeButton, mediumTimeButton, hardTimeButton, masterTimeButton;
  @FXML
  private RadioButton easyConfidenceButton,
      mediumConfidenceButton,
      hardConfidenceButton,
      masterConfidenceButton;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** When the user confirms they are ready, switch to the canvas view. */
  @FXML
  private void onConfirmReady() {
    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
  }

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {}

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  @FXML
  private void onSetAccuracy() {}

  @FXML
  private void onSetWords() {}

  @FXML
  private void onSetTime() {}

  @FXML
  private void onSetConfidence() {}
}
