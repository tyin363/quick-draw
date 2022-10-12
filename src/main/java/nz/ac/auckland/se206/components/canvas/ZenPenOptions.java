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

  public ZenPenOptions(final Consumer<Color> handleSwitchColour, final Pane... tools) {
    this.handleSwitchColour = handleSwitchColour;
    this.currentColour = this.renderCurrentColour();

    this.getStyleClass().addAll("bg-lightgray-100", "icon-btn-row", "pen-options");
    final HBox toolContainer = new HBox();
    toolContainer.getChildren().addAll(tools);

    this.getChildren()
        .addAll(toolContainer, new Separator(), this.renderColourPalette(), this.currentColour);
  }

  private GridPane renderColourPalette() {
    final GridPane colourPalette = new GridPane();
    colourPalette.getStyleClass().add("colour-palette");
    colourPalette.setHgap(5);
    colourPalette.setVgap(5);

    for (int i = 0; i < COLOURS.length; i++) {
      final int row = i / COLOURS_PER_ROW;
      final int column = i % COLOURS_PER_ROW;
      final Color colour = COLOURS[i];
      final Pane colourOption = new Pane();
      System.out.println(colour);
      this.setBackground(colourOption, colour);

      colourOption.setOnMouseClicked(
          event -> {
            this.handleSwitchColour.accept(colour);
            this.setBackground(this.currentColour, colour);
          });

      colourPalette.add(colourOption, column, row);
    }

    return colourPalette;
  }

  private Pane renderCurrentColour() {
    final Pane currentColour = new Pane();
    currentColour.getStyleClass().add("current-colour");
    this.setBackground(currentColour, COLOURS[0]);
    return currentColour;
  }

  private void setBackground(final Node node, final Color colour) {
    node.setStyle("-fx-background-color: %s;".formatted(colour));
  }
}
