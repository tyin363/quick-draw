package nz.ac.auckland.se206.statemachine.states;

import nz.ac.auckland.se206.controllers.CanvasController;

public abstract class CanvasState {

  protected CanvasController canvasController;

  public CanvasState(final CanvasController canvasController) {
    this.canvasController = canvasController;
  }

  /** Called when the state machine switches to this state. */
  public void onEnter() {}

  /** Called when the state machine switches away from this state. */
  public void onExit() {}

  /** Called when the canvas view is loaded. */
  public void onLoad() {}
}
