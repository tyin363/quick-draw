package nz.ac.auckland.se206.client.statemachine;

import nz.ac.auckland.se206.client.annotations.Inject;
import nz.ac.auckland.se206.client.annotations.Singleton;
import nz.ac.auckland.se206.client.controllers.scenemanager.InstanceFactory;
import nz.ac.auckland.se206.client.statemachine.states.CanvasState;

@Singleton
public class CanvasStateMachine {

  @Inject private InstanceFactory instanceFactory;

  private CanvasState currentState;

  /**
   * Switches the state of the state machine to the given state. If the current state is not null
   * then it will invoke {@link CanvasState#onExit()} on the current state before switching to the
   * new state and invoking {@link CanvasState#onEnter()}.
   *
   * @param newState The state to switch to
   */
  public void switchState(final Class<? extends CanvasState> newState) {
    if (this.currentState != null) {
      if (this.currentState.getClass() == newState) {
        return;
      }
      // Allow the current state to do any clean-up if necessary
      this.currentState.onExit();
    }
    this.currentState = this.instanceFactory.get(newState);
    this.currentState.onEnter();
  }

  /**
   * Get the current state of the state machine.
   *
   * @return The current state of the state machine
   */
  public CanvasState getCurrentState() {
    return this.currentState;
  }
}
