package nz.ac.auckland.se206.statemachine.states;

public abstract class CanvasState {

  /** Called when the state machine switches to this state. */
  public void onEnter() {}

  /** Called when the state machine switches away from this state. */
  public void onExit() {}

  /** Called when the canvas view is loaded. */
  public void onLoad() {}
}
