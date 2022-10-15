package nz.ac.auckland.se206.hiddenmode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import nz.ac.auckland.se206.dictionary.DictionaryLookup;
import nz.ac.auckland.se206.dictionary.WordEntry;
import nz.ac.auckland.se206.dictionary.WordInfo;
import nz.ac.auckland.se206.exceptions.WordNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Singleton
public class HiddenMode implements TerminationListener {

  @Inject private Logger logger;
  private List<String> definitions = new ArrayList<String>();
  private int definitionIndex = 0;
  private Task<Void> backgroundTask;

  /**
   * This method returns the list of definitions
   *
   * @return The list of definitions
   */
  public List<String> getDefinitions() {
    return this.definitions;
  }

  /** This method clears the definition list and resets the index */
  public void clearDefinitions() {
    this.definitions.clear();
    this.resetIndex();
  }

  /**
   * This method returns the definition index of the definitions list
   *
   * @return The definition index
   */
  public int getDefinitionIndex() {
    return this.definitionIndex;
  }

  /** This method increments the definition index */
  public void incrementIndex() {
    this.definitionIndex++;
  }

  /** This method decrements the definition index */
  public void decrementIndex() {
    this.definitionIndex--;
  }

  /** This method resets the definition index to zero */
  public void resetIndex() {
    this.definitionIndex = 0;
  }

  /**
   * This method will change the label font size to fit the screen accordingly.
   *
   * @param target The label to change the font size of
   * @param maxWidth The maximum width of the label
   * @param defaultFontSize The default font size of the label
   */
  public void changeFontDynamically(
      final Label target, final double maxWidth, final double defaultFontSize) {

    final String definition = target.getText();
    final double allowableFontSize = defaultFontSize * 3 / 4;
    double textWidth = this.getTemporaryWidth(definition, defaultFontSize);

    // Clear any styles on the target word
    target.setStyle(null);

    // If the sentence is too big, split it into smaller sentences
    if (textWidth >= maxWidth) {
      double newFontSize;
      final int spaceCount = StringUtils.countMatches(definition, " ");
      final int halfPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 2);
      final int oneThirdPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 3);
      final int secondThirdPlacement =
          StringUtils.ordinalIndexOf(definition, " ", 2 * spaceCount / 3);

      // Two variations of the definition sentence
      final String twoSentence =
          definition.substring(0, halfPlacement) + "\n" + definition.substring(halfPlacement + 1);

      // Get new width for two sentences
      textWidth = this.getTemporaryWidth(twoSentence, defaultFontSize);

      // Return with two sentences if size is allowable
      newFontSize = defaultFontSize * maxWidth / textWidth - 1;
      if (newFontSize >= allowableFontSize) {
        if (textWidth >= maxWidth) {
          target.setStyle("-fx-font-size: " + newFontSize + ";");
        }
        target.setText(twoSentence);
        return;
      }

      final String threeSentence =
          "%s\n%s\n%s"
              .formatted(
                  definition.substring(0, oneThirdPlacement),
                  definition.substring(oneThirdPlacement + 1, secondThirdPlacement),
                  definition.substring(secondThirdPlacement + 1));

