package nz.ac.auckland.se206.statemachine.states;

import ai.djl.modality.Classifications.Classification;
import java.util.List;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.controllers.CanvasController;

public abstract class CanvasState {

  @Inject protected CanvasController canvasController;

  /** Called when the state machine switches to this state. */
  public void onEnter() {}

  /** Called when the state machine switches away from this state. */
  public void onExit() {}

  /** Called when the canvas view is loaded. */
  public void onLoad() {}

  /** Called when the canvas view is left */
  public void onLeave() {}

  /**
   * If the canvas controller is showing predictions, this will be passed the predictions.
   *
   * @param predictions The predictions that have been made
   */
  public void handlePredictions(final List<Classification> predictions) {
    this.canvasController.displayPredictions(predictions);
  }
}
