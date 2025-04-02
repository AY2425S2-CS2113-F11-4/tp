package command;

import static constants.ErrorMessages.VIEW_INVALID_INDEX;
import static constants.ErrorMessages.VIEW_OUT_OF_BOUNDS;
import static deck.DeckManager.currentDeck;

import ui.Ui;

/**
 * Class to handle deleting of flashcards
 */
public class CommandDelete extends Command{
    private final String arguments;
    public CommandDelete(String arguments) {
        this.arguments = arguments;
    }

    /**
     * executes command to delete flashcard
     */
    @Override
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            int index = Integer.parseInt(arguments);
            Ui.showToUser(currentDeck.deleteFlashcard(index));
        } catch (NumberFormatException e) {
            Ui.showError(VIEW_INVALID_INDEX);
        } catch (ArrayIndexOutOfBoundsException e) {
            Ui.showError(VIEW_OUT_OF_BOUNDS);
        }
    }
}
