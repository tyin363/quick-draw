open module SE206Core {
  requires javafx.graphics;
  requires javafx.fxml;
  requires org.slf4j;
  requires com.fasterxml.jackson.databind;

  exports nz.ac.auckland.se206.core.annotations;
  exports nz.ac.auckland.se206.core.di;
  exports nz.ac.auckland.se206.core.listeners;
  exports nz.ac.auckland.se206.core.scenemanager;
}
