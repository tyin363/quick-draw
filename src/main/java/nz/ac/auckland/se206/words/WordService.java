package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.exceptions.MissingResourceException;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Config;
import org.slf4j.Logger;

@Singleton
public class WordService {

  @Inject private UserService userService;

  private final Map<Difficulty, List<String>> wordMapping;
  private final Random random = new Random();

  private final Logger logger;
  private final Config config;

  private String targetWord;

  /**
   * Creates a new instance of the word service and instantly creates a new word mapping from the
   * CSV file. As this requires the config and logger to be injected, they have been passed through
   * the constructor.
   */
  @Inject
  public WordService(final Logger logger, final Config config) {
    this.logger = logger;
    this.config = config;
    this.wordMapping = Collections.unmodifiableMap(this.createWordMapping());
  }

  /**
   * Constructs a mapping of all the words in the CSV file to their respective difficulties.
   *
   * @return The word mapping that has been created
   * @throws MissingResourceException If the CSV file is missing
   */
  private Map<Difficulty, List<String>> createWordMapping() {
    final Map<Difficulty, List<String>> wordMapping = new ConcurrentHashMap<>();
    final URL url = WordService.class.getResource("/" + this.config.getWordDifficultiesFileName());
    // If we can't load the words from the CSV file then the game can't be played
    if (url == null) {
      throw new MissingResourceException(
          "Could not retrieve %s resource".formatted(this.config.getWordDifficultiesFileName()));
    }
    try {
      final File file = new File(url.toURI());
      try (final CSVReader reader = new CSVReader(new FileReader(file))) {
        for (final String[] line : reader.readAll()) {
          final Difficulty difficulty = Difficulty.fromAlias(line[1]);
          // If the difficulty is not already in the map, create a new Arraylist for it.
          wordMapping.computeIfAbsent(difficulty, key -> new ArrayList<>()).add(line[0]);
        }
      }
    } catch (final URISyntaxException | IOException | CsvException e) {
      this.logger.error("There was an error reading the CSV file", e);
    }
    return wordMapping;
  }

  /**
   * Updates the target to word to a random word of the given difficulty.
   *
   * @param difficulty The difficulty of the word to select from
   */
  public void selectRandomTarget(final Difficulty difficulty) {
    final List<String> words = this.wordMapping.get(difficulty);

    // Remove user's past words for the new word selection
    words.removeAll(this.userService.getCurrentUser().getPastWords());

    // If the new world selection is empty get all the words again
    if (words.size() == 0) {
      this.wordMapping.get(difficulty);
    }

    this.targetWord = words.get(this.random.nextInt(words.size()));
  }

  /**
   * Gets the currently selected target word
   *
   * @return The target word
   */
  public String getTargetWord() {
    return this.targetWord;
  }

  /**
   * Gets an unmodifiable reference to the word mapping.
   *
   * @return The word mapping
   */
  public Map<Difficulty, List<String>> getWordMapping() {
    return this.wordMapping;
  }
}
