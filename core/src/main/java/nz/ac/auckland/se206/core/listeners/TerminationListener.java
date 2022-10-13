package nz.ac.auckland.se206.core.listeners;

public interface TerminationListener {

  /**
   * This method is invoked when implemented by controllers just before the application is
   * terminated.
   */
  void onTerminate();
}
