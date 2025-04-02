//@@author Betahaxer
package deck;

import static constants.ErrorMessages.DELETE_EMPTY_DECK_ERROR;
import static constants.ErrorMessages.DUPLICATE_DECK_NAME;
import static constants.ErrorMessages.EMPTY_DECK_NAME;
import static constants.ErrorMessages.MISSING_DECK_NAME;
import static constants.ErrorMessages.NO_DECK_TO_SWITCH;
import static constants.ErrorMessages.NO_DECK_TO_VIEW;
import static constants.ErrorMessages.NO_SUCH_DECK;
import static constants.ErrorMessages.SEARCH_RESULT_EMPTY;
import static constants.ErrorMessages.UNCHANGED_DECK_NAME;
import static constants.SuccessMessages.CREATE_DECK_SUCCESS;
import static constants.SuccessMessages.DELETE_DECK_SUCCESS;
import static constants.SuccessMessages.RENAME_DECK_SUCCESS;
import static constants.SuccessMessages.SEARCH_SUCCESS;
import static constants.SuccessMessages.SWITCH_DECK_SUCCESS;
import static constants.SuccessMessages.VIEW_DECKS_SUCCESS;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

/**
 * Manages a collection of Deck objects, allowing operations such as creation,
 * renaming, viewing, and switching between decks.
 *
 * <p>This class maintains a static reference to the current deck and a map of
 * all available decks. It provides methods to manage decks and enforce
 * constraints like unique deck names.</p>
 *
 * <p>Throws {@code FlashCLIArgumentException} for invalid input conditions.</p>
 */

public class DeckManager {
    public static Deck currentDeck = null;
    public static LinkedHashMap<String, Deck> decks = new LinkedHashMap<>();
    private static final Logger logger = Logger.getLogger(DeckManager.class.getName());

    /**
     * Returns the number of decks currently stored.
     *
     * @return the total count of decks.
     */
    public static int getDeckSize() {
        return decks.size();
    }

    /**
     * Creates a new deck with the given name.
     *
     * @param arguments the name of the new deck.
     * @return a success message indicating the deck was created.
     * @throws FlashCLIArgumentException if the name is empty or already exists.
     */
    public static String createDeck(String arguments) throws FlashCLIArgumentException {
        logger.info("Entering createDeck method with arguments: " + arguments);

        String newDeckName = arguments.trim();

        if (newDeckName.isEmpty()) {
            logger.warning("Deck name is empty.");
            throw new FlashCLIArgumentException(MISSING_DECK_NAME);
        }

        if (decks.containsKey(newDeckName)) {
            logger.warning("Attempt to create duplicate deck: " + newDeckName);
            throw new FlashCLIArgumentException(DUPLICATE_DECK_NAME);
        }

        decks.put(newDeckName, new Deck(newDeckName));
        logger.info("Deck created successfully: " + newDeckName);

        assert decks.containsKey(newDeckName) : "Deck was not added successfully!";

        return String.format(CREATE_DECK_SUCCESS, newDeckName, getDeckSize());
    }

    /**
     * Renames the current deck to a new name.
     *
     * @param arguments the new name for the current deck.
     * @return a success message indicating the deck was renamed.
     * @throws FlashCLIArgumentException if the name is empty or already exists.
     */
    public static String renameDeck(String arguments) throws FlashCLIArgumentException {
        logger.info("Entering renameDeck method with arguments: " + arguments);

        String newDeckName = arguments.trim();
        if (newDeckName.isEmpty()) {
            logger.warning("Deck name is empty.");
            throw new FlashCLIArgumentException(EMPTY_DECK_NAME);
        }

        boolean isNewDeckNameSameAsCurrent = currentDeck.getName().equals(newDeckName);
        boolean isDeckNameDuplicate = decks.containsKey(newDeckName);

        if (isNewDeckNameSameAsCurrent) {
            logger.warning("Deck name is unchanged");
            throw new FlashCLIArgumentException(UNCHANGED_DECK_NAME);
        }

        if (isDeckNameDuplicate) {
            logger.warning("Attempt to rename deck to an existing deck name: " + newDeckName);
            throw new FlashCLIArgumentException(DUPLICATE_DECK_NAME);
        }

        String oldDeckName = currentDeck.getName();
        logger.info("Renaming deck: " + oldDeckName + " -> " + newDeckName);

        decks.remove(oldDeckName);
        createDeck(newDeckName);
        switchDeck(newDeckName);

        assert !decks.containsKey(oldDeckName) : "Old deck name still exists after renaming!";
        assert decks.containsKey(newDeckName) : "New deck name was not successfully added!";
        assert currentDeck.getName().equals(newDeckName) : "Current deck name was not updated properly!";

        logger.info("Deck renamed successfully: " + oldDeckName + " -> " + newDeckName);
        return String.format(RENAME_DECK_SUCCESS, oldDeckName, currentDeck.getName());
    }

