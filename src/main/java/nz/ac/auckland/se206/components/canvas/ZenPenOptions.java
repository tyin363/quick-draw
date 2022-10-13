package nz.ac.auckland.se206.components.canvas;

import java.util.function.Consumer;
import javafx.geometry.Pos;
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
  private final HBox toolContainer;
  private Pane currentColour;

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
    this.toolContainer = this.renderToolContainer();

    this.getStyleClass().add("pen-options");

    // Render and add all the subcomponents
    this.getChildren()
        .addAll(this.toolContainer, new Separator(), this.renderColourPalette(initialColour));
  }

  /**
   * Sets the tools to be contained within the tool container.
   *
   * @param tools The tools to place in the container
   */
  public void setTools(final Pane... tools) {
    this.toolContainer.getChildren().clear();
    this.toolContainer.getChildren().addAll(tools);
  }

  /**
   * Renders a horizontal container for the tool icons.
   *
   * @return The rendered tool container
   */
  private HBox renderToolContainer() {
    return new HBox();
  }

  /**
   * Renders the possible colours that can be selected to draw with for the pen.
   *
   * @param initialColour The initial colour to use for the selected colour indicator
   * @return The rendered colour palette
   */
  private GridPane renderColourPalette(final Color initialColour) {
    final GridPane colourPalette = new GridPane();
    colourPalette.getStyleClass().add("colour-palette");
    colourPalette.setHgap(10);
    colourPalette.setVgap(5);
    colourPalette.setAlignment(Pos.CENTER);

    // Add all the colours to the colour palette.
    for (int i = 0; i < COLOURS.length; i++) {
      // Calculate the position in the grid for this colour given a fixed number of colours per row
      final int row = i / COLOURS_PER_ROW;
      final int column = i % COLOURS_PER_ROW;
      final Color colour = COLOURS[i];
      final Pane colourOption = new Pane();
      this.setBackground(colourOption, colour);

      if (colour.equals(initialColour)) {
        this.currentColour = colourOption;
        this.currentColour.getStyleClass().add("selected-colour");
      }

      // Anytime one of these colour options are clicked we want to switch the current colour
      // displayed and call the callback so this can be handled outside this component.
      colourOption.setOnMouseClicked(
          event -> {
            this.handleSwitchColour.accept(colour);
            this.currentColour.getStyleClass().remove("selected-colour");
            this.currentColour = colourOption;
            this.currentColour.getStyleClass().add("selected-colour");
          });

      colourPalette.add(colourOption, column, row);
    }

    return colourPalette;
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
