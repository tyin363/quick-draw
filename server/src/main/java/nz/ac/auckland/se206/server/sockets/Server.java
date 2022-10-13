package nz.ac.auckland.se206.server.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Vector;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.EnableListener;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;
import org.slf4j.Logger;

@Singleton
public class Server implements TerminationListener, EnableListener {

  @Inject private Logger logger;
  @Inject private ObjectMapper objectMapper;

  private final Vector<ClientSocketHandler> clients = new Vector<>();
  private ServerSocket serverSocket;

  public boolean isRunning() {
    return this.serverSocket != null && !this.serverSocket.isClosed();
  }

  public void start(final String host, final int port) {
    try {
      this.serverSocket = new ServerSocket(port, 100, InetAddress.getByName(host));
      this.logger.info(
          "Server started on port {} at {}",
          port,
          this.serverSocket.getInetAddress().getHostAddress());
      new Thread(this::handleClientConnections).start();
    } catch (final IOException e) {
      this.logger.error("Failed to start server", e);
    }
  }

  public void handleClientConnections() {
    while (this.isRunning()) {
      try {
        final ClientSocketHandler client =
            new ClientSocketHandler(this, this.serverSocket.accept(), this.objectMapper);
        this.clients.add(client);
        client.start();
      } catch (final IOException e) {
        this.logger.error("Failed to accept client connection", e);
      }
    }
  }

  public void startDrawingSession(final String word) {
    final DrawingSessionRequest request = new DrawingSessionRequest(word);
    this.clients.forEach(client -> client.sendDrawingSessionRequest(request));
  }

  public void stop() {
    try {
      this.serverSocket.close();
    } catch (final IOException e) {
      this.logger.error("Failed to close the server", e);
    }
  }

  @Override
  public void onTerminate() {
    this.stop();
  }

  @Override
  public void onEnable() {
    this.start("localhost", 5001);
  }
}
