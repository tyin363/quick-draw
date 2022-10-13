package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.words.WordService;

@Controller
public class WordDefinitionController implements LoadListener {
  @FXML private Label targetWordLabel;
  @FXML private VBox previousDefinitionVbox;
  @FXML private VBox nextDefinitionVbox;
  @FXML private Label numberOfDefinitionLabel;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private HiddenMode hiddenMode;

  private double maxWidth = 670;
  private double defaultFontSize = 36;

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

  @Override
  public void onLoad() {
    // Clear any styles on the target word
    this.targetWordLabel.setStyle(null);

    // Clear previous definitions
    if (!this.hiddenMode.getDefinitions().isEmpty()) {
      this.hiddenMode.setElements(
          targetWordLabel,
          numberOfDefinitionLabel,
          maxWidth,
          defaultFontSize,
          previousDefinitionVbox,
          nextDefinitionVbox);
      return;
    }

    // Make Hidden mode exclusive elements invisible
    previousDefinitionVbox.setVisible(false);
    nextDefinitionVbox.setVisible(false);
    numberOfDefinitionLabel.setVisible(false);

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
  }
}
