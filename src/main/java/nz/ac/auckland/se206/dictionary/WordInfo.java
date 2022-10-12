package nz.ac.auckland.se206.dictionary;

import java.util.List;

public class WordInfo {

  private String word;
  private List<WordEntry> entries;

  WordInfo(String word, List<WordEntry> entries) {
    this.word = word;
    this.entries = entries;
  }

  public String getWord() {
    return word;
  }

  public List<WordEntry> getWordEntries() {
    return entries;
  }

  public int getNumberOfEntries() {
    return entries.size();
  }
}
