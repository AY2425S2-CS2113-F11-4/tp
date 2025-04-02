package command;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static deck.DeckManager.currentDeck;
import static deck.DeckManager.globalSearch;

public class CommandSearchFlashcard extends Command {
    private final String arguments;
    public CommandSearchFlashcard(String arguments) {
        this.arguments = arguments;
    }
    /**
     * Executes the command to search globally/locally depending on whether the deck is selected
     * and shows the result to the user.
     *
     * <p>If an error occurs during deck creation, an error message is displayed.</p>
     */
    @Override
    public void executeCommand() {
        try {
            String list;
            if (currentDeck == null) {
                list = globalSearch(arguments);
            } else {
                list = currentDeck.searchFlashcard(arguments);
            }
            Ui.showToUser(list);
        } catch (FlashCLIArgumentException | EmptyListException e) {
            Ui.showError(e.getMessage());
        }
    }
}
