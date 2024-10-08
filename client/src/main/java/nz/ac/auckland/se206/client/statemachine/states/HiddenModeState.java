package nz.ac.auckland.se206.client.statemachine.states;

import nz.ac.auckland.se206.client.hiddenmode.HiddenMode;
import nz.ac.auckland.se206.client.users.Round.Mode;
import nz.ac.auckland.se206.core.annotations.Inject;
import nz.ac.auckland.se206.core.annotations.Singleton;

@Singleton(injectSuper = true)
public class HiddenModeState extends NormalCanvasState {

  @Inject private HiddenMode hiddenMode;

  /**
   * When the hidden mode canvas is switched to, make sure to make the definitions and hint elements
   * visible.
   */
  @Override
  public void onEnter() {
    // Set hidden mode exclusive elements to be visible
    this.canvasController.getWordDefinition().setVisible(true);
    this.canvasController.getDefaultHbox().setVisible(false);
  }

  /** Place the default canvas content back when we leave this state. */
  @Override
  public void onExit() {
    this.hiddenMode.clearDefinitions();

    // Make appropriate elements invisible or visible again
    this.canvasController.getHintsHbox().setVisible(false);
    this.canvasController.getWordDefinition().setVisible(false);
    this.canvasController.getDefaultHbox().setVisible(true);
  }

  /** When the hidden mode canvas is loaded, display the hint container. */
  @Override
  public void onLoad() {
    super.onLoad();
    this.canvasController.getHintsHbox().setVisible(true);
    this.canvasController.getTargetWordConfidenceLabel().setText("Hidden");
  }

  /**
   * {@inheritDoc}
   *
   * @param wasGuessed {@inheritDoc}
   */
  @Override
  public void gameOver(final boolean wasGuessed) {
    // Set hints elements invisible
    this.canvasController.getHintsHbox().setVisible(false);
    super.gameOver(wasGuessed);
  }

  /**
   * {@inheritDoc}
   *
   * @param wasGuessed {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  protected String getConclusionMessage(final boolean wasGuessed) {
    return "%s The word was %s."
        .formatted(super.getConclusionMessage(wasGuessed), this.wordService.getTargetWord());
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  protected Mode getMode() {
    return Mode.HIDDEN;
  }
}
