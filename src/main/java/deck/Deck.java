package deck;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;
import exceptions.QuizCancelledException;

import static constants.CommandConstants.ANOTHER;
import static constants.ErrorMessages.CREATE_INVALID_ORDER;
import static constants.ErrorMessages.CREATE_MISSING_DESCRIPTION;
import static constants.ErrorMessages.CREATE_MISSING_FIELD;
import static constants.ErrorMessages.EMPTY_LIST;
import static constants.ErrorMessages.INSERT_MISSING_CODE;
import static constants.ErrorMessages.INSERT_MISSING_FIELD;
import static constants.ErrorMessages.VIEW_OUT_OF_BOUNDS;
import static constants.ErrorMessages.INCOMPLETED_QUIZ;
import static constants.ErrorMessages.MISMATCHED_ARRAYS;
import static constants.QuizMessages.QUIZ_CANCEL;
import static constants.QuizMessages.QUIZ_CANCEL_MESSAGE;
import static constants.QuizMessages.QUIZ_CORRECT;
import static constants.QuizMessages.QUIZ_END;
import static constants.QuizMessages.QUIZ_INCORRECT;
import static constants.QuizMessages.QUIZ_LAST_QUESTION;
import static constants.QuizMessages.QUIZ_NO_ANSWER_DETECTED;
import static constants.QuizMessages.QUIZ_QUESTIONS_LEFT;
import static constants.QuizMessages.QUIZ_START;
import static constants.SuccessMessages.CREATE_SUCCESS;
import static constants.SuccessMessages.DELETE_SUCCESS;
import static constants.SuccessMessages.EDIT_SUCCESS;
import static constants.SuccessMessages.INSERT_SUCCESS;
import static constants.SuccessMessages.LIST_SUCCESS;
import static constants.SuccessMessages.VIEW_ANSWER_SUCCESS;
import static constants.SuccessMessages.VIEW_QUESTION_SUCCESS;
import static constants.SuccessMessages.VIEW_QUIZRESULT_SUCCESS;
import static constants.SuccessMessages.QUIZRESULT_FULL_MARKS;
import static deck.DeckManager.currentDeck;

import java.util.ArrayList;
import java.util.logging.Logger;

import exceptions.QuizCancelledException;
import parser.Parser;
import ui.Ui;
/**
 * Represents a deck that contains a collection of flashcards.
 *
 * <p>This class provides functionalities to manage flashcards within the deck,
 * including adding, viewing, editing, listing, and deleting flashcards.
 * Each deck has a name and maintains a list of flashcards.</p>
 *
 * <p>Instances of this class support various operations on flashcards,
 * such as retrieving a flashcard's question or answer, modifying its content,
 * and handling user input errors related to flashcard management.</p>
 *
 * <p>Logging is implemented to track actions performed on the deck,
 * ensuring better debugging and error handling.</p>
 */


public class Deck {

    private static final Logger logger = Logger.getLogger(Deck.class.getName());
    private String name;
    private final ArrayList<Flashcard> flashcards = new ArrayList<>();
    private final ArrayList<Flashcard> incorrectFlashcards = new ArrayList<>();
    private final ArrayList<Integer> incorrectIndexes = new ArrayList<>();
    private final ArrayList<String> incorrectAnswers = new ArrayList<>();
    private static ArrayList<Flashcard> queue = new ArrayList<>();
    private static boolean isQuizCompleted = false;


    /**
     * Creates a new deck with the specified name.
     *
     * @param name the name of the deck.
     */
    public Deck(String name) {
        this.name = name.trim();
    }

    /**
     * Returns the name of the deck.
     *
     * @return the deck name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the deck.
     *
     * @param name the new name for the deck.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of flashcards in the deck.
     *
     * @return an {@code ArrayList} of {@code Flashcard} objects.
     */
    public ArrayList<Flashcard> getFlashcards() {
        return flashcards;
    }

