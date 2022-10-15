package nz.ac.auckland.se206.controllers.scenemanager;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import org.slf4j.Logger;

@Singleton
public class SceneManager {

  public record ViewControllers(Parent parent, Set<Object> controllers) {}

  private final Map<View, ViewControllers> views = new EnumMap<>(View.class);

  private final List<Object> controllers = new LinkedList<>();
  private final Stage stage;
  private Scene scene;
  private View previousView;
  private View currentView;

  @Inject private Logger logger;
  @Inject private ApplicationContext applicationContext;

  public SceneManager(final Stage stage) {
    this.stage = stage;
    this.stage.setTitle("Quick, Draw! - Team 9");
  }

  /**
   * Initialise the starting view by creating a scene containing the starting view and showing it.
   * Lazy loading the views' means they'll be loaded only when they're first attempted to be loaded,
   * whereas if you're not lazy loading then they'll be all preloaded on startup.
   *
   * @param lazyLoading Whether to lazy load the views
   * @param startingView The initial view to start the application using
   */
  protected void initialise(final boolean lazyLoading, final View startingView) {
    // Prevent you from trying to initialise the SceneManager twice
    if (this.scene != null) {
      throw new UnsupportedOperationException("The scene manager has already been initialised");
    }

    if (!lazyLoading) { // If we're not lazy loading, preload all the views
      for (final View view : View.values()) {
        this.loadView(view);
      }
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
  private boolean loadView(final View view) {
    try {
      final FXMLLoader fxmlLoader =
          new FXMLLoader(App.class.getResource("/fxml/" + view.getFxml() + ".fxml"));

      // Use custom controller factory to support dependency injection within the controller.
      fxmlLoader.setControllerFactory(this.applicationContext);
      final Parent parent = fxmlLoader.load();
      this.views.put(view, new ViewControllers(parent, new HashSet<>()));

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

    // Keep track of the current and previous view, so we can switch back to it
    this.previousView = this.currentView;
    this.currentView = view;

    final ViewControllers viewControllers = this.views.get(view);
    this.invokeLoadListener(viewControllers);
    this.scene.setRoot(viewControllers.parent());
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
  public View getPreviousView() {
    return this.previousView;
  }
}
