package nz.ac.auckland.se206.speech;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.concurrent.Task;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Text-to-speech API using the JavaX speech library. */
@Singleton
public class TextToSpeech implements TerminationListener {

  /** Custom unchecked exception for Text-to-speech issues. */
  static class TextToSpeechException extends RuntimeException {

    public TextToSpeechException(final String message) {
      super(message);
    }
  }

  /**
   * Main function to speak the given list of sentences.
   *
   * @param args A sequence of strings to speak.
   */
  public static void main(final String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException(
          "You are not providing any arguments. You need to provide one or more sentences.");
    }

    final TextToSpeech textToSpeech = new TextToSpeech(LoggerFactory.getLogger(TextToSpeech.class));

    textToSpeech.speak(args);
    textToSpeech.onTerminate();
  }

  private final Logger logger;
  private final Synthesizer synthesizer;
  private final Task<Void> speakingTask;
  private final ConcurrentLinkedQueue<String> sentencesQueue = new ConcurrentLinkedQueue<>();

  /**
   * Constructs the TextToSpeech object creating and allocating the speech synthesizer. English
   * voice: com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory
   */
  @Inject
  public TextToSpeech(final Logger logger) {
    this.logger = logger;
    try {
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
      Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

      this.synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));

      this.synthesizer.allocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
    this.speakingTask =
        new Task<>() {
          @Override
          protected Void call() {
            while (!this.isCancelled()) {
              if (!TextToSpeech.this.sentencesQueue.isEmpty()) {
                final String sentence = TextToSpeech.this.sentencesQueue.poll();
                TextToSpeech.this.speak(sentence);
                TextToSpeech.this.sleep(); // Pause between sentences
              }
            }
            return null;
          }
        };

    this.speakingTask.setOnFailed(
        e ->
            this.logger.error(
                "There was an error speaking the sentences", e.getSource().getException()));
    new Thread(this.speakingTask).start();
  }

  /**
   * Speaks the given list of sentences.
   *
   * @param sentences A sequence of strings to speak.
   */
  public void speak(final String... sentences) {
    boolean isFirst = true;

    for (final String sentence : sentences) {
      if (isFirst) {
        isFirst = false;
      } else {
        // Add a pause between sentences.
        this.sleep();
      }

      this.speak(sentence);
    }
  }

  /**
   * Speaks the given sentence.
   *
   * @param sentence A string to speak.
   */
  public void speak(final String sentence) {
    if (sentence == null) {
      throw new IllegalArgumentException("Text cannot be null.");
    }

    try {
      this.synthesizer.resume();
      this.synthesizer.speakPlainText(sentence, null);
      this.synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    } catch (final AudioException | InterruptedException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }

  /**
   * Adds a sentence to the queue so that it can be spoken later on a different thread.
   *
   * @param sentence The sentence to speak.
   */
  public void queueSentence(final String sentence) {
    this.sentencesQueue.add(sentence);
  }

  /** Sleeps a while to add some pause between sentences. */
  private void sleep() {
    try {
      Thread.sleep(100);
    } catch (final InterruptedException e) {
      this.logger.error("There was an error while pausing between sentences", e);
    }
  }

  /**
   * It deallocates the speech synthesizer. If you are experiencing an IllegalThreadStateException,
   * avoid using this method and run the speak method without terminating.
   */
  @Override
  public void onTerminate() {

    // Stopping the text to speech when terminating
    try {
      this.speakingTask.cancel();
      this.synthesizer.deallocate();
    } catch (final EngineException e) {
      throw new TextToSpeechException(e.getMessage());
    }
  }
}
