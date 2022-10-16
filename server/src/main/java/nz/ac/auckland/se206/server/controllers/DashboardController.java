package nz.ac.auckland.se206.server.controllers;

import java.util.Locale;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.server.sockets.Server;

@Singleton
public class DashboardController {

  @Inject private Server server;

  @FXML private TextField targetWord;
  @FXML private Button startSession;
  @FXML private Label onlineStudents;

  /**
   * Set the initial number of online students and add a listener to update it as new clients join.
   */
  @FXML
  private void initialize() {
    this.onlineStudents.setText(Integer.toString(this.server.clientCount()));
    // This listener is called on a different thread, so we need to use Platform.runLater to
    // allow us to update the UI.
    this.server.addClientCountChangeListener(
        () ->
            Platform.runLater(
                () -> this.onlineStudents.setText(Integer.toString(this.server.clientCount()))));
  }

  /** Starts a new drawing session with the target word specified in the text field. */
  @FXML
  private void onStartSession() {
    this.server.startDrawingSession(this.targetWord.getText().toLowerCase(Locale.ROOT));
    this.targetWord.setText("");
    this.startSession.setDisable(true);
  }

  /** Only allow you to start a new session if the target word is not blank. */
  @FXML
  private void onChange() {
    this.startSession.setDisable(this.targetWord.getText().isBlank());
  }

  @FXML
  private void onRandomWord() {}
}
