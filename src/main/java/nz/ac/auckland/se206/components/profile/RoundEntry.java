package nz.ac.auckland.se206.components.profile;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.users.Round;

public class RoundEntry extends AnchorPane {

  /**
   * Creates a new RoundEntry component from the given round.
   *
   * @param round The round to create a RoundEntry for.
   */
  public RoundEntry(final Round round) {
    this.getStyleClass().add("round-entry");
    final Label wordLabel = new Label(round.getWord());
    final Label timeLabel = new Label(round.getTimeTaken() + "s");

    timeLabel.getStyleClass().add(round.wasGuessed() ? "round-won" : "round-lost");

    // Anchor the labels to the left and right of this entry
    AnchorPane.setLeftAnchor(wordLabel, 0D);
    AnchorPane.setRightAnchor(timeLabel, 0D);
    this.getChildren().addAll(wordLabel, timeLabel);
  }
}
