package flashcli;

import static constants.CommandConstants.EXIT;

import command.Command;
import deck.DeckManager;
import exceptions.FlashCLIArgumentException;
import logger.LoggingSetup;
import parser.Parser;
import storage.Loading;
import storage.Saving;
import ui.Ui;

import static ui.Ui.getUserCommand;
import java.io.IOException;

public class FlashCLI {

    /**
     * Main entry-point for the java.flashcli.FlashCLI application.
     */
    public static void main(String[] args) {
        Ui.showTitle();
        LoggingSetup.configureGlobalLogging();
        DeckManager.decks = Loading.loadAllDecks();
        String fullInputLine = getUserCommand();

        while (!(fullInputLine.equals(EXIT))) {
            try {
                Command c = Parser.parseInput(fullInputLine);
                if (c != null) {
                    c.executeCommand();
                }
            } catch (FlashCLIArgumentException e) {
                Ui.showError(e.getMessage());
            } finally {
                fullInputLine = getUserCommand();
            }
        }
        try {
            Saving.saveAllDecks(DeckManager.decks);
        } catch (IOException e) {
            System.out.println("Error saving decks: " + e.getMessage());
        }
        System.out.println("Thank you for using FlashCLI!");
    }
}
