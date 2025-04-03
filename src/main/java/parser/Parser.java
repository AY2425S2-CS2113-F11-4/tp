package parser;

import command.Command;
import command.CommandChangeLearned;
import command.CommandCreate;
import command.CommandCreateDeck;
import command.CommandDelete;
import command.CommandDeleteDeck;
import command.CommandEdit;
import command.CommandInsertCode;
import command.CommandListQuestion;
import command.CommandQuizFlashcards;
import command.CommandRenameDeck;
import command.CommandSwitchDeck;
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
import static constants.CommandConstants.DELETE_DECK;
import static constants.CommandConstants.INSERT_CODE;
import static constants.CommandConstants.MARK_LEARNED;
import static constants.CommandConstants.MARK_UNLEARNED;
import static constants.CommandConstants.NEW_DECK;
import static constants.CommandConstants.QUIZ;
import static constants.CommandConstants.RENAME_DECK;
import static constants.CommandConstants.SWITCH_DECK;
import static constants.CommandConstants.USER_GUIDE;
import static constants.CommandConstants.VIEW_ANS;
import static constants.CommandConstants.VIEW_DECKS;
import static constants.CommandConstants.VIEW_QN;
import static constants.CommandConstants.EDIT_CARD;
import static constants.CommandConstants.LIST_CARDS;
import static constants.CommandConstants.SEARCH_CARD;
import static constants.ConfirmationMessages.CONFIRM_DELETE_DECK;
import static constants.CommandConstants.VIEW_RES;
import static constants.ErrorMessages.NO_DECK_ERROR;
import static constants.ErrorMessages.POSSIBLE_COMMANDS;
import static deck.DeckManager.currentDeck;

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
        case ADD_CARD -> new CommandCreate(arguments);
        case VIEW_QN -> new CommandViewQuestion(arguments);
        case VIEW_ANS -> new CommandViewAnswer(arguments);
        case EDIT_CARD -> new CommandEdit(arguments);
        case LIST_CARDS -> new CommandListQuestion();
        case DELETE_CARD -> new CommandDelete(arguments);
        case INSERT_CODE -> new CommandInsertCode(arguments);
        case SEARCH_CARD -> new CommandSearchFlashcard(arguments);

        case NEW_DECK -> new CommandCreateDeck(arguments);
        case SWITCH_DECK -> new CommandSwitchDeck(arguments);
        case RENAME_DECK -> new CommandRenameDeck(arguments);
        case VIEW_DECKS -> new CommandViewDecks();
        case DELETE_DECK -> handleDeleteDeckConfirmation(arguments);

        case QUIZ -> new CommandQuizFlashcards();
        case VIEW_RES -> new CommandViewQuizResult();
        case MARK_UNLEARNED -> new CommandChangeLearned(arguments, false);
        case MARK_LEARNED -> new CommandChangeLearned(arguments, true);

        case USER_GUIDE -> new CommandUserGuide();
        default -> throw new FlashCLIArgumentException(POSSIBLE_COMMANDS);
        };
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
            isValidConfirmation = userInput.equals("y") || userInput.equals("n");
        } while (!isValidConfirmation);
        if (userInput.equals("n")) {
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
