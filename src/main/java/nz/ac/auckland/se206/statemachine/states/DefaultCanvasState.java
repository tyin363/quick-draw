package nz.ac.auckland.se206.statemachine.states;

import ai.djl.modality.Classifications.Classification;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.users.Round;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Config;
import nz.ac.auckland.se206.words.WordService;

public class DefaultCanvasState extends CanvasState implements EnableListener, TerminationListener {

  @Inject private TextToSpeech textToSpeech;
  @Inject private UserService userService;
  @Inject private WordService wordService;
  @Inject private Config config;

  private int secondsRemaining;
  private Timeline timer;

  /**
   * Create an instance of the default canvas state with a reference to the canvas controller whose
   * UI will be modified by this state.
   *
   * @param canvasController The canvas controller instance
   */
  @Inject
  public DefaultCanvasState(final CanvasController canvasController) {
    super(canvasController);
  }

  /**
   * When the default canvas state is loaded, make sure the game over actions aren't visible and
   * reset the count-down timer.
   */
  @Override
  public void onLoad() {
    this.canvasController.getGameOverActionsContainer().setVisible(false);
    this.secondsRemaining = this.config.getDrawingTimeSeconds();
    this.canvasController.getMainLabel().setText(this.config.getDrawingTimeSeconds() + " Seconds");
    this.timer.playFromStart();
  }

  /**
   * After the predictions have been rendered, check to see if the correct word is within the
   * winning placement. If so, then the user has won the round.
   *
   * @param predictions The predictions that have been made
   */
  @Override
  public void handlePredictions(final List<Classification> predictions) {
    super.handlePredictions(predictions);
    final int winPlacement = this.config.getWinPlacement();
    final String targetWord = this.wordService.getTargetWord();

    boolean wasGuessed = false;
    // Check if the target word is in the top number of predictions. If it is, you win.
    for (int i = 0; i < winPlacement; i++) {
      // The target word uses spaces rather than underscores
      final String guess = predictions.get(i).getClassName().replaceAll("_", " ");
      if (guess.equals(targetWord)) {
        wasGuessed = true;
        break;
      }
    }

    if (wasGuessed) {
      this.gameOver(true);
    }
  }

  /**
   * When this state is first created, construct a timer instance that can be reused to count down
   * the time the user has remaining.
   */
  @Override
  public void onEnable() {
    // Create a timeline that reduces the number of seconds remaining by 1 every second
    this.timer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  this.secondsRemaining--;
                  this.canvasController.getMainLabel().setText(this.secondsRemaining + " Seconds");
                }));
    this.timer.setCycleCount(this.config.getDrawingTimeSeconds());
    this.timer.setOnFinished(e -> this.gameOver(false));
  }

  /**
   * This method is called when the game is over. It updates the main label to show the appropriate
   * text depending on whether the user won or lost and handles all the necessary logic for
   * preventing further interaction with the game.
   *
   * @param wasGuessed Whether the user won or lost.
   */
  public void gameOver(final boolean wasGuessed) {
    // Get time taken
    final int timeTaken = this.config.getDrawingTimeSeconds() - this.secondsRemaining;
    final User currentUser = this.userService.getCurrentUser();

    // Get current round
    final Round round = new Round(this.wordService.getTargetWord(), timeTaken, wasGuessed);

    this.canvasController.getPredictionHandler().stopPredicting();
    this.timer.stop();
    this.canvasController.disableBrush();
    // Prevent the user from clearing their drawing
    this.canvasController.getClearPane().setDisable(true);
    final String message = wasGuessed ? "You Win!" : "Time up!";

    // Update statistics
    currentUser.addPastRound(round);
    this.userService.saveUser(currentUser);

    // Display game conclusion
    this.canvasController.getMainLabel().setText(message);
    this.textToSpeech.queueSentence(message);

    // Allow the user to save the image and restart the game
    this.canvasController.getGameOverActionsContainer().setVisible(true);
  }

  /** When the application is terminated, make sure to stop the timer. */
  @Override
  public void onTerminate() {
    this.timer.stop();
  }
}
