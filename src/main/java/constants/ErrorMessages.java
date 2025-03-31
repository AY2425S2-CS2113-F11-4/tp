package constants;
/**
 * class to hold all error messages
 */
public class ErrorMessages {
    public static final String CREATE_USAGE = "Usage: add /q {QUESTION} /a {ANSWER}";
    public static final String CREATE_MISSING_FIELD = "Missing /q or /a in input.";
    public static final String CREATE_MISSING_DESCRIPTION = "Question or Answer cannot be empty.";
    public static final String CREATE_INVALID_ORDER = "/a Answer first /q Question later";
    public static final String VIEW_INVALID_INDEX = "Input is not a number";
    public static final String VIEW_OUT_OF_BOUNDS = "Input is out of bounds of current list of flashcards";
    public static final String EDIT_USAGE = "Usage: edit {INDEX} /q {QUESTION} /a {ANSWER}";
    public static final String EMPTY_LIST = "Invalid: List is empty.";
    public static final String MISSING_DECK_NAME = "Deck must have a name.";
    public static final String INVALID_DECK_INDEX = "Deck index must be a number.";
    public static final String DECK_INDEX_OUT_OF_BOUNDS = "Deck index must be valid.";
    public static final String EMPTY_DECK_NUMBER = "Deck number cannot be left empty.";
    public static final String NO_DECK_TO_SWITCH = "No decks available. Create a deck before selecting.";
    public static final String POSSIBLE_COMMANDS =
            "Possible commands are: add, view_qn, view_ans, view_res, edit, list, delete, insert_code, quiz" +
                    "new, select, rename, decks and exit";
    public static final String NO_DECK_ERROR = "Select a deck first!";
    public static final String EMPTY_DECK_NAME = "Deck name must not be empty!";
    public static final String DUPLICATE_DECK_NAME = "Deck name already exists!";
    public static final String UNCHANGED_DECK_NAME = "Deck name is unchanged!";
    public static final String NO_SUCH_DECK = "Deck does not exist!";
    public static final String NO_DECK_TO_VIEW = "No decks available. Create a deck to start.";
    public static final String INCOMPLETED_QUIZ = "Complete a quiz first";
    public static final String MISMATCHED_ARRAYS = "Attempted to view Quiz results, but incorrectindexsize, incorrectFlashcarsize, and incorrectAnswersize are mismatched.";
    public static final String INSERT_MISSING_FIELD = "Missing /c in input.";
    public static final String INSERT_MISSING_CODE = "The quiz has not been started or completed yet, no result is available";
    public static final String INSERT_USAGE = "Usage: insert_code {INDEX} /c {CODE_SNIPPET}";
}
