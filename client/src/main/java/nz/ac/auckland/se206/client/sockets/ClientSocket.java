package nz.ac.auckland.se206.client.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.core.models.ActionResponse;
import org.slf4j.Logger;

@Singleton
public class ClientSocket {

  @Inject private ObjectMapper objectMapper;
  @Inject private Logger logger;

  private Socket socket;
  private PrintWriter writer;

  public ClientSocket() {
    try {
      this.socket = new Socket("localhost", 5001);
      this.writer = new PrintWriter(this.socket.getOutputStream(), true);
    } catch (final IOException e) {
      this.logger.error("Failed to connect to server", e);
    }
  }

  public void send(final ActionResponse.Action action, final Object value) {
    try {
      this.writer.println(this.objectMapper.writeValueAsString(new ActionResponse(action)));
      this.writer.println(this.objectMapper.writeValueAsString(value));
    } catch (final IOException e) {
      this.logger.error("Failed to send data to server", e);
    }
  }
}
