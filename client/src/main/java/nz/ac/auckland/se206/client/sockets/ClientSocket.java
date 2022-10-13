package nz.ac.auckland.se206.client.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import nz.ac.auckland.se206.core.models.ActionResponse;
import org.slf4j.Logger;

@Singleton
public class ClientSocket implements EnableListener {

  @Inject private ObjectMapper objectMapper;
  @Inject private Logger logger;

  private Socket socket;
  private PrintWriter writer;
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public void send(final ActionResponse.Action action, final Object value) {
    if (!this.isConnected()) {
      return;
    }
    try {
      this.writer.println(this.objectMapper.writeValueAsString(new ActionResponse(action)));
      this.writer.println(this.objectMapper.writeValueAsString(value));
    } catch (final IOException e) {
      this.logger.error("Failed to send data to server", e);
    }
  }

  @Override
  public void onEnable() {
    try {
      this.tryConnectToServer();
      if (this.isConnected()) {
        this.logger.info("Connected to the server!");
        return;
      }
    } catch (final IOException e) {
      this.logger.error("Failed to connect to server", e);
    }
    // Keep checking to see if you can connect to the server
    this.scheduleCheckForServer();
  }

  public void scheduleCheckForServer() {
    this.executor.scheduleAtFixedRate(this::checkForServer, 15, 15, TimeUnit.SECONDS);
  }

  public boolean isConnected() {
    return this.socket != null && this.socket.isConnected();
  }

  private void tryConnectToServer() throws IOException {
    this.logger.info("Trying to connect to server");
    this.socket = new Socket("localhost", 5001);
    this.writer = new PrintWriter(this.socket.getOutputStream(), true);
  }

  private void checkForServer() {
    if (!this.isConnected()) {
      try {
        this.tryConnectToServer();
        // Double check to make sure the connection was successful
        if (this.isConnected()) {
          this.logger.info("Connected to the server!");
          // Stop trying to connect to the server
          this.executor.shutdownNow();
        }
      } catch (final IOException ignored) {
        // The server must not have started yet... Just ignore this error for now.
      }
    }
  }
}
