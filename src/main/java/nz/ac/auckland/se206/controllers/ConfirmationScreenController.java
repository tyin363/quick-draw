package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.SceneManager;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import nz.ac.auckland.se206.dictionary.DictionaryLookup;
import nz.ac.auckland.se206.dictionary.WordEntry;
import nz.ac.auckland.se206.dictionary.WordInfo;
import nz.ac.auckland.se206.dictionary.WordNotFoundException;
import nz.ac.auckland.se206.util.Helpers;
import nz.ac.auckland.se206.words.Difficulty;
import nz.ac.auckland.se206.words.WordService;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class ConfirmationScreenController implements LoadListener {

  private static boolean HIDDEN_MODE = false;

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  private List<String> definitions = new ArrayList<String>();
  private int definitionIndex = 0;

  /** Hook up the back button action when the view is initialised. */
  @FXML
  private void initialize() {
    Helpers.getBackButton(this.header).setOnAction(event -> this.onSwitchBack());
  }

  /** When the user confirms they are ready, switch to the canvas view. */
  @FXML
  private void onConfirmSwitch() {
    this.sceneManager.switchToView(View.CANVAS);
  }

  /** The previous definition of the given word will be shown. */
  @FXML
  private void onClickPrevious() {
    if (definitionIndex - 1 >= 0) {
      definitionIndex--;
    }
    this.targetWordLabel.setText(definitions.get(definitionIndex));
    changeFontDynamically();
  }

  /** The next definition of the given word will be shown. */
  @FXML
  private void onClickNext() {
    if (definitionIndex + 1 <= definitions.size() - 1) {
      definitionIndex++;
    }
    this.targetWordLabel.setText(definitions.get(definitionIndex));
    changeFontDynamically();
  }

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {
    definitions.clear();
    HIDDEN_MODE = MainMenuController.isIS_HIDDEN();
    this.wordService.selectRandomTarget(Difficulty.EASY);

    if (HIDDEN_MODE) {
      getDefinitions(this.wordService.getTargetWord());
      System.out.println(definitions.size());
    } else {
      this.targetWordLabel.setText(this.wordService.getTargetWord());
    }
  }

  /** This method will change the label font size to fit the screen accordingly */
  public void changeFontDynamically() {
    this.targetWordLabel.setStyle(null);
    String definition = this.targetWordLabel.getText();
    System.out.println(definition);
    double maxWidth = 682;
    double defaultFontSize = 36;
    Font defaultFont = Font.font(defaultFontSize);

    Text tmpText = new Text(this.targetWordLabel.getText());
    tmpText.setFont(defaultFont);
    double textWidth = tmpText.getLayoutBounds().getWidth();

    // check if text width is smaller than maximum width allowed
    if (textWidth > maxWidth) {
      int spaceCount = StringUtils.countMatches(definition, " ");
      int firstPlacement = StringUtils.ordinalIndexOf(definition, " ", spaceCount / 3);
      int secondPlacement = StringUtils.ordinalIndexOf(definition, " ", 2 * spaceCount / 3);

      String threeSentence =
          definition.substring(0, firstPlacement)
              + "\n"
              + definition.substring(firstPlacement + 1, secondPlacement)
              + "\n"
              + definition.substring(secondPlacement + 1);

      System.out.println("\n" + threeSentence);
      tmpText = new Text(threeSentence);
      tmpText.setFont(defaultFont);
      textWidth = tmpText.getLayoutBounds().getWidth();
      if (textWidth > maxWidth) {
        double newFontSize = defaultFontSize * maxWidth / textWidth - 1;
        this.targetWordLabel.setStyle("-fx-font-size: " + newFontSize + ";");
      }
      this.targetWordLabel.setText(threeSentence);
    }
  }

  /**
   * This method searches the word info of the given query word and gets its definitions
   *
   * @param queryWord The word to search up on
   */
  public void getDefinitions(String queryWord) {

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
                    targetWordLabel.setText(definitions.get(definitionIndex));
                    changeFontDynamically();
                  });
            } catch (IOException e) {
              e.printStackTrace();
            } catch (WordNotFoundException e) {
              System.out.println("\"" + e.getWord() + "\" has problems: " + e.getMessage());
            }

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  /** When the user clicks the back button, take them back to the main menu. */
  private void onSwitchBack() {
    this.sceneManager.switchToView(View.MAIN_MENU);
  }
}
