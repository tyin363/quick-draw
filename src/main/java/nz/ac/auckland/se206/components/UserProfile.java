package nz.ac.auckland.se206.components;

import java.io.File;
import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.components.icons.ClockIcon;
import nz.ac.auckland.se206.components.icons.FlameIcon;
import nz.ac.auckland.se206.users.User;

public class UserProfile extends VBox {

  /**
   * Creates a UserProfile component from the given user. It consists of a profile picture, username
   * and a summary of their statistics.
   *
   * @param user The user to create a profile for.
   */
  public UserProfile(final User user) {
    this.getStyleClass().addAll("profile-card", "hover-border");

    // Have the clip on a child so that you can still have the border highlight on hover.
    final VBox clipContainer = new VBox();

    // Create a rectangle with rounded corners that will prevent profile images from overflowing
    // on the rounded corners.
    final Rectangle clip = new Rectangle(220, 300);
    clip.setArcHeight(24);
    clip.setArcWidth(24);
    clipContainer.setClip(clip);

    clipContainer.getChildren().addAll(this.renderProfilePicture(user), this.renderContent(user));

    this.getChildren().add(clipContainer);
  }

  /**
   * Renders the profile picture of the user. This will either be an image of their profile picture
   * or an abbreviated version of their username if they do not have a profile picture or if the
   * profile picture doesn't exist.
   *
   * @param user The user to render the profile picture for
   * @return The rendered profile picture
   */
  private VBox renderProfilePicture(final User user) {
    final VBox container = new VBox();
    container.getStyleClass().add("profile-picture");
    container.setAlignment(Pos.CENTER);

    if (user.getProfilePicture() != null) {
      final File file = new File(user.getProfilePicture());
      // Ensure that the image exists. If it doesn't then fall back to the abbreviated username
      // profile picture instead. It might not exist if a user accidentally deletes it or moves it.
      if (file.exists()) {
        final Image image = new Image(file.toURI().toString());
        final ImageView imageView = new ImageView(image);
        imageView.setFitHeight(220);
        imageView.setFitWidth(220);
        imageView.setPreserveRatio(true);

        container.getChildren().add(imageView);
        return container;
      }
    }

    // If they don't have a profile picture then render the abbreviated username profile picture
    container.getChildren().add(this.renderNoPicture(user.getUsername()));
    return container;
  }

  /**
   * Renders a label with the abbreviated username for the user profile picture.
   *
   * @param username The username of the user
   * @return The label with the abbreviated username
   * @see UserProfile#getAbbreviatedUsername(String)
   */
  private Label renderNoPicture(final String username) {
    return new Label(this.getAbbreviatedUsername(username));
  }

  /**
   * Gets the abbreviation of a username by taking the first letter of each word in the username and
   * concatenating them together separated by a period. Finally, it is converted to uppercase.
   *
   * <p>For example "Josh Jeffers" would become "J.J.".
   *
   * @param username The username to abbreviate
   * @return The abbreviated username
   */
  private String getAbbreviatedUsername(final String username) {
    // Combine the first letter of each word in the username into a single string
    return Arrays.stream(username.split("\\s+"))
        .map(word -> Character.toString(word.charAt(0)))
        .reduce("", (a, b) -> a + b + ".")
        .toUpperCase();
  }

  /**
   * Renders the user's username and a summary of their statistics.
   *
   * @param user The user to render the content for.
   * @return The rendered profile content
   */
  private VBox renderContent(final User user) {
    final VBox container = new VBox();
    container.getStyleClass().add("profile-content");
    container.setSpacing(10);

    final Label username = new Label(user.getUsername());
    container.getChildren().addAll(username, this.renderStats(user));
    return container;
  }

  /**
   * Renders a summary of the user's statistics. Specially, their best win streak and their fastest
   * win time.
   *
   * @param user The user to render the statistics for
   * @return The rendered statistics
   */
  private HBox renderStats(final User user) {
    final HBox container = new HBox();
    container.getStyleClass().add("profile-stats");
    container.setSpacing(20);

    final String bestWinStreakValue = Integer.toString(user.getBestWinStreak());

    // If the user has never won a game their fastest time will be -1. In this case we want to
    // render a '-' instead of a negative number.
    final String fastestTimeValue = user.getFastestTime() < 0 ? "-" : user.getFastestTime() + "s";

    container
        .getChildren()
        .addAll(
            this.renderStat(new FlameIcon(), bestWinStreakValue),
            this.renderStat(new ClockIcon(), fastestTimeValue));

    return container;
  }

  /**
   * Renders a specific statistic. A statistic consists of an icon and a value.
   *
   * @param icon The icon corresponding to this statistic
   * @param value The value of this statistic
   * @return The rendered statistic
   */
  private HBox renderStat(final Node icon, final String value) {
    final HBox container = new HBox();
    container.setAlignment(Pos.CENTER);
    container.setSpacing(10);

    icon.setStyle("-fx-background-color: -fx-lightgray-100");
    container.getChildren().addAll(icon, new Label(value));

    return container;
  }
}
