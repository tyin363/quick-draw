package nz.ac.auckland.se206.server.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import nz.ac.auckland.se206.core.models.ActionResponse;
import nz.ac.auckland.se206.core.models.CompleteDrawingResponse;
import nz.ac.auckland.se206.core.models.DrawingSessionRequest;

public class ClientSocketHandler extends Thread {

  private final Server server;
  private final Socket clientSocket;
  private final ObjectMapper objectMapper;
  private BufferedReader reader;
  private PrintWriter writer;
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
      this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);

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

  public void sendDrawingSessionRequest(final DrawingSessionRequest request) {
    try {
      this.writer.println(this.objectMapper.writeValueAsString(request));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      this.clientSocket.close();
      this.reader.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    this.server.removeClient(this);
  }

  private void handleResponse(final ActionResponse.Action action, final String line)
      throws IOException {
    if (action == ActionResponse.Action.COMPLETE_DRAWING) {
      final CompleteDrawingResponse completeDrawingResponse =
          this.objectMapper.readValue(line, CompleteDrawingResponse.class);
      System.out.println(completeDrawingResponse);
    }
  }
}
