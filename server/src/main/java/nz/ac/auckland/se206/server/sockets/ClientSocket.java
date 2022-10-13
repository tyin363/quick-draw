package nz.ac.auckland.se206.server.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import nz.ac.auckland.se206.server.models.ActionResponse;
import nz.ac.auckland.se206.server.models.CompleteDrawingModel;

public class ClientSocket extends Thread {

  private final Server server;
  private final Socket clientSocket;
  private final ObjectMapper objectMapper;
  private BufferedReader reader;

  public ClientSocket(
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
        final ActionResponse actionResponse =
            this.objectMapper.readValue(this.reader, ActionResponse.class);
        this.handleResponse(actionResponse.action());
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void handleResponse(final ActionResponse.Action action) throws IOException {
    if (action == ActionResponse.Action.COMPLETE_DRAWING) {
      final CompleteDrawingModel completeDrawingModel =
          this.objectMapper.readValue(this.reader, CompleteDrawingModel.class);
      System.out.println(completeDrawingModel);
    } else if (action == ActionResponse.Action.TERMINATE_CONNECTION) {
      this.clientSocket.close();
    }
  }
}
