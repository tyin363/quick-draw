package nz.ac.auckland.se206.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.users.User;
import nz.ac.auckland.se206.users.UserService;
import org.slf4j.Logger;

@Singleton
public class Config {

  @Inject private UserService userService;

  private final Logger logger;
  private final File userDataFile = new File("UserData");
  private Color highlight;

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

    // Create the UserData directory if it doesn't exist
    if (!this.userDataFile.exists()) {
      this.userDataFile.mkdir();
    }
  }

  /**
   * Automatically loads the defined {@link ColourType}s from the styles.css file and loads them
   * into the {@code colourMap}.
   *
   * @throws IOException If the styles.css file cannot be found or something goes wrong reading it
   */
  private void loadColours() throws IOException {
    final InputStream inputStream = Config.class.getResourceAsStream("/css/colours.css");
    if (inputStream == null) {
      throw new IOException("Could not find /css/colours.css file in resources");
    }
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String line = reader.readLine();
    // Get to the colour definitions
    while (line != null) {
      if (line.startsWith("* {")) {
        break;
      }
      line = reader.readLine();
    }
    // Keep reading lines until we've read all the lines
    while (line != null) {
      final String[] split = line.split(":");
      // If it's length is not 2, then it's not a valid property.
      if (split.length == 2) {
        final String key = split[0].trim();
        if (key.equals("-fx-highlight")) {
          // Remove any surrounding whitespace and the ending semicolon
          String colour = split[1].trim();
          colour = colour.substring(1, colour.length() - 1);

          // Color.web supports the hex, rgb and rgba formats.
          this.highlight = Color.web(colour);
          break;
        }
      }
      line = reader.readLine();
    }
    if (this.highlight == null) {
      this.logger.error("Missing required highlight colours in colour.css");
    } else {
      this.logger.info("Colours successfully extracted from colour.css");
    }
    reader.close();
  }

  /**
   * Retrieves the highlight colour.
   *
   * @return The highlight colour
   */
  public Color getHighlight() {
    return this.highlight;
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
    User currentUser = this.userService.getCurrentUser();
    if (currentUser.getGameSettings().getTime() == 60) {
      return 60;
    } else if (currentUser.getGameSettings().getTime() == 45) {
      return 45;
    } else if (currentUser.getGameSettings().getTime() == 30) {
      return 30;
    } else {
      return 15;
    }
  }

  /**
   * Retrieves where the placement of the target word prediction must be within for what the user to
   * win.
   *
   * @return The winning placement
   */
  public int getWinPlacement() {
    User currentUser = this.userService.getCurrentUser();
    if (currentUser.getGameSettings().getAccuracy().contentEquals("Easy")) {
      return 3;
    } else if (currentUser.getGameSettings().getAccuracy().contentEquals("Medium")) {
      return 2;
    } else {
      return 1;
    }
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

  /**
   * Retrieves the maximum number of users that can be created.
   *
   * @return The maximum number of users that can be created
   */
  public int getMaxUserCount() {
    return 6;
  }
}
