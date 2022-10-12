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

@Singleton
public class ConfirmationScreenController implements LoadListener {

  private static boolean HIDDEN_MODE = false;

  @FXML private Label targetWordLabel;
  @FXML private AnchorPane header;

  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  private List<String> defintions = new ArrayList<String>();

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

  /** Everytime this scene is switched to select a new random word. */
  @Override
  public void onLoad() {

    HIDDEN_MODE = MainMenuController.isIS_HIDDEN();
    this.wordService.selectRandomTarget(Difficulty.EASY);

    if (HIDDEN_MODE) {
      getDefinitions(this.wordService.getTargetWord());
      changeFontDynamically();

    } else {
      this.targetWordLabel.setText(this.wordService.getTargetWord());
    }
  }

  /** This method will change the label font size to fit the screen accordingly */
  public void changeFontDynamically() {

    double MAX_TEXT_WIDTH = 682;
    double defaultFontSize = 36;
    Font defaultFont = this.targetWordLabel.getFont();
    this.targetWordLabel
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Text tmpText = new Text(newValue);
              tmpText.setFont(defaultFont);

              double textWidth = tmpText.getLayoutBounds().getWidth();

              // check if text width is smaller than maximum width allowed
              if (textWidth <= this.targetWordLabel.getMaxWidth()) {
                System.out.println("AMBASING");
              } else {
                double newFontSize = defaultFontSize * MAX_TEXT_WIDTH / textWidth;
                this.targetWordLabel.setStyle("-fx-font-size: " + newFontSize + ";");
              }
            });
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

              Platform.runLater(
                  () -> {
                    targetWordLabel.setText(
                        wordResult.getWordEntries().get(0).getDefinitions().get(0));
                  });
              for (WordEntry entry : wordResult.getWordEntries()) {
                for (String definition : entry.getDefinitions()) {
                  defintions.add(definition);
                }
              }
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
