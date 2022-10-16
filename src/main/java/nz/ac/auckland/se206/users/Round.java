package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import nz.ac.auckland.se206.users.Round.Mode;

public record Round(
    String word, int timeTaken, @JsonProperty("wasGuessed") boolean wasGuessed, Mode mode) {

  public enum Mode {
    NORMAL,
    HIDDEN
  }
}
