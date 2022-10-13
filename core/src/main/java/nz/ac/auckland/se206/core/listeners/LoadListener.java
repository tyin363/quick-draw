package nz.ac.auckland.se206.core.listeners;

public interface LoadListener {

  /**
   * The method that is invoked everytime the view corresponding to the controller is switched to.
   * This is called after the initialized callback of the controller.
   */
  void onLoad();
}
