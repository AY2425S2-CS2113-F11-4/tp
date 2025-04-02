package command;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static constants.ErrorMessages.EDIT_USAGE;
import static constants.ErrorMessages.INVALID_INDEX_INPUT;
import static constants.ErrorMessages.INDEX_OUT_OF_BOUNDS;
import static deck.DeckManager.currentDeck;

public class CommandEdit extends Command {
    private final String arguments;

    public CommandEdit(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            int index = Integer.parseInt(arguments.split(" ", 2)[0]);
            Ui.showToUser(currentDeck.editFlashcard(index, arguments));
        } catch (ArrayIndexOutOfBoundsException e) {
            Ui.showError(INDEX_OUT_OF_BOUNDS);
        } catch (NumberFormatException e) {
            Ui.showError(INVALID_INDEX_INPUT);
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
            Ui.showError(EDIT_USAGE);
        }
    }
}
