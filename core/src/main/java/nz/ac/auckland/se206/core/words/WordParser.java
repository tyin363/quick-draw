package nz.ac.auckland.se206.core.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WordParser {

  /**
   * Constructs a mapping of all the words in the CSV file to their respective difficulties.
   *
   * @return The word mapping that has been created
   * @throws IOException If the CSV file is missing
   */
  public static Map<Difficulty, List<String>> createWordMapping() throws IOException {
    final Map<Difficulty, List<String>> wordMapping = new ConcurrentHashMap<>();
    final URL url = WordParser.class.getResource("/category_difficulty.csv");
    // If we can't load the words from the CSV file then the game can't be played
    if (url == null) {
      throw new IOException("Could not retrieve category_difficulty.csv resource");
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
      throw new IOException("There was an error reading the CSV file", e);
    }
    return wordMapping;
  }
}
