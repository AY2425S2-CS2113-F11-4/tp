package constants;

import static constants.CommandConstants.ADD_CARD;
import static constants.CommandConstants.NEW_DECK;
import static constants.CommandConstants.SWITCH_DECK;
import static constants.CommandConstants.LIST_CARDS;
import static constants.CommandConstants.VIEW_QN;
import static constants.CommandConstants.VIEW_ANS;
import static constants.CommandConstants.DELETE_CARD;
import static constants.CommandConstants.EDIT_CARD;
import static constants.CommandConstants.INSERT_CODE;
import static constants.CommandConstants.MARK_LEARNED;
import static constants.CommandConstants.MARK_UNLEARNED;
import static constants.CommandConstants.QUIZ;
import static constants.CommandConstants.VIEW_RES;
import static constants.CommandConstants.RENAME_DECK;
import static constants.CommandConstants.VIEW_DECKS;
import static constants.CommandConstants.SEARCH_CARD;
import static constants.CommandConstants.DELETE_DECK;
import static constants.CommandConstants.EXIT;

/**
 * Class to store the user guide information.
 */
public class UserGuideMessages {
    public static final String USER_GUIDE_INFORMATION =
            "Quick Start:\n" +
                    "Create a deck of flashcards with \"" + NEW_DECK +
                    "\", select it with \"" + SWITCH_DECK +
                    "\", and begin adding flashcards with \"" + ADD_CARD + "\"!\n\n" +
                    "List of commands:\n" +
                    "\"" + ADD_CARD + "\": creates a flashcard\n" +
                    "\"" + LIST_CARDS + "\": shows list of flashcards\n" +
                    "\"" + VIEW_QN + "\": views the flashcard's question\n" +
                    "\"" + VIEW_ANS + "\": views the flashcard's answer\n" +
                    "\"" + DELETE_CARD + "\": deletes the flashcard\n" +
                    "\"" + EDIT_CARD + "\": edits the flashcard question/answer\n" +
                    "\"" + INSERT_CODE + "\": inserts code snippet for a flashcard\n" +
                    "\"" + MARK_LEARNED + "\": marks flashcard as learned\n" +
                    "\"" + MARK_UNLEARNED + "\": marks flashcard as unlearned\n" +
                    "\"" + QUIZ + "\": quizzes the unlearned flashcards\n" +
                    "\"" + VIEW_RES + "\": views results from quiz after quiz is completed\n" +
                    "\"" + NEW_DECK + "\": creates a new deck of flashcards\n" +
                    "\"" + RENAME_DECK + "\": renames flashcard deck\n" +
                    "\"" + VIEW_DECKS + "\": shows list of decks\n" +
                    "\"" + SWITCH_DECK + "\": selects deck of flashcards\n" +
                    "\"" + SEARCH_CARD + "\": searches within a deck or globally\n" +
                    "\"" + DELETE_DECK + "\": deletes the deck\n" +
                    "\"" + EXIT + "\": exits the program\n\n" +
                    "Go to the flashCli User Guide website for more details";
}
