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

  /**
   * Constructs a new ClientSocketHandler with the given server and client socket.
   *
   * @param server The server instance
   * @param clientSocket The connected client socket
   * @param objectMapper The object mapper to use for serialisation/deserialisation
   */
  public ClientSocketHandler(
      final Server server, final Socket clientSocket, final ObjectMapper objectMapper) {
    this.server = server;
    this.clientSocket = clientSocket;
    this.objectMapper = objectMapper;
  }

  /** The main loop for handling incoming messages from the client. */
  @Override
  public void run() {
    try {
      // Retrieve the input and output streams from the socket
      final InputStream input = this.clientSocket.getInputStream();
      this.reader = new BufferedReader(new InputStreamReader(input));
      this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);

      while (!this.clientSocket.isClosed()) {
        this.handleSocketInput();
      }
    } catch (final IOException ignored) {
      // We've likely lost connection to the client
    }
    this.close();
  }

  /**
   * Checks if the client has sent an action and if so, will parse the payload corresponding to it.
   *
   * @throws IOException If the connection to the client has been lost or terminated.
   */
  private void handleSocketInput() throws IOException {
    // Modified from:
    // https://www.alpharithms.com/detecting-client-disconnections-java-sockets-091416/
    final int peek = this.reader.read();
    if (peek == -1) {
      // The client has disconnected
      throw new IOException("Client connection lost");
    }

    final String line = (char) peek + this.reader.readLine();
    if (this.action == null) {
      final ActionResponse response = this.objectMapper.readValue(line, ActionResponse.class);
      if (this.action == ActionResponse.Action.TERMINATE_CONNECTION) {
        throw new IOException("Client disconnected");
      }
      // Store the action, so we know what payload to expect.
      this.action = response.action();
    } else {
      this.handleResponse(this.action, line);
      this.action = null;
    }
  }

  /**
   * Sends a drawing session request to the client.
   *
   * @param request The drawing session request to send
   */
  public void sendDrawingSessionRequest(final DrawingSessionRequest request) {
    try {
      // Send the request to the client
      this.writer.println(this.objectMapper.writeValueAsString(request));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /** Closes the connection to the client and removes this handler from the server. */
  public void close() {
    try {
      this.clientSocket.close();
      this.reader.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    // Remove this handler from the server.
    this.server.removeClient(this);
  }

  /**
   * Handle the response payload from the client based on the previously retrieved action.
   *
   * @param action The action to handle
   * @param line The payload to handle
   * @throws IOException If there is an error parsing the payload
   */
  private void handleResponse(final ActionResponse.Action action, final String line)
      throws IOException {
    if (action == ActionResponse.Action.COMPLETE_DRAWING) {
      // Read in the complete drawing response
      final CompleteDrawingResponse completeDrawingResponse =
          this.objectMapper.readValue(line, CompleteDrawingResponse.class);
      System.out.println(completeDrawingResponse);
    }
  }
}
