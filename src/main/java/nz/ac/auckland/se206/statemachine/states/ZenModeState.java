package nz.ac.auckland.se206.statemachine.states;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.components.canvas.ZenPenOptions;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;
import nz.ac.auckland.se206.util.SoundEffect;

@Singleton(injectSuper = true)
public class ZenModeState extends CanvasState implements EnableListener {
  @Inject private SoundEffect soundEffect;
  private ZenPenOptions zenPenOptions;
  private VBox oldToolContainerContent;

  /** When the zen mode canvas state is loaded play its exclusive music */
  @Override
  public void onLoad() {

    // Set Background music
    this.soundEffect.terminateBackgroundMusic();
    this.soundEffect.playDefaultCanvasMusic();
  }

  /** Switch the UI elements to the Zen Mode UI. */
  @Override
  public void onEnter() {

    // Swap out the old tool container content with the zen mode version
    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.zenPenOptions);
    this.canvasController.getGameOverActionsContainer().setVisible(true);
    this.canvasController.getMainLabel().setText("Zen Mode");

    // Reuse the same instances that already exist so that the on click functions remain unaffected.
    this.zenPenOptions.setTools(
        this.canvasController.getEraserPane(),
        this.canvasController.getPenPane(),
        this.canvasController.getClearPane());
  }

  /** Place the default canvas content back when we leave this state. */
  @Override
  public void onExit() {
    // Put the old tool container content back
    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.oldToolContainerContent);
    // When you add the tools to the zen pen options, it removes them from the old tool container,
    // so we need to manually add them back :(
    this.oldToolContainerContent
        .getChildren()
        .addAll(
            this.canvasController.getEraserPane(),
            this.canvasController.getPenPane(),
            this.canvasController.getClearPane());
    this.canvasController.setPenColour(Color.BLACK);
  }

  /** When the user leaves the canvas scene, stop the timer. */
  @Override
  public void onLeave() {
    this.canvasController.getPredictionHandler().stopPredicting();
  }

  /**
   * When this state is first created we want to construct the UI which will replace the default
   * canvas elements and get references to the elements we will be replacing so that they can be put
   * back when this state is exited.
   */
  @Override
  public void onEnable() {
    this.zenPenOptions =
        new ZenPenOptions(
            this.canvasController.getPenColour(), this.canvasController::setPenColour);

    // Save the old tool container content so that it can be restored when the state is exited
    final VBox toolContainer = this.canvasController.getToolContainer();
    this.oldToolContainerContent = (VBox) toolContainer.getChildren().get(0);
  }
}
