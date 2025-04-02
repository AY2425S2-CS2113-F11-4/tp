package command;

import static constants.ErrorMessages.INVALID_INDEX_INPUT;
import static constants.ErrorMessages.INDEX_OUT_OF_BOUNDS;
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
            Ui.showError(INVALID_INDEX_INPUT);
        } catch (ArrayIndexOutOfBoundsException e) {
            Ui.showError(INDEX_OUT_OF_BOUNDS);
        }
    }
}
