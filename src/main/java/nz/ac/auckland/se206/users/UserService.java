package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;
import nz.ac.auckland.se206.util.Config;
import org.slf4j.Logger;

@Singleton
public class UserService implements EnableListener {

  private final Map<UUID, User> users = new ConcurrentHashMap<>();
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
    final String dataFilePath = this.config.getUserDataFile().getAbsolutePath();
    final String path = "%s/%s.json".formatted(dataFilePath, user.getId());
    return new File(path);
  }

  /**
   * Returns true if the file has the {@code .json} extension, otherwise false.
   *
   * @param file The file to check the extension of
   * @return Whether the file has the {@code .json} extension
   */
  private boolean hasJsonExtension(final File file) {
    return file.getName().endsWith(".json");
  }

  /**
   * Iterates through all the persisted users in the data directory and loads them into memory. If
   * there is an issue loading one of the users, it will be skipped and logged.
   */
  private void loadAllUsers() {
    // Retrieve all the files in the UserData directory that have the .json extension
    final File[] files = this.config.getUserDataFile().listFiles(this::hasJsonExtension);
    if (files == null) {
      this.logger.error(
          "There is something wrong with the user data directory: "
              + this.config.getUserDataFile().getAbsolutePath());
    } else {
      for (final File file : files) {
        try {
          // Attempt to load the user from the file
          final User user = this.objectMapper.readValue(file, User.class);
          this.users.put(user.getId(), user);
        } catch (final IOException e) {
          this.logger.error("Failed to load user from file: " + file.getAbsolutePath(), e);
        }
      }
    }
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
      this.users.put(user.getId(), user);
      return true;
    } catch (final IOException e) {
      this.logger.error("Failed to save user", e);
    }
    return false;
  }

  /**
   * Retrieves the user with the given UUID. If there is no user with the UUID then null will be
   * returned.
   *
   * @param id The UUID of the user to retrieve
   * @return The user with the given UUID or null if there is no user with the UUID
   */
  public User getUser(final UUID id) {
    return this.users.get(id);
  }

  /**
   * Retrieves a list of all the users that exist. If no users exist then an empty list will be
   * returned.
   *
   * @return A list of all the users that exist.
   */
  public List<User> getUsers() {
    return new ArrayList<>(this.users.values());
  }

  /**
   * Removes the user and deletes the persisted JSON file.
   *
   * @param user The user to delete
   */
  public void deleteUser(final User user) {
    this.users.remove(user.getId());
    if (!this.getUserFile(user).delete()) {
      this.logger.error("Failed to delete user file: " + this.getUserFile(user).getAbsolutePath());
    }
  }

  /** When this instance is first created load all the users from the user data directory. */
  @Override
  public void onEnable() {
    this.loadAllUsers();
    this.logger.info(
        "Loaded {} users from {}",
        this.users.size(),
        this.config.getUserDataFile().getAbsolutePath());
    this.users.values().stream().map(User::toString).forEach(this.logger::info);
  }
}
