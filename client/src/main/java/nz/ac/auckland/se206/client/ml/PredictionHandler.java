package nz.ac.auckland.se206.client.ml;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nz.ac.auckland.se206.client.controllers.CanvasController;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredictionHandler implements TerminationListener {

  public static final int NUMBER_OF_PREDICTIONS = 10;

  private final Logger logger = LoggerFactory.getLogger(PredictionHandler.class);
  private final Timeline timeline;
  private final DoodlePrediction model;
  private Task<List<Classifications.Classification>> backgroundTask;
  private final WordService wordService;
  private final CanvasController canvasController;

  /**
   * Constructs a new prediction handler which will generate predictions every second in the
   * background once {@link PredictionHandler#startPredicting()} is called.
   *
   * @param snapshotSupplier The supplier of the current snapshot of the canvas
   * @param onSuccess The consumer of the prediction results
   * @throws ModelException If there is an error in reading the input/output of the DL model
   * @throws IOException If the model cannot be found on the file system
   */
  public PredictionHandler(
      final Supplier<BufferedImage> snapshotSupplier,
      final Consumer<List<Classifications.Classification>> onSuccess,
      final WordService wordService,
      final CanvasController canvasController)
      throws ModelException, IOException, URISyntaxException {
    this.wordService = wordService;
    this.canvasController = canvasController;
    this.model = new DoodlePrediction();
    // Create a timeline that performs a prediction every second indefinitely.
    this.timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1), e -> this.performPredictions(snapshotSupplier, onSuccess)));
    this.timeline.setCycleCount(Animation.INDEFINITE);
  }

  /**
   * This method starts the background prediction task.
   *
   * @param snapshotSupplier The supplier of the current snapshot of the canvas
   * @param onSuccess The consumer of the prediction results
   */
  private void performPredictions(
      final Supplier<BufferedImage> snapshotSupplier,
      final Consumer<List<Classifications.Classification>> onSuccess) {

    // If this is called too quickly, wait for the previous task to finish.
    if (this.backgroundTask != null && this.backgroundTask.isRunning()) {
      this.logger.info("Already running");
      return;
    }

    final long start = System.currentTimeMillis();
    // This can only be retrieved on the main thread.
    final BufferedImage snapshot = snapshotSupplier.get();
    this.backgroundTask =
        new Task<>() {
          @Override
          protected List<Classifications.Classification> call() throws TranslateException {
            // Using the snapshot retrieved from the main thread, perform the prediction on
            // another thread.
            final List<Classifications.Classification> predictions =
                PredictionHandler.this.model.getPredictions(snapshot, 345);
            PredictionHandler.this.changeConfidenceImage(predictions);
            return PredictionHandler.this.model.getPredictions(snapshot, NUMBER_OF_PREDICTIONS);
          }
        };

    this.backgroundTask.setOnSucceeded(
        event -> {
          this.logger.info("prediction performed in {} ms", System.currentTimeMillis() - start);
          final List<Classifications.Classification> predictions = this.backgroundTask.getValue();
          // Predictions can be null if you hold down on the exit button but don't click it.
          if (predictions != null) {
            onSuccess.accept(predictions);
          }
        });
    this.backgroundTask.setOnFailed(
        event -> this.logger.info(this.backgroundTask.getState().toString()));

    new Thread(this.backgroundTask).start();
  }

  /** This method starts the background prediction task. */
  public void startPredicting() {
    this.timeline.play();
  }

  /**
   * This method stops the background prediction task. If there is currently a task running it will
   * attempt to cancel it.
   */
  public void stopPredicting() {
    this.timeline.stop();
    if (this.backgroundTask != null && this.backgroundTask.isRunning()) {
      this.backgroundTask.cancel();
    }
  }

  /** When the application is closing, stop the predicting task. */
  @Override
  public void onTerminate() {
    this.stopPredicting();
  }

  /**
   * This changes the confidence level image depending on the current word's confidence level
   *
   * @param predictions list of predictions
   */
  public void changeConfidenceImage(final List<Classifications.Classification> predictions) {
    // Finding current word in prediction list
    for (final Classification prediction : predictions) {
      final String targetWord = this.wordService.getTargetWord().replace(" ", "_");
      if (prediction.getClassName().equals(targetWord)) {

        // Retrieving the current and previous confidence level of the current word
        final double probability = prediction.getProbability();
        final double currentProbability = this.canvasController.getCurrentWordConfidenceLevel();

        // Changing confidence level image depending on the current word's confidence
        // level
        if (currentProbability != 0.0) {
          if (probability > currentProbability) {

            // If confidence level increases show an up arrow icon
            this.canvasController.setCurrentWordConfidenceLevel(prediction.getProbability());
            this.canvasController.getTargetWordConfidenceLabel().setTextFill(Color.web("3aa55d"));
            this.canvasController.getConfidenceIcon().getStyleClass().remove(1);
            this.canvasController.getConfidenceIcon().getStyleClass().add("up-icon");
          } else if (probability < currentProbability) {

            // If confidence level decreases show a down arrow icon
            this.canvasController.setCurrentWordConfidenceLevel(prediction.getProbability());
            this.canvasController.getTargetWordConfidenceLabel().setTextFill(Color.web("ED4245"));
            this.canvasController.getConfidenceIcon().getStyleClass().remove(1);
            this.canvasController.getConfidenceIcon().getStyleClass().add("down-icon");
          } else {

            // If confidence level s show a dash icon
            this.canvasController.getTargetWordConfidenceLabel().setTextFill(Color.BLACK);
            this.canvasController.getConfidenceIcon().getStyleClass().remove(1);
            this.canvasController.getConfidenceIcon().getStyleClass().add("dash-icon");
          }
        } else {
          this.canvasController.setCurrentWordConfidenceLevel(prediction.getProbability());
        }
      }
    }
  }
}
