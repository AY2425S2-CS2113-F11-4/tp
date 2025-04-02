package ui;

import static constants.ColorConstants.*;
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

    public static void showTitle() {
        // 2. ASCII 艺术字（带颜色渐变）
        System.out.println();
        String[] asciiArt = {
                "  _____ _           _    _____ _      ____ ___ ",
                " |  ___| | __ _ ___| |__|  ___| |    / ___|_ _|",
                " | |_  | |/ _` / __| '_ \\ |_  | |   | |    | | ",
                " |  _| | | (_| \\__ \\ | | |  _| | |___| |___ | | ",
                " |_|   |_|\\__,_|___/_| |_|_|   |_____\\____|___|"
        };

        for (int i = 0; i < asciiArt.length; i++) {
            String color = RAINBOW[i % RAINBOW.length];
            System.out.println(color + BOLD + asciiArt[i] + RESET);
        }
    }

    public static void loadingeffect() {
        // 1. 动态彩虹边框
        System.out.print(BG_GRADIENT);
        for (int i = 0; i < 3; i++) {
            for (String color : RAINBOW) {
                System.out.print(color + BOLD + "processing your command......" + RESET);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // 恢复中断状态（重要！）
                    Thread.currentThread().interrupt();
                    System.err.println("Sleep interrupted: " + e.getMessage());
                }
                System.out.print("\r");
            }
        }
    }

    public static void flashingeffect(String message) {
        // 3. 闪烁警告框
//         : RAINBOW[10]
        System.out.print(
                RAINBOW[3] + BG_BLACK + BOLD + BLINK +
                        message + RESET
        );
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // 恢复中断状态（重要！）
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }

        System.out.println();
    }

    public static void showRedColor(String message) {
        System.out.print(RAINBOW[0] + BG_BLACK + BOLD + BLINK +
                message + RESET);
    }
}
