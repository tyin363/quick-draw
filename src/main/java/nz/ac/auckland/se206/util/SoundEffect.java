package nz.ac.auckland.se206.util;

import java.io.File;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.annotations.Singleton;

@Singleton
public class SoundEffect {

  private Task<Void> soundTask;
  private Task<Void> backgroundTask;
  private Media soundEffect;
  private MediaPlayer mediaPlayer;
  private MediaPlayer backgroundMediaPlayer;
  private String mainMusic = "src/main/resources/sounds/Pixel-Peeker-Polka-faster.mp3";
  private String defaultCanvasMusic = "src/main/resources/sounds/Exit-the-Premises.mp3";
  private String winSound = "src/main/resources/sounds/mixkit-achievement-bell-600.wav";
  private String loseSound = "src/main/resources/sounds/mixkit-negative-answer-lose-2032.wav";
  private String victoryMusic = "src/main/resources/sounds/Victory.mp3";
  private String clickSound = "src/main/resources/sounds/mixkit-select-click-1109.wav";
  private String LoseMusic =
      "src/main/resources/sounds/Loyalty_Freak_Music_-_05_-_We_all_gonna_die_.mp3";
  private String specialClickSound =
      "src/main/resources/sounds/mixkit-quick-win-video-game-notification-269.wav";
  private String settingsClickSound =
      "src/main/resources/sounds/mixkit-modern-technology-select-3124.wav";

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  private void playSound(String soundLocation, double volume) {

    soundEffect = new Media(new File(soundLocation).toURI().toString());
    mediaPlayer = new MediaPlayer(soundEffect);
    mediaPlayer.setVolume(volume);
    mediaPlayer.play();
  }

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   * @param volume The volume of the sound effect
   */
  private void playBackgroundMusic(String music) {

    soundEffect = new Media(new File(music).toURI().toString());
    backgroundMediaPlayer = new MediaPlayer(soundEffect);
    backgroundMediaPlayer.setVolume(0.1);
    backgroundMediaPlayer.play();

    if (music == mainMusic || music == defaultCanvasMusic) {
      backgroundMediaPlayer.setCycleCount(Integer.MAX_VALUE);
    }
  }

  /** This method plays the default canvas background music of the game. */
  public void playDefaultCanvasMusic() {
    playBackgroundMusic(defaultCanvasMusic);
  }

  /** This method plays the main background music of the game. */
  public void playMainMusic() {
    playBackgroundMusic(mainMusic);
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

  /** This method plays the special click sound effect of the game. */
  public void playSpecialClickSound() {
    playSound(specialClickSound, 0.3);
  }

  /** This method plays the canvas victory sound effect of the game. */
  public void playVictorySound() {
    playSound(winSound, 0.3);
  }

  /** This method plays the canvas lose sound effect of the game. */
  public void playLoseSound() {
    playSound(loseSound, 0.3);
  }

  /** This method plays the settings click sound effect of the game. */
  public void playSettingsClickSound() {
    playSound(settingsClickSound, 0.2);
  }

  /**
   * This method terminates the current media player and task. Use to prevent too many instance of
   * mediaplayer playing.
   */
  public void terminate() {
    if (!(this.mediaPlayer == null)) {
      this.mediaPlayer.stop();
    }
  }

  /**
   * This method terminates the background music media player and task. Use to prevent too many
   * instance of mediaplayer playing.
   */
  public void terminateBackgroundMusic() {
    if (!(this.backgroundMediaPlayer == null)) {
      this.backgroundMediaPlayer.stop();
    }
  }
}
