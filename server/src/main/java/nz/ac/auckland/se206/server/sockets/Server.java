package nz.ac.auckland.se206.server.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import org.slf4j.Logger;

@Singleton
public class Server implements TerminationListener, EnableListener {

  private final CopyOnWriteArrayList<ClientSocketHandler> clients = new CopyOnWriteArrayList<>();
  private final Vector<Runnable> onClientCountChangeListeners = new Vector<>();
  @Inject private Logger logger;
  @Inject private ObjectMapper objectMapper;
  private ServerSocket serverSocket;

  /**
   * Starts the server on the specified host and port.
   *
   * @param host The host/ip to start the server on
   * @param port The port to start the server on
   */
  public void start(final String host, final int port) {
    try {
      // Start a server socket on the specified host and port
      final InetAddress address = InetAddress.getByName(host);
      this.serverSocket = new ServerSocket(port, 100, address);
      this.logger.info("Server started on port {} at {}", port, address.getHostAddress());
      // Start a separate thread for handling incoming connections as this is a blocking call
      new Thread(this::handleClientConnections).start();
    } catch (final IOException e) {
      this.logger.error("Failed to start server", e);
    }
  }

  /**
   * A method that runs on a separate thread which handles the processes of accepting new clients.
   */
  public void handleClientConnections() {
    // Only keep running this loop if the server is running
    while (this.isRunning()) {
      try {
        // This will wait until a client connects.
        final ClientSocketHandler client =
            new ClientSocketHandler(this, this.serverSocket.accept(), this.objectMapper);
        this.clients.add(client);
        client.start();
        this.onClientCountChangeListeners.forEach(Runnable::run);
      } catch (final IOException e) {
        this.logger.error("Failed to accept client connection", e);
      }
    }
  }

  /**
   * Checks if the server is currently running.
   *
   * @return True if the server is running, false otherwise
   */
  public boolean isRunning() {
    return this.serverSocket != null && !this.serverSocket.isClosed();
  }

  /**
   * Sends a drawing session payload to all the currently connected clients.
   *
   * @param word The word to send to the clients
   */
  public void startDrawingSession(final String word) {
    this.logger.info("Sending drawing session request to {} clients", this.clients.size());
    final DrawingSessionRequest request = new DrawingSessionRequest(word);
    this.clients.forEach(client -> client.sendDrawingSessionRequest(request));
  }

  /**
   * Add a listener to be called when the number of connected clients changes.
   *
   * @param listener The listener to be called
   */
  public void addClientCountChangeListener(final Runnable listener) {
    this.onClientCountChangeListeners.add(listener);
  }

  /**
   * Removes a client from the list of clients. This is automatically called when a client
   * disconnects.
   *
   * @param clientSocketHandler The client to remove
   */
  public void removeClient(final ClientSocketHandler clientSocketHandler) {
    this.clients.remove(clientSocketHandler);
    this.onClientCountChangeListeners.forEach(Runnable::run);
  }

  /** Stops the server and closes all client connections. */
  public void stop() {
    try {
      // When stopping the server, close the server socket and all client sockets.
      this.clients.forEach(ClientSocketHandler::close);
      this.serverSocket.close();
    } catch (final IOException e) {
      this.logger.error("Failed to close the server", e);
    }
  }

  /**
   * Retrieve the number of currently connected clients.
   *
   * @return The number of currently connected clients
   */
  public int clientCount() {
    return this.clients.size();
  }

  /** Stop the server thread when the application is closed. */
  @Override
  public void onTerminate() {
    this.stop();
  }

  /** When this instance is created, start the server. */
  @Override
  public void onEnable() {
    this.start("localhost", 5001);
  }
}
