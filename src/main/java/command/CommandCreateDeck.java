package command;

import static deck.DeckManager.createDeck;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

/**
 * Command to create a new deck.
 *
 * <p>Executes the creation of a deck using the provided arguments.
 * Displays either the success message or an error message based on the result.</p>
 */
public class CommandCreateDeck extends Command{
    public final String arguments;

    public CommandCreateDeck(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Executes the command to create the deck and shows the result to the user.
     *
     * <p>If an error occurs during deck creation, an error message is displayed.</p>
     */
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            Ui.showToUser(createDeck(arguments));
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }

}
