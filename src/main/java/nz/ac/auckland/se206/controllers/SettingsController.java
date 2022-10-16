package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.util.Sound;
import nz.ac.auckland.se206.util.SoundEffect;

@Singleton
public class SettingsController implements LoadListener {

  @FXML private AnchorPane header;
  @FXML private RadioButton easyAccuracyButton;
  @FXML private RadioButton mediumAccuracyButton;
  @FXML private RadioButton hardAccuracyButton;
  @FXML private RadioButton easyWordsButton;
  @FXML private RadioButton mediumWordsButton;
  @FXML private RadioButton hardWordsButton;
  @FXML private RadioButton masterWordsButton;
  @FXML private RadioButton easyTimeButton;
  @FXML private RadioButton mediumTimeButton;
  @FXML private RadioButton hardTimeButton;
  @FXML private RadioButton masterTimeButton;
  @FXML private RadioButton easyConfidenceButton;
  @FXML private RadioButton mediumConfidenceButton;
  @FXML private RadioButton hardConfidenceButton;
  @FXML private RadioButton masterConfidenceButton;
  @FXML private ToggleGroup accuracy;
  @FXML private ToggleGroup words;
  @FXML private ToggleGroup time;
  @FXML private ToggleGroup confidence;
  @FXML private Button readyButton;

  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private SoundEffect soundEffect;

  private User currentUser;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    this.setToolTips();
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** When the user confirms they are ready, switch to the confirmation screen. */
  @FXML
  private void onConfirmReady() {
    // Play the click sound effect
    this.soundEffect.playSound(Sound.CLICK);

    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
  }

