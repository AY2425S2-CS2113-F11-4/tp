package deck;

/**
 * Represents a flashcard with a question and an answer.
 */
public class Flashcard {
    protected int index;
    protected String question;
    protected String answer;
    protected String codeSnippet;
    protected boolean isLearned;

    /**
     * Constructs a Flashcard with the specified question and answer.
     *
     * @param question The question for the flashcard.
     * @param answer The answer to the flashcard question.
     */
    public Flashcard(int index, String question, String answer) {
        this.index = index;
        this.question = question;
        this.answer = answer;
        this.isLearned = false;
    }

    /**
     * Returns the index of the flashcard.
     *
     * @return The flashcard index in the deck
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the question of the flashcard.
     *
     * @return The flashcard question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the answer of the flashcard.
     *
     * @return The flashcard answer.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Returns a string representation of the flashcard.
     *
     * @return A formatted string containing the question and answer.
     */
    @Override
    public String toString() {
        return String.format("Question: %s\n" +
                "Answer: %s", this.question, this.answer);
    }

    /**
     * Sets the code snippet of the flashcard.
     *
     */
    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    /**
     * Returns the code snippet of the flashcard.
     *
     * @return The flashcard code snippet.
     */
    public String getCodeSnippet() {
        return (codeSnippet == null) ? "" : codeSnippet;
    }

    /**
     * Returns boolean value of whether flashcard is learned
     * @return The boolean value isLearned
     */
    public boolean getIsLearned() {
        return isLearned;
    }

    /**
     * sets the flashcard to be "learned"
     * @param change in boolean value
     */
    public void setIsLearned(boolean change) {
        this.isLearned = change;
    }
}

