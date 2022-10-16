package nz.ac.auckland.se206.client.sounds;

import javafx.scene.media.Media;
import nz.ac.auckland.se206.client.util.Helpers;

public enum Music {
  CANVAS_MUSIC("Exit-the-Premises.mp3"),
  LOSE_MUSIC("Loyalty_Freak_Music_-_05_-_We_all_gonna_die_.mp3"),
  WIN_MUSIC("Victory.mp3"),
  MAIN_MUSIC("Pixel-Peeker-Polka-faster.mp3");

  private final Media media;

  /**
   * Constructs a new sound and loads the media from the given filename. If the filename is invalid,
   * or it cannot be found, an {@link IllegalArgumentException} is thrown.
   *
   * @param filename The filename of the sound effect
   * @throws IllegalArgumentException If the filename is invalid or it cannot be found
   */
  Music(final String filename) {
    this.media = Helpers.loadSound("music/" + filename);
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