    /**
     * Creates a new flashcard
     *
     * <p>The arguments must contain both a question (denoted by "/q") and an answer (denoted by "/a").
     * The question must appear before the answer in the input string.</p>
     *
     * @param arguments A string with the flashcard details
     * @return A success message indicating the flashcard has been created.
     * @throws FlashCLIArgumentException If required fields are missing,
     *         the question and answer are in the wrong order, or either field is empty.
     */

    public String createFlashcard(String arguments) throws FlashCLIArgumentException {
        logger.info("Starting to create a flashcard with arguments: " + arguments);

        boolean containsAllArguments = arguments.contains("/q") && arguments.contains("/a");
        if (!containsAllArguments) {
            logger.warning("Missing required fields: /q or /a");
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }

        int questionStart = arguments.indexOf("/q");
        int answerStart = arguments.indexOf("/a");

        assert questionStart >= 0 : "Index of /q should be valid";
        assert answerStart >= 0 : "Index of /a should be valid";

        logger.fine("Index of /q: " + questionStart + ", Index of /a: " + answerStart);

        if (questionStart > answerStart) {
            logger.warning("Invalid order: /q comes after /a");
            throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
        }

        assert questionStart < answerStart : "Question should come before answer in arguments";

        String question = arguments.substring(questionStart + "/q".length(), answerStart).trim();
        String answer = arguments.substring(answerStart + "/a".length()).trim();

        if (question.isEmpty() || answer.isEmpty()) {
            logger.warning("Missing description: question or answer is empty");
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }

        int flashcardIndex = flashcards.size();
        Flashcard newFlashcard = new Flashcard(flashcardIndex, question, answer);
        flashcards.add(newFlashcard);

        logger.info("Successfully created a flashcard: Question: " + question + ", Answer: " + answer);
        return String.format(CREATE_SUCCESS,
                newFlashcard.getQuestion(), newFlashcard.getAnswer(), flashcards.size());
    }

