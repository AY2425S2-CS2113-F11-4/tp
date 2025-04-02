package flashcli;

import static constants.CommandConstants.EXIT;
import static constants.ColorConstants.*;

import command.Command;
import exceptions.FlashCLIArgumentException;
import logger.LoggingSetup;
import parser.Parser;
import ui.Ui;

import static ui.Ui.getUserCommand;

public class FlashCLI {

    private String user_input;
    /**
     * Main entry-point for the java.flashcli.FlashCLI application.
     */
    public String getUserInput(){
        return user_input;

    }
    public static void main(String[] args) {
        Ui.showTitle();
        LoggingSetup.configureGlobalLogging();
        String fullInputLine = getUserCommand();

        while (!(fullInputLine.equals(EXIT))) {
            try {
                Command c = Parser.parseInput(fullInputLine);
                c.executeCommand();
            } catch (FlashCLIArgumentException e) {
                Ui.showError(e.getMessage());
            } finally {
                fullInputLine = getUserCommand();
            }
        }

        System.out.println("Thank you for using FlashCLI!");
    }
}
