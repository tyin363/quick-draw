package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.words.WordService;
import org.slf4j.Logger;

@Controller
public class WordDefinitionController implements LoadListener {

  @FXML private Label targetWordLabel;
  @FXML private VBox previousDefinitionVbox;
  @FXML private VBox nextDefinitionVbox;
  @FXML private Label numberOfDefinitionLabel;
  @FXML private Button nextButton;
  @FXML private Label nextLabel;
  @FXML private Button previousButton;
  @FXML private Label previousLabel;

  @Inject private WordService wordService;
  @Inject private HiddenMode hiddenMode;
  @Inject private Logger logger;

  private double maxWidth = 640;
  private double defaultFontSize = 40;

  /** This will recolour the previous definition buttons to a light gray. */
  @FXML
  private void onEnterPreviousButton() {
    this.previousButton.setStyle("-fx-background-color: -fx-text-default;");
    this.previousLabel.setStyle("-fx-text-fill: -fx-text-default;");
  }

  /** This will recolour the previous definition buttons to a light gray. */
  @FXML
  private void onEnterNextButton() {
    this.nextButton.setStyle("-fx-background-color: -fx-text-default;");
    this.nextLabel.setStyle("-fx-text-fill: -fx-text-default;");
  }

  /** This will reset the colour of the previous definition buttons. */
  @FXML
  private void onExitPreviousButton() {
    this.previousButton.setStyle("-fx-background-color: -fx-lightgray-100;");
    this.previousLabel.setStyle("-fx-text-fill: -fx-lightgray-100;");
  }

  /** This will reset the colour of the next definition buttons. */
  @FXML
  private void onExitNextButton() {
    this.nextButton.setStyle("-fx-background-color: -fx-lightgray-100;");
    this.nextLabel.setStyle("-fx-text-fill: -fx-lightgray-100;");
  }

  /** The previous definition of the given word will be shown. */
  @FXML
  private void onClickPrevious() {
    this.hiddenMode.previousDefinition();
    this.hiddenMode.setElements(
        this.targetWordLabel,
        this.numberOfDefinitionLabel,
        this.maxWidth,
        this.defaultFontSize,
        this.previousDefinitionVbox,
        this.nextDefinitionVbox);
  }

  /** The next definition of the given word will be shown. */
  @FXML
  private void onClickNext() {
    this.hiddenMode.nextDefinition();
    this.hiddenMode.setElements(
        this.targetWordLabel,
        this.numberOfDefinitionLabel,
        this.maxWidth,
        this.defaultFontSize,
        this.previousDefinitionVbox,
        this.nextDefinitionVbox);
  }

  /**
   * Whenever the scene is switched to, either display the current definition or if there is none,
   * then fetch the definitions based on the current target word.
   */
  @Override
  public void onLoad() {
    // Clear any styles on the target word
    this.targetWordLabel.setStyle(null);

    // If it's not hidden mode, we can ignore the rest of this method.
    if (!this.hiddenMode.isHiddenMode()) {
      return;
    }

    // Clear previous definitions
    if (!this.hiddenMode.getDefinitions().isEmpty()) {
      this.hiddenMode.setElements(
          this.targetWordLabel,
          this.numberOfDefinitionLabel,
          this.maxWidth,
          this.defaultFontSize,
          this.previousDefinitionVbox,
          this.nextDefinitionVbox);
      return;
    }

    // Make Hidden mode exclusive elements invisible
    this.previousDefinitionVbox.setVisible(false);
    this.nextDefinitionVbox.setVisible(false);
    this.numberOfDefinitionLabel.setVisible(false);

    this.targetWordLabel.setText("Loading Definitions");
    this.numberOfDefinitionLabel.setText("Number of Definitions");
    this.numberOfDefinitionLabel.setVisible(true);
    this.logger.info("The word is: {}", this.wordService.getTargetWord());
    // Search for the definition of the current target word.
    this.hiddenMode.searchWord(
        this.wordService.getTargetWord(),
        this.targetWordLabel,
        this.numberOfDefinitionLabel,
        this.maxWidth,
        this.defaultFontSize,
        this.previousDefinitionVbox,
        this.nextDefinitionVbox);
  }
}
