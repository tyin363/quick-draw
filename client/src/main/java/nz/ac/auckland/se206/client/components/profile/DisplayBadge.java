package nz.ac.auckland.se206.client.components.profile;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import nz.ac.auckland.se206.client.badges.Badge;
import nz.ac.auckland.se206.client.users.User;

public class DisplayBadge extends HBox {

  private Badge badge;

  /**
   * Constructs a new DisplayBadge for the specified badge.
   *
   * @param badge The badge to display
   */
  public DisplayBadge(final Badge badge) {
    final Label label = new Label(badge.getDisplayName());
    final Pane pane = new Pane();

    // Push the icon to the very right
    setHgrow(label, Priority.ALWAYS);
    label.setMaxWidth(Double.MAX_VALUE);

    final boolean isSpeed = badge.getBadgeGroup().equals("speed");
    pane.getStyleClass().add(isSpeed ? "clock-icon" : "fire-curved-icon");

    // Add a tooltip describing what the badge is awarded for
    final Tooltip badgeToolTip = new Tooltip(badge.getDescription());
    badgeToolTip.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");

    // Reducing show delay for badge tool tip
    badgeToolTip.setShowDelay(Duration.millis(100));
    Tooltip.install(this, badgeToolTip);
    this.getStyleClass().addAll("badge", badge.getBadgeTier());
    this.getChildren().addAll(label, pane);
    this.setAlignment(Pos.CENTER);
    this.badge = badge;
  }

  /**
   * Updates this badge to account for whether the user has achieved it or not.
   *
   * @param user The user to update the badge with respect to
   */
  public void update(final User user) {
    if (this.badge.hasAchievedBadge(user)) {
      // Only add the achieved-badge class if it isn't already added
      if (!this.getStyleClass().contains("achieved-badge")) {
        this.getStyleClass().add("achieved-badge");
      }
    } else {
      this.getStyleClass().remove("achieved-badge");
    }
  }

  /**
   * Retrieves the badge that this DisplayBadge is displaying.
   *
   * @return The badge being displayed
   */
  public Badge getBadge() {
    return this.badge;
  }
}
