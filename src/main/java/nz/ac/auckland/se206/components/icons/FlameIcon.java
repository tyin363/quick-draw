package nz.ac.auckland.se206.components.icons;

import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class FlameIcon extends Region {

  private static final String PATH =
      """
          M153.6 29.9l16-21.3C173.6 3.2 180 0 186.7 0C198.4 0 208 9.6 208 21.3V43.5c0 13.1 5.4 25.7
           14.9 34.7L307.6 159C356.4 205.6 384 270.2 384 337.7C384 434 306 512 209.7 512H192C86 512 0
           426 0 320v-3.8c0-48.8 19.4-95.6 53.9-130.1l3.5-3.5c4.2-4.2 10-6.6 16-6.6C85.9 176 96 186.1
           96 198.6V288c0 35.3 28.7 64 64 64s64-28.7
           64-64v-3.9c0-18-7.2-35.3-19.9-48l-38.6-38.6c-24-24-37.5-56.7-37.5-90.7c0-27.7
           9-54.8 25.6-76.9z
          """
          .replaceAll("\n", "");

  public FlameIcon() {
    final SVGPath svgPath = new SVGPath();
    svgPath.setContent(PATH);

    this.setShape(svgPath);
    // Make sure to preserve the aspect ratio of the icon
    this.setPrefSize(15, 20);
    this.setMaxSize(15, 20);
  }
}
