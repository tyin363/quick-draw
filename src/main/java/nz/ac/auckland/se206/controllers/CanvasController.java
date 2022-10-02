package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.ml.PredictionHandler;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.users.Round;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.BrushType;
import nz.ac.auckland.se206.util.Config;
import nz.ac.auckland.se206.words.WordService;
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
  @FXML private HBox gameOverActionsHoriBox;
  @FXML private Pane eraserPane;
  @FXML private Pane penPane;
  @FXML private Pane clearPane;
  @FXML private Button saveButton;
  @FXML private Label targetWordLabel;
  @FXML private Label mainLabel;
  private Label[] predictionLabels;

  @Inject private Logger logger;
  @Inject private Config config;
  @Inject private WordService wordService;
  @Inject private TextToSpeech textToSpeech;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;

  private GraphicsContext graphic;
  private PredictionHandler predictionHandler;
  private Timeline timer;
  private int secondsRemaining;
  private User user;
  private boolean isUpdatingPredictions;

  // Mouse coordinates
  private double currentX;
  private double currentY;

  private final BrushType penBrush =
      (e) -> {
        final double x = e.getX() - this.config.getBrushSize() / 2;
        final double y = e.getY() - this.config.getBrushSize() / 2;

        this.graphic.setFill(Color.BLACK);
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

    // Set current user as user
    this.user = this.userService.getCurrentUser();

    this.gameOverActionsHoriBox.setVisible(false);
    this.targetWordLabel.setText(this.wordService.getTargetWord());
    // Reset the timer and start predicting instantly
    this.secondsRemaining = this.config.getDrawingTimeSeconds();
    this.mainLabel.setText(this.config.getDrawingTimeSeconds() + " Seconds");
    this.timer.playFromStart();
    this.predictionHandler.startPredicting();

    // Clear any previous predictions
    this.clearPredictions();

    // Re-enable all the buttons
    this.penPane.setDisable(false);
    this.eraserPane.setDisable(false);
    this.clearPane.setDisable(false);
    this.saveButton.setDisable(false);
    this.onSelectPen();
  }

  /**
   * JavaFX calls this method once the GUI elements are loaded. It contains all the one-off
   * initialisation for this controller, such as setting up the canvas, the prediction handler and
   * the timer.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {
    Tooltip.install(this.eraserPane, new Tooltip(this.eraserPane.getAccessibleHelp()));
    Tooltip.install(this.penPane, new Tooltip(this.penPane.getAccessibleHelp()));
    Tooltip.install(this.clearPane, new Tooltip(this.clearPane.getAccessibleHelp()));

    this.graphic = this.canvas.getGraphicsContext2D();

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
        new PredictionHandler(this::getCurrentSnapshot, this::onPredictSuccess);

    // Generate the labels for all the predictions
    this.predictionLabels = new Label[this.config.getNumberOfPredictions()];
    for (int i = 0; i < this.config.getNumberOfPredictions(); i++) {
      this.predictionLabels[i] = new Label();
      this.predictionVertBox.getChildren().add(this.predictionLabels[i]);
    }

    // Create a timeline that reduces the number of seconds remaining by 1 every second
    this.timer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  this.secondsRemaining--;
                  this.mainLabel.setText(this.secondsRemaining + " Seconds");
                }));
    this.timer.setCycleCount(this.config.getDrawingTimeSeconds());
    this.timer.setOnFinished(e -> this.gameOver(false));
  }

  /** This method is called when the "Clear" button is pressed and clears the canvas. */
  @FXML
  private void onClear() {
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
    this.onClear();
    this.sceneManager.switchToView(View.CONFIRMATION_SCREEN);
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

    boolean wasGuessed = false;
    // Check if the target word is in the top number of predictions. If it is, you win.
    for (int i = 0; i < this.config.getWinPlacement(); i++) {
      // The target word uses spaces rather than underscores
      final String guess = predictions.get(i).getClassName().replaceAll("_", " ");
      if (guess.equals(this.wordService.getTargetWord())) {
        wasGuessed = true;
        break;
      }
    }

    for (int i = 0; i < predictions.size(); i++) {
      final Classification prediction = predictions.get(i);
      final String guess = prediction.getClassName().replaceAll("_", " ");
      // Make the text colour more blue if it's more confident in the prediction.
      final Color textColour =
          Color.BLACK.interpolate(this.config.getHighlight(), prediction.getProbability() * 10);

      this.predictionLabels[i].setText(guess);
      this.predictionLabels[i].setTextFill(textColour);
    }

    if (wasGuessed) {
      this.gameOver(true);
    }
  }

  /**
   * This method is called when the game is over. It updates the main label to show the appropriate
   * text depending on whether the user won or lost and handles all the necessary logic for
   * preventing further interaction with the game.
   *
   * @param wasGuessed Whether the user won or lost.
   */
  private void gameOver(final boolean wasGuessed) {
    // Get time taken
    final int timeTaken = this.config.getDrawingTimeSeconds() - this.secondsRemaining;

    // Get current round
    final Round round = new Round(this.wordService.getTargetWord(), timeTaken, wasGuessed);

    this.predictionHandler.stopPredicting();
    this.timer.stop();
    this.disableBrush();
    // Prevent the user from clearing their drawing
    this.clearPane.setDisable(true);
    final String message = wasGuessed ? "You Win!" : "Time up!";

    // Update statistics
    this.user.addPastRound(round);
    this.userService.saveUser(this.user);

    // Display game conclusion
    this.mainLabel.setText(message);
    this.textToSpeech.queueSentence(message);

    // Allow the user to save the image and restart the game
    this.gameOverActionsHoriBox.setVisible(true);
  }

  /**
   * Disables the brush so that you can't draw anymore. This also prevents you from clicking the
   * brush type buttons.
   */
  private void disableBrush() {
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
    this.timer.stop();
    this.textToSpeech.terminate();
  }

  /** Clears any prediction text by setting all prediction labels to an empty string */
  private void clearPredictions() {
    for (final Label predictionLabel : this.predictionLabels) {
      predictionLabel.setText("");
    }
  }

  /** Clears the canvas and switches back to the Main Menu Screen */
  @FXML
  private void onReturnToMainMenu() {
    this.onClear();
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
