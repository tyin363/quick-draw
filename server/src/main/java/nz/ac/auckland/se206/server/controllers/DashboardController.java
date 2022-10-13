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

  @FXML
  private void initialize() {
    this.onlineStudents.setText(Integer.toString(this.server.clientCount()));
    this.server.addClientCountChangeListener(
        () ->
            Platform.runLater(
                () -> this.onlineStudents.setText(Integer.toString(this.server.clientCount()))));
  }

  @FXML
  private void onStartSession() {
    this.server.startDrawingSession(this.targetWord.getText().toLowerCase(Locale.ROOT));
    this.targetWord.setText("");
    this.startSession.setDisable(true);
  }

  @FXML
  private void onChange() {
    this.startSession.setDisable(this.targetWord.getText().isBlank());
  }
}
