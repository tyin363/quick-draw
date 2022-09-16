package nz.ac.auckland.se206;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;

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
    SceneManager.getInstance().initialise(stage, View.SWITCH_USER);
  }
}
