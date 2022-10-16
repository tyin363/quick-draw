package nz.ac.auckland.se206.server;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.se206.core.di.ApplicationContext;

public class App extends Application {

  /**
   * The entry point for this application which is used to launch JavaFX.
   *
   * @param args The command line arguments
   */
  public static void main(final String[] args) {
    launch(args);
  }

  /**
   * Called by JavaFX to start the application. It's used to initialise the application context and
   * the starting view.
   *
   * @param stage The stage to show the application in
   */
  @Override
  public void start(final Stage stage) {
    ApplicationContext.start(stage, View.DASHBOARD);
  }
}
