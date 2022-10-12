package nz.ac.auckland.se206.server;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

  public static void main(final String[] args) {
    launch();
  }

  @Override
  public void start(final Stage stage) throws IOException {
    final FXMLLoader fxmlLoader = new FXMLLoader(
        HelloApplication.class.getResource("hello-view.fxml"));
    final Scene scene = new Scene(fxmlLoader.load(), 320, 240);
    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();
  }
}