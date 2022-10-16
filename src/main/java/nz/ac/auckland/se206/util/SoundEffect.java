package nz.ac.auckland.se206.util;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SoundEffect implements TerminationListener {
  @Inject private UserService userService;

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
  private String cancelSound =
      "src/main/resources/sounds/mixkit-negative-tone-interface-tap-2569.wav";
  private double musicVolume = 0.1;
  private double soundEffectVolume = 0.3;

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param soundLocation The location of the sound file
   */
  private void playSound(String soundLocation) {
    // Set volume
    setUserVolume();

    // Play sound effect
    soundEffect = new Media(new File(soundLocation).toURI().toString());
    mediaPlayer = new MediaPlayer(soundEffect);
    mediaPlayer.setVolume(soundEffectVolume);
    mediaPlayer.play();
  }

  /**
   * This method plays a sound effect given the file location of the sound effect and its volume.
   *
   * @param music The location of the sound file =
   */
  private void playBackgroundMusic(String music) {
    // Set volume
    setUserVolume();

    // Play music
    soundEffect = new Media(new File(music).toURI().toString());
    backgroundMediaPlayer = new MediaPlayer(soundEffect);
    backgroundMediaPlayer.setVolume(musicVolume);
    backgroundMediaPlayer.play();

    // Loop if music is the main or canvas music
    if (music.equals(mainMusic) || music.equals(defaultCanvasMusic)) {
      backgroundMediaPlayer.setCycleCount(Integer.MAX_VALUE);
    }
  }

  /** Gets the user volume and sets it if a current user exists */
  private void setUserVolume() {
    if (this.userService.getCurrentUser() != null) {
      this.musicVolume = this.userService.getCurrentUser().getMusicVolume();
      this.soundEffectVolume = this.userService.getCurrentUser().getSoundEffectVolume();
    }
  }

  /**
   * Gets the volume for background music
   *
   * @return The music volume
   */
  public double getMusicVolume() {
    return musicVolume;
  }

  /**
   * Sets the volume for background music
   *
   * @param musicVolume Volume for music
   */
  public void setMusicVolume(double musicVolume) {
    this.musicVolume = musicVolume;
  }

  /**
   * Gets the volume for sound effects
   *
   * @return The sound effect volume
   */
  public double getSoundEffectVolume() {
    return soundEffectVolume;
  }

  /**
   * Sets the volume for sound effects
   *
   * @param soundEffectVolume Volume for sound effects
   */
  public void setSoundEffectVolume(double soundEffectVolume) {
    this.soundEffectVolume = soundEffectVolume;
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
    playSound(clickSound);
  }

  /** This method plays the special click sound effect of the game. */
  public void playSpecialClickSound() {
    playSound(specialClickSound);
  }

  /** This method plays the special click sound effect of the game. */
  public void playCancelSound() {
    playSound(cancelSound);
  }

  /** This method plays the canvas victory sound effect of the game. */
  public void playVictorySound() {
    playSound(winSound);
  }

  /** This method plays the canvas lose sound effect of the game. */
  public void playLoseSound() {
    playSound(loseSound);
  }

  /** This method plays the settings click sound effect of the game. */
  public void playSettingsClickSound() {
    playSound(settingsClickSound);
  }

  /**
   * Get the media player responsible for playing the background music
   *
   * @return The media player that plays music
   */
  public MediaPlayer getBackgroundMediaPlayer() {
    return backgroundMediaPlayer;
  }

  /**
   * Get the media player responsible for playing the sound effects
   *
   * @return The media player that plays sound effects
   */
  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
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

  @Override
  public void onTerminate() {
    terminate();
    terminateBackgroundMusic();
  }
}
