package nz.ac.auckland.se206.controllers;

import nz.ac.auckland.se206.annotations.Controller;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.View;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;
import org.slf4j.Logger;

@Singleton
@Controller(value = {View.CANVAS, View.MAIN_MENU, View.CONFIRMATION_SCREEN})
public class HeaderController implements LoadListener {

  @Inject private Logger logger;

  @Override
  public void onLoad() {
    this.logger.info("HeaderController loaded");
  }
}
