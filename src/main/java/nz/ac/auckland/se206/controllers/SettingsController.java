package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
  @FXML private ToggleGroup accuracy;
  @FXML private ToggleGroup words;
  @FXML private ToggleGroup time;
  @FXML private ToggleGroup confidence;

  @FXML private Button readyButton;

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
    this.readyButton.setDisable(true);
    this.easyAccuracyButton.setSelected(false);
    this.mediumAccuracyButton.setSelected(false);
    this.hardAccuracyButton.setSelected(false);
    this.easyWordsButton.setSelected(false);
    this.mediumWordsButton.setSelected(false);
    this.hardWordsButton.setSelected(false);
    this.masterWordsButton.setSelected(false);
    this.easyTimeButton.setSelected(false);
    this.mediumTimeButton.setSelected(false);
    this.hardTimeButton.setSelected(false);
    this.masterTimeButton.setSelected(false);
    this.easyConfidenceButton.setSelected(false);
    this.mediumConfidenceButton.setSelected(false);
    this.hardConfidenceButton.setSelected(false);
    this.masterConfidenceButton.setSelected(false);
    this.currentUser = this.userService.getCurrentUser();
    this.loadAccuracy(currentUser);
    this.loadWords(currentUser);
    this.loadTime(currentUser);
    this.loadConfidence(currentUser);
    this.checkSettings();
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

    this.checkSettings();
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

    this.checkSettings();
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

    this.checkSettings();
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

    this.checkSettings();
    this.userService.saveUser(currentUser);
  }

  private void loadAccuracy(User currentUser) {
    if (currentUser.getGameSettings().getAccuracy().contentEquals("Easy")) {
      easyAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Medium")) {
      mediumAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Hard")) {
      hardAccuracyButton.setSelected(true);
    }
  }

  private void loadWords(User currentUser) {
    if (currentUser.getGameSettings().getWords().contentEquals("Easy")) {
      easyWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Medium")) {
      mediumWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Hard")) {
      hardWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Master")) {
      masterWordsButton.setSelected(true);
    }
  }

  private void loadTime(User currentUser) {
    if (currentUser.getGameSettings().getTime() == 60) {
      easyTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 45) {
      mediumTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 30) {
      hardTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 15) {
      masterTimeButton.setSelected(true);
    }
  }

  private void loadConfidence(User currentUser) {
    if (currentUser.getGameSettings().getConfidence().contentEquals("Easy")) {
      easyConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Medium")) {
      mediumConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Hard")) {
      hardConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Master")) {
      masterConfidenceButton.setSelected(true);
    }
  }

  private void checkSettings() {
    if ((accuracy.getSelectedToggle() != null)
        && (words.getSelectedToggle() != null)
        && (time.getSelectedToggle() != null)
        && (confidence.getSelectedToggle() != null)) {
      this.readyButton.setDisable(false);
    }
  }
}
