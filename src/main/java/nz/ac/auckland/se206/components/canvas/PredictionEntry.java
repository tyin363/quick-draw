package nz.ac.auckland.se206.components.canvas;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class PredictionEntry extends HBox {

  private final Label wordLabel;
  private final Label confidenceLabel;

  /**
   * Creates a blank PredictionEntry component. To set the values call {@link #update(String, int)}.
   */
  public PredictionEntry() {
    this.getStyleClass().add("prediction-entry");
    this.setAlignment(Pos.CENTER);

    this.wordLabel = new Label();
    this.confidenceLabel = new Label();
    this.confidenceLabel.setAlignment(Pos.CENTER_RIGHT);

    // Make the word label take up all the space it can so that the content box gets right aligned.
    this.wordLabel.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(this.wordLabel, Priority.ALWAYS);

    this.getChildren().addAll(this.wordLabel, this.confidenceLabel);
  }

  /**
   * Updates the word and confidence values of this PredictionEntry.
   *
   * @param word The word to display
   * @param confidence The confidence value to display (In the range 0-100)
   */
  public void update(final String word, final int confidence) {
    this.wordLabel.setText(word);
    this.confidenceLabel.setText(confidence + "%");
  }

  /**
   * Sets the colour of the word and confidence.
   *
   * @param colour The colour to set
   */
  public void setTextColour(final Color colour) {
    this.wordLabel.setTextFill(colour);
    this.confidenceLabel.setTextFill(colour);
  }

  /** Clears the word and confidence values. */
  public void reset() {
    this.wordLabel.setText("");
    this.confidenceLabel.setText("");
  }
}
