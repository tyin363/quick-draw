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
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.dictionary.DictionaryLookup;
import nz.ac.auckland.se206.dictionary.WordEntry;
import nz.ac.auckland.se206.dictionary.WordInfo;
import nz.ac.auckland.se206.dictionary.WordNotFoundException;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class HiddenMode {
  private List<String> definitions = new ArrayList<String>();
  private int definitionIndex = 0;

  /**
   * This method returns the list of definitions
   *
   * @return The list of definitions
   */
  public List<String> getDefinitions() {
    return definitions;
  }

  /** This method clears the definition list and resets the index */
  public void clearDefinitions() {
    this.definitions.clear();
    resetIndex();
  }

  /**
   * This method returns the definition index of the definitions list
   *
   * @return The definition index
   */
  public int getDefinitionIndex() {
    return definitionIndex;
  }

  /** This method increments the definition index */
  public void incrementIndex() {
    definitionIndex++;
  }

  /** This method decrements the definition index */
  public void decrementIndex() {
    definitionIndex--;
  }

  /** This method resets the definition index to zero */
  public void resetIndex() {
    definitionIndex = 0;
  }

  /** This method will change the label font size to fit the screen accordingly */
  public void changeFontDynamically(Label target, double maxWidth, double defaultFontSize) {

    String definition = target.getText();
    double allowableFontSize = defaultFontSize * 3 / 4;
    Font defaultFont = Font.font(defaultFontSize);
    Text tmpText = new Text(target.getText());
    double textWidth;
    tmpText.setFont(defaultFont);
    textWidth = tmpText.getLayoutBounds().getWidth();

    // Clear any styles on the target word
    target.setStyle(null);

    // If the sentence is too big, split it into smaller sentences
    if (textWidth > maxWidth) {
      double newFontSize;
      int spaceCount = StringUtils.countMatches(definition, " ");
      int halfPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 2);
      int oneThirdPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 3);
      int secondThirdPlacement = StringUtils.ordinalIndexOf(definition, " ", 2 * spaceCount / 3);

      // Two variations of the definition sentence
      String twoSentence =
          definition.substring(0, halfPlacement) + "\n" + definition.substring(halfPlacement + 1);
      String threeSentence =
          definition.substring(0, oneThirdPlacement)
              + "\n"
              + definition.substring(oneThirdPlacement + 1, secondThirdPlacement)
              + "\n"
              + definition.substring(secondThirdPlacement + 1);

      tmpText = new Text(twoSentence);
      tmpText.setFont(defaultFont);
      textWidth = tmpText.getLayoutBounds().getWidth();
      newFontSize = defaultFontSize * maxWidth / textWidth - 1;

      // Return with two sentences if size is allowable
      if (newFontSize >= allowableFontSize) {
        target.setStyle("-fx-font-size: " + newFontSize + ";");
        target.setText(twoSentence);
        return;
      }

      // Otherwise, return with three sentences and downsize the font
      tmpText = new Text(threeSentence);
      tmpText.setFont(defaultFont);
      textWidth = tmpText.getLayoutBounds().getWidth();
      newFontSize = defaultFontSize * maxWidth / textWidth - 1;
      target.setStyle("-fx-font-size: " + newFontSize + ";");
      target.setText(threeSentence);
    }
  }

  /** This gets the number of definitions and the current index number */
  public void getNumberOfDefinitions(Label target) {
    int index = definitionIndex + 1;
    int size = definitions.size();
    target.setText(index + "/" + size);
  }

  /**
   * This method checks if there is a previous and next definition and hides or shows appropriate
   * buttons when needed.
   */
  public void checkForPreviousAndNext(Node previous, Node next) {
    // Check previous definition
    if (definitionIndex == 0) {
      previous.setVisible(false);
    } else {
      previous.setVisible(true);
    }

    // Check next definition
    if (definitionIndex == definitions.size() - 1) {
      next.setVisible(false);
    } else {
      next.setVisible(true);
    }
  }

  /**
   * This method sets text and visibility of the elements involved in the Hidden game mode
   *
   * @param target The chosen target word
   * @param numberOfDefinitionLabel The label showing the number of definitions and current index
   * @param maxWidth The max width of the target label
   * @param defaultFontSize The default font size of the target label
   * @param previousDefinitionVbox The previous box
   * @param nextDefinitionVbox The next box
   */
  public void setElements(
      Label target,
      Label numberOfDefinitionLabel,
      double maxWidth,
      double defaultFontSize,
      Node previousDefinitionVbox,
      Node nextDefinitionVbox) {
    target.setText(getDefinitions().get(this.definitionIndex));
    changeFontDynamically(target, maxWidth, defaultFontSize);
    checkForPreviousAndNext(previousDefinitionVbox, nextDefinitionVbox);
    getNumberOfDefinitions(numberOfDefinitionLabel);
  }

  /** The previous definition of the given word will be shown. */
  public void previousDefinition() {
    if (this.definitionIndex - 1 >= 0) {
      decrementIndex();
    }
  }

  /** The next definition of the given word will be shown. */
  public void nextDefinition() {
    if (this.definitionIndex + 1 <= getDefinitions().size() - 1) {
      incrementIndex();
    }
  }

  /**
   * This method searches the word info of the given query word and gets its definitions
   *
   * @param queryWord The word to search up on
   */
  public void searchWord(
      String queryWord,
      Label target,
      Label numberOfDefinitions,
      double maxWidth,
      double fontSize,
      Node previous,
      Node next) {

    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            try {
              // get word info
              WordInfo wordResult = DictionaryLookup.searchWordInfo(queryWord);
              for (WordEntry entry : wordResult.getWordEntries()) {
                for (String definition : entry.getDefinitions()) {
                  definitions.add(definition);
                }
              }

              Platform.runLater(
                  () -> {
                    setElements(target, numberOfDefinitions, maxWidth, fontSize, previous, next);
                  });
            } catch (IOException e) {
              e.printStackTrace();
            } catch (WordNotFoundException e) {
              System.out.println("Sorry there were no definitions!");
              Platform.runLater(
                  () -> {
                    target.setText("Sorry! There was no definition for " + queryWord);
                    definitions.add("Sorry! There was no definition for " + queryWord);
                    changeFontDynamically(target, maxWidth, fontSize);
                  });
            }

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
