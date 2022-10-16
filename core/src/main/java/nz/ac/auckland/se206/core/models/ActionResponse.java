package nz.ac.auckland.se206.core.models;

public record ActionResponse(Action action) {

  public enum Action {
    COMPLETE_DRAWING,
    TERMINATE_CONNECTION,
  }
}
