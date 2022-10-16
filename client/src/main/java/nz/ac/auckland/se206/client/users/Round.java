package nz.ac.auckland.se206.client.users;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Round(
    String word, int timeTaken, @JsonProperty("wasGuessed") boolean wasGuessed, Mode mode) {

  public enum Mode {
    NORMAL,
    HIDDEN
  }
}
