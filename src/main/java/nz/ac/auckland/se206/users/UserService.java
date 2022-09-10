package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.util.Config;
import org.slf4j.Logger;

@Singleton
public class UserService {

  @Inject private Logger logger;
  @Inject private Config config;
  @Inject private ObjectMapper objectMapper;

  /**
   * Each user will be persisted to their own JSON file where the filename is the UUID of the user.
   * This should improve saving performance as we won't have to save every user to prevent existing
   * users from being overwritten.
   *
   * @param user The user to retrieve the JSON file for
   * @return The file where the user is persisted.
   */
  private File getUserFile(final User user) {
    final String path = "%s/%s.json".formatted(this.config.getUserDataFile(), user.getId());
    return new File(path);
  }

  /**
   * Saves the given user to their respective JSON file and returns whether the save was successful.
   *
   * @param user The user to save
   * @return Whether the save was successful
   */
  public boolean saveUser(final User user) {
    try {
      this.objectMapper.writeValue(this.getUserFile(user), user);
      return true;
    } catch (final IOException e) {
      this.logger.error("Failed to save user", e);
    }
    return false;
  }
}
