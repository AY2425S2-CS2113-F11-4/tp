package command;

import static deck.DeckManager.switchDeck;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

/**
 * Command to switch to a deck.
 *
 * <p>Switches to a deck that is available.
 * Displays either the success message or an error message based on the result.</p>
 */
public class CommandSwitchDeck extends Command {
    private final String arguments;

    public CommandSwitchDeck(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Executes the command to switch to the deck and shows the result to the user.
     *
     * <p>If an error occurs during switching, an error message is displayed.</p>
     */
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            Ui.showToUser(switchDeck(arguments));
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }
}
