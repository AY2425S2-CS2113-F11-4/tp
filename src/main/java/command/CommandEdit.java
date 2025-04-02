package command;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static constants.ErrorMessages.EDIT_USAGE;
import static constants.ErrorMessages.VIEW_INVALID_INDEX;
import static constants.ErrorMessages.VIEW_OUT_OF_BOUNDS;
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
            Ui.showError(VIEW_OUT_OF_BOUNDS);
        } catch (NumberFormatException e) {
            Ui.showError(VIEW_INVALID_INDEX);
        } catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
            Ui.showError(EDIT_USAGE);
        }
    }
}
