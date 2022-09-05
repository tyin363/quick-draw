package nz.ac.auckland.se206.util;

import java.util.function.BiConsumer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public enum BrushType {
  PEN(BrushType::pen),
  ERASER(BrushType::eraser),
  DISABLED(BrushType::disabled);

  /**
   * Applies the pen brush to the given graphics context. This is only temporary due to the
   * style-marker not liking lambdas within enums, apparently.
   *
   * @param e The mouse event to use for the brush
   * @param graphic The graphics context to apply the brush to
   */
  private static void pen(final MouseEvent e, final GraphicsContext graphic) {
    final double x = e.getX() - Config.BRUSH_SIZE / 2;
    final double y = e.getY() - Config.BRUSH_SIZE / 2;
    // This is the colour of the brush.
    graphic.setFill(Color.BLACK);
    graphic.fillOval(x, y, Config.BRUSH_SIZE, Config.BRUSH_SIZE);
  }

  /**
   * Applies the eraser brush to the given graphics context. This is only temporary due to the
   * style-marker not liking lambdas within enums, apparently.
   *
   * @param e The mouse event to use for the brush
   * @param graphic The graphics context to apply the brush to
   */
  private static void eraser(final MouseEvent e, final GraphicsContext graphic) {
    final double x = e.getX() - Config.BRUSH_SIZE;
    final double y = e.getY() - Config.BRUSH_SIZE;
    graphic.clearRect(x, y, Config.BRUSH_SIZE * 2, Config.BRUSH_SIZE * 2);
  }

  /**
   * Disables the brush. It does nothing when you try and use it. This is only temporary due to the
   * style-marker not liking lambdas within enums, apparently.
   *
   * @param e The mouse event to use for the brush
   * @param graphic The graphics context to apply the brush to
   */
  private static void disabled(final MouseEvent e, final GraphicsContext graphic) {
    // Do nothing
  }

  private final BiConsumer<MouseEvent, GraphicsContext> graphicsContextConsumer;

  /**
   * Constructs a new brush type. Each brush type takes in a BiConsumer which describes how the
   * brush should modify the graphics context.
   *
   * @param graphicsContextConsumer The BiConsumer that implements the brush behaviour
   */
  BrushType(final BiConsumer<MouseEvent, GraphicsContext> graphicsContextConsumer) {
    this.graphicsContextConsumer = graphicsContextConsumer;
  }

  /**
   * Apply this brush to the graphics context.
   *
   * @param event The mouse event
   * @param graphicsContext The graphics context to apply the brush to
   */
  public void apply(final MouseEvent event, final GraphicsContext graphicsContext) {
    this.graphicsContextConsumer.accept(event, graphicsContext);
  }
}
