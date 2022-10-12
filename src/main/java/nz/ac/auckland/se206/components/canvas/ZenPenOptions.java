package nz.ac.auckland.se206.components.canvas;

import java.util.function.Consumer;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ZenPenOptions extends VBox {

  private static final Color[] COLOURS =
      new Color[] {
        Color.BLACK,
        Color.web("#424549"), // Gray-25
        Color.web("#8e460f"), // Brown
        Color.RED,
        Color.ORANGE,
        Color.web("#ddc02e"), // Yellow
        Color.web("#1bad1e"), // Green
        Color.web("#a132c9"), // Purple
        Color.web("#FFC0CB"), // Pink
        Color.BLUE,
        Color.web("#5865F2"), // Blurple
        Color.web("#42a4e5") // Light blue
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
    Tooltip.install(this.currentColour, new Tooltip("The current pen colour"));

    this.getStyleClass().add("pen-options");

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

    for (int i = 0; i < COLOURS_PER_ROW; i++) {
      final ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100D / COLOURS_PER_ROW);
      colourPalette.getColumnConstraints().add(columnConstraints);
    }

    // Add all the colours to the colour palette.
    for (int i = 0; i < COLOURS.length; i++) {
      // Calculate the position in the grid for this colour given a fixed number of colours per row
      final int row = i / COLOURS_PER_ROW;
      final int column = i % COLOURS_PER_ROW;
      final Color colour = COLOURS[i];
      final Pane colourOption = new Pane();
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
    currentColour.getStyleClass().add("selected-colour-indicator");
    // Initially, use the specified colour
    this.setBackground(currentColour, initialColour);
    return currentColour;
  }

  /**
   * Set the background colour for a specific pane to the specified colour.
   *
   * @param pane The pane to set the background colour for
   * @param colour The colour to set the background to
   */
  private void setBackground(final Pane pane, final Color colour) {
    pane.setStyle("-fx-background-color: %s;".formatted(this.toHex(colour)));
  }

  /**
   * Converts a JavaFX colour to a hex string.
   *
   * @param colour The colour to convert
   * @return The hex string representation of the colour
   */
  private String toHex(final Color colour) {
    // By default, the colour values are in the range of 0-1. We need to multiply by 255 to get
    // the range of 0-255.
    return String.format(
        "#%02X%02X%02X",
        (int) (colour.getRed() * 255),
        (int) (colour.getGreen() * 255),
        (int) (colour.getBlue() * 255));
  }
}
