package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Round(
    String word, int timeTaken, @JsonProperty("wasGuessed") boolean wasGuessed, Mode mode) {

  public enum Mode {
    @JsonEnumDefaultValue
    NORMAL,
    HIDDEN
  }
}
