package nz.ac.auckland.se206.components.canvas;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ZenPenOptions extends VBox {

  private static final Color[] COLOURS =
      new Color[] {
        Color.BLACK,
        Color.WHITE,
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.ORANGE,
        Color.PURPLE,
        Color.BROWN,
        Color.GRAY,
        Color.web("#FFC0CB"),
        Color.web("#FFA500")
      };

  private static final int COLOURS_PER_ROW = 3;

  private final Consumer<Color> handleSwitchColour;
  private final Pane currentColour;

  /**
   * Constructs a pen options component for the zen mode canvas.
   *
   * @param initialColour The initial colour to use for the selected colour indicator
   * @param handleSwitchColour The callback for when the user switches the colour of the pen
   * @param tools The tool icons to render
   */
  public ZenPenOptions(
      final Color initialColour, final Consumer<Color> handleSwitchColour, final Pane... tools) {

    this.handleSwitchColour = handleSwitchColour;
    // Keep a reference to this so that the background colour can be as the user switches the pen
    // colour.
    this.currentColour = this.renderCurrentColour(initialColour);

    this.getStyleClass().addAll("bg-lightgray-100", "icon-btn-row", "pen-options");

    // Render and add all the subcomponents
    this.getChildren()
        .addAll(
            this.renderToolContainer(tools),
            new Separator(),
            this.renderColourPalette(),
            this.currentColour);
  }

  /**
   * Renders a horizontal container for the tool icons.
   *
   * @param tools The tool icons to render
   * @return The rendered tool container
   */
  private HBox renderToolContainer(final Pane... tools) {
    final HBox toolContainer = new HBox();
    toolContainer.getChildren().addAll(tools);
    return toolContainer;
  }

  /**
   * Renders the possible colours that can be selected to draw with for the pen.
   *
   * @return The rendered colour palette
   */
  private GridPane renderColourPalette() {
    final GridPane colourPalette = new GridPane();
    colourPalette.getStyleClass().add("colour-palette");
    colourPalette.setHgap(5);
    colourPalette.setVgap(5);

    // Add all the colours to the colour palette.
    for (int i = 0; i < COLOURS.length; i++) {
      // Calculate the position in the grid for this colour given a fixed number of colours per row
      final int row = i / COLOURS_PER_ROW;
      final int column = i % COLOURS_PER_ROW;
      final Color colour = COLOURS[i];
      final Pane colourOption = new Pane();
      System.out.println(colour);
      this.setBackground(colourOption, colour);

      // Anytime one of these colour options are clicked we want to switch the current colour
      // displayed and call the callback so this can be handled outside this component.
      colourOption.setOnMouseClicked(
          event -> {
            this.handleSwitchColour.accept(colour);
            this.setBackground(this.currentColour, colour);
          });

      colourPalette.add(colourOption, column, row);
    }

    return colourPalette;
  }

  /**
   * Renders the current colour indicator for what colour the pen is currently set to.
   *
   * @param initialColour The initial colour to use for the selected colour indicator
   * @return The rendered current colour indicator
   */
  private Pane renderCurrentColour(final Color initialColour) {
    final Pane currentColour = new Pane();
    currentColour.getStyleClass().add("current-colour");
    // Initially, use the specified colour
    this.setBackground(currentColour, initialColour);
    return currentColour;
  }

  /**
   * Set the background colour for a specific node to the specified colour.
   *
   * @param node The node to set the background colour for
   * @param colour The colour to set the background to
   */
  private void setBackground(final Node node, final Color colour) {
    node.setStyle("-fx-background-color: %s;".formatted(colour));
  }
}
