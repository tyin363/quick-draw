package nz.ac.auckland.se206.util;

import java.io.File;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.View;

@Singleton
public class SoundEffect {

  private Task<Void> soundTask;
  private Task<Void> backgroundTask;
  private Media soundEffect;
  private MediaPlayer mediaPlayer;
  private MediaPlayer backgroundMediaPlayer;
  private String mainMusic = "src/main/resources/sounds/Pixel-Peeker-Polka-faster.mp3";
  private String canvasMusic = "src/main/resources/sounds/Exit-the-Premises.mp3";

  /**
   * This method returns the media player responsible for playing the background music
   *
   * @return the media player for background music
   */
  public MediaPlayer getBackgroundMediaPlayer() {
    return backgroundMediaPlayer;
  }

  /**
   * This method will change the music the media player would play depending on the current view.
   * This is done by retrieving the different music file locations.
   *
   * @param view The current view.
   * @return The file location of music choice.
   */
  public String changeMusic(View view) {
    String music = mainMusic;

    if (view.equals(View.CANVAS)) {
      music = canvasMusic;
    }

    return music;
  }

  private void createSound(MediaPlayer player, String soundLocation, double volume) {

    soundEffect = new Media(new File(soundLocation).toURI().toString());
    player = new MediaPlayer(soundEffect);
    player.setVolume(volume);
    player.play();
  }

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
            createSound(mediaPlayer, soundLocation, volume);
            return null;
          }
          ;
        };
    Thread backgroundThread = new Thread(soundTask);
    backgroundThread.start();
  }

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  public void playBackgroundMusic(String music) {
    backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            createSound(backgroundMediaPlayer, music, 0.1);
            return null;
          }
          ;
        };
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  /**
   * This method terminates the current media player and task. Use to prevent too many instance of
   * mediaplayer playing.
   */
  public void terminate() {
    if (!(this.mediaPlayer == null)) {
      this.mediaPlayer.stop();
      this.soundTask.cancel();
    }
  }

  /**
   * This method terminates the background music media player and task. Use to prevent too many
   * instance of mediaplayer playing.
   */
  public void terminateBackgroundMusic() {
    if (!(this.backgroundMediaPlayer == null)) {
      this.backgroundMediaPlayer.stop();
      this.backgroundTask.cancel();
    }
  }
}
