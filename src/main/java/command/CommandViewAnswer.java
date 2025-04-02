package command;

import static constants.ErrorMessages.VIEW_INVALID_INDEX;
import static constants.ErrorMessages.VIEW_OUT_OF_BOUNDS;
import static deck.DeckManager.currentDeck;

import ui.Ui;

/**
 * class to handle viewing of flashcard answer
 */
public class CommandViewAnswer extends Command {
    private final String arguments;
    public CommandViewAnswer(String arguments) {
        this.arguments = arguments;
    }

    /**
     * executes the command to view the answer
     */
    @Override
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            int index = Integer.parseInt(arguments);
            Ui.showToUser(currentDeck.viewFlashcardAnswer(index));
        } catch (NumberFormatException e) {
            Ui.showError(VIEW_INVALID_INDEX);
        } catch (ArrayIndexOutOfBoundsException e) {
            Ui.showError(VIEW_OUT_OF_BOUNDS);
        }
    }
}
