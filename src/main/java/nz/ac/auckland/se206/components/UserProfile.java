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
import nz.ac.auckland.se206.components.icons.ClockIcon;
import nz.ac.auckland.se206.components.icons.FlameIcon;
import nz.ac.auckland.se206.users.User;

public class UserProfile extends VBox {

  public UserProfile(final User user) {
    this.getStyleClass().add("profile-card");
    this.getChildren().addAll(this.renderProfilePicture(user), this.renderContent(user));
  }

  private VBox renderProfilePicture(final User user) {
    final VBox container = new VBox();
    container.getStyleClass().add("profile-picture");
    container.setAlignment(Pos.CENTER);

    if (user.getProfilePicture() != null) {
      final File file = new File(user.getProfilePicture());
      if (file.exists()) {
        final Image image = new Image(file.toURI().toString());
        final ImageView imageView = new ImageView(image);
        imageView.setFitHeight(220);
        imageView.setFitWidth(240);
        imageView.setPreserveRatio(true);

        container.getChildren().add(imageView);
        return container;
      }
    }

    container.getChildren().add(this.renderNoPicture(user.getUsername()));
    return container;
  }

  private Label renderNoPicture(final String username) {
    return new Label(this.getAbbreviatedUsername(username));
  }

  private String getAbbreviatedUsername(final String username) {
    // Combine the first letter of each word in the username into a single string
    return Arrays.stream(username.split("\\s+"))
        .map(word -> Character.toString(word.charAt(0)))
        .reduce("", (a, b) -> a + b + ".")
        .toUpperCase();
  }

  private VBox renderContent(final User user) {
    final VBox container = new VBox();

    final Label username = new Label(user.getUsername());

    container.getChildren().addAll(username, this.renderStats(user));
    container.getStyleClass().add("profile-content");

    return container;
  }

  private HBox renderStats(final User user) {
    final HBox container = new HBox();
    container.setSpacing(20);
    container.getStyleClass().add("profile-stats");

    final String bestWinStreakValue = Integer.toString(user.getBestWinStreak());
    final String fastestTimeValue =
        user.getFastestTime() < 0 ? "-" : Integer.toString(user.getFastestTime());

    container
        .getChildren()
        .addAll(
            this.renderStat(new FlameIcon(), bestWinStreakValue),
            this.renderStat(new ClockIcon(), fastestTimeValue));

    return container;
  }

  private HBox renderStat(final Node icon, final String value) {
    final HBox container = new HBox();
    container.setAlignment(Pos.CENTER);
    container.setSpacing(10);

    icon.setStyle("-fx-background-color: -fx-lightgray-100");
    container.getChildren().addAll(icon, new Label(value));

    return container;
  }
}
