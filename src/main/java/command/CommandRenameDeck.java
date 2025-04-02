package command;

import static deck.DeckManager.renameDeck;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

/**
 * Command to rename a selected deck.
 *
 * <p>Renames a deck to the provided new deck name.
 * Displays either the success message or an error message based on the result.</p>
 */
public class CommandRenameDeck extends Command {
    private final String arguments;

    public CommandRenameDeck(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Executes the command to rename the deck and shows the result to the user.
     *
     * <p>If an error occurs during renaming, an error message is displayed.</p>
     */
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            Ui.showToUser(renameDeck(this.arguments));
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }
}
