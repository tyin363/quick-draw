package nz.ac.auckland.se206.server.models;

public record ActionResponse(Action action) {

  public enum Action {
    COMPLETE_DRAWING,
  }
}
