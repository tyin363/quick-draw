package nz.ac.auckland.se206.util;

import javafx.scene.media.Media;

public enum Sound {
  WIN("mixkit-achievement-bell-600.wav"),
  LOSE("mixkit-negative-answer-lose-2032.wav"),
  CLICK("mixkit-select-click-1109.wav"),
  SPECIAL_CLICK("mixkit-quick-win-video-game-notification-269.wav"),
  SETTINGS_CLICK("mixkit-modern-technology-select-3124.wav"),
  CANCEL("mixkit-negative-tone-interface-tap-2569.wav");

  private final Media media;

  /**
   * Constructs a new sound and loads the media from the given filename. If the filename is invalid,
   * or it cannot be found, an {@link IllegalArgumentException} is thrown.
   *
   * @param filename The filename of the sound effect
   * @throws IllegalArgumentException If the filename is invalid or it cannot be found
   */
  Sound(final String filename) {
    this.media = Helpers.loadSound("soundeffects/" + filename);
  }

  /**
   * Retrieve the media object for this sound.
   *
   * @return the media object for this sound.
   */
  public Media getMedia() {
    return this.media;
  }
}
