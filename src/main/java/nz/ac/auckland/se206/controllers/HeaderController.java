package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import org.slf4j.Logger;

@Singleton
public class HeaderController implements LoadListener {

  @Inject private Logger logger;

  @FXML
  private void initialize() {
    this.logger.info("HeaderController initialized");
  }

  @Override
  public void onLoad() {
    this.logger.info("HeaderController loaded");
  }
}
