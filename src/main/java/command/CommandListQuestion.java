package command;

import exceptions.EmptyListException;
import ui.Ui;

import static deck.DeckManager.currentDeck;

/**
 * Class that handles listing all the questions
 */
public class CommandListQuestion extends Command{
    /**
     * executes the command to view all the questions.
     */
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            String list = currentDeck.listFlashcards();
            Ui.showToUser(list);
        } catch (EmptyListException e) {
            Ui.showError(e.getMessage());
        }
    }
}
