package command;

import static deck.DeckManager.deleteDeck;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

public class CommandDeleteDeck extends Command {

    private final String arguments;

    public CommandDeleteDeck(String arguments) {
        this.arguments = arguments;
    }

    public void executeCommand() {
        try {
            Ui.showToUser(deleteDeck(this.arguments));
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }

}
