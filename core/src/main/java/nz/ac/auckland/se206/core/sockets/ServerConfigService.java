package nz.ac.auckland.se206.core.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import org.slf4j.Logger;

@Singleton
public class ServerConfigService implements EnableListener {

  @Inject private ObjectMapper objectMapper;
  @Inject private Logger logger;

  private ServerConfig config;

  /**
   * Attempts to load the server config from config.json. If the file doesn't exist then a default
   * config file is created.
   */
  @Override
  public void onEnable() {
    final File configFile = new File("config.json");
    if (configFile.exists()) {
      try {
        // Attempt to read the config file
        this.config = this.objectMapper.readValue(configFile, ServerConfig.class);
        this.logger.info("Loaded server config: {}", this.config);
        return;
      } catch (final IOException e) {
        this.logger.warn("Failed to read server config file", e);
      }
    }
    // If the config file doesn't exist or there was an error reading it, use a default config
    this.config = new ServerConfig("localhost", 5001);
    this.logger.info("Loaded default server config: {}", this.config);
  }

  /**
   * Retrieves the server config which has been loaded from config.json.
   *
   * @return The server config
   */
  public ServerConfig getConfig() {
    return this.config;
  }
}
