package nz.ac.auckland.se206.client.sockets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import nz.ac.auckland.se206.client.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.client.statemachine.states.NormalCanvasState;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;

@Singleton
public class DrawingSessionService {

  @Inject private CanvasStateMachine stateMachine;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;

  private final SimpleObjectProperty<DrawingSessionRequest> drawingSessionProperty =
      new SimpleObjectProperty<>();

  private List<Runnable> respondListeners = new ArrayList<>();

  /**
   * Set the current drawing session. This will automatically cause the listeners to be called.
   *
   * @param drawingSessionRequest The drawing session to set
   */
  public void setDrawingSession(final DrawingSessionRequest drawingSessionRequest) {
    this.drawingSessionProperty.set(drawingSessionRequest);
  }

  /**
   * Add a listener to be called when the drawing session changes.
   *
   * @param listener The listener to be called
   */
  public void addDrawingSessionListener(final Consumer<DrawingSessionRequest> listener) {
    this.drawingSessionProperty.addListener(
        (observable, oldValue, newValue) -> listener.accept(newValue));
  }

  /**
   * Add a listener to be called when the drawing session is responded to.
   *
   * @param listener The listener to be called
   */
  public void addRespondSessionListener(final Runnable listener) {
    this.respondListeners.add(listener);
  }

  /**
   * Accept the current drawing session. This will take you to the confirmation screen instantly.
   */
  public void acceptSession() {

    // Accept session
    this.wordService.setTargetWord(this.drawingSessionProperty.get().word());
    this.stateMachine.switchState(NormalCanvasState.class);
    this.sceneManager.switchToView(View.CANVAS);
    this.respondListeners.forEach(Runnable::run);
  }

  /** Decline the current drawing session. This will cause all the response listeners to run. */
  public void declineSession() {
    this.respondListeners.forEach(Runnable::run);
  }
}
