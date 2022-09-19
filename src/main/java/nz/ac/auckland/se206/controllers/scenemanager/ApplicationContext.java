package nz.ac.auckland.se206.controllers.scenemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext extends InstanceFactory {

  /**
   * Starts the application with the given stage and starting view. It will initialise the {@link
   * ApplicationContext} and the {@link SceneManager} and automatically create the view.
   *
   * @param primaryStage The primary stage of the application
   * @param startingView The view to start the application with
   * @return The created {@link ApplicationContext} instance.
   */
  public static ApplicationContext start(final Stage primaryStage, final View startingView) {
    final ApplicationContext context = new ApplicationContext();
    final SceneManager sceneManager = new SceneManager(primaryStage);

    primaryStage.setOnCloseRequest(e -> context.onTerminate());

    // Make sure that all the fields are injected before initialising the scene manager
    context.injectFields(sceneManager);
    context.bind(sceneManager);
    sceneManager.initialise(false, startingView);

    return context;
  }

  /**
   * Constructs a new {@link ApplicationContext} instance and binds itself so that it can be
   * injected either as an {@link ApplicationContext} or an {@link InstanceFactory}. It also adds
   * bindings for {@link ObjectMapper} and a supplier for {@link Logger}.
   */
  public ApplicationContext() {
    this.bind(this);
    this.bind(InstanceFactory.class, this); // Allow you to inject this as an InstanceFactory
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
