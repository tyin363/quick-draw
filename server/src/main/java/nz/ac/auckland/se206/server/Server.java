package nz.ac.auckland.se206.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.listeners.TerminationListener;
import org.slf4j.Logger;

@Singleton
public class Server implements TerminationListener {

  @Inject private Logger logger;
  @Inject private ObjectMapper objectMapper;

  private ServerSocket serverSocket;

  public void start(final String host, final int port) {
    try {
      this.serverSocket = new ServerSocket(port, 100, InetAddress.getByName(host));
      while (true) {
        new ClientSocket(this, this.serverSocket.accept(), this.objectMapper).start();
      }
    } catch (final IOException e) {
      this.logger.error("Failed to start server", e);
    }
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
}
