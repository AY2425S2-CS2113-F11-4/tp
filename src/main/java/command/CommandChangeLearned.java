package command;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static constants.ErrorMessages.INVALID_INDEX_INPUT;
import static deck.DeckManager.currentDeck;

public class CommandChangeLearned extends Command {
    private final String arguments;
    private final boolean isLearned;

    public CommandChangeLearned(String arguments, boolean isLearned) {
        this.arguments = arguments;
        this.isLearned = isLearned;
    }

    public void executeCommand() {
        try {
            String output = currentDeck.changeIsLearned(arguments, isLearned);
            Ui.showToUser(output);
        } catch (NumberFormatException e) {
            Ui.showError(INVALID_INDEX_INPUT);
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }

    }
}
