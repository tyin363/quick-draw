package nz.ac.auckland.se206.dictionary;

import java.util.List;

public class WordEntry {

  private String partOfSpeech;
  private List<String> definitions;

  public WordEntry(String partOfSpeech, List<String> definitions) {
    this.partOfSpeech = partOfSpeech;
    this.definitions = definitions;
  }

  public String getPartOfSpeech() {
    return partOfSpeech;
  }

  public List<String> getDefinitions() {
    return definitions;
  }
}
