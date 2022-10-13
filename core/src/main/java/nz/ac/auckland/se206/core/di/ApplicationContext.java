package nz.ac.auckland.se206.core.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.stage.Stage;
import nz.ac.auckland.se206.core.annotations.Controller;
import nz.ac.auckland.se206.core.scenemanager.FxmlView;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext extends InstanceFactory {

  /**
   * Starts the application with the given stage and starting view. It will initialise the {@link
   * ApplicationContext} and the {@link SceneManager} and automatically create the view.
   *
   * @param primaryStage The primary stage of the application
   * @param startingView The view to start the application with
   * @param views All the views to preload
   * @return The created {@link ApplicationContext} instance.
   */
  public static ApplicationContext start(
      final Stage primaryStage, final FxmlView startingView, final FxmlView... views) {
    final ApplicationContext context = new ApplicationContext();
    final SceneManager sceneManager = new SceneManager(primaryStage);

    primaryStage.setOnCloseRequest(e -> context.onTerminate());

    // Make sure that all the fields are injected before initialising the scene manager
    context.injectFields(sceneManager);
    context.bind(sceneManager);

    // Check if we need to manually register any controllers
    context.registerPostConstructionCallback(
        (instance) -> registerControllers(sceneManager, instance));
    sceneManager.initialise(startingView, views);

    return context;
  }

  /**
   * Checks if the instance has the {@link Controller} annotation and if it does, registers it to
   * the scene manager.
   *
   * @param sceneManager The scene manager to register the controllers with
   * @param instance The instance to check if it needs to be rendered
   */
  private static void registerControllers(final SceneManager sceneManager, final Object instance) {
    if (instance.getClass().isAnnotationPresent(Controller.class)) {
      sceneManager.registerController(instance);
    }
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