    /**
     * Views the flashcard question
     *
     * @param index index of flashcard to view
     * @return the question in the format of VIEW_QUESTION_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String viewFlashcardQuestion(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToView = flashcards.get(arrayIndex);
        assert flashcardToView != null : "flashcard object should not be null";
        String question = flashcardToView.getQuestion();
        assert !question.isEmpty() : "Question should not be empty when viewing flashcards";
        String codeSnippet = flashcardToView.getCodeSnippet();
        return String.format(VIEW_QUESTION_SUCCESS, index, question, codeSnippet);
    }

    /**
     * Views flashcard answer
     * @param index index of flashcard
     * @return the answer in the format of VIEW_ANSWER_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String viewFlashcardAnswer(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToView = flashcards.get(arrayIndex);
        assert flashcardToView != null : "flashcard object should not be null";
        String answer = flashcardToView.getAnswer();
        assert !answer.isEmpty() : "Answer should not be empty when viewing flashcards";
        return String.format(VIEW_ANSWER_SUCCESS, index, answer);
    }

    /**
     * Edits the flashcard
     *
     * @param index     index of flashcard to view
     * @param arguments user inputs containing updated question and answer
     * @return the updated flashcard in the format of EDIT_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String editFlashcard(int index, String arguments)
            throws ArrayIndexOutOfBoundsException,
            FlashCLIArgumentException {
        boolean containsAllArguments = arguments.contains("/q") && arguments.contains("/a");
        if (!containsAllArguments) {
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }
        int questionStart = arguments.indexOf("/q");
        int answerStart = arguments.indexOf("/a");

        if (questionStart > answerStart) {
            throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
        }

        String updatedQuestion = arguments.substring(questionStart + "/q".length(), answerStart).trim();
        String updatedAnswer = arguments.substring(answerStart + "/a".length()).trim();
        if (updatedQuestion.isEmpty() || updatedAnswer.isEmpty()) {
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }

        Flashcard updatedFlashcard = new Flashcard(index, updatedQuestion, updatedAnswer);

        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;

        Flashcard oldFlashcard = flashcards.get(arrayIndex);
        String oldQuestion = oldFlashcard.getQuestion();
        String oldAnswer = oldFlashcard.getAnswer();
        flashcards.set(arrayIndex, updatedFlashcard);
        return String.format(EDIT_SUCCESS,
                oldQuestion, updatedQuestion, oldAnswer, updatedAnswer);
    }

    /**
     * lists out the questions of the flashcards
     * @return list of questions in the format of LIST_SUCCESS
     * @throws EmptyListException if the list is empty
     */
    public String listFlashcards() throws EmptyListException {
        if (flashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        StringBuilder list = new StringBuilder();
        int i = 1;
        for (Flashcard question : flashcards) {
            String currentQuestion = question.getQuestion();
            list.append(i).append(". ").append(currentQuestion);
            if (i != flashcards.size()) {
                list.append("\n");
            }
            i++;
        }

        return String.format(LIST_SUCCESS, list);
    }

    /**
     * Deletes the flashcard
     * @param index index of flashcard
     * @return the flashcard details in the format of DELETE_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String deleteFlashcard(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToDelete = flashcards.get(arrayIndex);
        assert flashcardToDelete != null : "flashcard object should not be null";
        flashcards.remove(arrayIndex);
        return String.format(DELETE_SUCCESS, flashcardToDelete);
    }

    /**
     * quizzes flashcards within the current deck
     * clears incorrect_flashcards, incorrect_card_indexes and incorrect_answers if quiz starts.
     * adds into incorrect_flashcards, incorrect_card_indexes and incorrect_answers if incorrect answers are given.
     * @throws EmptyListException if there are no flashcards in the deck
     */
    //@@author felfelyuen
    public boolean quizFlashcards(boolean isFullDeckTest) throws EmptyListException, QuizCancelledException {
        ArrayList<Flashcard> testedflashcards;
        if (isFullDeckTest == true) {
            testedflashcards = flashcards;

        } else {
            testedflashcards = incorrectFlashcards;
        }

        logger.info("starting to enter quiz mode:");
        if (testedflashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        incorrectIndexes.clear();
        incorrectFlashcards.clear();
        incorrectAnswers.clear();

        logger.info("There are " + testedflashcards.size() + " flashcards in this test");
        logger.info("starting shuffling:");
        queue = testedflashcards;
        logger.info("There are " + queue.size() + " flashcards in this test");

        Ui.showToUser(QUIZ_START);
        int lastIndex = queue.size() - 1;
        assert lastIndex >= 0 : "Queue size should not be zero";
        for (int i = 0; i < lastIndex; i++) {
            int questionsLeft = queue.size() - i;
            Ui.showToUser(String.format(QUIZ_QUESTIONS_LEFT, questionsLeft));
            Ui.showToUser(queue.get(i).getQuestion());
            handleQuestionForQuiz(queue.get(i));
        }
        logger.info("Last question:");
        Ui.showToUser(queue.get(lastIndex).getQuestion());
        Ui.showToUser(QUIZ_LAST_QUESTION);
        Ui.showToUser(queue.get(lastIndex).getQuestion());
        handleQuestionForQuiz(queue.get(lastIndex));

        logger.info("Finished asking questions, tabulating timer amount:");
        //DELETE THESE COMMENTS ONCE DONE:
        //HANDLE TIMER HERE
        //placeholder code (5 is an arbitrary value):
        int timerAmount = 5;
        assert timerAmount > 0 : "Timer_amount should not be zero";

        logger.info("Exiting quiz mode:");
        Ui.showToUser(String.format(QUIZ_END, timerAmount));
        isQuizCompleted = true;
        return true;
    }

    /**
     * handles asking the flashcard's question and taking in the input
     * function specific for quiz, as incorrect answer would affect the arrays for incorrect answers
     * @param indexCard to ask the question from
     * @throws QuizCancelledException if user wants to cancel halfway through the quiz
     */
    //@@author felfelyuen
    public void handleQuestionForQuiz(Flashcard indexCard) throws QuizCancelledException {
        Ui.showToUser("The line before qus");
        Ui.showToUser(indexCard.getQuestion());
        Ui.showToUser("THe line after qus");

        String userAnswer = Ui.getUserCommand().trim();
        while (userAnswer.isEmpty()) {
            logger.info("no answer detected");
            Ui.showError(QUIZ_NO_ANSWER_DETECTED);
            userAnswer = Ui.getUserCommand().trim();
        }

        boolean answerCorrect = handleAnswerForFlashcard(indexCard, userAnswer);
        if (!answerCorrect) {
            logger.info("Adding into incorrect answer arrays:");
            int incorrectIndex = indexCard.getIndex();
            incorrectIndexes.add(incorrectIndex);
            incorrectFlashcards.add(indexCard);
            incorrectAnswers.add(userAnswer);
        }
    }

    /**
     * checks if the answer is correct with a specific flashcard
     * @param indexCard the card with the question and index
     * @param userAnswer the answer that is inputted
     * @return boolean value of whether the answer is correct
     * @throws QuizCancelledException if the user wants to cancel the quiz half-way.
     */
    //@@author felfelyuen
    public boolean handleAnswerForFlashcard (Flashcard indexCard, String userAnswer)
            throws QuizCancelledException {
        if(userAnswer.equals(QUIZ_CANCEL)) {
            logger.info("Quiz cancelled by user. Exiting quiz:");
            incorrectIndexes.clear();
            incorrectFlashcards.clear();
            incorrectAnswers.clear();
            throw new QuizCancelledException(QUIZ_CANCEL_MESSAGE);
        }

        logger.info("answer detected:" + userAnswer);
        if (userAnswer.equals(indexCard.getAnswer())) {
            logger.info("Correct answer detected");
            Ui.showToUser(QUIZ_CORRECT);
            return true;
        } else {
            logger.info("Wrong answer detected, should be:" +
                    indexCard.getAnswer());
            Ui.showToUser(QUIZ_INCORRECT);
            return false;
        }
    }


    /**
     <<<<<<< HEAD
     * Handles showing result upon the completion of a quiz
     *
     * @return a success message indicating the result has been shown.
     * @throws  FlashCLIArgumentException if the quiz is not completed or mismatched arrays
     */
    //@@author shunyang12
    public String showQuizResult() throws FlashCLIArgumentException {
        logger.info("Trying to generate your quiz result...");

        if (isQuizCompleted == false) {
            throw new FlashCLIArgumentException(INCOMPLETED_QUIZ);
        }

        int incorrectAnswersSize = incorrectAnswers.size();
        int incorrectIndexesSize = incorrectIndexes.size();
        int incorrectFlashcardsSize = incorrectFlashcards.size();
        int totalQuestionsSize = queue.size();

        if (incorrectAnswersSize != incorrectIndexesSize | incorrectAnswersSize != incorrectFlashcardsSize | incorrectIndexesSize != incorrectFlashcardsSize) {
            throw new FlashCLIArgumentException(MISMATCHED_ARRAYS);
        }

        Ui.showToUser("You have answered " + totalQuestionsSize + " questions in the quiz.");

        if (incorrectAnswersSize == 0) {
            return QUIZRESULT_FULL_MARKS;
        }

        Ui.showToUser("You got " + (totalQuestionsSize-incorrectAnswersSize) + " questions correctly.");
        Ui.showToUser("You got " + incorrectAnswersSize + " questions incorrectly.");

        Ui.showToUser("Review your mistakes: ");

        showMistakes();

//        ChooseToEnterAnotherRound();

        return VIEW_QUIZRESULT_SUCCESS;
    }

    /**
     * Handles showing result upon the completion of a quiz
     *
     * @throws  FlashCLIArgumentException if the quiz is not completed or mismatched arrays
     */
    //@@author shunyang12
    public void showMistakes() throws ArrayIndexOutOfBoundsException {
        int wrongAnswerCount = 0;
        for (Integer indexIncorrect: incorrectIndexes) {
            if(indexIncorrect < 0 | indexIncorrect >= queue.size() ) {
                throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
            }

            Ui.showToUser("FlashCard " + indexIncorrect + " question: " + flashcards.get(indexIncorrect).getQuestion() +
                    " correct answer: " + flashcards.get(indexIncorrect).getAnswer() + " Your answer: " +
                    incorrectAnswers.get(wrongAnswerCount));
            wrongAnswerCount++;
        }
    }

    /**
     * Give user another chance to take the quiz with only wrong answers from the last round, enhancing their understanding
     *
     */
//    //@@author shunyang12
//    public void ChooseToEnterAnotherRound() {
//        Ui.showToUser("Enter another to have a second quiz reviewing only your wrong questions. enter any message to continue");
//        String fullInputLine = Ui.getUserCommand();
//        if(fullInputLine.equals(ANOTHER)) {
//            try {
//                quizFlashcards(false);;
//            } catch (EmptyListException e) {
//                Ui.showError(e.getMessage());
//            } catch (QuizCancelledException e) {
//                Ui.showToUser(e.getMessage());
//            }
//        } else {
//            Ui.showToUser("Continue...");
//        }
//    }
    /**
     * Inserts code snippets to the flashcard
     *
     * @param index     index of flashcard to insert code snippet
     * @param arguments user inputs containing updated question and answer
     * @return the updated flashcard in the format of EDIT_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    //@@author ElonKoh
    public String insertCodeSnippet(int index, String arguments)
            throws ArrayIndexOutOfBoundsException,
            FlashCLIArgumentException {
        boolean containsAllArguments = arguments.contains("/c");
        if (!containsAllArguments) {
            throw new FlashCLIArgumentException(INSERT_MISSING_FIELD);
        }
        int codeStart = arguments.indexOf("/c");

        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        String codeSnippet = arguments.substring(codeStart + "/c".length()).trim();
        if (codeSnippet.isEmpty()) {
            throw new FlashCLIArgumentException(INSERT_MISSING_CODE);
        }
        String formattedCodeSnippet = Parser.parseCodeSnippet(codeSnippet);

        int arrayIndex = index - 1;
        Flashcard insertFlashcard = flashcards.get(arrayIndex);

        insertFlashcard.setCodeSnippet(formattedCodeSnippet);
        return String.format(INSERT_SUCCESS,
                insertFlashcard.getQuestion(), insertFlashcard.getAnswer(),
                formattedCodeSnippet);
    }

    /**
     * Inserts code snippets to the flashcard
     *
     * @param index     index of flashcard to insert code snippet
     * @param arguments user inputs containing updated question and answer
     * @return the updated flashcard in the format of EDIT_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
//    //@@author ElonKoh
//    public String insertCodeSnippet(int index, String arguments)
//            throws ArrayIndexOutOfBoundsException,
//            FlashCLIArgumentException {
//        boolean containsAllArguments = arguments.contains("/c");
//        if (!containsAllArguments) {
//            throw new FlashCLIArgumentException(INSERT_MISSING_FIELD);
//        }
//        int codeStart = arguments.indexOf("/c");
//
//        if (index <= 0 || index > flashcards.size()) {
//            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
//        }
//        String codeSnippet = arguments.substring(codeStart + "/c".length()).trim();
//        if (codeSnippet.isEmpty()) {
//            throw new FlashCLIArgumentException(INSERT_MISSING_CODE);
//        }
//        String formattedCodeSnippet = Parser.parseCodeSnippet(codeSnippet);
//
//        int arrayIndex = index - 1;
//        Flashcard insertFlashcard = flashcards.get(arrayIndex);
//
//        insertFlashcard.setCodeSnippet(formattedCodeSnippet);
//        return String.format(INSERT_SUCCESS,
//                insertFlashcard.getQuestion(), insertFlashcard.getAnswer(),
//                formattedCodeSnippet);
//    }



    public ArrayList<Flashcard> shuffleDeck (ArrayList<Flashcard> deck) {
        //add shuffle deck code here

        return deck;
    }
}
