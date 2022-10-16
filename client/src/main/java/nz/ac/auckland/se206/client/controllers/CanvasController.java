package nz.ac.auckland.se206.client.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.client.components.canvas.PredictionEntry;
import nz.ac.auckland.se206.client.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.client.ml.PredictionHandler;
import nz.ac.auckland.se206.client.sounds.Music;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.client.util.BrushType;
import nz.ac.auckland.se206.client.util.Config;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;
import org.slf4j.Logger;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
@Singleton
public class CanvasController implements LoadListener, TerminationListener {

  @FXML private Canvas canvas;
  @FXML private VBox predictionVertBox;
  @FXML private HBox gameOverActionsContainer;
  @FXML private HBox defaultHbox;
  @FXML private VBox toolContainer;
  @FXML private Pane eraserPane;
  @FXML private Pane penPane;
  @FXML private Pane clearPane;
  @FXML private Button saveButton;
  @FXML private Label targetWordLabel;
  @FXML private Label mainLabel;
  @FXML private Label targetWordConfidenceLabel;
  @FXML private AnchorPane wordDefinition;
  @FXML private Label hintLabel;
  @FXML private HBox hintsHbox;
  @FXML private Pane confidenceIcon;

  @Inject private Logger logger;
  @Inject private Config config;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private SoundEffect soundEffect;
  @Inject private CanvasStateMachine stateMachine;
  @Inject private HiddenMode hiddenMode;

  private GraphicsContext graphic;
  private PredictionHandler predictionHandler;
  private HeaderController headerController;
  private boolean isUpdatingPredictions;
  private Color penColour = Color.BLACK;
  private PredictionEntry[] predictionEntries;
  private double currentWordConfidenceLevel;

  // Mouse coordinates
  private double currentX;
  private double currentY;

  private final BrushType penBrush =
      (e) -> {
        final double x = e.getX() - this.config.getBrushSize() / 2;
        final double y = e.getY() - this.config.getBrushSize() / 2;

        this.graphic.setStroke(this.penColour);
        this.graphic.setLineWidth(this.config.getBrushSize());

        // Create a line that goes from the point (currentX, currentY) and (x,y)
        this.graphic.strokeLine(this.currentX, this.currentY, x, y);

        // Update the coordinates
        this.currentX = x;
        this.currentY = y;
      };

  private final BrushType eraserBrush =
      (e) -> {
        final double x = e.getX() - this.config.getBrushSize();
        final double y = e.getY() - this.config.getBrushSize();
        this.graphic.clearRect(
            x, y, this.config.getBrushSize() * 2, this.config.getBrushSize() * 2);
        // Update the coordinates
        this.currentX = x;
        this.currentY = y;
      };

  private final BrushType disabledBrush =
      (e) -> {
        this.currentX = e.getX();
        this.currentY = e.getY();
      };

  private BrushType selectedBrushType = this.disabledBrush;

  /**
   * This method is called everytime this view is switched to. It manages the resetting of any
   * changes that might have been made during the last time the view was switched to.
   */
  @Override
  public void onLoad() {
    this.setCurrentWordConfidenceLevel(0.00);
    this.targetWordConfidenceLabel.setTextFill(Color.BLACK);
    this.targetWordLabel.setVisible(true);
    this.targetWordLabel.setText(this.wordService.getTargetWord());
    this.targetWordConfidenceLabel.setText(this.wordService.getTargetWord());
    this.predictionHandler.startPredicting();

    // Clear any previous predictions
    this.clearPredictions();

    // Re-enable all the buttons
    this.penPane.setDisable(false);
    this.eraserPane.setDisable(false);
    this.clearPane.setDisable(false);
    this.saveButton.setDisable(false);
    this.onSelectPen();
    this.stateMachine.getCurrentState().onLoad();
  }

  /**
   * JavaFX calls this method once the GUI elements are loaded. It contains all the one-off
   * initialisation for this controller, such as setting up the canvas, the prediction handler and
   * the timer.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException, URISyntaxException {
    // Disable header buttons in canvas controller
    this.headerController = this.sceneManager.getSubController(HeaderController.class);
    this.headerController.disableButtons();
    Tooltip.install(this.eraserPane, new Tooltip(this.eraserPane.getAccessibleHelp()));
    Tooltip.install(this.penPane, new Tooltip(this.penPane.getAccessibleHelp()));
    Tooltip.install(this.clearPane, new Tooltip(this.clearPane.getAccessibleHelp()));

    this.graphic = this.canvas.getGraphicsContext2D();
    // Initially make the word definition visible
    this.wordDefinition.setVisible(false);

    // save coordinates when mouse is pressed on the canvas
    this.canvas.setOnMousePressed(
        e -> {
          this.currentX = e.getX();
          this.currentY = e.getY();
          this.isUpdatingPredictions = true;
        });

    // When the user draws on the canvas apply the relevant effect of the selected brush
    this.canvas.setOnMouseDragged(e -> this.selectedBrushType.accept(e));

    this.predictionHandler =
        new PredictionHandler(
            this::getCurrentSnapshot, this::onPredictSuccess, this.wordService, this);

    // Generate the entries for all the predictions
    this.predictionEntries = new PredictionEntry[this.config.getNumberOfPredictions()];
    for (int i = 0; i < this.config.getNumberOfPredictions(); i++) {
      this.predictionEntries[i] = new PredictionEntry();
    }
    this.predictionVertBox.getChildren().addAll(this.predictionEntries);
  }

  /** This method is called when the "Get Hint" button is pressed and gets a hint. */
  @FXML
  private void onGetHint() {
    if (this.targetWordLabel == null) {
      return;
    }
    final char firstCharacter = this.targetWordLabel.getText().toUpperCase().charAt(0);
    this.hintLabel.setText("The word starts with: " + firstCharacter);
  }

