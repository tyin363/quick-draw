package nz.ac.auckland.se206.statemachine.states;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.components.canvas.ZenPenOptions;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;

@Singleton
public class ZenModeState extends CanvasState implements EnableListener, TerminationListener {

  private ZenPenOptions zenPenOptions;
  private Node oldToolContainerContent;
  private Timeline timer;
  private int elapsedSeconds;

  /**
   * Creates a new ZenModeState which handles the stateful logic of the canvas when the zen mode has
   * been selected.
   *
   * @param canvasController The canvas controller instance
   */
  @Inject
  public ZenModeState(final CanvasController canvasController) {
    super(canvasController);
  }

  /** Switch the UI elements to the Zen Mode UI. */
  @Override
  public void onEnter() {
    // Swap out the old tool container content with the zen mode version
    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.zenPenOptions);
    this.canvasController.getGameOverActionsContainer().setVisible(true);
  }

  /** Place the default canvas content back when we leave this state. */
  @Override
  public void onExit() {
    // Put the old tool container content back
    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.oldToolContainerContent);
    this.canvasController.getGameOverActionsContainer().setVisible(false);
  }

  /** Reset the number of seconds that have elapsed. */
  @Override
  public void onLoad() {
    this.elapsedSeconds = 0;
    this.displayCurrentElapsedTime();
    this.timer.playFromStart();
  }

  /** When the user leaves the canvas scene, stop the timer. */
  @Override
  public void onLeave() {
    this.timer.stop();
    this.canvasController.getPredictionHandler().stopPredicting();
  }

  /**
   * When this state is first created we want to construct the UI which will replace the default
   * canvas elements and get references to the elements we will be replacing so that they can be put
   * back when this state is exited.
   */
  @Override
  public void onEnable() {
    // Reuse the same instances that already exist so that the on click functions remain unaffected.
    final Pane[] tools = {
      this.canvasController.getEraserPane(),
      this.canvasController.getPenPane(),
      this.canvasController.getClearPane(),
    };

    this.zenPenOptions =
        new ZenPenOptions(
            this.canvasController.getPenColour(), this.canvasController::setPenColour, tools);

    // Save the old tool container content so that it can be restored when the state is exited
    final VBox toolContainer = this.canvasController.getToolContainer();
    this.oldToolContainerContent = toolContainer.getChildren().get(0);
    this.createTimer();
  }

  /**
   * Constructs the timer which will be used to count the number of seconds that have elapsed since
   * the user started.
   */
  private void createTimer() {
    // Create a timeline that increments the elapsed number of seconds by 1 every second
    this.timer =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  this.elapsedSeconds++;
                  this.displayCurrentElapsedTime();
                }));
    this.timer.setCycleCount(Timeline.INDEFINITE);
  }

  /** Displays the current elapsed time in the UI. */
  private void displayCurrentElapsedTime() {
    this.canvasController
        .getMainLabel()
        .setText("%02d:%02d".formatted(this.elapsedSeconds / 60, this.elapsedSeconds % 60));
  }

  /** When the application is terminated, make sure to stop the timer. */
  @Override
  public void onTerminate() {
    this.timer.stop();
  }
}
