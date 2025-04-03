package constants;

import static constants.CommandConstants.VIEW_RES;

/**
 * Class to hold all messages related to quiz.
 */
public class QuizMessages {
    public static final String QUIZ_START =
            "Entering quiz mode... get ready!\n" +
            "Type 'exit_quiz' to cancel the quiz and leave at anytime\n" +
            "Cancelling the quiz would not save your results";
    public static final String QUIZ_END =
            "You finished the test! You took: %s seconds!\n" +
            "Type " + VIEW_RES + " to check your test result";
    public static final String QUIZ_NO_ANSWER_DETECTED = "No answer detected. Input your answer again:";
    public static final String QUIZ_CANCEL = "exit_quiz";
    public static final String QUIZ_CANCEL_MESSAGE = "Quiz stopped! Exiting quiz...";
    public static final String QUIZ_CORRECT = "Correct!";
    public static final String QUIZ_INCORRECT = "Incorrect.";
    public static final String QUIZ_QUESTIONS_LEFT = "You have %s questions left:";
    public static final String QUIZ_LAST_QUESTION = "You have 1 question left:";
    public static final String QUIZ_NO_UNLEARNED = "You have no unlearned questions.\n" +
            "Unmark some questions with mark_unlearned before continuing";

}