  /** This method is called when the "Clear" button is pressed and clears the canvas. */
  @FXML
  private void onClear() {
    this.setCurrentWordConfidenceLevel(0.00);
    this.targetWordConfidenceLabel.setTextFill(Color.BLACK);
    this.confidenceIcon.getStyleClass().remove(1);
    this.confidenceIcon.getStyleClass().add("dash-icon");
    this.graphic.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    this.clearPredictions();

    // Stop updating predictions
    this.isUpdatingPredictions = false;
  }

  /** Switches the brush type to eraser and adjust the icon styling. */
  @FXML
  private void onSelectEraser() {
    // So that you can't change the brush type if it's been disabled
    if (this.selectedBrushType != this.eraserBrush) {
      this.penPane.getStyleClass().remove("icon-btn-selected");
      this.eraserPane.getStyleClass().add("icon-btn-selected");
      this.selectedBrushType = this.eraserBrush;
    }
  }

  /** Switches the brush type to pen and adjust the icon styling. */
  @FXML
  private void onSelectPen() {
    // So that you can't change the brush type if it's been disabled
    if (this.selectedBrushType != this.penBrush) {
      this.eraserPane.getStyleClass().remove("icon-btn-selected");
      this.penPane.getStyleClass().add("icon-btn-selected");
      this.selectedBrushType = this.penBrush;
    }
  }

  /**
   * This method is called when the "Save" button is pressed. It saves the current drawing as a PNG
   * to a file of the users choosing. If the file is saved successfully the save button is disabled.
   */
  @FXML
  private void onSave() {
    // Play click sound effect
    this.soundEffect.playSound(Sound.CLICK);
    if (this.saveCurrentSnapshotOnFile()) {
      this.saveButton.setDisable(true);
    }
  }

  /**
   * Clears the canvas and switches back to the Confirmation Screen where a new word is randomly
   * generated.
   */
  @FXML
  private void onRestart() {
    this.handleLeave();
    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
  }

  /**
   * Handles the cleanup when leaving the canvas view, including the changing of the music back to
   * the main menu music.
   */
  private void handleLeave() {
    // Play music and sounds
    this.soundEffect.playSound(Sound.CLICK);
    this.soundEffect.terminateBackgroundMusic();
    this.soundEffect.playBackgroundMusic(Music.MAIN_MUSIC);

    // Hidden mode exclusive
    this.hiddenMode.clearDefinitions();
    this.hintLabel.setText(null);

    this.onClear();
    this.stateMachine.getCurrentState().onLeave();
  }

  /**
   * The callback for the prediction handler. It is invoked after every successful prediction
   * generation by the model and is used to update the predictions on the screen.
   *
   * @param predictions The predictions returned by the model.
   */
  private void onPredictSuccess(final List<Classification> predictions) {
    if (!this.isUpdatingPredictions) {
      return;
    }

    this.stateMachine.getCurrentState().handlePredictions(predictions);
  }

  /**
   * Displays the predictions on the screen with the text colouring being closer to the highlight
   * the more confident the model is in the prediction.
   *
   * @param predictions The predictions to display
   */
  public void displayPredictions(final List<Classification> predictions) {
    for (int i = 0; i < predictions.size(); i++) {
      final Classification prediction = predictions.get(i);
      final String guess = prediction.getClassName().replaceAll("_", " ");
      // Make the text colour more blue if it's more confident in the prediction.
      final Color textColour =
          Color.BLACK.interpolate(this.config.getHighlight(), prediction.getProbability() * 10);
      this.predictionEntries[i].update(guess, (int) (prediction.getProbability() * 100));
      this.predictionEntries[i].setTextColour(textColour);
    }
  }

