package nz.ac.auckland.se206.util;

import java.io.File;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundEffect {

  private Task<Void> soundTask;

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  public void playSound(String soundLocation, double volume) {
    soundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            Media soundEffect = new Media(new File(soundLocation).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(soundEffect);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();

            return null;
          }
          ;
        };
    Thread backgroundThread = new Thread(soundTask);
    backgroundThread.start();
  }

  /**
   * It deallocates the speech synthesizer. If you are experiencing an IllegalThreadStateException,
   * avoid using this method and run the speak method without terminating.
   */
  public void terminate() {
    this.soundTask.cancel();
  }
}