      // Otherwise, return with three sentences and downsize the font
      textWidth = this.getTemporaryWidth(threeSentence, defaultFontSize);
      newFontSize = defaultFontSize * maxWidth / textWidth - 1;
      target.setStyle("-fx-font-size: " + newFontSize + ";");
      target.setText(threeSentence);
    }
  }

  /**
   * This is a helper method for the changeFontDynamically method.
   *
   * <p>It calculates the width of a text element given the text that is in it.
   *
   * @param sentence The text to be put in the Text element
   * @param fontSize The size of the font
   * @return The width of the text element
   */
  private double getTemporaryWidth(final String sentence, final double fontSize) {
    final Font defaultFont = Font.font(fontSize);
    final Text tmpText = new Text(sentence);
    final double textWidth;
    tmpText.setFont(defaultFont);

    // Get width of the text
    textWidth = tmpText.getLayoutBounds().getWidth();

    return textWidth;
  }

  /** This gets the number of definitions and the current index numbe.r */
  public void getNumberOfDefinitions(final Label target) {
    final int index = this.definitionIndex + 1;
    final int size = this.definitions.size();
    target.setText(index + "/" + size);
  }

  /**
   * This method checks if there is a previous and next definition and hides or shows appropriate
   * buttons when needed.
   */
  public void checkForPreviousAndNext(final Node previous, final Node next) {
    // Check previous definition
    previous.setVisible(this.definitionIndex != 0);

    // Check next definition
    next.setVisible(this.definitionIndex != this.definitions.size() - 1);
  }

  /**
   * This method sets text and visibility of the elements involved in the Hidden game mode.
   *
   * @param target The chosen target word
   * @param numberOfDefinitionLabel The label showing the number of definitions and current index
   * @param maxWidth The max width of the target label
   * @param defaultFontSize The default font size of the target label
   * @param previousDefinitionVbox The previous box
   * @param nextDefinitionVbox The next box
   */
  public void setElements(
      final Label target,
      final Label numberOfDefinitionLabel,
      final double maxWidth,
      final double defaultFontSize,
      final Node previousDefinitionVbox,
      final Node nextDefinitionVbox) {
    // Update the displayed definitions and buttons
    target.setText(this.getDefinitions().get(this.definitionIndex));
    this.changeFontDynamically(target, maxWidth, defaultFontSize);
    this.checkForPreviousAndNext(previousDefinitionVbox, nextDefinitionVbox);
    this.getNumberOfDefinitions(numberOfDefinitionLabel);
  }

  /** The previous definition of the given word will be shown. */
  public void previousDefinition() {
    if (this.definitionIndex - 1 >= 0) {
      this.decrementIndex();
    }
  }

  /** The next definition of the given word will be shown. */
  public void nextDefinition() {
    if (this.definitionIndex + 1 <= this.getDefinitions().size() - 1) {
      this.incrementIndex();
    }
  }

  /**
   * This method searches the word info of the given query word and gets its definitions.
   *
   * @param queryWord The word to search up on
   * @param target The target label to display the definitions
   * @param numberOfDefinitions The label showing the number of definitions and current index
   * @param maxWidth The max width of the target label
   * @param fontSize The default font size of the target label
   * @param previous The previous button container
   * @param next The next button container
   */
  public void searchWord(
      final String queryWord,
      final Label target,
      final Label numberOfDefinitions,
      final double maxWidth,
      final double fontSize,
      final Node previous,
      final Node next) {

    // Keep a reference to the task so that it can be cancelled if the application closes mid-task
    this.backgroundTask =
        new Task<>() {

          @Override
          protected Void call() {
            try {
              // get word info
              final WordInfo wordResult = DictionaryLookup.searchWordInfo(queryWord);
              for (final WordEntry entry : wordResult.getWordEntries()) {
                HiddenMode.this.definitions.addAll(entry.getDefinitions());
              }

              // On the JavaFX Application Thread update the displayed definitions and buttons
              Platform.runLater(
                  () ->
                      HiddenMode.this.setElements(
                          target, numberOfDefinitions, maxWidth, fontSize, previous, next));
            } catch (final IOException e) {
              HiddenMode.this.logger.error("There was an error fetching the definition", e);
            } catch (final WordNotFoundException e) {
              // Let the user know that there is no definition for this word
              HiddenMode.this.logger.info("Sorry there were no definitions!");
              Platform.runLater(
                  () -> {
                    target.setText("Sorry! There was no definition for " + queryWord);
                    HiddenMode.this.definitions.add(
                        "Sorry! There was no definition for " + queryWord);
                    HiddenMode.this.changeFontDynamically(target, maxWidth, fontSize);
                  });
            }

            return null;
          }
        };

    // Perform the search on a background thread so that it doesn't cause the UI to freeze
    new Thread(this.backgroundTask).start();
  }

  /** If the Application closes, make sure to cancel the background task. */
  @Override
  public void onTerminate() {
    if (this.backgroundTask != null) {
      this.backgroundTask.cancel();
    }
  }
}
