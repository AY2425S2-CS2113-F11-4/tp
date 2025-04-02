package constants;

import static constants.CommandConstants.ADD_CARD;
import static constants.CommandConstants.VIEW_QN;
import static constants.CommandConstants.VIEW_ANS;
import static constants.CommandConstants.EDIT_CARD;
import static constants.CommandConstants.LIST_CARDS;
import static constants.CommandConstants.DELETE_CARD;
import static constants.CommandConstants.INSERT_CODE;
import static constants.CommandConstants.SEARCH_CARD;
import static constants.CommandConstants.QUIZ;
import static constants.CommandConstants.MARK_UNLEARNED;
import static constants.CommandConstants.MARK_LEARNED;
import static constants.CommandConstants.VIEW_RES;
import static constants.CommandConstants.NEW_DECK;
import static constants.CommandConstants.SWITCH_DECK;
import static constants.CommandConstants.RENAME_DECK;
import static constants.CommandConstants.VIEW_DECKS;
import static constants.CommandConstants.DELETE_DECK;
import static constants.CommandConstants.USER_GUIDE;
import static constants.CommandConstants.EXIT;

/**
 * Class to hold all error messages.
 */
public class ErrorMessages {
    // Flashcard error messages
    public static final String CREATE_USAGE = "Usage: " + ADD_CARD + " /q {QUESTION} /a {ANSWER}";
    public static final String CREATE_MISSING_FIELD = "Missing /q or /a in input.";
    public static final String CREATE_MISSING_DESCRIPTION = "Question or Answer cannot be empty.";
    public static final String CREATE_INVALID_ORDER = "/a Answer first /q Question later";
    public static final String INVALID_INDEX_INPUT = "Input is not a number";
    public static final String INDEX_OUT_OF_BOUNDS = "Input is out of bounds of current list of flashcards";
    public static final String EDIT_USAGE = "Usage: " + EDIT_CARD + " {INDEX} /q {QUESTION} /a {ANSWER}";
    public static final String EMPTY_LIST = "Invalid: List is empty.";

    // Deck error messages
    public static final String MISSING_DECK_NAME = "Deck must have a name.";
    public static final String NO_DECK_TO_SWITCH = "No decks available. Create a deck before selecting.";
    public static final String NO_DECK_ERROR = "Select a deck first!";
    public static final String EMPTY_DECK_NAME = "Deck name must not be empty!";
    public static final String DUPLICATE_DECK_NAME = "Deck name already exists!";
    public static final String UNCHANGED_DECK_NAME = "Deck name is unchanged!";
    public static final String NO_SUCH_DECK = "Deck does not exist!";
    public static final String NO_DECK_TO_VIEW = "No decks available. Create a deck to start.";
    public static final String DELETE_EMPTY_DECK_ERROR = "No decks available. Unable to delete.";

    // Code insertion error messages
    public static final String INSERT_MISSING_FIELD = "Missing /c in input.";
    public static final String INSERT_MISSING_CODE = "Code snippet cannot be empty.";
    public static final String INSERT_USAGE = "Usage: " + INSERT_CODE + " {INDEX} /c {CODE_SNIPPET}";

    // Quiz error messages
    public static final String CHANGE_IS_LEARNED_MISSING_INDEX = "No input index detected.";
    public static final String INCOMPLETED_QUIZ = "Complete a quiz first";
    public static final String MISMATCHED_ARRAYS = "Attempted to view Quiz results, but arrays are mismatched.";

    // Search error messages
    public static final String SEARCH_MISSING_FIELD = "Missing /q or /a in input.";
    public static final String SEARCH_RESULT_EMPTY = "No matching flashcards found.";
    public static final String SEARCH_EMPTY_DECK = "Deck is empty!";

    // Command help message
    public static final String POSSIBLE_COMMANDS = String.format(
            "Possible commands are: %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s.",
            ADD_CARD, VIEW_QN, VIEW_ANS, EDIT_CARD, LIST_CARDS, DELETE_CARD,
            INSERT_CODE, SEARCH_CARD, QUIZ, MARK_UNLEARNED, MARK_LEARNED, VIEW_RES,
            NEW_DECK, SWITCH_DECK, RENAME_DECK, VIEW_DECKS, DELETE_DECK, USER_GUIDE, EXIT
    );
}
