package nz.ac.auckland.se206.controllers.scenemanager;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneManager {

  private static final SceneManager instance = new SceneManager(false);

  /**
   * Retrieve the singleton instance of the SceneManager.
   *
   * @return A non-null instance of the SceneManager
   */
  public static SceneManager getInstance() {
    return instance;
  }

  private final Map<View, Pair<Parent, Object>> views = new EnumMap<>(View.class);
  private final InstanceFactory instanceFactory = new InstanceFactory();
  private final Logger logger = LoggerFactory.getLogger(SceneManager.class);
  private Scene scene;
  private Stage stage;

  /**
   * Internal constructor for the SceneManager. Lazy loading the views' means they'll be loaded only
   * when they're first attempted to be loaded, whereas if you're not lazy loading then they'll be
   * all preloaded on startup.
   *
   * @param lazyLoading Whether to lazy load the views
   */
  private SceneManager(final boolean lazyLoading) {
    if (!lazyLoading) { // If we're not lazy loading, preload all the views
      for (final View view : View.values()) {
        this.loadView(view);
      }
    }
  }

  /**
   * Initialise the SceneManager with the given Stage and starting view. It will automatically
   * create the scene containing the starting view and show it.
   *
   * @param stage The stage to initialise the scene on
   * @param startingView The initial view to start the application using
   * @throws UnsupportedOperationException If the SceneManager has already been initialised
   */
  public void initialise(final Stage stage, final View startingView) {
    // Prevent you from trying to initialise the SceneManager twice
    if (this.scene != null) {
      throw new UnsupportedOperationException("The scene manager has already been initialised");
    }

    this.stage = stage;
    this.stage.setOnCloseRequest(e -> this.onTerminate());
    // Make sure the starting view is loaded if lazy loading has been enabled
    if (!this.views.containsKey(startingView)) {
      this.loadView(startingView);
    }

    final Pair<Parent, Object> pair = this.views.get(startingView);
    this.invokeLoadListener(pair.getValue());
    this.scene = new Scene(pair.getKey(), 750, 550);
    stage.setScene(this.scene);
    stage.show();
  }

  /**
   * Load the FXML file for the given view by getting the {@code /fxml/<view>.fxml} file and caching
   * it for future use. If there is an exception then it will be caught and logged.
   *
   * @param view The view to load the FXML file for
   * @return If the view was successfully loaded
   */
  private boolean loadView(final View view) {
    try {
      final FXMLLoader fxmlLoader =
          new FXMLLoader(App.class.getResource("/fxml/" + view.getFxml() + ".fxml"));

      // Use custom controller factory to support dependency injection within the controller.
      fxmlLoader.setControllerFactory(this.instanceFactory);
      final Parent parent = fxmlLoader.load();
      final Object controller = fxmlLoader.getController();

      // Cache the view so that we only have to load it once
      this.views.put(view, new Pair<>(parent, controller));
      return true;
    } catch (final IOException e) {
      this.logger.error("There was an error loading the view " + view.getFxml(), e);
      return false;
    }
  }

  /**
   * Switches the scene to the given view. If it has not currently been loaded then it will attempt
   * to load the fxml file. If there is an error while trying to load it then it will remain on the
   * current view.
   *
   * @param view The view to switch to
   */
  public void switchToView(final View view) {
    if (!this.views.containsKey(view)) {
      if (!this.loadView(view)) {
        // Something went wrong loading the new view so don't switch to it
        return;
      }
    }
    final Pair<Parent, Object> pair = this.views.get(view);
    this.invokeLoadListener(pair.getValue());
    this.scene.setRoot(pair.getKey());
  }

  /**
   * Checks if the Controller is an instance of {@link LoadListener} and if so invokes the {@code
   * onLoad} callback.
   *
   * @param controller The controller to try and invoke the onLoad callback for
   */
  private void invokeLoadListener(final Object controller) {
    if (controller instanceof LoadListener loadListener) {
      loadListener.onLoad();
    }
  }

  /**
   * When a close request has been received it will invoke the {@code onTermination} callback on all
   * controllers that are instances of {@link TerminationListener}.
   */
  private void onTerminate() {
    for (final Pair<Parent, Object> pair : this.views.values()) {
      if (pair.getValue() instanceof TerminationListener terminationListener) {
        terminationListener.onTerminate();
      }
    }
    Platform.exit();
  }

  /**
   * Get the reference to the primary stage.
   *
   * @return The primary stage
   */
  public Stage getStage() {
    return this.stage;
  }
}
