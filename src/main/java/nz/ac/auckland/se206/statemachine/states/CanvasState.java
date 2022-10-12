package nz.ac.auckland.se206.statemachine.states;

import ai.djl.modality.Classifications.Classification;
import java.util.List;
import nz.ac.auckland.se206.controllers.CanvasController;

public abstract class CanvasState {

  protected CanvasController canvasController;

  /**
   * Create an instance of a canvas state with a reference to the canvas controller whose UI will be
   * modified by this state.
   *
   * @param canvasController The canvas controller instance
   */
  public CanvasState(final CanvasController canvasController) {
    this.canvasController = canvasController;
  }

  /** Called when the state machine switches to this state. */
  public void onEnter() {}

  /** Called when the state machine switches away from this state. */
  public void onExit() {}

  /** Called when the canvas view is loaded. */
  public void onLoad() {}

  /**
   * If the canvas controller is showing predictions, this will be passed the predictions.
   *
   * @param predictions The predictions to handle.
   */
  public void handlePredictions(final List<Classification> predictions) {
    this.canvasController.displayPredictions(predictions);
  }
}
