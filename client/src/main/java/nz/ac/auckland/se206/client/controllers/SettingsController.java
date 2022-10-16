package nz.ac.auckland.se206.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.users.User;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.Helpers;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;

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
    this.loadAccuracy(this.currentUser);
    this.loadWords(this.currentUser);
    this.loadTime(this.currentUser);
    this.loadConfidence(this.currentUser);

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
  private void onSetAccuracy(final ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing accuracy setting into current user's data depending on selection
    if (this.easyAccuracyButton.isSelected()) {
      this.currentUser.getGameSettings().setAccuracy("Easy");
    } else if (this.mediumAccuracyButton.isSelected()) {
      this.currentUser.getGameSettings().setAccuracy("Medium");
    } else if (this.hardAccuracyButton.isSelected()) {
      this.currentUser.getGameSettings().setAccuracy("Hard");
    }
    this.userService.saveUser(this.currentUser);

    // Enabling ready button if all settings are checked
    this.checkSettings();
  }

  /**
   * When user clicks a words button, store the words settings locally in the current user's data
   *
   * @param event ActionEvent when clicking a words setting
   */
  @FXML
  private void onSetWords(final ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing word setting into current user's data depending on selection
    if (this.easyWordsButton.isSelected()) {
      this.currentUser.getGameSettings().setWords("Easy");
    } else if (this.mediumWordsButton.isSelected()) {
      this.currentUser.getGameSettings().setWords("Medium");
    } else if (this.hardWordsButton.isSelected()) {
      this.currentUser.getGameSettings().setWords("Hard");
    } else if (this.masterWordsButton.isSelected()) {
      this.currentUser.getGameSettings().setWords("Master");
    }
    this.userService.saveUser(this.currentUser);

    // Enabling ready button if all settings are checked
    this.checkSettings();
  }

  /**
   * When user clicks a time button, store the time settings locally in the current user's data
   *
   * @param event ActionEvent when clicking a time setting
   */
  @FXML
  private void onSetTime(final ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing time setting into current user's data depending on selection
    if (this.easyTimeButton.isSelected()) {
      this.currentUser.getGameSettings().setTime(60);
    } else if (this.mediumTimeButton.isSelected()) {
      this.currentUser.getGameSettings().setTime(45);
    } else if (this.hardTimeButton.isSelected()) {
      this.currentUser.getGameSettings().setTime(30);
    } else if (this.masterTimeButton.isSelected()) {
      this.currentUser.getGameSettings().setTime(15);
    }
    this.userService.saveUser(this.currentUser);

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
  private void onSetConfidence(final ActionEvent event) {

    // Play settings click sound effect
    this.soundEffect.playSound(Sound.SETTINGS_CLICK);

    // Storing confidence setting into current user's data depending on selection
    if (this.easyConfidenceButton.isSelected()) {
      this.currentUser.getGameSettings().setConfidence("Easy");
    } else if (this.mediumConfidenceButton.isSelected()) {
      this.currentUser.getGameSettings().setConfidence("Medium");
    } else if (this.hardConfidenceButton.isSelected()) {
      this.currentUser.getGameSettings().setConfidence("Hard");
    } else if (this.masterConfidenceButton.isSelected()) {
      this.currentUser.getGameSettings().setConfidence("Master");
    }
    this.userService.saveUser(this.currentUser);

    // Enabling time button if all settings are checked
    this.checkSettings();
  }

  /**
   * Retrieves and loads the current's user accuracy settings
   *
   * @param currentUser Current user in play
   */
  private void loadAccuracy(final User currentUser) {

    // Selects accuracy setting depending on user's current accuracy setting stored
    if (currentUser.getGameSettings().getAccuracy().contentEquals("Easy")) {
      this.easyAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Medium")) {
      this.mediumAccuracyButton.setSelected(true);
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Hard")) {
      this.hardAccuracyButton.setSelected(true);
    }
  }

  /**
   * Retrieves and loads the current's user words settings
   *
   * @param currentUser Current user in play
   */
  private void loadWords(final User currentUser) {

    // Selects words setting depending on user's current words setting stored
    if (currentUser.getGameSettings().getWords().contentEquals("Easy")) {
      this.easyWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Medium")) {
      this.mediumWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Hard")) {
      this.hardWordsButton.setSelected(true);
    } else if (currentUser.getGameSettings().getWords().contentEquals("Master")) {
      this.masterWordsButton.setSelected(true);
    }
  }

  /**
   * Retrieves and loads the current's user time settings
   *
   * @param currentUser Current user in play
   */
  private void loadTime(final User currentUser) {

    // Selects time setting depending on user's current time setting stored
    if (currentUser.getGameSettings().getTime() == 60) {
      this.easyTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 45) {
      this.mediumTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 30) {
      this.hardTimeButton.setSelected(true);
    } else if (currentUser.getGameSettings().getTime() == 15) {
      this.masterTimeButton.setSelected(true);
    }
  }

  /**
   * Retrieves and loads the current's user confidence settings
   *
   * @param currentUser Current user in play
   */
  private void loadConfidence(final User currentUser) {

    // Selects confidence setting depending on user's current confidence setting stored
    if (currentUser.getGameSettings().getConfidence().contentEquals("Easy")) {
      this.easyConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Medium")) {
      this.mediumConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Hard")) {
      this.hardConfidenceButton.setSelected(true);
    } else if (currentUser.getGameSettings().getConfidence().contentEquals("Master")) {
      this.masterConfidenceButton.setSelected(true);
    }
  }

  /** Enables ready button if all settings are selected */
  private void checkSettings() {
    if ((this.accuracy.getSelectedToggle() != null)
        && (this.words.getSelectedToggle() != null)
        && (this.time.getSelectedToggle() != null)
        && (this.confidence.getSelectedToggle() != null)) {
      this.readyButton.setDisable(false);
    }
  }

  /** Sets the tool tips for all the different game settings */
  private void setToolTips() {

    // Creating accuracy tool tips
    final Tooltip easyAccuracyTip = new Tooltip("Your word must be in the top 3 predictions");
    final Tooltip mediumAccuracyTip = new Tooltip("Your word must be in the top 2 predictions");
    final Tooltip hardAccuracyTip = new Tooltip("Your word must be the top 1 prediction");

    // Creating words tool tips
    final Tooltip easyWordsTip = new Tooltip("Easy words only");
    final Tooltip mediumWordsTip = new Tooltip("Easy and Medium words only");
    final Tooltip hardWordsTip = new Tooltip("All words");
    final Tooltip masterWordsTip = new Tooltip("Hard words only");

    // Creating time tool tips
    final Tooltip easyTimeTip = new Tooltip("60 seconds to draw");
    final Tooltip mediumTimeTip = new Tooltip("45 seconds to draw");
    final Tooltip hardTimeTip = new Tooltip("30 seconds to draw");
    final Tooltip masterTimeTip = new Tooltip("15 seconds to draw");

    // Creating confidence tool tips
    final Tooltip easyConfidenceTip =
        new Tooltip("Your drawing's confidence level must be at least 1%");
    final Tooltip mediumConfidenceTip =
        new Tooltip("Your drawing's confidence level must be at least 10%");
    final Tooltip hardConfidenceTip =
        new Tooltip("Your drawing's confidence level must be at least 25%");
    final Tooltip masterConfidenceTip =
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
