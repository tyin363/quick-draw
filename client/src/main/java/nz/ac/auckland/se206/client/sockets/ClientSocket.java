package nz.ac.auckland.se206.client.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import nz.ac.auckland.se206.core.models.ActionResponse;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import org.slf4j.Logger;

@Singleton
public class ClientSocket implements EnableListener, TerminationListener {

  @Inject private ObjectMapper objectMapper;
  @Inject private Logger logger;
  private Socket socket;
  private PrintWriter writer;
  private BufferedReader reader;
  private Thread handlerThread;
  private boolean connected = true;

  public void send(final ActionResponse.Action action, final Object value) {
    if (!this.isConnected()) {
      this.logger.warn("Cannot send message to server, not connected");
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
    this.handlerThread = new Thread(this::handleSocketConnection);
    this.handlerThread.start();
  }

  private void handleSocketConnection() {
    while (true) {
      try {
        this.checkForServer();
        if (!this.isConnected()) {
          Thread.sleep(10_000); // Only check if the server is up every 10 seconds
        } else {
          this.handleSocketInput();
        }
      } catch (final InterruptedException e) {
        this.logger.error("Failed to sleep", e);
      } catch (final IOException ignored) {
        // If we fail to read the input, it's likely we've lost connection to the server
        this.logger.info("Lost connection to server");
        this.connected = false;
      }
    }
  }

  private void handleSocketInput() throws IOException {
    // This is a method that can be used to detect if the connection has been lost
    // https://www.alpharithms.com/detecting-client-disconnections-java-sockets-091416/
    if (this.reader.read() == -1) {
      this.logger.info("Lost connection to server");
      this.connected = false;
      return;
    }

    final String line = this.reader.readLine();
    if (line != null) {
      final DrawingSessionRequest request =
          this.objectMapper.readValue(line, DrawingSessionRequest.class);
      this.logger.info("Received drawing session request: {}", request);
    }
  }

  public boolean isConnected() {
    return this.socket != null && this.socket.isConnected() && this.connected;
  }

  private void tryConnectToServer() throws IOException {
    this.logger.info("Trying to connect to server");
    // Establish a connection to the server and create a reader/writer
    this.socket = new Socket("localhost", 5001);
    this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    final InputStream input = this.socket.getInputStream();
    this.reader = new BufferedReader(new InputStreamReader(input));
    this.connected = true;
  }

  private void checkForServer() {
    if (!this.isConnected()) {
      try {
        this.tryConnectToServer();
        // Double check to make sure the connection was successful
        if (this.isConnected()) {
          this.logger.info("Connected to the server!");
        }
      } catch (final IOException ignored) {
        // The server must not have started yet... Just ignore this error for now.
      }
    }
  }

  @Override
  public void onTerminate() {
    this.handlerThread.interrupt();
    this.logger.info("Terminating client socket");
  }
}
