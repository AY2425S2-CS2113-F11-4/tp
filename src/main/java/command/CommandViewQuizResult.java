package command;

import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static deck.DeckManager.currentDeck;

/**
 * Class that handles viewing of the quiz results
 */

public class CommandViewQuizResult extends Command{
    /**
     * executes command to quiz all the questions in the deck.
     */
    @Override
    public void executeCommand() {
        try {
            Ui.showToUser(currentDeck.showQuizResult());
        }  catch (FlashCLIArgumentException e) {
            Ui.showError(e.getMessage());
        }
    }
}
