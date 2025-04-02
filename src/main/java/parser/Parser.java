package parser;

import command.Command;
import command.CommandCreate;
import command.CommandCreateDeck;
import command.CommandDelete;
import command.CommandEdit;
import command.CommandInsertCode;
import command.CommandListQuestion;
import command.CommandQuizFlashcards;
import command.CommandRenameDeck;
import command.CommandSwitchDeck;
import command.CommandViewAnswer;
import command.CommandViewDecks;
import command.CommandViewQuestion;
import command.CommandViewQuizResult;
import exceptions.FlashCLIArgumentException;

import static constants.CommandConstants.CREATE;
import static constants.CommandConstants.DELETE;
import static constants.CommandConstants.EDIT;
import static constants.CommandConstants.INSERT_CODE;
import static constants.CommandConstants.LIST;
import static constants.CommandConstants.NEW_DECK;
import static constants.CommandConstants.QUIZ;
import static constants.CommandConstants.RENAME_DECK;
import static constants.CommandConstants.SWITCH_DECK;
import static constants.CommandConstants.VIEW_ANS;
import static constants.CommandConstants.VIEW_DECKS;
import static constants.CommandConstants.VIEW_QN;
import static constants.CommandConstants.VIEW_RES;
import static constants.ErrorMessages.NO_DECK_ERROR;
import static constants.ErrorMessages.POSSIBLE_COMMANDS;
import static deck.DeckManager.currentDeck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ui.Ui;
import static ui.Ui.*;

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
                new ArrayList<>(Arrays.asList(CREATE, VIEW_QN, VIEW_ANS, VIEW_RES, EDIT, LIST, DELETE, QUIZ, VIEW_RES, RENAME_DECK, INSERT_CODE));
        if (currentDeck == null && commandsWithDeck.contains(command)) {
            throw new FlashCLIArgumentException(NO_DECK_ERROR);
        }


        return switch (command) {
            case CREATE -> new CommandCreate(arguments);
            case VIEW_QN -> new CommandViewQuestion(arguments);
            case VIEW_ANS -> new CommandViewAnswer(arguments);
            case EDIT -> new CommandEdit(arguments);
            case LIST -> new CommandListQuestion();
            case DELETE -> new CommandDelete(arguments);
            case NEW_DECK -> new CommandCreateDeck(arguments);
            case SWITCH_DECK -> new CommandSwitchDeck(arguments);
            case RENAME_DECK -> new CommandRenameDeck(arguments);
            case VIEW_DECKS -> new CommandViewDecks();
            case QUIZ -> new CommandQuizFlashcards();
            case VIEW_RES -> new CommandViewQuizResult();
            case INSERT_CODE -> new CommandInsertCode(arguments);
            default -> throw new FlashCLIArgumentException(POSSIBLE_COMMANDS);
        };
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
