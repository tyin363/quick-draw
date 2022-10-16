open module SE206Server {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.slf4j;
  requires com.fasterxml.jackson.databind;
  requires SE206Core;

  exports nz.ac.auckland.se206.server;
  exports nz.ac.auckland.se206.server.sockets;
}
