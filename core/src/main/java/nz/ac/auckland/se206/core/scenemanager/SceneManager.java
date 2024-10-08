package nz.ac.auckland.se206.core.scenemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.di.ApplicationContext;
import nz.ac.auckland.se206.core.listeners.LoadListener;
import org.slf4j.Logger;

@Singleton
public class SceneManager {

  private record ViewControllers(Parent parent, List<Object> controllers) {}

  private final Map<FxmlView, ViewControllers> views = new HashMap<>();

  private final List<Object> controllers = new LinkedList<>();
  private final Stage stage;
  private Scene scene;
  private FxmlView previousView;
  private FxmlView currentView;

  @Inject private Logger logger;
  @Inject private ApplicationContext applicationContext;

  public SceneManager(final Stage stage) {
    this.stage = stage;
    this.stage.setTitle("Quick, Draw! - Team 9");
  }

  /**
   * Initialise the starting view by creating a scene containing the starting view and showing it.
   * You can specify all the views you also want to preload. Otherwise, the view will be loaded when
   * it is switched to.
   *
   * @param startingView The initial view to start the application using
   * @param preLoadViews All the views to preload
   */
  public void initialise(final FxmlView startingView, final FxmlView... preLoadViews) {

    // Prevent you from trying to initialise the SceneManager twice
    if (this.scene != null) {
      throw new UnsupportedOperationException("The scene manager has already been initialised");
    }

    for (final FxmlView view : preLoadViews) {
      this.loadView(view);
    }

    // Make sure the starting view is loaded if lazy loading has been enabled
    if (!this.views.containsKey(startingView)) {
      this.loadView(startingView);
    }

    this.currentView = startingView;
    final ViewControllers viewControllers = this.views.get(startingView);
    this.invokeLoadListener(viewControllers);
    this.scene = new Scene(viewControllers.parent(), 1150, 800);
    this.stage.setScene(this.scene);
    this.stage.show();
  }

  /**
   * Load the FXML file for the given view by getting the {@code /fxml/<view>.fxml} file and caching
   * it for future use. If there is an exception then it will be caught and logged.
   *
   * @param view The view to load the FXML file for
   * @return If the view was successfully loaded
   */
  private boolean loadView(final FxmlView view) {
    try {
      final FXMLLoader fxmlLoader =
          new FXMLLoader(view.getClass().getResource("/fxml/" + view.getFxml() + ".fxml"));

      // Use custom controller factory to support dependency injection within the controller.
      fxmlLoader.setControllerFactory(this.applicationContext);
      final Parent parent = fxmlLoader.load();
      this.views.put(view, new ViewControllers(parent, new ArrayList<>()));

      final Object controller = fxmlLoader.getController();
      // Cache the view so that we only have to load it once.
      this.views.get(view).controllers().add(controller);

      // Add any additional controllers that were created while loading this view
      this.views.get(view).controllers().addAll(this.controllers);
      this.controllers.clear();

      return true;
    } catch (final IOException e) {
      this.logger.error("There was an error loading the view " + view.getFxml(), e);
      return false;
    }
  }

  /**
   * Switches the scene to the given view. If it has not currently been loaded then it will attempt
   * to load the fxml file. If there is an error while trying to load it then it will remain on the
   * current view. The scenes will all have the same base background music except the canvas scene.
   *
   * @param view The view to switch to
   */
  public void switchToView(final FxmlView view) {
    if (!this.views.containsKey(view)) {
      if (!this.loadView(view)) {
        // Something went wrong loading the new view so don't switch to it
        return;
      }
    }

    // Keep track of the current and previous view, so we can switch back to it
    this.previousView = this.currentView;
    this.currentView = view;

    final ViewControllers viewControllers = this.views.get(view);
    this.invokeLoadListener(viewControllers);
    this.scene.setRoot(viewControllers.parent());
  }

  /**
   * Retrieves the first sub-controller for the currently loading view. This can only be called in
   * the initialize method of a controller.
   *
   * @param type The class of the sub-controller to find
   * @param <T> The type of the sub-controller
   * @return The sub-controller if it exists, otherwise null
   */
  public <T> T getSubController(final Class<T> type) {
    // Iterate through all the sub-controllers which have just been loaded.
    for (final Object controller : this.controllers) {
      if (type.isInstance(controller)) {
        return type.cast(controller);
      }
    }
    return null;
  }

  /**
   * Manually register a controller to be added to the currently loading view.
   *
   * @param controller The controller to register
   */
  public void registerController(final Object controller) {
    this.controllers.add(controller);
  }

  /**
   * Checks if any of the controllers are an instance of {@link LoadListener} and if so invokes the
   * {@code onLoad} callback.
   *
   * @param viewControllers The controllers to try and invoke the onLoad callback for
   */
  private void invokeLoadListener(final ViewControllers viewControllers) {
    for (final Object controller : viewControllers.controllers()) {
      if (controller instanceof LoadListener loadListener) {
        loadListener.onLoad();
      }
    }
  }

  /**
   * Switches the scene back to the previous view. If there is no previous view then it will do
   * nothing.
   */
  public void switchToPreviousView() {
    if (this.previousView == null) {
      return;
    }

    this.switchToView(this.previousView);
  }

  /**
   * Get the reference to the primary stage.
   *
   * @return The primary stage
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * Get the previous view of the current screen.
   *
   * @return The previous view
   */
  public FxmlView getPreviousView() {
    return this.previousView;
  }
}
