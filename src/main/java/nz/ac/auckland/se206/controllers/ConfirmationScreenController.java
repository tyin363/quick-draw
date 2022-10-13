package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.Difficulty;
import nz.ac.auckland.se206.words.WordService;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  private static boolean HIDDEN_MODE = false;

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;
  @FXML private VBox previousDefinitionVbox;
  @FXML private VBox nextDefinitionVbox;
  @FXML private Label numberOfDefinitionLabel;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private HiddenMode hiddenMode;

  private double maxWidth = 670;
  private double defaultFontSize = 36;

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

  /** The previous definition of the given word will be shown. */
  @FXML
  private void onClickPrevious() {
    this.hiddenMode.previousDefinition();
    this.hiddenMode.setElements(
        targetWordLabel,
        numberOfDefinitionLabel,
        maxWidth,
        defaultFontSize,
        previousDefinitionVbox,
        nextDefinitionVbox);
  }

  /** The next definition of the given word will be shown. */
  @FXML
  private void onClickNext() {
    this.hiddenMode.nextDefinition();
    this.hiddenMode.setElements(
        targetWordLabel,
        numberOfDefinitionLabel,
        maxWidth,
        defaultFontSize,
        previousDefinitionVbox,
        nextDefinitionVbox);
  }

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {
    // Clear any styles on the target word
    this.targetWordLabel.setStyle(null);

    // Clear previous definitions
    if (!(this.hiddenMode.getDefinitions() == null)) {
      this.hiddenMode.getDefinitions().clear();
      this.hiddenMode.resetIndex();
    }

    // Make Hidden mode exclusive elements invisible
    previousDefinitionVbox.setVisible(false);
    nextDefinitionVbox.setVisible(false);
    numberOfDefinitionLabel.setVisible(false);

    HIDDEN_MODE = MainMenuController.isHiddenMode();
    this.wordService.selectRandomTarget(Difficulty.EASY);

    if (HIDDEN_MODE) {
      this.targetWordLabel.setText("Loading Definitions");
      this.numberOfDefinitionLabel.setText("Number of Definitions");
      this.numberOfDefinitionLabel.setVisible(true);
      System.out.println("The word is: " + this.wordService.getTargetWord());
      this.hiddenMode.searchWord(
          this.wordService.getTargetWord(),
          this.targetWordLabel,
          this.numberOfDefinitionLabel,
          maxWidth,
          defaultFontSize,
          previousDefinitionVbox,
          nextDefinitionVbox);
    } else {
      this.targetWordLabel.setText(this.wordService.getTargetWord());
    }
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
