package nz.ac.auckland.se206.client.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import nz.ac.auckland.se206.client.statemachine.CanvasStateMachine;
import nz.ac.auckland.se206.client.statemachine.states.DefaultCanvasState;
import nz.ac.auckland.se206.client.users.UserService;
import nz.ac.auckland.se206.client.util.View;
import nz.ac.auckland.se206.client.words.WordService;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import nz.ac.auckland.se206.core.models.ActionResponse;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import nz.ac.auckland.se206.core.scenemanager.SceneManager;
import org.slf4j.Logger;

@Singleton
public class ClientSocket implements EnableListener, TerminationListener {

  @Inject private ObjectMapper objectMapper;
  @Inject private WordService wordService;
  @Inject private SceneManager sceneManager;
  @Inject private UserService userService;
  @Inject private CanvasStateMachine stateMachine;
  @Inject private Logger logger;
  private Socket socket;
  private PrintWriter writer;
  private BufferedReader reader;
  private Thread handlerThread;
  private boolean isConnected = true;
  private boolean isStopped = false;

  public void send(final ActionResponse.Action action, final Object value) {
    if (!this.isConnected() || this.writer == null) {
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
    while (!this.isStopped) {
      try {
        this.checkForServer();
        if (!this.isConnected()) {
          // Only check if the server is up every 10 seconds
          Thread.sleep(10_000);
        } else {
          this.handleSocketInput();
        }
      } catch (final InterruptedException e) {
        this.logger.error("Failed to sleep", e);
      } catch (final IOException ignored) {
        // If we fail to read the input, it's likely we've lost connection to the server
        this.logger.info("Lost connection to server");
        this.isConnected = false;
      }
    }
  }

  private void handleSocketInput() throws IOException {
    final int peek = this.reader.read();
    // If the server has closed the connection, we'll get a -1
    if (peek == -1) {
      this.logger.info("Lost connection to server");
      this.isConnected = false;
      return;
    }

    // Peek removes the character from the stream, so we need to add it back to form the full line
    final String line = (char) peek + this.reader.readLine();
    final DrawingSessionRequest request =
        this.objectMapper.readValue(line, DrawingSessionRequest.class);
    if (this.userService.getCurrentUser() != null) {
      this.wordService.setTargetWord(request.word());
      Platform.runLater(
          () -> {
            this.stateMachine.switchState(DefaultCanvasState.class);
            this.sceneManager.switchToView(View.CANVAS);
          });
    }
    this.logger.info("Received drawing session request: {}", request);
  }

  /**
   * Checks if the client is connected to the server.
   *
   * @return If the client is connected to the server
   */
  public boolean isConnected() {
    return this.socket != null && this.socket.isConnected() && this.isConnected;
  }

  /**
   * Attempts to connect to the server. If the connection fails then an {@link IOException} is
   * thrown.
   *
   * @throws IOException If the connection fails
   */
  private void tryConnectToServer() throws IOException {
    if (this.socket != null) {
      this.socket.close();
    }
    this.logger.info("Trying to connect to server");
    // Establish a connection to the server and create a reader/writer
    this.socket = new Socket("localhost", 5001);
    this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    final InputStream input = this.socket.getInputStream();
    this.reader = new BufferedReader(new InputStreamReader(input));
    this.isConnected = true;
  }

  /**
   * Checks if the client is connected to the server. If it is not, then it attempts to connect to
   * it.
   */
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

  /** */
  @Override
  public void onTerminate() {
    this.logger.info("Terminating client socket");
    try {
      this.isStopped = true;
      // Make sure to close all the threads
      this.handlerThread.interrupt();
      this.socket.close();
    } catch (final IOException e) {
      this.logger.error("Failed to close socket", e);
    }
  }
}
