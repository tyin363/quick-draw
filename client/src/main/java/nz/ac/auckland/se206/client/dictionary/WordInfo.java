package nz.ac.auckland.se206.client.dictionary;

import java.util.List;

public class WordInfo {

  private String word;
  private List<WordEntry> entries;

  public WordInfo(String word, List<WordEntry> entries) {
    this.word = word;
    this.entries = entries;
  }

  /**
   * This method retrieves the required word
   *
   * @return The word
   */
  public String getWord() {
    return word;
  }

  /**
   * This method retrieves the required word entries
   *
   * @return The word entries
   */
  public List<WordEntry> getWordEntries() {
    return entries;
  }

  /**
   * This method retrieves the size of the number of entries
   *
   * @return The number of entries
   */
  public int getNumberOfEntries() {
    return entries.size();
  }
}
