package nz.ac.auckland.se206.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import org.slf4j.Logger;

@Singleton
public class Config {

  private final Logger logger;
  private final Map<ColourType, Color> colourMap = new EnumMap<>(ColourType.class);
  private final File userDataFile = new File("UserData");

  /**
   * Constructs a new config instance with the given logger and automatically begins parsing the
   * colours from the css file.
   *
   * @param logger The logger to use for logging
   * @throws IOException If there is an error reading the css file
   */
  @Inject
  public Config(final Logger logger) throws IOException {
    // Inject the logger into the constructor so that it's available while loading the colours.
    this.logger = logger;
    this.loadColours();
  }

  /**
   * Automatically loads the defined {@link ColourType}s from the styles.css file and loads them
   * into the {@code colourMap}.
   *
   * @throws IOException If the styles.css file cannot be found or something goes wrong reading it
   */
  private void loadColours() throws IOException {
    final URL url = Config.class.getResource("/css/styles.css");
    if (url == null) {
      throw new IOException("Could not find /css/styles.css file in resources");
    }
    final BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
    String line = reader.readLine();
    // Get to the colour definitions
    while (line != null) {
      if (line.startsWith("* {")) {
        break;
      }
      line = reader.readLine();
    }
    int requiredColours = ColourType.values().length;

    // Keep reading lines until we've read all the colours
    while (requiredColours != 0 && line != null) {
      if (line.contains(":")) {
        final String[] split = line.split(":");
        // If it's length is not 2, then it's not a valid property.
        if (split.length == 2) {
          final String key = split[0].trim();

          // Attempt to parse the key to see if it's a colour type
          final Optional<ColourType> colourType = ColourType.fromKey(key);
          if (colourType.isPresent()) {
            // Remove any surrounding whitespace and the ending semicolon
            String colour = split[1].trim();
            colour = colour.substring(1, colour.length() - 1);

            // Color.web supports the hex, rgb and rgba formats.
            this.colourMap.put(colourType.get(), Color.web(colour));
            requiredColours--;
          }
        }
      }
      line = reader.readLine();
    }
    if (requiredColours != 0) {
      this.logger.error("Missing required primary colours in styles.css");
    } else {
      this.logger.info("Colours successfully extracted from styles.css");
    }
    reader.close();
  }

  /**
   * Retrieves the primary main colour.
   *
   * @return The primary main colour
   */
  public Color getPrimaryMain() {
    return this.colourMap.get(ColourType.PRIMARY_MAIN);
  }

  /**
   * Retrieves the primary light colour.
   *
   * @return The primary light colour
   */
  public Color getPrimaryLight() {
    return this.colourMap.get(ColourType.PRIMARY_LIGHT);
  }

  /**
   * Retrieves the primary dark colour.
   *
   * @return The primary dark colour
   */
  public Color getPrimaryDark() {
    return this.colourMap.get(ColourType.PRIMARY_DARK);
  }

  /**
   * Retrieves the number of predictions that should be made when guessing what the drawing is.
   *
   * @return The number of predictions to make
   */
  public int getNumberOfPredictions() {
    return 10;
  }

  /**
   * Retrieves the number of seconds that the user has to complete the drawing.
   *
   * @return The number of seconds to complete the drawing
   */
  public int getDrawingTimeSeconds() {
    return 60;
  }

  /**
   * Retrieves where the placement of the target word prediction must be within for what the user to
   * win.
   *
   * @return The winning placement
   */
  public int getWinPlacement() {
    return 3;
  }

  /**
   * Retrieves the name of the file that contains all the possible target words and their respective
   * difficulties.
   *
   * @return The name of the file that contains the target words
   */
  public String getWordDifficultiesFileName() {
    return "category_difficulty.csv";
  }

  /**
   * Retrieves the file where all the user data will be persisted.
   *
   * @return The file where all the user data will be persisted
   */
  public File getUserDataFile() {
    return this.userDataFile;
  }

  /**
   * Retrieves the size of the brush.
   *
   * @return The size of the brush
   */
  public double getBrushSize() {
    return 6;
  }
}
