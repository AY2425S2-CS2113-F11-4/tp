package deck;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

import static constants.ErrorMessages.MISSING_INPUT_INDEX;
import static constants.ErrorMessages.CREATE_INVALID_INPUT_ERROR;
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
import static constants.QuizMessages.RESULT_HEADER;
import static constants.QuizMessages.RESULT_FORMAT;
import static constants.QuizMessages.MISTAKES_HEADER;
import static constants.QuizMessages.FLASHCARD_FORMAT;
import static constants.SuccessMessages.CHANGED_ISLEARNED_NOCHANGENEEDED;
import static constants.SuccessMessages.CHANGED_ISLEARNED_SUCCESS;
import static constants.SuccessMessages.CREATE_SUCCESS;
import static constants.SuccessMessages.DELETE_SUCCESS;
import static constants.SuccessMessages.EDIT_SUCCESS;
import static constants.SuccessMessages.INSERT_SUCCESS;
import static constants.SuccessMessages.LIST_SUCCESS;
import static constants.SuccessMessages.QUIZRESULT_FULL_MARKS;
import static constants.SuccessMessages.SEARCH_SUCCESS;
import static constants.SuccessMessages.VIEW_ANSWER_SUCCESS;
import static constants.SuccessMessages.VIEW_QUESTION_SUCCESS;
import static constants.SuccessMessages.VIEW_QUIZRESULT_SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import exceptions.QuizCancelledException;
import parser.Parser;
import storage.Saving;
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
    private ArrayList<Flashcard> incorrectFlashcards = new ArrayList<>();
    private ArrayList<Integer> incorrectIndexes = new ArrayList<>();
    private ArrayList<String> incorrectAnswers = new ArrayList<>();
    private int quizAmtAnswered;
    private Timer timer;

    private record Result(String question, String answer) { }

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

        Result result = checkQuestionAndAnswer(arguments);
        String question = result.question;
        String answer = result.answer;

        int flashcardIndex = flashcards.size() + 1;
        Flashcard newFlashcard = new Flashcard(flashcardIndex, question, answer);
        flashcards.add(newFlashcard);
        try {
            Saving.saveDeck(name, this);
        } catch (IOException e) {
            System.out.println("Error saving deck: " + e.getMessage());
        }
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
    private Result checkQuestionAndAnswer(String arguments) throws FlashCLIArgumentException {
        int qIndex = arguments.indexOf("/q");
        int aIndex = arguments.indexOf("/a");

        if (qIndex == -1 || aIndex == -1) {
            logger.warning("Missing /q or /a tag.");
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }

        if (qIndex > aIndex) {
            logger.warning("/q must come before /a.");
            throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
        }

        boolean hasTextBeforeQTag = !arguments.substring(0, qIndex).trim().isEmpty();
        if (hasTextBeforeQTag) {
            logger.warning("Text found before /q tag.");
            throw new FlashCLIArgumentException(CREATE_INVALID_INPUT_ERROR);
        }

        String question = arguments.substring(qIndex + "/q".length(), aIndex).trim();
        String answer = arguments.substring(aIndex + "/a".length()).trim();

        if (question.isEmpty() || answer.isEmpty()) {
            logger.warning("Question or answer is empty.");
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }
        return new Result(question, answer);
    }

    /**
     * Views the flashcard question
     *
     * @param arguments index of flashcard to view
     * @return the question in the format of VIEW_QUESTION_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    //@@author felfelyuen
    public String viewFlashcardQuestion(String arguments) throws FlashCLIArgumentException, NumberFormatException {
        if (arguments.isEmpty()) {
            throw new FlashCLIArgumentException(MISSING_INPUT_INDEX);
        }

        int index = Integer.parseInt(arguments);
        if (index <= 0 || index > flashcards.size()) {
            throw new FlashCLIArgumentException(INDEX_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToView = flashcards.get(arrayIndex);
        assert flashcardToView != null : "flashcard object should not be null";
        String isLearned =  flashcardToView.getIsLearnedAsString();
        String question = flashcardToView.getQuestion();
        assert !question.isEmpty() : "Question should not be empty when viewing flashcards";
        String codeSnippet = flashcardToView.getCodeSnippet();
        return String.format(VIEW_QUESTION_SUCCESS, index, isLearned, question, codeSnippet);
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
        boolean containsAllArguments = arguments.contains("/q") || arguments.contains("/a");
        if (!containsAllArguments) {
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS);
        }
        int questionStart = 0;
        int answerStart = arguments.length();
        int arrayIndex = index - 1;
        Flashcard updatedFlashcard = flashcards.get(arrayIndex);
        String updatedQuestion = updatedFlashcard.getQuestion();
        String updatedAnswer = updatedFlashcard.getAnswer();
        if (arguments.contains("/a")) {
            answerStart = arguments.indexOf("/a");
            updatedAnswer = arguments.substring(answerStart + "/a".length()).trim();
        }
        if (arguments.contains("/q")) {
            questionStart = arguments.indexOf("/q");
            if (questionStart > answerStart) {
                throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
            }
            updatedQuestion = arguments.substring(questionStart + "/q".length(), answerStart).trim();
        }

        if (updatedQuestion.isEmpty() || updatedAnswer.isEmpty()) {
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }

        updatedFlashcard = new Flashcard(index, updatedQuestion, updatedAnswer);

        Flashcard oldFlashcard = flashcards.get(arrayIndex);
        String oldQuestion = oldFlashcard.getQuestion();
        String oldAnswer = oldFlashcard.getAnswer();
        flashcards.set(arrayIndex, updatedFlashcard);
        try {
            Saving.saveDeck(name, this);
        } catch (IOException e) {
            System.out.println("Error saving deck: " + e.getMessage());
        }
        return String.format(EDIT_SUCCESS,
                oldQuestion, updatedQuestion, oldAnswer, updatedAnswer);
    }

    /**
     * lists out the questions of the flashcards
     * @return list of questions in the format of LIST_SUCCESS
     * @throws EmptyListException if the list is empty
     */
    //@@author felfelyuen
    public String listFlashcards() throws EmptyListException {
        if (flashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        StringBuilder list = new StringBuilder();
        int i = 1;
        for (Flashcard question : flashcards) {
            list.append(i).append(". ");
            String isLearned = question.getIsLearnedAsString();
            list.append(isLearned).append(" ");
            String currentQuestion = question.getQuestion();
            list.append(currentQuestion);
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
        //reset the indexes of the other flashcards
        for (int i = arrayIndex; i < flashcards.size(); i++) {
            Flashcard flashccard = flashcards.get(i);
            flashccard.setIndex(i + 1);
        }
        try {
            Saving.saveDeck(name, this);
        } catch (IOException e) {
            System.out.println("Error saving deck: " + e.getMessage());
        }
        return String.format(DELETE_SUCCESS, index, flashcardToDelete.getQuestion(), flashcardToDelete.getAnswer());
    }

    /**
     * Starts the quiz mode with the current set of flashcards.
     * Displays questions one by one, tracks incorrect answers, and measures completion time.
     *
     * @throws EmptyListException if there are no flashcards to quiz
     * @throws QuizCancelledException if user cancels the quiz mid-way
     */
    //@@author felfelyuen
    public void quizFlashcards() throws EmptyListException, QuizCancelledException {
        logger.info("starting to enter quiz mode:");
        if (flashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        ArrayList<Flashcard> tempIncorrectFlashcards = new ArrayList<>();
        ArrayList<Integer> tempIncorrectIndexes = new ArrayList<>();
        ArrayList<String> tempIncorrectAnswers = new ArrayList<>();
        quizAmtAnswered = 0;

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
            handleQuestionForQuiz(queue.get(i),
                    tempIncorrectFlashcards,
                    tempIncorrectIndexes,
                    tempIncorrectAnswers);
        }
        logger.info("Last question:");
        Ui.showToUser(QUIZ_LAST_QUESTION);
        handleQuestionForQuiz(queue.get(lastIndex),
                tempIncorrectFlashcards,
                tempIncorrectIndexes,
                tempIncorrectAnswers);

        logger.info("Finished asking questions, tabulating timer amount:");
        long timeTaken = timer.getDuration();
        assert timeTaken > 0 : "Timer_amount should not be zero";

        logger.info("Exiting quiz mode:");
        Ui.showToUser(String.format(QUIZ_END, timeTaken));
        isQuizCompleted = true;
        quizAmtAnswered = queue.size();
        incorrectFlashcards = tempIncorrectFlashcards;
        incorrectIndexes = tempIncorrectIndexes;
        incorrectAnswers = tempIncorrectAnswers;
    }

    /**
     * Handles the process of asking a single quiz question and processing the user's answer.
     * Tracks incorrect answers in temporary lists during the quiz.
     *
     * @param indexCard the flashcard containing the question to ask
     * @param tempIncorrectFlashcards list to store flashcards answered incorrectly
     * @param tempIncorrectIndexes list to store indexes of incorrect answers
     * @param tempIncorrectAnswers list to store user's incorrect answers
     * @throws QuizCancelledException if user cancels the quiz mid-way
     */
    public void handleQuestionForQuiz(
            Flashcard indexCard,
            ArrayList<Flashcard> tempIncorrectFlashcards,
            ArrayList<Integer> tempIncorrectIndexes,
            ArrayList<String> tempIncorrectAnswers)
            throws QuizCancelledException {
        //show question
        Ui.showToUser(indexCard.getQuestion());
        //get answer
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
            tempIncorrectIndexes.add(incorrectIndex);
            tempIncorrectFlashcards.add(indexCard);
            tempIncorrectAnswers.add(userAnswer);
        }
        indexCard.setIsLearned(answerCorrect);
    }

    /**
     * Checks if the user's answer matches the flashcard's correct answer.
     * Provides feedback to the user about the correctness of their answer.
     *
     * @param indexCard the flashcard containing the correct answer
     * @param userAnswer the answer provided by the user
     * @return true if the answer is correct, false otherwise
     * @throws QuizCancelledException if user cancels the quiz mid-way
     */
    public boolean handleAnswerForFlashcard(Flashcard indexCard, String userAnswer)
            throws QuizCancelledException {
        assert (!userAnswer.isEmpty()) : "userAnswer should not be empty";
        if(userAnswer.equals(QUIZ_CANCEL)) {
            logger.info("Quiz cancelled by user. Exiting quiz:");
            isQuizCompleted = false;
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
     * Displays the quiz results including statistics and accuracy.
     *
     * @return A success message indicating the result has been shown
     * @throws FlashCLIArgumentException If the quiz is not completed or arrays are mismatched
     */
    //@@author shunyang12
    public String showQuizResult() throws FlashCLIArgumentException {
        logger.info("Generating quiz results...");
        validateQuizCompletion();

        int incorrectCount = incorrectAnswers.size();
        validateArrayConsistency(incorrectCount);

        displayQuizStatistics(incorrectCount);
        displayMistakesIfAny(incorrectCount);

        resetQuizState();
        return VIEW_QUIZRESULT_SUCCESS;
    }

    /**
     * Validates that the quiz was properly completed before showing results.
     *
     * @throws FlashCLIArgumentException If the quiz was not completed
     */
    private void validateQuizCompletion() throws FlashCLIArgumentException {
        if (!isQuizCompleted) {
            throw new FlashCLIArgumentException(INCOMPLETED_QUIZ);
        }
    }

    /**
     * Ensures all mistake-tracking arrays have consistent sizes.
     *
     * @param expectedSize The expected size for all mistake-tracking arrays
     * @throws FlashCLIArgumentException If array sizes don't match
     */
    private void validateArrayConsistency(int expectedSize) throws FlashCLIArgumentException {
        if (expectedSize != incorrectIndexes.size() ||
                expectedSize != incorrectFlashcards.size()) {
            throw new FlashCLIArgumentException(MISMATCHED_ARRAYS);
        }
    }

    /**
     * Builds and displays the quiz statistics message.
     *
     * @param incorrectCount Number of incorrect answers
     */
    private void displayQuizStatistics(int incorrectCount) {
        int totalQuestions = quizAmtAnswered;
        assert totalQuestions > 0 : "totalQuestions should not be zero";
        double accuracy = calculateAccuracyPercentage(totalQuestions, incorrectCount);

        String resultMessage = buildStatisticsMessage(totalQuestions, incorrectCount, accuracy);
        Ui.showToUser(resultMessage);
    }

    /**
     * Builds a formatted statistics message containing quiz results including:
     * - Total questions count
     * - Correct and incorrect answer counts
     * - Accuracy percentage
     * - Letter grade
     * - Performance comment
     * - Achievement art (if applicable)
     * - Full marks message or mistakes header
     *
     * @param totalQuestions The total number of questions in the quiz
     * @param incorrectCount The number of incorrectly answered questions
     * @param accuracy The calculated accuracy percentage (0-100)
     * @return A formatted string containing all quiz statistics and results,
     *         ready for display. The string includes:
     *         - Header section with basic statistics
     *         - Accuracy and grade information
     *         - Motivational performance comment
     *         - Achievement medal art for high scores (90%+)
     *         - Either full marks congratulation or mistakes review header
     */
    private String buildStatisticsMessage(int totalQuestions, int incorrectCount, double accuracy) {
        String accuracyGrade = calculateAccuracyGrade(accuracy);

        StringBuilder sb = new StringBuilder(RESULT_HEADER)
                .append(String.format(RESULT_FORMAT, "Total Question(s)", totalQuestions))
                .append(String.format(RESULT_FORMAT, "Correct Answer(s)", totalQuestions - incorrectCount))
                .append(String.format(RESULT_FORMAT, "Incorrect Answer(s)", incorrectCount))
                .append(String.format(RESULT_FORMAT, "Accuracy", formatPercentage(accuracy)))
                .append(String.format(RESULT_FORMAT, "Grade", accuracyGrade))
                .append("\n").append(getPerformanceComment(accuracy)).append("\n");

        if (accuracy >= 90) {
            sb.append("\n").append(getAchievementArt(accuracy));
        }

        if (incorrectCount == 0) {
            sb.append("\n").append("\n" + QUIZRESULT_FULL_MARKS + "\n");
        } else {
            sb.append(MISTAKES_HEADER);
        }

        return sb.toString();
    }

    /**
     * Generates ASCII art achievement medals based on quiz accuracy.
     * Medals are awarded for exceptional performance:
     * - Gold medal for perfect score (100% accuracy)
     * - Silver medal for near-perfect score (95-99% accuracy)
     * - No medal for scores below 95%
     *
     * @param accuracy The quiz accuracy percentage (0-100)
     * @return String containing ASCII art medal representation:
     *         - Gold medal art for 100% accuracy
     *         - Silver medal art for 95-99% accuracy
     *         - Empty string for accuracy below 95%
     *         Each medal consists of 6 lines forming the medal shape
     *         with appropriate label underneath
     */
    private String getAchievementArt(double accuracy) {
        if (accuracy == 100) {
            return "   ,d88b.d88b,\n" +
                    "   88888888888\n" +
                    "   `Y8888888Y'\n" +
                    "     `Y888Y'\n" +
                    "       `Y'\n" +
                    "   GOLD MEDAL";
        }
        if (accuracy >= 70) {
            return "   ,d88b.d88b,\n" +
                    "   88888888888\n" +
                    "    `Y88888Y'\n" +
                    "      `Y88Y'\n" +
                    "        `Y'\n" +
                    "  SILVER MEDAL";
        }
        return "";
    }

    /**
     * Calculates a letter grade based on accuracy percentage
     * @param accuracy The accuracy percentage (0-100)
     * @return Letter grade with emoji
     */
    private String calculateAccuracyGrade(double accuracy) {
        if (accuracy >= 90) {
            return "A+ :)";
        }
        if (accuracy >= 85) {
            return "A :>";
        }
        if (accuracy >= 80) {
            return "A-";
        }
        if (accuracy >= 75) {
            return "B+";
        }
        if (accuracy >= 70) {
            return "B";
        }
        if (accuracy >= 65) {
            return "B-";
        }
        if (accuracy >= 60) {
            return "C+";
        }
        if (accuracy >= 55) {
            return "C";
        }
        if (accuracy >= 50) {
            return "C-";
        }
        if (accuracy >= 40) {
            return "D";
        }
        return "F ;(";
    }

    private String getPerformanceComment(double accuracy) {
        if (accuracy == 100) {
            return "PERFECT SCORE! FLAWLESS PERFORMANCE!";
        }
        if (accuracy >= 95) {
            return "Outstanding! You've mastered this material!";
        }
        if (accuracy >= 85) {
            return "Excellent work! You're doing great!";
        }
        if (accuracy >= 75) {
            return "Good job! You're making solid progress.";
        }
        if (accuracy >= 65) {
            return "Not bad! Review your mistakes to improve.";
        }
        if (accuracy >= 50) {
            return "You passed, but more practice would help.";
        }
        return "Keep practicing! Review the material and try again.";
    }

    /**
     * Calculates the accuracy percentage.
     *
     * @param total Total questions answered
     * @param incorrect Number of incorrect answers
     * @return Accuracy percentage (0-100)
     */
    private double calculateAccuracyPercentage(int total, int incorrect) {
        return total == 0 ? 0 : ((double)(total - incorrect) / total) * 100;
    }

    /**
     * Formats a percentage value to 2 decimal places.
     *
     * @param value Percentage value to format
     * @return Formatted percentage string (e.g., "85.71%")
     */
    private String formatPercentage(double value) {
        return String.format("%.2f%%", value);
    }

    /**
     * Displays mistakes if there were any incorrect answers.
     *
     * @param incorrectCount Number of incorrect answers
     */
    private void displayMistakesIfAny(int incorrectCount) {
        if (incorrectCount > 0) {
            showMistakes();
        }
    }

    /**
     * Resets the quiz completion state after showing results.
     */
    private void resetQuizState() {
        isQuizCompleted = false;
    }

    /**
     * Displays each mistake with the original question, correct answer, and user's answer.
     *
     * @throws ArrayIndexOutOfBoundsException If any index in incorrectIndexes is invalid
     */
    //@@author shunyang12
    public void showMistakes() throws ArrayIndexOutOfBoundsException {
        for (int i = 0; i < incorrectIndexes.size(); i++) {
            int index = incorrectIndexes.get(i);

            Flashcard card = flashcards.get(index);
            String userAnswer = incorrectAnswers.get(i);

            Ui.showToUser(String.format(FLASHCARD_FORMAT,
                    index + 1,
                    card.getQuestion(),
                    card.getAnswer(),
                    userAnswer));
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
        if (flashcards.isEmpty()) {
            logger.warning("empty list, nothing to change");
            throw new FlashCLIArgumentException(EMPTY_LIST);
        }

        if (arguments.isEmpty()) {
            logger.warning("No input detected.");
            throw new FlashCLIArgumentException(MISSING_INPUT_INDEX);
        }

        int index = Integer.parseInt(arguments.trim());
        logger.info("index received:" + index);
        if (index <= 0 || index > flashcards.size()) {
            logger.warning("Index out of bounds");
            throw new FlashCLIArgumentException(INDEX_OUT_OF_BOUNDS);
        }

        Flashcard indexCard = flashcards.get(index - 1);
        String learnedString = isLearned ? "learned" : "unlearned";
        if(indexCard.getIsLearned() == isLearned) {
            throw new FlashCLIArgumentException(String.format(CHANGED_ISLEARNED_NOCHANGENEEDED, learnedString));
        }

        indexCard.setIsLearned(isLearned);
        logger.info("indexCard " + index + "'s isLearned changed");
        if (isLearned) {
            return (String.format(CHANGED_ISLEARNED_SUCCESS, index, learnedString));
        } else {
            return (String.format(CHANGED_ISLEARNED_SUCCESS, index, learnedString));
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
