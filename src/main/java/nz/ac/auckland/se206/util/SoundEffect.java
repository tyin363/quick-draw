package nz.ac.auckland.se206.util;

import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.users.UserService;

@Singleton
public class SoundEffect implements TerminationListener {
  @Inject private UserService userService;

  private Sound soundEffect;
  private Music music;
  private MediaPlayer mediaPlayer;
  private MediaPlayer backgroundMediaPlayer;
  private double musicVolume = 0.1;
  private double soundEffectVolume = 0.3;

  /**
   * This plays the sound effect given the Sound enum
   *
   * @param sound The Sound enum
   */
  public void playSound(final Sound sound) {
    // Set volume
    setUserVolume();

    // Play sound effect
    this.soundEffect = sound;
    this.mediaPlayer = new MediaPlayer(soundEffect.getMedia());
    this.mediaPlayer.setVolume(soundEffectVolume);
    this.mediaPlayer.play();
  }

  /**
   * This plays the background music given the Music enum
   *
   * @param sound The Music enum
   */
  public void playBackgroundMusic(final Music sound) {
    // Set volume
    setUserVolume();

    // Play music
    this.music = sound;
    this.backgroundMediaPlayer = new MediaPlayer(music.getMedia());
    this.backgroundMediaPlayer.setVolume(musicVolume);
    this.backgroundMediaPlayer.play();

    // Loop if music is the main or canvas music
    if (music.equals(Music.CANVAS_MUSIC) || music.equals(Music.MAIN_MUSIC)) {
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