  /**
   * Disables the brush so that you can't draw anymore. This also prevents you from clicking the
   * brush type buttons.
   */
  public void disableBrush() {
    this.selectedBrushType = this.disabledBrush;
    // Remove the selected css class from the brush type buttons if they have it
    this.penPane.getStyleClass().remove("icon-btn-selected");
    this.eraserPane.getStyleClass().remove("icon-btn-selected");
    this.penPane.setDisable(true);
    this.eraserPane.setDisable(true);
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = this.canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /**
   * Prompts the user to select a location to save the current snapshot.
   *
   * @return Whether the file was successfully saved.
   */
  private boolean saveCurrentSnapshotOnFile() {
    final File tmpFolder = new File("tmp");
    if (!tmpFolder.exists()) {
      tmpFolder.mkdir();
    }

    // Avoid spaces in the file name.
    final String targetWord = this.wordService.getTargetWord().replaceAll(" ", "_");

    final FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(tmpFolder);
    fileChooser.setTitle("Save Image");
    fileChooser.setInitialFileName(targetWord);
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG", "*.png"));

    final File file = fileChooser.showSaveDialog(this.sceneManager.getStage());
    if (file != null) {
      try {
        // Save the image to a file.
        ImageIO.write(this.getCurrentSnapshot(), "png", file);
        return true;
      } catch (final IOException e) {
        this.logger.error("Error saving image", e);
      }
    }
    return false;
  }

  /**
   * Just before the application exists, we need to stop the predictions, timer and text to speech.
   */
  @Override
  public void onTerminate() {
    this.predictionHandler.stopPredicting();
  }

  /** Clears any prediction text by resetting all the prediction entries */
  private void clearPredictions() {
    for (final PredictionEntry predictionLabel : this.predictionEntries) {
      predictionLabel.reset();
    }
  }

  /** Clears the canvas and switches back to the Main Menu Screen */
  @FXML
  private void onReturnToMainMenu() {
    this.handleLeave();
    this.sceneManager.switchToView(View.MAIN_MENU);
  }

  /**
   * Retrieve the eraser brush icon container.
   *
   * @return The eraser brush icon container
   */
  public Pane getEraserPane() {
    return this.eraserPane;
  }

  /**
   * Retrieve the pen brush icon container.
   *
   * @return The pen brush icon container
   */
  public Pane getPenPane() {
    return this.penPane;
  }

  /**
   * Retrieve the clear canvas icon container.
   *
   * @return The clear canvas icon container
   */
  public Pane getClearPane() {
    return this.clearPane;
  }

  /**
   * Retrieves the container which holds the 3 action icon buttons.
   *
   * @return The container which holds the 3 action icon buttons
   */
  public VBox getToolContainer() {
    return this.toolContainer;
  }

  /**
   * Retrieve the container for the 3 buttons that appear when the game ends.
   *
   * @return The game over action container
   */
  public HBox getGameOverActionsContainer() {
    return this.gameOverActionsContainer;
  }

  /**
   * Retrieve the main label which appears just above the canvas.
   *
   * @return The main label
   */
  public Label getMainLabel() {
    return this.mainLabel;
  }

  /**
   * Retrieve the current pen colour being used.
   *
   * @return The current pen colour
   */
  public Color getPenColour() {
    return this.penColour;
  }

  /**
   * Set the current pen colour to use when drawing.
   *
   * @param penColour The new pen colour
   */
  public void setPenColour(final Color penColour) {
    this.penColour = penColour;
  }

  /**
   * Retrieves the prediction handler instance which is used to make predictions.
   *
   * @return The prediction handler instance
   */
  public PredictionHandler getPredictionHandler() {
    return this.predictionHandler;
  }

  /**
   * Retrieves the confidence level of the current word
   *
   * @return current word confidence level
   */
  public double getCurrentWordConfidenceLevel() {
    return this.currentWordConfidenceLevel;
  }

  /**
   * Sets the confidence level of the current word
   *
   * @param confidenceLevel The new confidence level
   */
  public void setCurrentWordConfidenceLevel(final double confidenceLevel) {
    this.currentWordConfidenceLevel = confidenceLevel;
  }

  /*
   * This retrieves the label with the word hint on it
   *
   * @return The label with the word hint
   */

  public Label getHintLabel() {
    return this.hintLabel;
  }

  /**
   * This retrieves the Hbox containing the hints elements
   *
   * @return The Hbox containing the hints elements
   */
  public HBox getHintsHbox() {
    return this.hintsHbox;
  }

  /**
   * This retrieves the Anchor pane responsible for displaying the word definitions in the hidden
   * game mode
   *
   * @return The anchor pane for hidden game mode
   */
  public AnchorPane getWordDefinition() {
    return this.wordDefinition;
  }

  /**
   * This retrieves the Hbox that contains the normal game mode elements
   *
   * @return Normal game mode Hbox
   */
  public HBox getDefaultHbox() {
    return this.defaultHbox;
  }

  /**
   * This retrieves the target word confidence label
   *
   * @return target word confidence label
   */
  public Label getTargetWordConfidenceLabel() {
    return this.targetWordConfidenceLabel;
  }

  /**
   * This retrieves the current confidence icon
   *
   * @return confidence icon
   */
  public Pane getConfidenceIcon() {
    return this.confidenceIcon;
  }
}
