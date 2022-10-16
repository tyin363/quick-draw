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
import nz.ac.auckland.se206.users.Round;
import nz.ac.auckland.se206.users.UserService;
import nz.ac.auckland.se206.util.Config;
import org.slf4j.Logger;

@Singleton
public class WordService {

  private final Map<Difficulty, List<String>> wordMapping;
  private final Random random = new Random();
  private final Logger logger;
  private final Config config;
  @Inject private UserService userService;
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
   * Updates the target to word to a random word of the given difficulty but does not let target be
   * a previously played word unless all words are played
   *
   * @param difficulty The difficulty of the word to select from
   */
  public void selectRandomTarget(final String difficulty) {
    List<String> words = this.getWordSelection(difficulty);
    // Remove user's past words from the new word selection
    for (final Round round : this.userService.getCurrentUser().getPastRounds()) {
      words.remove(round.word());
    }

    // If the new word selection is empty let playable words be any from the given difficulty
    if (words.size() == 0) {
      words = this.getWordSelection(difficulty);
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

  /**
   * Retrieves the selection of words depending on words difficulty setting
   *
   * @param difficulty
   * @return word selection
   */
  public List<String> getWordSelection(final String difficulty) {

    // Initialising word selection
    List<String> words = null;

    // Setting word selection depending on words difficulty setting
    if (difficulty.contains("Easy")) {
      words = this.wordMapping.get(Difficulty.EASY);
    } else if (difficulty.contains("Medium")) {
      words = this.wordMapping.get(Difficulty.EASY);
      words.addAll(this.wordMapping.get(Difficulty.MEDIUM));
    } else if (difficulty.contains("Hard")) {
      words = this.wordMapping.get(Difficulty.EASY);
      words.addAll(this.wordMapping.get(Difficulty.MEDIUM));
      words.addAll(this.wordMapping.get(Difficulty.HARD));
    } else {
      words = this.wordMapping.get(Difficulty.HARD);
    }
    return words;
  }
}