  /** Everytime this scene is switched to set the tooltpis and load user's previous settings. */
  @Override
  public void onLoad() {
    // Disabling the ready button initially
    this.readyButton.setDisable(true);

    // Unselecting all settings initially to prevent same settings transferred between users
    if (this.accuracy.getSelectedToggle() != null) {
      this.accuracy.getSelectedToggle().setSelected(false);
    }

    if (this.words.getSelectedToggle() != null) {
      this.words.getSelectedToggle().setSelected(false);
    }

    if (this.time.getSelectedToggle() != null) {
      this.time.getSelectedToggle().setSelected(false);
    }

    if (this.confidence.getSelectedToggle() != null) {
      this.confidence.getSelectedToggle().setSelected(false);
    }

    // Loading current user's settings
    this.currentUser = this.userService.getCurrentUser();
    this.loadAccuracy(currentUser);
    this.loadWords(currentUser);
    this.loadTime(currentUser);
    this.loadConfidence(currentUser);

    // Enable ready button if all settings are selected
    this.checkSettings();
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    // Play the click sound effect
    this.soundEffect.playSound(Sound.CLICK);

    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  /**
   * When user clicks an accuracy button, store the accuracy settings locally in the current user's
   * data
   *
   * @param event ActionEvent when clicking an accuracy setting
   */
  @FXML
  private void onSetAccuracy(ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing accuracy setting into current user's data depending on selection
    if (easyAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Easy");
    } else if (mediumAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Medium");
    } else if (hardAccuracyButton.isSelected()) {
      currentUser.getGameSettings().setAccuracy("Hard");
    }
    this.userService.saveUser(currentUser);

    // Enabling ready button if all settings are checked
    this.checkSettings();
  }

  /**
   * When user clicks a words button, store the words settings locally in the current user's data
   *
   * @param event ActionEvent when clicking a words setting
   */
  @FXML
  private void onSetWords(ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing word setting into current user's data depending on selection
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

    // Enabling ready button if all settings are checked
    this.checkSettings();
  }

  /**
   * When user clicks a time button, store the time settings locally in the current user's data
   *
   * @param event ActionEvent when clicking a time setting
   */
  @FXML
  private void onSetTime(ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing time setting into current user's data depending on selection
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

    // Enabling time button if all settings are checked
    this.checkSettings();
  }

  /**
   * When user clicks a confidence button, store the confidence settings locally in the current
   * user's data
   *
   * @param event ActionEvent when clicking a confidence setting
   */
  @FXML
  private void onSetConfidence(ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing confidence setting into current user's data depending on selection
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

    // Enabling time button if all settings are checked
    this.checkSettings();
  }

  /**
   * Retrieves and loads the current's user accuracy settings
   *
   * @param currentUser Current user in play
   */
  private void loadAccuracy(User currentUser) {

    // Selects accuracy setting depending on user's current accuracy setting stored
    if (currentUser.getGameSettings().getAccuracy().contentEquals("Easy")) {
      easyAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Medium")) {
      mediumAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Hard")) {
      hardAccuracyButton.setSelected(true);
    }
  }

  /**
   * Retrieves and loads the current's user words settings
   *
   * @param currentUser Current user in play
   */
  private void loadWords(User currentUser) {

    // Selects words setting depending on user's current words setting stored
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

  /**
   * Retrieves and loads the current's user time settings
   *
   * @param currentUser Current user in play
   */
  private void loadTime(User currentUser) {

    // Selects time setting depending on user's current time setting stored
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

  /**
   * Retrieves and loads the current's user confidence settings
   *
   * @param currentUser Current user in play
   */
  private void loadConfidence(User currentUser) {

    // Selects confidence setting depending on user's current confidence setting stored
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

  /** Enables ready button if all settings are selected */
  private void checkSettings() {
    if ((accuracy.getSelectedToggle() != null)
        && (words.getSelectedToggle() != null)
        && (time.getSelectedToggle() != null)
        && (confidence.getSelectedToggle() != null)) {
      this.readyButton.setDisable(false);
    }
  }

  /** Sets the tool tips for all the different game settings */
  private void setToolTips() {

    // Creating accuracy tool tips
    Tooltip easyAccuracyTip = new Tooltip("Your word must be in the top 3 predictions");
    Tooltip mediumAccuracyTip = new Tooltip("Your word must be in the top 2 predictions");
    Tooltip hardAccuracyTip = new Tooltip("Your word must be the top 1 prediction");

    // Creating words tool tips
    Tooltip easyWordsTip = new Tooltip("Easy words only");
    Tooltip mediumWordsTip = new Tooltip("Easy and Medium words only");
    Tooltip hardWordsTip = new Tooltip("All words");
    Tooltip masterWordsTip = new Tooltip("Hard words only");

    // Creating time tool tips
    Tooltip easyTimeTip = new Tooltip("60 seconds to draw");
    Tooltip mediumTimeTip = new Tooltip("45 seconds to draw");
    Tooltip hardTimeTip = new Tooltip("30 seconds to draw");
    Tooltip masterTimeTip = new Tooltip("15 seconds to draw");

    // Creating confidence tool tips
    Tooltip easyConfidenceTip = new Tooltip("Your drawing's confidence level must be at least 1%");
    Tooltip mediumConfidenceTip =
        new Tooltip("Your drawing's confidence level must be at least 10%");
    Tooltip hardConfidenceTip = new Tooltip("Your drawing's confidence level must be at least 25%");
    Tooltip masterConfidenceTip =
        new Tooltip("Your drawing's confidence level must be at least 50%");

    // Setting accuracy delay for tool tips
    easyAccuracyTip.setShowDelay(Duration.millis(100));
    mediumAccuracyTip.setShowDelay(Duration.millis(100));
    hardAccuracyTip.setShowDelay(Duration.millis(100));

    // Setting words delay for tool tips
    easyWordsTip.setShowDelay(Duration.millis(100));
    mediumWordsTip.setShowDelay(Duration.millis(100));
    hardWordsTip.setShowDelay(Duration.millis(100));
    masterWordsTip.setShowDelay(Duration.millis(100));

    // Setting time delay for tool tips
    easyTimeTip.setShowDelay(Duration.millis(100));
    mediumTimeTip.setShowDelay(Duration.millis(100));
    hardTimeTip.setShowDelay(Duration.millis(100));
    masterTimeTip.setShowDelay(Duration.millis(100));

    // Setting confidence delay for tool tips
    easyAccuracyTip.setShowDelay(Duration.millis(100));
    mediumConfidenceTip.setShowDelay(Duration.millis(100));
    hardConfidenceTip.setShowDelay(Duration.millis(100));
    masterConfidenceTip.setShowDelay(Duration.millis(100));

    // Setting accuracy tool tips
    this.easyAccuracyButton.setTooltip(easyAccuracyTip);
    this.mediumAccuracyButton.setTooltip(mediumAccuracyTip);
    this.hardAccuracyButton.setTooltip(hardAccuracyTip);

    // Setting words tool tips
    this.easyWordsButton.setTooltip(easyWordsTip);
    this.mediumWordsButton.setTooltip(mediumWordsTip);
    this.hardWordsButton.setTooltip(hardWordsTip);
    this.masterWordsButton.setTooltip(masterWordsTip);

    // Setting time tool tips
    this.easyTimeButton.setTooltip(easyTimeTip);
    this.mediumTimeButton.setTooltip(mediumTimeTip);
    this.hardTimeButton.setTooltip(hardTimeTip);
    this.masterTimeButton.setTooltip(masterTimeTip);

    // Setting confidence tool tips
    this.easyConfidenceButton.setTooltip(easyConfidenceTip);
    this.mediumConfidenceButton.setTooltip(mediumConfidenceTip);
    this.hardConfidenceButton.setTooltip(hardConfidenceTip);
    this.masterConfidenceButton.setTooltip(masterConfidenceTip);
  }
}
