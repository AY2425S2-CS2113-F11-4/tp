package parser;

import command.Command;
import command.CommandChangeLearned;
import command.CommandCreateFlashcard;
import command.CommandCreateDeck;
import command.CommandDeleteFlashcard;
import command.CommandDeleteDeck;
import command.CommandEditFlashcard;
import command.CommandInsertCode;
import command.CommandListQuestion;
import command.CommandQuizFlashcards;
import command.CommandRenameDeck;
import command.CommandSelectDeck;
import command.CommandUserGuide;
import command.CommandViewAnswer;
import command.CommandViewDecks;
import command.CommandViewQuestion;
import command.CommandSearchFlashcard;
import command.CommandViewQuizResult;
import exceptions.FlashCLIArgumentException;
import ui.Ui;

import static constants.CommandConstants.ADD_CARD;
import static constants.CommandConstants.DELETE_CARD;
import static constants.CommandConstants.REMOVE_DECK;
import static constants.CommandConstants.INSERT_CODE;
import static constants.CommandConstants.MARK_LEARNED;
import static constants.CommandConstants.MARK_UNLEARNED;
import static constants.CommandConstants.NEW_DECK;
import static constants.CommandConstants.QUIZ;
import static constants.CommandConstants.RENAME_DECK;
import static constants.CommandConstants.SELECT_DECK;
import static constants.CommandConstants.USER_GUIDE;
import static constants.CommandConstants.VIEW_ANS;
import static constants.CommandConstants.VIEW_DECKS;
import static constants.CommandConstants.VIEW_QN;
import static constants.CommandConstants.EDIT_CARD;
import static constants.CommandConstants.LIST_CARDS;
import static constants.CommandConstants.SEARCH_CARD;
import static constants.ConfirmationMessages.CONFIRM_DELETE_DECK;
import static constants.CommandConstants.VIEW_RES;
import static constants.ErrorMessages.DECK_INDEX_OUT_OF_BOUNDS;
import static constants.ErrorMessages.DELETE_EMPTY_DECK_ERROR;
import static constants.ErrorMessages.NO_DECK_ERROR;
import static constants.ErrorMessages.POSSIBLE_COMMANDS;
import static deck.DeckManager.checkAndGetListIndex;
import static deck.DeckManager.currentDeck;
import static deck.DeckManager.decks;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses user input and returns the corresponding command.
 *
 * <p>Splits the input string into command and arguments,
 * validates them, and returns the appropriate command object for execution.</p>
 */
public class Parser {

    /**
     * Parses the given input string to create a corresponding command.
     *
     * @param input the user input containing a command and optional arguments.
     * @return the corresponding Command object based on the parsed input.
     * @throws FlashCLIArgumentException if the input is invalid or required arguments are missing.
     */
    public static Command parseInput(String input) throws FlashCLIArgumentException {
        String[] inputList = input.split(" ", 2);
        String command = inputList[0];
        String arguments = inputList.length > 1 ? inputList[1] : "";
        assert arguments != null : "Arguments should not be null";

        ArrayList<String> commandsWithDeck =
                new ArrayList<>(List.of(ADD_CARD, VIEW_QN, VIEW_ANS, VIEW_RES, EDIT_CARD, LIST_CARDS, DELETE_CARD,
                        QUIZ, RENAME_DECK, INSERT_CODE));
        if (currentDeck == null && commandsWithDeck.contains(command)) {
            throw new FlashCLIArgumentException(NO_DECK_ERROR);
        }

        return switch (command) {
        case ADD_CARD -> new CommandCreateFlashcard(arguments);
        case VIEW_QN -> new CommandViewQuestion(arguments);
        case VIEW_ANS -> new CommandViewAnswer(arguments);
        case EDIT_CARD -> new CommandEditFlashcard(arguments);
        case LIST_CARDS -> new CommandListQuestion();
        case DELETE_CARD -> new CommandDeleteFlashcard(arguments);
        case INSERT_CODE -> new CommandInsertCode(arguments);
        case SEARCH_CARD -> new CommandSearchFlashcard(arguments);

        case NEW_DECK -> new CommandCreateDeck(arguments);
        case SELECT_DECK -> new CommandSelectDeck(arguments);
        case RENAME_DECK -> new CommandRenameDeck(arguments);
        case VIEW_DECKS -> new CommandViewDecks(arguments);
        case REMOVE_DECK -> validateDeckExistsForDelete(arguments);

        case QUIZ -> new CommandQuizFlashcards();
        case VIEW_RES -> new CommandViewQuizResult();
        case MARK_UNLEARNED -> new CommandChangeLearned(arguments, false);
        case MARK_LEARNED -> new CommandChangeLearned(arguments, true);

        case USER_GUIDE -> new CommandUserGuide();
        default -> throw new FlashCLIArgumentException(POSSIBLE_COMMANDS);
        };
    }

    /**
     * Validates that the deck to be deleted exists and is valid.
     * Throws an exception if the deck list is empty, the name is empty, or the deck does not exist.
     * If validation passes, proceeds to confirmation for deletion.
     *
     * @param arguments the raw user input representing the deck name.
     * @return a {@code CommandDeleteDeck} if deletion is confirmed, or {@code null} if cancelled.
     * @throws FlashCLIArgumentException if validation fails due to missing or invalid deck.
     */
    public static Command validateDeckExistsForDelete(String arguments) throws FlashCLIArgumentException {
        int listIndex = checkAndGetListIndex(arguments);

        if (decks.isEmpty()) {
            throw new FlashCLIArgumentException(DELETE_EMPTY_DECK_ERROR);
        }

        if (listIndex < 0 || listIndex >= decks.size()) {
            throw new FlashCLIArgumentException(DECK_INDEX_OUT_OF_BOUNDS);
        }

        return handleDeleteDeckConfirmation(arguments);
    }

    /**
     * Handles the confirmation process when a user attempts to delete a deck.
     * Prompts the user for confirmation and ensures valid input ("y" or "n").
     * If the user confirms ("y"), a {@code CommandDeleteDeck} is returned.
     * If the user cancels ("n"), {@code null} is returned.
     *
     * @param arguments the name or identifier of the deck to be deleted.
     * @return a {@code CommandDeleteDeck} if confirmed, or {@code null} if canceled.
     */
    private static Command handleDeleteDeckConfirmation(String arguments) {
        boolean isValidConfirmation;
        String userInput;
        do {
            Ui.showToUser(String.format(CONFIRM_DELETE_DECK, arguments));
            userInput = Ui.getUserCommand().toLowerCase();
            isValidConfirmation = userInput.equals("yes") || userInput.equals("no");
        } while (!isValidConfirmation);
        if (userInput.equals("no")) {
            return null;
        }
        return new CommandDeleteDeck(arguments);
    }

    /**
     * Parses a given code snippet to the correct formatting.
     *
     * @param codeSnippet the code snippet input.
     * @return the formatted code snippet for storage and printing.
     * @throws FlashCLIArgumentException if the input is invalid or required arguments are missing.
     */
    public static String parseCodeSnippet(String codeSnippet) {
        return codeSnippet;
    }
}
