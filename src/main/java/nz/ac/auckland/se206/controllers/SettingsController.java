package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
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
  @Inject private UserService userService;

  private User currentUser;

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
  public void onLoad() {
    currentUser = this.userService.getCurrentUser();
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  @FXML
  private void onSetAccuracy(ActionEvent event) {
    if (easyAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Easy");
    } else if (mediumAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Medium");
    } else if (hardAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Hard");
    }

    this.userService.saveUser(currentUser);
  }

  @FXML
  private void onSetWords(ActionEvent event) {
    if (easyWordsButton.isSelected()) {
      currentUser.getGameSettings().setWords("Easy");
    } else if (mediumWordsButton.isSelected()) {
      currentUser.getGameSettings().setWords("Medium");
    } else if (hardWordsButton.isSelected()) {
      currentUser.getGameSettings().setWords("Hard");
    } else if (masterWordsButton.isSelected()) {
      currentUser.getGameSettings().setWords("Master");
    }

    this.userService.saveUser(currentUser);
  }

  @FXML
  private void onSetTime(ActionEvent event) {
    if (easyTimeButton.isSelected()) {
      currentUser.getGameSettings().setTime(60);
    } else if (mediumTimeButton.isSelected()) {
      currentUser.getGameSettings().setTime(45);
    } else if (hardTimeButton.isSelected()) {
      currentUser.getGameSettings().setTime(30);
    } else if (masterTimeButton.isSelected()) {
      currentUser.getGameSettings().setTime(15);
    }

    this.userService.saveUser(currentUser);
  }

  @FXML
  private void onSetConfidence(ActionEvent event) {
    if (easyConfidenceButton.isSelected()) {
      currentUser.getGameSettings().setConfidence("Easy");
    } else if (mediumConfidenceButton.isSelected()) {
      currentUser.getGameSettings().setConfidence("Medium");
    } else if (hardConfidenceButton.isSelected()) {
      currentUser.getGameSettings().setConfidence("Hard");
    } else if (masterConfidenceButton.isSelected()) {
      currentUser.getGameSettings().setConfidence("Master");
    }
    this.userService.saveUser(currentUser);
  }
}
