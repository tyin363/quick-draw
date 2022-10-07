package nz.ac.auckland.se206.util;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundEffect {
  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  public static void playSound(String soundLocation, double volume) {
    Media soundEffect = new Media(new File(soundLocation).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(soundEffect);
    mediaPlayer.setVolume(volume);
    mediaPlayer.play();
  }
}
