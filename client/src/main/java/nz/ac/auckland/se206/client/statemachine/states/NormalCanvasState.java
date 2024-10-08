package nz.ac.auckland.se206.client.statemachine.states;

import ai.djl.modality.Classifications.Classification;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import nz.ac.auckland.se206.client.sounds.Music;
import nz.ac.auckland.se206.client.sounds.Sound;
import nz.ac.auckland.se206.client.sounds.SoundEffect;
import nz.ac.auckland.se206.client.speech.TextToSpeech;
import nz.ac.auckland.se206.client.users.Round;
import nz.ac.auckland.se206.client.users.Round.Mode;
import nz.ac.auckland.se206.client.users.User;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.Config;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.TerminationListener;

@Singleton(injectSuper = true)
public class NormalCanvasState extends CanvasState implements TerminationListener {

  @Inject protected SoundEffect soundEffect;
  @Inject protected TextToSpeech textToSpeech;
  @Inject protected UserService userService;
  @Inject protected WordService wordService;
  @Inject protected Config config;

  private int secondsRemaining;
  private Timeline timer;

  /**
   * When the default canvas state is loaded, make sure the game over actions aren't visible and
   * reset the count-down timer.
   */
  @Override
  public void onLoad() {

    // Set Background music
    this.soundEffect.terminateBackgroundMusic();
    this.soundEffect.playBackgroundMusic(Music.CANVAS_MUSIC);

    this.canvasController.getGameOverActionsContainer().setVisible(false);

    // Getting the time depending time setting and setting the respective time label
    this.secondsRemaining = this.config.getDrawingTimeSeconds();
    this.canvasController.getMainLabel().setText(this.config.getDrawingTimeSeconds() + " Seconds");

    // Creating and starting the timer
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
    final double targetConfidence = this.config.getTargetConfidence();

    boolean wasGuessed = false;
    // Check if the target word is in the top number of predictions. If it is, you win.
    for (int i = 0; i < winPlacement; i++) {
      // The target word uses spaces rather than underscores
      final String guess = predictions.get(i).getClassName().replaceAll("_", " ");
      final double probability = predictions.get(i).getProbability();

      if ((guess.equals(targetWord)) && (probability >= targetConfidence)) {
        wasGuessed = true;
        break;
      }
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
  public void gameOver(final boolean wasGuessed) {
    // Get time taken
    final int timeTaken = this.config.getDrawingTimeSeconds() - this.secondsRemaining;
    final User currentUser = this.userService.getCurrentUser();

    // Get current round
    final Round round =
        new Round(this.wordService.getTargetWord(), timeTaken, wasGuessed, this.getMode());

    // Play sound effect and music based on if the user won or lost
    this.soundEffect.terminateBackgroundMusic();
    if (wasGuessed) {
      this.soundEffect.playSound(Sound.WIN);
      this.soundEffect.playBackgroundMusic(Music.WIN_MUSIC);
    } else {
      this.soundEffect.playSound(Sound.LOSE);
      this.soundEffect.playBackgroundMusic(Music.LOSE_MUSIC);
    }
    this.canvasController.getPredictionHandler().stopPredicting();
    this.timer.stop();
    this.canvasController.disableBrush();

    // Prevent the user from clearing their drawing
    this.canvasController.getClearPane().setDisable(true);
    final String message = this.getConclusionMessage(wasGuessed);

    // Update statistics
    currentUser.addPastRound(round);
    this.userService.saveUser(currentUser);

    // Display game conclusion
    this.canvasController.getMainLabel().setText(message);
    this.textToSpeech.queueSentence(message);

    // Allow the user to save the image and restart the game
    this.canvasController.getGameOverActionsContainer().setVisible(true);
  }

  /**
   * Retrieve the message that is displayed after the game has ended.
   *
   * @param wasGuessed If the user guessed the target word
   * @return The message to display
   */
  protected String getConclusionMessage(final boolean wasGuessed) {
    return wasGuessed ? "You Win!" : "Time up!";
  }

  /**
   * Retrieves the current mode of the game.
   *
   * @return The current mode of the game
   */
  protected Mode getMode() {
    return Mode.NORMAL;
  }

  /** When the application is terminated, make sure to stop the timer. */
  @Override
  public void onTerminate() {
    if (this.timer != null) {
      this.timer.stop();
    }
  }
}
