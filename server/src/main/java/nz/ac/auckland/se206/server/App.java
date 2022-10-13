package nz.ac.auckland.se206.server;

import javafx.application.Application;
import javafx.stage.Stage;
import nz.ac.auckland.se206.core.di.ApplicationContext;

public class App extends Application {

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage stage) {
    ApplicationContext.start(stage, View.DASHBOARD);
  }
}
