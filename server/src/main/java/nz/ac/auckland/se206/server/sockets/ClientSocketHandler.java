package nz.ac.auckland.se206.server.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import nz.ac.auckland.se206.core.models.ActionResponse;
import nz.ac.auckland.se206.core.models.CompleteDrawingModel;

public class ClientSocketHandler extends Thread {

  private final Server server;
  private final Socket clientSocket;
  private final ObjectMapper objectMapper;
  private BufferedReader reader;
  private ActionResponse.Action action = null;

  public ClientSocketHandler(
      final Server server, final Socket clientSocket, final ObjectMapper objectMapper) {
    this.server = server;
    this.clientSocket = clientSocket;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run() {
    try {
      final InputStream input = this.clientSocket.getInputStream();
      this.reader = new BufferedReader(new InputStreamReader(input));

      while (!this.clientSocket.isClosed()) {
        final String line = this.reader.readLine();
        if (line != null) {
          if (this.action == null) {
            final ActionResponse actionResponse =
                this.objectMapper.readValue(line, ActionResponse.class);
            this.action = actionResponse.action();
            if (this.action == ActionResponse.Action.TERMINATE_CONNECTION) {
              break;
            }
          } else {
            this.handleResponse(this.action, line);
            this.action = null;
          }
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    this.close();
  }

  public void close() {
    try {
      this.clientSocket.close();
      this.reader.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void handleResponse(final ActionResponse.Action action, final String line)
      throws IOException {
    if (action == ActionResponse.Action.COMPLETE_DRAWING) {
      final CompleteDrawingModel completeDrawingModel =
          this.objectMapper.readValue(line, CompleteDrawingModel.class);
      System.out.println(completeDrawingModel);
    }
  }
}
