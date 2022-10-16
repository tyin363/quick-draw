package nz.ac.auckland.se206.client.sockets;

import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;

@Singleton
public class DrawingSessionService {

  private final SimpleObjectProperty<DrawingSessionRequest> drawingSessionProperty =
      new SimpleObjectProperty<>();

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
}
