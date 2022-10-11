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
  private String winSound = "src/main/resources/sounds/mixkit-achievement-bell-600.wav";
  private String loseSound = "src/main/resources/sounds/mixkit-negative-answer-lose-2032.wav";
  private String victoryMusic = "src/main/resources/sounds/Victory.mp3";
  private String clickSound = "src/main/resources/sounds/mixkit-select-click-1109.wav";
  private String LoseMusic =
      "src/main/resources/sounds/Loyalty_Freak_Music_-_05_-_We_all_gonna_die_.mp3";

  /**
   * This method will change the music the media player would play depending on the current view.
   * This is done by retrieving the different music file locations.
   *
   * @param view The current view.
   * @return The file location of music choice.
   */
  public void changeMusic(View view) {
    String music;

    switch (view) {
      case CANVAS:
        music = canvasMusic;
        break;
      default:
        music = mainMusic;
        break;
    }

    playBackgroundMusic(music);
  }

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  private void playSound(String soundLocation, double volume) {
    soundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            soundEffect = new Media(new File(soundLocation).toURI().toString());
            mediaPlayer = new MediaPlayer(soundEffect);
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
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  private void playBackgroundMusic(String music) {
    backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            soundEffect = new Media(new File(music).toURI().toString());
            backgroundMediaPlayer = new MediaPlayer(soundEffect);
            backgroundMediaPlayer.setVolume(0.1);
            backgroundMediaPlayer.play();

            if (music == mainMusic) {
              backgroundMediaPlayer.setCycleCount(Integer.MAX_VALUE);
            }

            return null;
          }
          ;
        };
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  /** This method plays the canvas victory background music of the game. */
  public void playVictoryMusic() {
    playBackgroundMusic(victoryMusic);
  }

  /** This method plays the canvas losing background music of the game. */
  public void playLoseMusic() {
    playBackgroundMusic(LoseMusic);
  }

  /** This method plays the click sound effect of the game. */
  public void playClickSound() {
    playSound(clickSound, 0.3);
  }

  /** This method plays the canvas victory sound effect of the game. */
  public void playVictorySound() {
    playSound(winSound, 0.3);
  }

  /** This method plays the canvas lose sound effect of the game. */
  public void playLoseSound() {
    playSound(loseSound, 0.3);
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
