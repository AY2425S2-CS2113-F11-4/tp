package command;

import exceptions.EmptyListException;
import exceptions.QuizCancelledException;
import ui.Ui;

import static deck.DeckManager.currentDeck;

/**
 * Class that handles quizzing of the current Deck.
 */
public class CommandQuizFlashcards extends Command{
    /**
     * executes command to quiz all the questions in the deck.
     */
    @Override
    public void executeCommand() {
        try {
            Ui.loadingeffect();
            currentDeck.quizFlashcards();
        } catch (EmptyListException e) {
            Ui.showError(e.getMessage());
        } catch (QuizCancelledException e) {
            Ui.showToUser(e.getMessage());
        }
    }
}
