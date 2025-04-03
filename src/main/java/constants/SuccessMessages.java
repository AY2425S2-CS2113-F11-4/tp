package constants;
/**
 * class to hold all success messages
 */
public class SuccessMessages {
    public static final String CREATE_SUCCESS = "Added a new flashcard.\n" +
            "Question: %s\n" +
            "Answer: %s\n" +
            "You have %d flashcard(s) in your deck.";

    public static final String VIEW_QUESTION_SUCCESS = "Question of flashcard: %s\n" +
            "Question: %s\n" +
            "%s";

    public static final String VIEW_ANSWER_SUCCESS = "Answer of flashcard: %s\n" +
            "Answer: %s\n";

    public static final String EDIT_SUCCESS = "Updated flashcard.\n" +
            "Edit Question: %s\n" +
            "Updated: %s\n" +
            "Edit Answer: %s\n" +
            "Updated: %s\n";

    public static final String LIST_SUCCESS = "List of flashcards:\n%s";

    public static final String DELETE_SUCCESS = "Deleted flashcard: %s";
    public static final String CREATE_DECK_SUCCESS = "Deck \"%s\" created, number of decks: %d";
    public static final String SWITCH_DECK_SUCCESS = "Switched to deck \"%s\"";
    public static final String RENAME_DECK_SUCCESS = "Renamed deck \"%s\" to \"%s\" ";
    public static final String VIEW_DECKS_SUCCESS = "List of decks:\n%s";
    public static final String DELETE_DECK_SUCCESS = "Deleted deck \"%s\"";
    public static final String INSERT_SUCCESS = "Inserted code snippet to flashcard.\n" +
            "Question: %s\n" +
            "Answer: %s\n" +
            "Code Snippet: %s\n";

    public static final String CHANGED_ISLEARNED_SUCCESS = "Changed flashcard number %s into %s";
    public static final String SEARCH_SUCCESS = "Flashcards matched: \n%s";
    public static final String QUIZRESULT_FULL_MARKS = "Great job! You have answered all of questions correctly.";
    public static final String VIEW_QUIZRESULT_SUCCESS = "This is the end of the test report.";
}
