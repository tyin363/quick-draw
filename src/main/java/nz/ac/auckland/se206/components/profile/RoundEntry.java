package nz.ac.auckland.se206.components.profile;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import nz.ac.auckland.se206.users.Round;
import nz.ac.auckland.se206.users.Round.Mode;

public class RoundEntry extends HBox {

  /**
   * Creates a new RoundEntry component from the given round.
   *
   * @param round The round to create a RoundEntry for.
   */
  public RoundEntry(final Round round) {
    this.getStyleClass().add("round-entry");
    this.setAlignment(Pos.CENTER);

    final Label wordLabel = new Label(round.word());
    final HBox content = this.renderTimeAndMode(round);

    // Make the word label take up all the space it can so that the content box gets right aligned.
    wordLabel.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(wordLabel, Priority.ALWAYS);

    // Adding word label and content to round entry
    this.getChildren().addAll(wordLabel, content);
  }

  /**
   * Renders a container of round time and mode as a label and icon.
   *
   * @param round The round to render the time and mode for
   * @return The rendered container
   */
  private HBox renderTimeAndMode(final Round round) {
    final HBox container = new HBox();
    final Label timeLabel = new Label(round.timeTaken() + "s");
    final Pane icon = new Pane();

    // Getting icon's style class
    icon.getStyleClass().add(round.mode() == Mode.NORMAL ? "eye-icon" : "eye-slash-icon");

    // Adjust the styling depending on the round result
    container.setAlignment(Pos.CENTER);
    container.setSpacing(round.mode() == Mode.NORMAL ? 6 : 4);
    container.getStyleClass().add(round.wasGuessed() ? "round-won" : "round-lost");
    container.getChildren().addAll(timeLabel, icon);

    return container;
  }
}
