package nz.ac.auckland.se206.controllers.scenemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext extends InstanceFactory {

  public static ApplicationContext start(final Stage primaryStage, final View startingView) {
    final ApplicationContext context = new ApplicationContext();
    final SceneManager sceneManager = new SceneManager(primaryStage);

    primaryStage.setOnCloseRequest(e -> context.onTerminate());

    context.injectFields(sceneManager);
    context.bind(sceneManager);
    sceneManager.initialise(true, startingView);

    return context;
  }

  public ApplicationContext() {
    this.bind(this);
    this.bind(new ObjectMapper());
    this.registerSupplier(Logger.class, LoggerFactory::getLogger);
  }
  /**
   * When a close request has been received it will invoke {@link InstanceFactory#onTerminate()} and
   * then invokes {@code Platform.exit()}.
   */
  @Override
  public void onTerminate() {
    super.onTerminate();
    Platform.exit();
  }
}
