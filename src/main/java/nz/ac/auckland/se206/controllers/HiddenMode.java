package nz.ac.auckland.se206.controllers;

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
    // Clear any styles on the target word
    target.setStyle(null);

    String definition = target.getText();

    Font defaultFont = Font.font(defaultFontSize);
    Text tmpText = new Text(target.getText());
    double textWidth;
    tmpText.setFont(defaultFont);
    textWidth = tmpText.getLayoutBounds().getWidth();

    /*
     * Check if text width is smaller than maximum width allowed.
     *
     * If the sentence is too big, split it into 3 smaller sentences
     */
    if (textWidth > maxWidth) {
      int spaceCount = StringUtils.countMatches(definition, " ");
      int firstPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 3);
      int secondPlacement = StringUtils.ordinalIndexOf(definition, " ", 2 * spaceCount / 3);

      // Create new string and combining the three split sentences
      String threeSentence =
          definition.substring(0, firstPlacement)
              + "\n"
              + definition.substring(firstPlacement + 1, secondPlacement)
              + "\n"
              + definition.substring(secondPlacement + 1);
      tmpText = new Text(threeSentence);
      tmpText.setFont(defaultFont);
      textWidth = tmpText.getLayoutBounds().getWidth();
      /*
       * Check if text width is smaller than maximum width allowed.
       *
       * If the sentence is too big, make the font smaller
       */
      if (textWidth > maxWidth) {
        double newFontSize = defaultFontSize * maxWidth / textWidth - 1;
        target.setStyle("-fx-font-size: " + newFontSize + ";");
      }
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
