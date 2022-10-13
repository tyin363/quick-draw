package nz.ac.auckland.se206.server.controllers;

import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;
import nz.ac.auckland.se206.server.sockets.Server;

@Singleton
public class DashboardController {

  @Inject private Server server;
}
