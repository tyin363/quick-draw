package nz.ac.auckland.se206.components;

import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nz.ac.auckland.se206.users.User;

public class UserProfile extends VBox {

  public UserProfile(final User user) {
    this.getStyleClass().add("profile-card");
    this.getChildren().addAll(this.renderProfilePicture(user), this.renderStats(user));
  }

  private VBox renderProfilePicture(final User user) {
    final VBox container = new VBox();
    container.setAlignment(Pos.CENTER);

    if (user.getProfilePicture() != null) {
      final BackgroundImage image =
          new BackgroundImage(new Image(user.getProfilePicture()), null, null, null, null);
      final Background background = new Background(image);

      container.setBackground(background);
      container.getStyleClass().add("profile-picture");
    } else {
      final Label abbreviatedUsername = new Label(this.getAbbreviatedUsername(user.getUsername()));

      container.getChildren().add(abbreviatedUsername);
      container.getStyleClass().add("no-image");
    }

    return container;
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

    return container;
  }
}
