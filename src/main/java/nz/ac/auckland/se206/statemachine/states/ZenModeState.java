package nz.ac.auckland.se206.statemachine.states;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.components.canvas.ZenPenOptions;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;

@Singleton
public class ZenModeState extends CanvasState implements EnableListener {

  @Inject private CanvasController canvasController;

  private ZenPenOptions zenPenOptions;
  private Node oldToolContainerContent;

  /** Switch the UI elements to the Zen Mode UI. */
  @Override
  public void onEnter() {
    super.onEnter();

    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.zenPenOptions);
  }

  /** Place the default canvas content back when we leave this state. */
  @Override
  public void onExit() {
    // Put the old tool container content back
    final VBox toolContainer = this.canvasController.getToolContainer();
    toolContainer.getChildren().clear();
    toolContainer.getChildren().add(this.oldToolContainerContent);
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
  }
}
