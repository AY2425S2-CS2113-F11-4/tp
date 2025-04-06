package deck;

import static constants.ErrorMessages.CREATE_INVALID_INPUT_ERROR;
import static constants.ErrorMessages.MISSING_INPUT_INDEX;
import static constants.ErrorMessages.CREATE_INVALID_ORDER;
import static constants.ErrorMessages.CREATE_MISSING_DESCRIPTION;
import static constants.ErrorMessages.CREATE_MISSING_FIELD;
import static constants.ErrorMessages.EMPTY_LIST;
import static constants.ErrorMessages.INDEX_OUT_OF_BOUNDS;
import static constants.ErrorMessages.INSERT_MISSING_CODE;
import static constants.ErrorMessages.INSERT_MISSING_FIELD;
import static constants.ErrorMessages.INVALID_INDEX_INPUT;
import static constants.ErrorMessages.SEARCH_EMPTY_DECK;
import static constants.ErrorMessages.SEARCH_MISSING_FIELD;
import static constants.ErrorMessages.SEARCH_RESULT_EMPTY;
import static constants.QuizMessages.QUIZ_CANCEL;
import static constants.QuizMessages.QUIZ_CANCEL_MESSAGE;
import static constants.SuccessMessages.CHANGED_ISLEARNED_NOCHANGENEEDED;
import static constants.SuccessMessages.CHANGED_ISLEARNED_SUCCESS;
import static constants.SuccessMessages.CREATE_SUCCESS;
import static constants.SuccessMessages.EDIT_SUCCESS;
import static constants.SuccessMessages.LIST_SUCCESS;
import static constants.SuccessMessages.SEARCH_SUCCESS;
import static constants.SuccessMessages.VIEW_ANSWER_SUCCESS;
import static constants.SuccessMessages.VIEW_QUESTION_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import command.Command;
import command.CommandCreateFlashcard;
import command.CommandDeleteFlashcard;
import command.CommandEditFlashcard;
import command.CommandInsertCode;
import command.CommandViewQuestion;
import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

