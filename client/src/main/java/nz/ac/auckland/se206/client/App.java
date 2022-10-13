package nz.ac.auckland.se206.client;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.se206.client.sockets.ClientSocket;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.core.di.ApplicationContext;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static void main(final String[] args) {
    launch();
  }

  /**
   * This method is invoked when the application starts. It initialises the scene manager with the
   * starting scene.
   *
   * @param stage The primary stage of the application
   */
  @Override
  public void start(final Stage stage) {
    final ApplicationContext applicationContext =
        ApplicationContext.start(stage, View.SWITCH_USER, View.values());
    final ClientSocket clientSocket = applicationContext.get(ClientSocket.class);
  }
}
