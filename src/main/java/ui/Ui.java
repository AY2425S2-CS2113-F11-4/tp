package ui;

import static constants.CommandConstants.EXIT;

import java.util.Scanner;

/**
 * Handles user interaction by reading input and displaying messages.
 */
public class Ui {
    private static final Scanner in = new Scanner(System.in);

    /**
     * Reads and returns the next user command from input.
     * If no input is available, returns a safe default value.
     *
     * @return The user input as a string, or EXIT if no input is available.
     */
    public static String getUserCommand() {
        if (in.hasNextLine()) {
            return in.nextLine();
        } else {
            return EXIT; // Return a safe default to avoid exceptions
        }
    }

    /**
     * Displays a message to the user.
     *
     * @param message The message to display.
     */
    public static void showToUser(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public static void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays the application title with rainbow-colored ASCII art.
     */
    public static void showTitle() {
        String[] asciiArt = {
            " ______   _          ____      _____     __    __     ______    _         _   ",
            "|  ____| | |        / /\\ \\    /  ___|   |  |  |  |   /  ____/  | |       | |  ",
            "| |__    | |       / /__\\ \\  |  (___    |  |__|  |   | |       | |       | |  ",
            "|  __|   | |      |  ____  |  \\___  \\   |   __   |   | |       | |       | |  ",
            "| |      | |____  | |    | | _____|  |  |  |  |  |   | |____   | |____   | |  ",
            "|_|      |______|  \\_\\  /_/  \\______/   |__|  |__|   \\ ____/   |______|  |_|  "
        };
        for (String line : asciiArt) {
            System.out.println(line);
        }
    }
}
