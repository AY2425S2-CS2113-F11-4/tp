package deck;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

import static constants.ErrorMessages.CHANGE_IS_LEARNED_MISSING_INDEX;
import static constants.ErrorMessages.CREATE_INVALID_ORDER;
import static constants.ErrorMessages.CREATE_MISSING_FIELD;
import static constants.ErrorMessages.CREATE_MISSING_DESCRIPTION;
import static constants.ErrorMessages.EMPTY_LIST;
import static constants.ErrorMessages.INDEX_OUT_OF_BOUNDS;
import static constants.ErrorMessages.INSERT_MISSING_CODE;
import static constants.ErrorMessages.INSERT_MISSING_FIELD;
import static constants.ErrorMessages.SEARCH_EMPTY_DECK;
import static constants.ErrorMessages.SEARCH_MISSING_FIELD;
import static constants.ErrorMessages.SEARCH_RESULT_EMPTY;
import static constants.ErrorMessages.INCOMPLETED_QUIZ;
import static constants.ErrorMessages.MISMATCHED_ARRAYS;
import static constants.QuizMessages.QUIZ_CANCEL;
import static constants.QuizMessages.QUIZ_CANCEL_MESSAGE;
import static constants.QuizMessages.QUIZ_CORRECT;
import static constants.QuizMessages.QUIZ_END;
import static constants.QuizMessages.QUIZ_INCORRECT;
import static constants.QuizMessages.QUIZ_LAST_QUESTION;
import static constants.QuizMessages.QUIZ_NO_ANSWER_DETECTED;
import static constants.QuizMessages.QUIZ_NO_UNLEARNED;
import static constants.QuizMessages.QUIZ_QUESTIONS_LEFT;
import static constants.QuizMessages.QUIZ_START;
import static constants.SuccessMessages.CHANGED_ISLEARNED_SUCCESS;
import static constants.SuccessMessages.CREATE_SUCCESS;
import static constants.SuccessMessages.DELETE_SUCCESS;
import static constants.SuccessMessages.EDIT_SUCCESS;
import static constants.SuccessMessages.INSERT_SUCCESS;
import static constants.SuccessMessages.LIST_SUCCESS;
import static constants.SuccessMessages.SEARCH_SUCCESS;
import static constants.SuccessMessages.VIEW_ANSWER_SUCCESS;
import static constants.SuccessMessages.VIEW_QUESTION_SUCCESS;
import static constants.SuccessMessages.VIEW_QUIZRESULT_SUCCESS;
import static constants.SuccessMessages.QUIZRESULT_FULL_MARKS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import exceptions.QuizCancelledException;
import parser.Parser;
import ui.Ui;
import timer.Timer;
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

    private static boolean isQuizCompleted = false;
    private static final Logger logger = Logger.getLogger(Deck.class.getName());
    private String name;
    private ArrayList<Flashcard> flashcards = new ArrayList<>();
    private final ArrayList<Flashcard> incorrectFlashcards = new ArrayList<>();
    private final ArrayList<Integer> incorrectIndexes = new ArrayList<>();
    private final ArrayList<String> incorrectAnswers = new ArrayList<>();
    private Timer timer;

    private record Result(int questionStart, int answerStart) { }

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

    public void setFlashcards(ArrayList<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    /**
     * Returns the list of flashcards in the deck.
     *
     * @return an {@code ArrayList} of {@code Flashcard} objects.
     */
    public ArrayList<Flashcard> getFlashcards() {
        return flashcards;
    }

    public int getDeckSize() {
        return flashcards.size();
    }

    /**
     * helper function to directly insert a flashcard into the deck
     * without having to create one
     * @param flashcard
     */
    //@@ author ManZ9802
    public void insertFlashcard (Flashcard flashcard) {
        flashcards.add(flashcard);
    }

    /**
     * Creates a new flashcard from the given input string.
     *
     * <p>The input must include both a question ("/q") and an answer ("/a"),
     * with the question appearing before the answer.</p>
     *
     * @param arguments The input string containing the flashcard details.
     * @return A success message confirming the flashcard creation.
     * @throws FlashCLIArgumentException If required fields are missing,
     *         incorrectly ordered, or empty.
     */
    //@@author Betahaxer
    public String createFlashcard(String arguments) throws FlashCLIArgumentException {
        logger.info("Starting to create a flashcard with arguments: " + arguments);

        Result result = parseQuestionAndAnswer(arguments);
        int questionStart = result.questionStart();
        int answerStart = result.answerStart();

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
     * Helper function that extracts the positions of the
     * question ("/q") and answer ("/a") from the input string.
     * Checks if the required fields are missing or in the wrong order.
     *
     * @param arguments The input string containing the flashcard details.
     * @return A {@code Result} containing the start indices of "/q" and "/a".
     * @throws FlashCLIArgumentException If required fields are missing or in the wrong order.
     */
    private Result parseQuestionAndAnswer(String arguments) throws FlashCLIArgumentException {
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

        return new Result(questionStart, answerStart);
    }

    /**
     * Views the flashcard question
     *
     * @param index index of flashcard to view
     * @return the question in the format of VIEW_QUESTION_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    //@@author
    public String viewFlashcardQuestion(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
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
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
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
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
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
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
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
    public boolean quizFlashcards() throws EmptyListException, QuizCancelledException {
        logger.info("starting to enter quiz mode:");
        if (flashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        incorrectIndexes.clear();
        incorrectFlashcards.clear();
        incorrectAnswers.clear();

        logger.info("Found " + flashcards.size() + " flashcards in the deck");
        logger.info("starting sorting and shuffling:");
        ArrayList<Flashcard> queue = shuffleDeck(flashcards);
        if (queue.isEmpty()) {
            throw new EmptyListException(QUIZ_NO_UNLEARNED);
        }

        Ui.showToUser(QUIZ_START);
        long startTime = System.nanoTime();
        timer = new Timer(startTime);
        int lastIndex = queue.size() - 1;
        assert lastIndex >= 0 : "Queue size should not be zero";
        for (int i = 0; i < lastIndex; i++) {
            int questionsLeft = queue.size() - i;
            Ui.showToUser(String.format(QUIZ_QUESTIONS_LEFT, questionsLeft));
            handleQuestionForQuiz(queue.get(i));
        }
        logger.info("Last question:");
        Ui.showToUser(QUIZ_LAST_QUESTION);
        handleQuestionForQuiz(queue.get(lastIndex));

        logger.info("Finished asking questions, tabulating timer amount:");
        long timeTaken = timer.getDuration();
        assert timeTaken > 0 : "Timer_amount should not be zero";

        logger.info("Exiting quiz mode:");
        Ui.showToUser(String.format(QUIZ_END, timeTaken));
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
        Ui.showToUser(indexCard.getQuestion());

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
        indexCard.setIsLearned(answerCorrect);
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
        assert (!userAnswer.isEmpty()) : "userAnswer should not be empty";
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
        int totalQuestionsSize = flashcards.size();

        if (incorrectAnswersSize != incorrectIndexesSize 
            | incorrectAnswersSize != incorrectFlashcardsSize 
            | incorrectIndexesSize != incorrectFlashcardsSize) {
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
            if(indexIncorrect < 0 | indexIncorrect >= flashcards.size() ) {
                throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
            }

            Ui.showToUser("FlashCard " + indexIncorrect + " question: " + flashcards.get(indexIncorrect).getQuestion() +
                    " correct answer: " + flashcards.get(indexIncorrect).getAnswer() + " Your answer: " +
                    incorrectAnswers.get(wrongAnswerCount));
            wrongAnswerCount++;
        }
    }

    /**
     * Inserts code snippets to the flashcard
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
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
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
     * Shuffles the given deck, including only unlearned flashcards.
     * Filters out learned flashcards before shuffling the remaining ones.
     *
     * @param deck the list of flashcards to be shuffled.
     * @return a shuffled list containing only unlearned flashcards.
     */
    private ArrayList<Flashcard> shuffleDeck (ArrayList<Flashcard> deck) {
        //sort into unlearned ones only
        ArrayList<Flashcard> queue = new ArrayList<>();
        for (Flashcard flashcard : deck) {
            if(!flashcard.getIsLearned()) {
                queue.add(flashcard);
            }
        }

        Collections.shuffle(queue);
        return queue;
    }

    /**
     * changes isLearned of Flashcard
     * @param arguments index of flashcard
     * @param isLearned new boolean value of isLearned
     * @throws NumberFormatException if arguments is not a number
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size.
     */
    public String changeIsLearned (String arguments, boolean isLearned)
            throws NumberFormatException,
            FlashCLIArgumentException {
        if (arguments.isEmpty()) {
            logger.warning("No input detected.");
            throw new FlashCLIArgumentException(CHANGE_IS_LEARNED_MISSING_INDEX);
        }

        int index = Integer.parseInt(arguments.trim());
        logger.info("index received:" + index);
        if (index < 0 || index > flashcards.size()) {
            logger.warning("Index out of bounds");
            throw new FlashCLIArgumentException(INDEX_OUT_OF_BOUNDS);
        }

        Flashcard indexCard = flashcards.get(index - 1);
        indexCard.setIsLearned(isLearned);
        logger.info("indexCard " + index + "'s isLearned changed");
        if (isLearned) {
            return (String.format(CHANGED_ISLEARNED_SUCCESS, index, "learned"));
        } else {
            return (String.format(CHANGED_ISLEARNED_SUCCESS, index, "unlearned"));
        }
    }

    /**
     * helper function to search for flashcards
     * @param arguments question and/or answer to be searched
     * @return arraylist of flashcards
     * @throws FlashCLIArgumentException if no question or answer is provided
     */
    //@@author ManZ9802
    public ArrayList<Flashcard> searchFlashcardHelper(String arguments) throws FlashCLIArgumentException {
        boolean hasQuestion = arguments.contains("/q");
        boolean hasAnswer = arguments.contains("/a");

        if (!hasQuestion && !hasAnswer) {
            throw new FlashCLIArgumentException(SEARCH_MISSING_FIELD);
        }

        String queryQuestion = "";
        String queryAnswer = "";

        if (hasQuestion) {
            int qStart = arguments.indexOf("/q") + 2;
            int aStart = hasAnswer ? arguments.indexOf("/a") : arguments.length();
            queryQuestion = arguments.substring(qStart, aStart).trim().toLowerCase();
        }

        if (hasAnswer) {
            int aStart = arguments.indexOf("/a") + 2;
            queryAnswer = arguments.substring(aStart).trim().toLowerCase();
        }

        ArrayList<Flashcard> matchedFlashcards = new ArrayList<>();

        for (Flashcard flashcard : flashcards) {
            String question = flashcard.getQuestion();
            String answer = flashcard.getAnswer();
            boolean questionMatches = hasQuestion && question.toLowerCase().contains(queryQuestion);
            boolean answerMatches = hasAnswer && answer.toLowerCase().contains(queryAnswer);

            if ((hasQuestion && hasAnswer && questionMatches && answerMatches)
                    || (hasQuestion && !hasAnswer && questionMatches)
                    || (!hasQuestion && hasAnswer && answerMatches)) {
                matchedFlashcards.add(flashcard);
            }
        }

        return matchedFlashcards;
    }

    /**
     * wrapper function for searching flashcards
     * @param arguments question and/or answer to be searched
     * @return string of matching questions and answers
     * @throws FlashCLIArgumentException if no question or answer is provided
     * @throws EmptyListException if search is not a match / if the deck is empty
     */
    public String searchFlashcard(String arguments) throws FlashCLIArgumentException, EmptyListException {
        ArrayList<Flashcard> matchedFlashcards = searchFlashcardHelper(arguments);
        if (flashcards.isEmpty()) {
            throw new EmptyListException(SEARCH_EMPTY_DECK);
        }
        if (matchedFlashcards.isEmpty()) {
            throw new EmptyListException(SEARCH_RESULT_EMPTY);
        }
        StringBuilder result = new StringBuilder();
        for (Flashcard flashcard : matchedFlashcards) {
            result.append(String.format("Question: %s\nAnswer: %s\n\n",
                    flashcard.getQuestion(),
                    flashcard.getAnswer()));
        }

        return String.format(SEARCH_SUCCESS, result.toString().trim());
    }
}
