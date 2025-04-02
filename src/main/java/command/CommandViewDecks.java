package command;

import static deck.DeckManager.viewDecks;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

/**
 * Command to view all decks available.
 *
 * <p>Lists all decks that are available.
 * Displays either the success message or an error message based on the result.</p>
 */
public class CommandViewDecks extends Command {

    /**
     * Executes the command to list decks and shows the result to the user.
     *
     * <p>If an error occurs during deck viewing, an error message is displayed.</p>
     */
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            Ui.showToUser(viewDecks());
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }
}