    /**
     * Returns a formatted list of all decks.
     *
     * @return a formatted string listing all available decks.
     * @throws FlashCLIArgumentException if there are no decks to view.
     */
    public static String viewDecks() throws FlashCLIArgumentException {
        logger.info("Entering viewDecks method");

        if (decks.isEmpty()) {
            logger.warning("Attempted to view decks, but no decks are available.");
            throw new FlashCLIArgumentException(NO_DECK_TO_VIEW);
        }

        StringBuilder deckList = new StringBuilder();
        int listIndex = 1;
        for (Map.Entry<String, Deck> deck : decks.entrySet()) {
            deckList.append(listIndex).append(". ").append(deck.getKey());
            if (listIndex != decks.size()) {
                deckList.append("\n");
            }
            listIndex++;
        }

        int expectedDeckCount = decks.size();
        int actualDeckCount = listIndex - 1;
        assert actualDeckCount == expectedDeckCount :
                "Mismatch in deck count! Expected: " + expectedDeckCount + ", Found: " + actualDeckCount;

        logger.info("Decks viewed successfully. Total decks: " + expectedDeckCount);
        return String.format(VIEW_DECKS_SUCCESS, deckList);
    }

    /**
     * Switches the current deck to the specified deck.
     *
     * @param arguments the name of the deck to switch to.
     * @return a success message indicating the active deck has changed.
     * @throws FlashCLIArgumentException if the deck does not exist or input is invalid.
     */
    public static String switchDeck(String arguments) throws FlashCLIArgumentException {
        logger.info("Entering switchDeck method with arguments: " + arguments);
        String deckName = arguments.trim();
        if (decks.isEmpty()) {
            logger.warning("Attempted to switch decks, but no decks are available.");
            throw new FlashCLIArgumentException(NO_DECK_TO_SWITCH);
        }

        if (!decks.containsKey(deckName)) {
            logger.warning("Deck '" + deckName + "' does not exist.");
            throw new FlashCLIArgumentException(NO_SUCH_DECK);
        }

        currentDeck = decks.get(deckName);
        logger.info("Switched to deck: " + currentDeck.getName());

        assert currentDeck != null : "Current deck should not be null after switching!";
        assert decks.containsKey(currentDeck.getName()) : "Switched deck does not exist in decks!";

        logger.info("Deck switched successfully: " + currentDeck.getName());
        return String.format(SWITCH_DECK_SUCCESS, currentDeck.getName());
    }

    /**
     * Deletes a deck by its name if the user has confirmed the deletion.
     * Ensures that the deck exists before removal and updates the current deck if necessary.
     *
     * @param arguments the name of the deck to be deleted.
     * @return a success message indicating that the deck has been deleted.
     * @throws FlashCLIArgumentException if the deck list is empty, the deck name is missing,
     *                                   or the specified deck does not exist.
     */
    public static String deleteDeck(String arguments) throws FlashCLIArgumentException {
        String deckName = arguments.trim();
        if (decks.isEmpty()) {
            throw new FlashCLIArgumentException(DELETE_EMPTY_DECK_ERROR);
        }
        if (deckName.isEmpty()) {
            throw new FlashCLIArgumentException(EMPTY_DECK_NAME);
        }

        // checks if selected deck is the one that will be deleted
        if (currentDeck == decks.get(deckName)) {
            currentDeck = null;
        }

        Deck deletedDeck = decks.remove(deckName);

        if (deletedDeck == null) {
            throw new FlashCLIArgumentException(NO_SUCH_DECK);
        }

        return String.format(DELETE_DECK_SUCCESS, deckName);
    }

    /**
     * Searches for flashcards across all decks based on the provided question and/or answer arguments.
     *
     * @param arguments the search query in the format "q/QUESTION a/ANSWER"
     * @return formatted string of matching flashcards with their respective decks
     * @throws FlashCLIArgumentException if the search arguments are invalid
     * @throws EmptyListException        if there are no decks to search in / if there is no matching flashcards
     */
    //@@author ManZ9802
    public static String globalSearch(String arguments) throws FlashCLIArgumentException, EmptyListException {
        if (decks.isEmpty()) {
            throw new EmptyListException("No decks available for searching.");
        }

        StringBuilder result = new StringBuilder();
        int matchCount = 0;

        for (Map.Entry<String, Deck> deckEntry : decks.entrySet()) {
            String deckName = deckEntry.getKey();
            Deck deck = deckEntry.getValue();

            for (Flashcard flashcard : deck.searchFlashcardHelper(arguments)) {
                matchCount++;
                result.append(String.format("Deck: %s\nQuestion: %s\nAnswer: %s\n\n",
                        deckName,
                        flashcard.getQuestion(),
                        flashcard.getAnswer()));
            }
        }

        if (matchCount == 0) {
            throw new EmptyListException(SEARCH_RESULT_EMPTY);
        }

        return String.format(SEARCH_SUCCESS, result.toString().trim());
    }

}
