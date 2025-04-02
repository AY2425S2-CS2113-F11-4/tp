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
import static deck.DeckManager.createDeck;
import static deck.DeckManager.currentDeck;
import static deck.DeckManager.decks;
import static deck.DeckManager.deleteDeck;
import static deck.DeckManager.globalSearch;
import static deck.DeckManager.renameDeck;

import static deck.DeckManager.switchDeck;
import static deck.DeckManager.viewDecks;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

public class DeckManagerTest {

    @BeforeEach
    void setUp() {
        decks.clear();
    }

    /*
     * Tests for create deck command ==============================================================================
     */

    @Test
    void createDeck_simpleDeckName_success() throws FlashCLIArgumentException {
        String result = createDeck("test1");
        assertEquals(String.format(CREATE_DECK_SUCCESS, "test1", 1), result);
    }

    @Test
    void createDeck_emptyDeckName_throwsException() {
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            createDeck("");
        });
        assertEquals(MISSING_DECK_NAME, exception.getMessage());
    }

    @Test
    void createDeck_duplicateDeckName_throwsException() throws FlashCLIArgumentException {
        createDeck("test1");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            createDeck("test1");
        });
        assertEquals(DUPLICATE_DECK_NAME, exception.getMessage());
    }

    /*
     * Tests for rename deck command ==============================================================================
     */

    @Test
    void renameDeck_validNewName_success() throws FlashCLIArgumentException {
        createDeck("Coding Quality Concepts");
        currentDeck = decks.get("Coding Quality Concepts");
        String result = renameDeck("Code Quality");
        assertEquals(String.format(RENAME_DECK_SUCCESS, "Coding Quality Concepts", "Code Quality"), result);
    }

    @Test
    void renameDeck_emptyNewName_throwsException() throws FlashCLIArgumentException {
        createDeck("Coding Quality Concepts");
        currentDeck = decks.get("Coding Quality Concepts");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            renameDeck("");
        });
        assertEquals(EMPTY_DECK_NAME, exception.getMessage());
    }

    @Test
    void renameDeck_sameName_noChange() throws FlashCLIArgumentException {
        createDeck("Coding Quality Concepts");
        currentDeck = decks.get("Coding Quality Concepts");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            renameDeck("Coding Quality Concepts");
        });
        assertEquals(UNCHANGED_DECK_NAME, exception.getMessage());
    }

    @Test
    void renameDeck_duplicateName_throwsException() throws FlashCLIArgumentException {
        createDeck("Coding Quality Concepts");
        createDeck("Abstraction");
        currentDeck = decks.get("Abstraction");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            renameDeck("Coding Quality Concepts");
        });
        assertEquals(DUPLICATE_DECK_NAME, exception.getMessage());
    }

    /*
     * Tests for view decks command ==============================================================================
     */

    @Test
    void viewDecks_multipleDecks_success() throws FlashCLIArgumentException {
        createDeck("Class Diagrams");
        createDeck("Object Diagrams");
        createDeck("Sequence Diagrams");
        String expectedOutput = "1. Class Diagrams\n2. Object Diagrams\n3. Sequence Diagrams";
        String result = viewDecks();
        assertEquals(String.format(VIEW_DECKS_SUCCESS, expectedOutput), result);
    }

    @Test
    void viewDecks_noDecks_throwsException() {
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, DeckManager::viewDecks);
        assertEquals(NO_DECK_TO_VIEW, exception.getMessage());
    }

    /*
     * Tests for switch decks command ==============================================================================
     */

    @Test
    void switchDeck_validDeckName_success() throws FlashCLIArgumentException {
        createDeck("Quality Assurance");
        String result = switchDeck("Quality Assurance");
        assertEquals(String.format(SWITCH_DECK_SUCCESS, "Quality Assurance"), result);
    }

    @Test
    void switchDeck_noDecksAvailable_throwsException() {
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            switchDeck("Design Patterns");
        });
        assertEquals(NO_DECK_TO_SWITCH, exception.getMessage());
    }

    @Test
    void switchDeck_emptyDeckName_throwsException() throws FlashCLIArgumentException {
        createDeck("Test Coverage");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            switchDeck("");
        });
        assertEquals(NO_SUCH_DECK, exception.getMessage());
    }

    @Test
    void switchDeck_nonExistentDeck_throwsException() throws FlashCLIArgumentException {
        createDeck("Architecture");
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            switchDeck("Mona Lisa");
        });
        assertEquals(NO_SUCH_DECK, exception.getMessage());
    }

    /*
     * Tests for delete decks command ==============================================================================
     */

    @Test
    void deleteDeck_hasDeck_successMessage() throws FlashCLIArgumentException {
        decks.put("System Testing", new Deck("System Testing"));
        String result = deleteDeck("System Testing");
        assertEquals(String.format(DELETE_DECK_SUCCESS, "System Testing"), result);
        assertFalse(decks.containsKey("System Testing"));
    }

    @Test
    void deleteDeck_emptyDeckList_throwsException() {
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            deleteDeck("System Testing");
        });
        assertEquals(DELETE_EMPTY_DECK_ERROR, exception.getMessage());
    }

    @Test
    void deleteDeck_nonExistentDeck_throwsException() {
        decks.put("Unit Testing", new Deck("Unit Testing"));
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            deleteDeck("Test Case Design");
        });
        assertEquals(NO_SUCH_DECK, exception.getMessage());
    }

    @Test
    void deleteDeck_emptyDeckName_throwsException() {
        decks.put("System Testing", new Deck("System Testing"));
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            deleteDeck(" ");
        });
        assertEquals(EMPTY_DECK_NAME, exception.getMessage());
    }

    @Test
    void deleteDeck_trimsInputBeforeDeletion_successMessage() throws FlashCLIArgumentException {
        decks.put("Alpha/Beta Testing", new Deck("Alpha/Beta Testing"));
        String result = deleteDeck("  Alpha/Beta Testing  ");
        assertEquals(String.format(DELETE_DECK_SUCCESS, "Alpha/Beta Testing"), result);
        assertFalse(decks.containsKey("Alpha/Beta Testing"));
    }

    @Test
    void deleteDeck_caseSensitiveDeletion_throwsException() {
        decks.put("Partitioning", new Deck("Partitioning"));
        FlashCLIArgumentException exception = assertThrows(FlashCLIArgumentException.class, () -> {
            deleteDeck("partitioning");
        });
        assertEquals(NO_SUCH_DECK, exception.getMessage());
    }

    @Test
    void deleteDeck_selectedDeckIsDeleted_setsCurrentDeckToNull() throws FlashCLIArgumentException {
        Deck deck = new Deck("Equivalence");
        decks.put("Equivalence", deck);
        currentDeck = deck;

        String result = deleteDeck("Equivalence");
        assertEquals(String.format(DELETE_DECK_SUCCESS, "Equivalence"), result);
        assertNull(currentDeck, "Current deck should be set to null after deletion.");
    }

    //@@author ManZ9802
    /*
     * Tests for search decks command ==============================================================================
     */

    @Test
    void searchGlobal_fullSearch_successMessage() throws FlashCLIArgumentException, EmptyListException {
        Deck deck1 = new Deck("Test1");
        decks.put("Test1", deck1);
        Deck deck2 = new Deck("Test2");
        decks.put("Test2", deck2);

        String input = "/q What is love? /a Baby don't hurt me";
        deck1.createFlashcard(input);
        deck2.createFlashcard(input);

        String qna = "Question: What is love?\nAnswer: Baby don't hurt me";
        String expected = "Deck: Test1\n" + qna + "\n\nDeck: Test2\n" + qna;
        String result1 = globalSearch("/q What is love? /a Baby don't hurt me");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result1);
        String result2 = globalSearch("/q What /a Baby");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result2);
    }

    @Test
    void searchGlobal_partialSearch_successMessage() throws FlashCLIArgumentException, EmptyListException {
        Deck deck1 = new Deck("Test1");
        decks.put("Test1", deck1);
        Deck deck2 = new Deck("Test2");
        decks.put("Test2", deck2);

        String input = "/q What is love? /a Baby don't hurt me";
        deck1.createFlashcard(input);
        deck2.createFlashcard(input);

        String qna = "Question: What is love?\nAnswer: Baby don't hurt me";
        String expected = "Deck: Test1\n" + qna + "\n\nDeck: Test2\n" + qna;
        String result1 = globalSearch("/q What is love?");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result1);
        String result2 = globalSearch("/q love?");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result2);
        String result3 = globalSearch("/a Baby don't hurt me");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result3);
        String result4 = globalSearch("/a Baby");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result4);
    }

    @Test
    void searchGlobal_emptyDeck_throwsException() throws EmptyListException, FlashCLIArgumentException {
        Deck deck1 = new Deck("Test1");
        decks.put("Test1", deck1);
        Deck deck2 = new Deck("Test2");
        decks.put("Test2", deck2);
        try {
            globalSearch("/q What is love? /a Baby don't hurt me");
        } catch (EmptyListException e) {
            assertEquals(SEARCH_RESULT_EMPTY, e.getMessage());
        }
    }

    @Test
    void searchGlobal_singleEmptyDeck_successMessage() throws FlashCLIArgumentException, EmptyListException {
        Deck deck1 = new Deck("Test1");
        decks.put("Test1", deck1);
        Deck deck2 = new Deck("Test2");
        decks.put("Test2", deck2);

        String input = "/q What is love? /a Baby don't hurt me";
        deck1.createFlashcard(input);

        String qna = "Question: What is love?\nAnswer: Baby don't hurt me";
        String expected = "Deck: Test1\n" + qna;
        String result1 = globalSearch("/q What is love? /a Baby don't hurt me");
        assertEquals(String.format(SEARCH_SUCCESS, expected), result1);
    }
}
