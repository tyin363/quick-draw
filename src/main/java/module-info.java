module SE206Project {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  requires freetts;
  requires ai.djl.api;
  requires imgscalr.lib;
  requires org.slf4j;
  requires com.opencsv;
  requires javafx.graphics;
  requires javafx.base;

  opens nz.ac.auckland.se206 to
      javafx.fxml;
  opens nz.ac.auckland.se206.util to
      javafx.fxml;
  opens nz.ac.auckland.se206.controllers to
      javafx.fxml;
  opens nz.ac.auckland.se206.controllers.scenemanager to
      javafx.fxml;

  exports nz.ac.auckland.se206;
}
