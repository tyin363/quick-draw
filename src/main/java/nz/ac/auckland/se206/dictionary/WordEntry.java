package nz.ac.auckland.se206.dictionary;

import java.util.List;

public class WordEntry {

  private String partOfSpeech;
  private List<String> definitions;

  public WordEntry(String partOfSpeech, List<String> definitions) {
    this.partOfSpeech = partOfSpeech;
    this.definitions = definitions;
  }

  /**
   * This method retrieves the part of speech
   *
   * @return The part of speech
   */
  public String getPartOfSpeech() {
    return partOfSpeech;
  }

  /**
   * This method retrieves the definitions
   *
   * @return The definitions
   */
  public List<String> getDefinitions() {
    return definitions;
  }
}