import exceptions.QuizCancelledException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck("test1");
        DeckManager.currentDeck = deck;
    }

    @Test
    void createFlashcard_validInputs_success() {
        String input = "/q What is Java? /a A programming language.";
        try {
            String output = deck.createFlashcard(input);

            assertEquals(1, deck.getFlashcards().size());
            Flashcard createdFlashcard = deck.getFlashcards().get(0);
            assertEquals("What is Java?", createdFlashcard.getQuestion());
            assertEquals("A programming language.", createdFlashcard.getAnswer());
            assertEquals(String.format(CREATE_SUCCESS,
                "What is Java?", "A programming language.", 1), output);
        } catch (FlashCLIArgumentException e) {
            fail("Unexpected FlashCLI_IllegalArgumentException was thrown: " + e.getMessage());
        }
    }

    @Test
    void createFlashcard_invalidQuestionField_flashCLIArgumentExceptionThrown() {
        try {
            String input = "/q /a A programming language.";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_DESCRIPTION, e.getMessage());
        }

        try {
            String input = "/a A programming language.";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_FIELD, e.getMessage());
        }

        try {
            String input = "/q       /a A programming language.";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_DESCRIPTION, e.getMessage());
        }
    }


    @Test
    void createFlashcard_invalidAnswerField_illegalArgumentExceptionThrown() {
        try {
            String input = "/q What is Java? ";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_FIELD, e.getMessage());
        }

        try {
            String input = "/q What is Java? /a";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_DESCRIPTION, e.getMessage());
        }

        try {
            String input = "/q What is Java? /a     ";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_DESCRIPTION, e.getMessage());
        }
    }

    @Test
    void createFlashcard_invalidQuestionAndAnswerField_illegalArgumentExceptionThrown() {
        try {
            String input = "  ";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_FIELD, e.getMessage());
        }

        try {
            String input = "/q /a";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_DESCRIPTION, e.getMessage());
        }

        try {
            String input = "/q";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_FIELD, e.getMessage());
        }

        try {
            String input = "/a";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_MISSING_FIELD, e.getMessage());
        }

        try {
            String input = "/a A programming language. /q What is Java?";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_INVALID_ORDER, e.getMessage());
        }

        try {
            String input = "/a /q";
            deck.createFlashcard(input);
        } catch (FlashCLIArgumentException e) {
            assertEquals(CREATE_INVALID_ORDER, e.getMessage());
        }
    }

    @Test
    public void createFlashcard_multipleQTags_questionCreatedSuccessfully() throws FlashCLIArgumentException {
        String input = "/q What is Java? /q Extra question /a A programming language.";
        deck.createFlashcard(input);

        assertEquals(1, deck.getFlashcards().size());
        Flashcard createdFlashcard = deck.getFlashcards().get(0);
        assertEquals(" What is Java? /q Extra question ".trim(), createdFlashcard.getQuestion());
        assertEquals(" A programming language.".trim(), createdFlashcard.getAnswer());
    }

    @Test
    public void createFlashcard_hasTextBeforeQ_throwsException() {
        String input = "afljafja/q What is Java? /a A programming language.";
        FlashCLIArgumentException exception = assertThrows(
                FlashCLIArgumentException.class, () -> deck.createFlashcard(input));
        assertEquals(CREATE_INVALID_INPUT_ERROR, exception.getMessage());
    }

    @Test
    void viewFlashcardQuestion_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            String viewOutput = deck.viewFlashcardQuestion("1");
            assertEquals(1, deck.getFlashcards().size());
            assertEquals(String.format(VIEW_QUESTION_SUCCESS, 1, "[ ]", "What is Java?", ""), viewOutput);
        } catch (FlashCLIArgumentException | NumberFormatException e) {
            fail("Unexpected ArrayIndexOutOfBoundsException was thrown: " + e.getMessage());
        }
    }

    @Test
    void viewFlashcardQuestion_invalidIndex_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            deck.viewFlashcardQuestion("3");
            fail("no exception was thrown");
        } catch (FlashCLIArgumentException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        } catch (NumberFormatException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void viewFlashcardQuestion_indexNotANumber_numberFormatExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            deck.viewFlashcardQuestion("sjd");
            fail("no exception was thrown");
        } catch (NumberFormatException e) {
            assertEquals("For input string: \"sjd\"", e.getMessage());
        } catch (FlashCLIArgumentException e) {
            fail("Unexpected FlashCLIArgumentException was thrown: " + e.getMessage());
        }
    }

    @Test
    void viewFlashcardQuestion_noInputs_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            deck.viewFlashcardQuestion("");
            fail("no exception was thrown");
        } catch (FlashCLIArgumentException e) {
            assertEquals(MISSING_INPUT_INDEX, e.getMessage());
        } catch (NumberFormatException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void viewFlashcardAnswer_validInputs_success() {
        String createInput = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(createInput);
        createTest.executeCommand();
        String viewOutput = deck.viewFlashcardAnswer(1);
        assertEquals(1, deck.getFlashcards().size());
        assertEquals(String.format(VIEW_ANSWER_SUCCESS, 1, "A programming language."), viewOutput);
    }

    @Test
    void viewFlashcardAnswer_indexNotANumber_numberFormatExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            new CommandViewQuestion("sjd");
        } catch (NumberFormatException e) {
            assertEquals(INVALID_INDEX_INPUT, e.getMessage());
        }
    }

    @Test
    void viewFlashcardAnswer_invalidIndex_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            new CommandViewQuestion("72");
        } catch (NumberFormatException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }
    }

    @Test
    void editFlashcard_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            String editInput = "1 /q What is Python? /a A different programming language.";
            String editOutput = deck.editFlashcard(1, editInput);
            assertEquals(1, deck.getFlashcards().size());
            Flashcard editedFlashcard = deck.getFlashcards().get(0);
            assertEquals("What is Python?", editedFlashcard.getQuestion());
            assertEquals("A different programming language.", editedFlashcard.getAnswer());
            assertEquals(String.format(EDIT_SUCCESS,
                    "What is Java?", "What is Python?",
                    "A programming language.", "A different programming language."), editOutput);
        } catch (FlashCLIArgumentException e) {
            fail("Unexpected FlashCLI_IllegalArgumentException was thrown: " + e.getMessage());
        }
    }

    @Test
    void editFlashcard_indexNotANumber_numberFormatExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            new CommandEditFlashcard("sjd /q What is Python? /a A different programming language.");
        } catch (NumberFormatException e) {
            assertEquals(INVALID_INDEX_INPUT, e.getMessage());
        }
    }

    @Test
    void editFlashcard_invalidIndex_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            new CommandEditFlashcard("4 /q What is Python? /a A different programming language.");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }
    }

    @Test
    void listFlashcards_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            createTest.executeCommand();
            String listOutput = deck.listFlashcards();
            assertEquals(3, deck.getFlashcards().size());
            assertEquals(String.format(LIST_SUCCESS,
                    "1. [ ] What is Java?\n" +
                            "2. [ ] What is Java?\n" +
                            "3. [ ] What is Java?"),
                    listOutput);
        } catch (EmptyListException e) {
            fail("Unexpected EmptyListException was thrown: " + e.getMessage());
        }
    }

    @Test
    void listFlashcards_emptyList_emptyListExceptionThrown() {
        try {
            String listOutput = deck.listFlashcards();
            assertEquals(String.format(LIST_SUCCESS,
                            "1. What is Java?\n2. What is Java?\n3. What is Java?"),
                    listOutput);
        } catch (EmptyListException e) {
            assertEquals(EMPTY_LIST, e.getMessage());
        }
    }

    @Test
    void deleteFlashcard_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            String listOutput = deck.listFlashcards();
            assertEquals(2, deck.getFlashcards().size());
            assertEquals(String.format(LIST_SUCCESS, "1. [ ] What is Java?\n2. [ ] What is Java?"), listOutput);
            Command deleteTest = new CommandDeleteFlashcard("1");
            deleteTest.executeCommand();
            String listAfterDeleteOutput = deck.listFlashcards();
            assertEquals(1, deck.getFlashcards().size());
            assertEquals(String.format(LIST_SUCCESS, "1. [ ] What is Java?"), listAfterDeleteOutput);
            assertEquals(1, deck.getFlashcards().get(0).getIndex());
        } catch (EmptyListException e) {
            fail("Unexpected EmptyListException was thrown: " + e.getMessage());
        }
    }

    @Test
    void deleteFlashcard_indexNotANumber_numberFormatExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            String listOutput = deck.listFlashcards();
            assertEquals(2, deck.getFlashcards().size());
            assertEquals(String.format(LIST_SUCCESS, "1. [ ] What is Java?\n2. [ ] What is Java?"), listOutput);
            Command deleteTest = new CommandDeleteFlashcard("sdsd");
            deleteTest.executeCommand();
        } catch (EmptyListException e) {
            fail("Unexpected EmptyListException was thrown: " + e.getMessage());
        } catch (NumberFormatException e) {
            assertEquals(INVALID_INDEX_INPUT, e.getMessage());
        }
    }

    @Test
    void deleteFlashcard_invalidIndex_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            String listOutput = deck.listFlashcards();
            assertEquals(2, deck.getFlashcards().size());
            assertEquals(String.format(LIST_SUCCESS, "1. [ ] What is Java?\n2. [ ] What is Java?"), listOutput);
            Command deleteTest = new CommandDeleteFlashcard("72");
            deleteTest.executeCommand();
        } catch (EmptyListException e) {
            fail("Unexpected EmptyListException was thrown: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }
    }

    @Test
    void quizFlashcards_correctAnswer_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();

            ArrayList<Flashcard> flashcards = deck.getFlashcards();
            String userAnswer = "A programming language.";
            boolean testSuccess = deck.handleAnswerForFlashcard(flashcards.get(0), userAnswer);
            assertTrue(testSuccess);
        } catch (Exception e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void quizFlashcards_wrongAnswer_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();

            ArrayList<Flashcard> flashcards = deck.getFlashcards();
            String userAnswer = "dummy response";
            boolean testSuccess = deck.handleAnswerForFlashcard(flashcards.get(0), userAnswer);
            assertFalse(testSuccess);
        } catch (QuizCancelledException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void quizFlashcards_emptyList_emptyListExceptionThrown() {
        try {
            deck.quizFlashcards();
            fail("No exception was thrown");
        } catch (EmptyListException e) {
            assertEquals(EMPTY_LIST,e.getMessage());
        } catch (QuizCancelledException e) {
            fail("Unexpected QuizCancelledException was thrown: " + e.getMessage());
        }
    }

    @Test
    void quizFlashcards_cancelQuiz_quizCancelledExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();

            ArrayList<Flashcard> flashcards = deck.getFlashcards();
            String userAnswer = "dummy response";
            boolean testSuccess = deck.handleAnswerForFlashcard(flashcards.get(0), userAnswer);
            assertFalse(testSuccess);
            boolean exitQuizSuccess = deck.handleAnswerForFlashcard(flashcards.get(1), QUIZ_CANCEL);
            fail("No exception was thrown");
        } catch (QuizCancelledException e) {
            assertEquals(QUIZ_CANCEL_MESSAGE,e.getMessage());
        }
    }

    @Test
    void insertCodeSnippet_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            String insertCodeSnippet = "1 /c Class Java { void method() {...} }";
            Command insertTest = new CommandInsertCode(insertCodeSnippet);
            insertTest.executeCommand();
            String viewOutput = deck.viewFlashcardQuestion("1");
            assertEquals(1, deck.getFlashcards().size());
            assertEquals(String.format(VIEW_QUESTION_SUCCESS, 1, "[ ]", "What is Java?",
                    "Class Java {\n    void method() {\n      ...\n   } \n}"), viewOutput);
        } catch (FlashCLIArgumentException | NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void insertCodeSnippet_invalidCodeSnippetField_illegalArgumentExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            deck.createFlashcard(createInput);
            String insertCodeSnippet = " ";
            deck.insertCodeSnippet(1, insertCodeSnippet);
        } catch (FlashCLIArgumentException e) {
            assertEquals(INSERT_MISSING_FIELD, e.getMessage());
        }

        try {
            String createInput = "/q What is Java? /a A programming language.";
            deck.createFlashcard(createInput);
            String insertCodeSnippet = "/c ";
            deck.insertCodeSnippet(1, insertCodeSnippet);
        } catch (FlashCLIArgumentException e) {
            assertEquals(INSERT_MISSING_CODE, e.getMessage());
        }
    }

    @Test
    void insertCodeSnippet_invalidIndex_arrayIndexOutOfBoundsExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            assertEquals(1, deck.getFlashcards().size());
            new CommandInsertCode("72 /c somecode");
        } catch (NumberFormatException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }
    }

    @Test
    void markIsLearned_validInputs_success() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            assertEquals(2, deck.getFlashcards().size());

            String isLearnedOutput = deck.changeIsLearned("1", true);
            assertTrue(deck.getFlashcards().get(0).getIsLearned());
            assertEquals(String.format(CHANGED_ISLEARNED_SUCCESS, 1, "learned"), isLearnedOutput);

            String isUnLearnedOutput = deck.changeIsLearned("1", false);
            assertFalse(deck.getFlashcards().get(0).getIsLearned());
            assertEquals(String.format(CHANGED_ISLEARNED_SUCCESS, 1, "unlearned"), isUnLearnedOutput);

            String nextLearnedOutput = deck.changeIsLearned("2", true);
            assertTrue(deck.getFlashcards().get(1).getIsLearned());
            assertEquals(String.format(CHANGED_ISLEARNED_SUCCESS, 2, "learned"), nextLearnedOutput);
        } catch (FlashCLIArgumentException | NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void markIsLearned_invalidInputs_exceptionThrown(){
        String createInput = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(createInput);
        createTest.executeCommand();
        createTest.executeCommand();
        assertEquals(2, deck.getFlashcards().size());

        try { //invalid index
            deck.changeIsLearned("a", true);
            fail("Did not detect invalid input: Input not a number");
        } catch (NumberFormatException e) {
            assertEquals("For input string: \"a\"", e.getMessage());
        } catch (FlashCLIArgumentException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }

        try { //empty inputs
            deck.changeIsLearned("", true);
            fail("Did not detect invalid input: Input empty");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(MISSING_INPUT_INDEX, e.getMessage());
        }

        try { //out of bounds index
            deck.changeIsLearned("5", true);
            fail("Did not detect invalid input: Input out of bounds");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }

        try { //zero inputted
            deck.changeIsLearned("0", true);
            fail("Did not detect invalid input: Input not a number");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(INDEX_OUT_OF_BOUNDS, e.getMessage());
        }
    }

    @Test
    void markIsLearned_emptyList_flashCLIArgumentExceptionThrown() {
        try {
            assertEquals(0, deck.getFlashcards().size());
            deck.changeIsLearned("1", true);
            fail("Did not detect empty list");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(EMPTY_LIST, e.getMessage());
        }
    }

    @Test
    void markIsLearned_alreadyUnlearned_illegalArgumentExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            assertEquals(2, deck.getFlashcards().size());

            deck.changeIsLearned("2", false);
            fail("Did not detect invalid input: Input out of bounds");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(String.format(CHANGED_ISLEARNED_NOCHANGENEEDED, "unlearned"), e.getMessage());
        }
    }

    @Test
    void markIsLearned_alreadyLearned_illegalArgumentExceptionThrown() {
        try {
            String createInput = "/q What is Java? /a A programming language.";
            Command createTest = new CommandCreateFlashcard(createInput);
            createTest.executeCommand();
            createTest.executeCommand();
            assertEquals(2, deck.getFlashcards().size());

            deck.changeIsLearned("2", true);
            deck.changeIsLearned("2", true);
            fail("Did not detect invalid input: Input out of bounds");
        } catch (NumberFormatException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        } catch (FlashCLIArgumentException e) {
            assertEquals(String.format(CHANGED_ISLEARNED_NOCHANGENEEDED, "learned"), e.getMessage());
        }
    }

    @Test
    void searchDeck_fullSearch_success() {
        String input = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(input);
        createTest.executeCommand();
        try {
            String expected = "Question: What is Java?\nAnswer: A programming language.";
            String result = deck.searchFlashcard(input);
            assertEquals(String.format(SEARCH_SUCCESS, expected),
                    result);
        } catch (FlashCLIArgumentException | EmptyListException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void searchDeck_partialSearch_success() {
        String input = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(input);
        createTest.executeCommand();
        try {
            String expected = "Question: What is Java?\nAnswer: A programming language.";
            String result = deck.searchFlashcard("/q W /a p");
            assertEquals(String.format(SEARCH_SUCCESS, expected),
                    result);
        } catch (FlashCLIArgumentException | EmptyListException e) {
            fail("Unexpected Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void searchDeck_emptySearch_flashCLIArgumentExceptionThrown() {
        String input = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(input);
        createTest.executeCommand();
        try {
            String result = deck.searchFlashcard("");
        } catch (FlashCLIArgumentException | EmptyListException e) {
            assertEquals(SEARCH_MISSING_FIELD, e.getMessage());
        }
    }

    @Test
    void searchDeck_emptyResult_emptyListExceptionThrown() {
        String input = "/q What is Java? /a A programming language.";
        Command createTest = new CommandCreateFlashcard(input);
        createTest.executeCommand();
        try {
            deck.searchFlashcard("/q v /a v");
        } catch (FlashCLIArgumentException | EmptyListException e) {
            assertEquals(SEARCH_RESULT_EMPTY, e.getMessage());
        }
    }

    @Test
    void searchDeck_emptyDeck_emptyListExceptionThrown() {
        try {
            deck.searchFlashcard("/q v /a v");
        } catch (FlashCLIArgumentException | EmptyListException e) {
            assertEquals(SEARCH_EMPTY_DECK, e.getMessage());
        }
    }

    /**
     * Tests successful flashcard creation with valid inputs.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void createFlashcard_validInputs_success() throws Exception {
        String result = deck.createFlashcard("/q Question /a Answer");
        assertTrue(result.contains("Question"));
        assertTrue(result.contains("Answer"));
        assertEquals(1, deck.getFlashcards().size());
    }

    /**
     * Tests viewing a flashcard question with valid index.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void viewFlashcardQuestion_validIndex_success() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        String result = deck.viewFlashcardQuestion("1");
        assertTrue(result.contains("Q1"));
    }

    // ==================== Quiz Functionality Tests ====================

    /**
     * Tests quiz initiation with empty deck throws EmptyListException.
     */
    @Test
    void quizFlashcards_emptyDeck_throwsException() {
        assertThrows(EmptyListException.class, () -> deck.quizFlashcards());
    }

    /**
     * Tests correct answer handling returns true.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void handleAnswerForFlashcard_correctAnswer_returnsTrue() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        boolean result = deck.handleAnswerForFlashcard(deck.getFlashcards().get(0), "A1");
        assertTrue(result);
    }

    // ==================== Quiz Result Tests ====================

    /**
     * Tests perfect score results show gold medal and 100% accuracy.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void showQuizResult_perfectScore_showsGoldMedal() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        deck.handleAnswerForFlashcard(deck.getFlashcards().get(0), "A1");
        String result = deck.showQuizResult();
        assertTrue(result.contains("100.00%"));
        assertTrue(result.contains("GOLD MEDAL"));
    }

    /**
     * Tests all incorrect answers show 0% accuracy.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void showQuizResult_allIncorrect_shows0Percent() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        deck.handleAnswerForFlashcard(deck.getFlashcards().get(0), "Wrong");
        String result = deck.showQuizResult();
        assertTrue(result.contains("0.00%"));
        assertTrue(result.contains("Keep practicing"));
    }

    /**
     * Tests mismatched arrays throw FlashCLIArgumentException.
     */
    @Test
    void showQuizResult_mismatchedArrays_throwsException() {
        deck.getIncorrectFlashcards().add(new Flashcard(1, "Q1", "A1"));
        deck.getIncorrectIndexes().add(1); // Mismatched index
        assertThrows(FlashCLIArgumentException.class, () -> deck.showQuizResult());
    }

    // ==================== Mistake Display Tests ====================

    /**
     * Tests mistake display handles various formats correctly.
     */
    @Test
    void showMistakes_multipleFormats_displaysCorrectly() {
        deck.getIncorrectFlashcards().add(new Flashcard(1, "Long question?", "Expected"));
        deck.getIncorrectIndexes().add(0);
        deck.getIncorrectAnswers().add("User answer");
        deck.showMistakes(); // Verify output format
    }

    /**
     * Tests empty mistake lists are handled without errors.
     */
    @Test
    void showMistakes_emptyLists_noErrors() {
        deck.showMistakes(); // Should handle empty case
    }

    // ==================== Edge Case Tests ====================

    /**
     * Tests missing fields throw FlashCLIArgumentException.
     */
    @Test
    void createFlashcard_missingFields_throwsException() {
        assertThrows(FlashCLIArgumentException.class, 
            () -> deck.createFlashcard("/q Only question"));
    }

    /**
     * Tests invalid index throws FlashCLIArgumentException.
     */
    @Test
    void viewFlashcardQuestion_invalidIndex_throwsException() {
        assertThrows(FlashCLIArgumentException.class, 
            () -> deck.viewFlashcardQuestion("999"));
    }

    // ==================== Performance Tests ====================

    /**
     * Tests large deck handling doesn't throw exceptions.
     */
    @Test
    void quizFlashcards_largeDeck_performanceTest() {
        for (int i = 1; i <= 100; i++) {
            deck.insertFlashcard(new Flashcard(i, "Q"+i, "A"+i));
        }
        assertDoesNotThrow(() -> deck.quizFlashcards());
    }

    // ==================== Helper Method Tests ====================

    /**
     * Tests accuracy grade calculation at boundary conditions.
     */
    @Test
    void calculateAccuracyGrade_boundaryConditions() {
        assertEquals("A+ :)", deck.calculateAccuracyGrade(100));
        assertEquals("A :>", deck.calculateAccuracyGrade(87));
        assertEquals("F ;(", deck.calculateAccuracyGrade(30));
    }

    // ==================== Integration Tests ====================

    /**
     * Tests complete workflow from creation to quiz results.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void fullWorkflow_createQuizViewResults() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        deck.createFlashcard("/q Q2 /a A2");
        
        deck.handleAnswerForFlashcard(deck.getFlashcards().get(0), "A1");
        deck.handleAnswerForFlashcard(deck.getFlashcards().get(1), "Wrong");
        
        String results = deck.showQuizResult();
        assertTrue(results.contains("50.00%"));
        assertTrue(results.contains("Q2")); // In mistakes section
    }

    // ==================== Code Snippet Tests ====================

    /**
     * Tests successful code snippet insertion.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void insertCodeSnippet_validInput_success() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        String result = deck.insertCodeSnippet(1, "/c System.out.println();");
        assertTrue(result.contains("System.out.println()"));
    }

    /**
     * Tests missing code snippet throws FlashCLIArgumentException.
     */
    @Test
    void insertCodeSnippet_missingCode_throwsException() {
        assertThrows(FlashCLIArgumentException.class,
            () -> deck.insertCodeSnippet(1, "/c "));
    }

    // ==================== Search Functionality Tests ====================

    /**
     * Tests successful flashcard search by question.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void searchFlashcard_byQuestion_success() throws Exception {
        deck.createFlashcard("/q FindMe /a A1");
        String result = deck.searchFlashcard("/q Find");
        assertTrue(result.contains("FindMe"));
    }

    /**
     * Tests empty search results throw EmptyListException.
     */
    @Test
    void searchFlashcard_noMatches_throwsException() {
        assertThrows(EmptyListException.class,
            () -> deck.searchFlashcard("/q NoSuchQuestion"));
    }

    // ==================== Learning Status Tests ====================

    /**
     * Tests successful change of learning status.
     * @throws Exception if any unexpected error occurs
     */
    @Test
    void changeIsLearned_validChange_success() throws Exception {
        deck.createFlashcard("/q Q1 /a A1");
        String result = deck.changeIsLearned("1", true);
        assertTrue(result.contains("learned"));
    }

    /**
     * Tests redundant status change throws FlashCLIArgumentException.
     */
    @Test
    void changeIsLearned_redundantChange_throwsException() {
        assertThrows(FlashCLIArgumentException.class,
            () -> deck.changeIsLearned("1", true));
    }


}
