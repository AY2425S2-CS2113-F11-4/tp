//@@ author ManZ9802
package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import deck.Deck;
import deck.Flashcard;

/**
 * Class to handle loading of pre-existing decks when program is first started up
 */
public class Loading {
    /**
     * Method checks through the folder for all .txt files to create the decks
     * It will then parse through every .txt file to create the flashcard of the deck
     * @return LinkedHashMap
     */
    public static LinkedHashMap<String, Deck> loadAllDecks() {
        LinkedHashMap<String, Deck> decks = new LinkedHashMap<>();
        File folder = new File("./data/decks");

        if (!folder.exists() || !folder.isDirectory()) {
            return decks; // Return empty if no folder or not a directory
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            return decks;
        }

        for (File file : files) {
            String deckName = file.getName().replace(".txt", "");
            Deck deck = new Deck(deckName);

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                String question = null;
                String answer = null;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Q: ")) {
                        question = line.substring(3).trim();
                    } else if (line.startsWith("A: ")) {
                        answer = line.substring(3).trim();
                        if (question != null && !question.isEmpty()) {
                            deck.insertFlashcard(new Flashcard(deck.getDeckSize(), question, answer));
                            question = null;
                            answer = null;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading deck: " + deckName);
            }

            decks.put(deckName, deck);
        }

        return decks;
    }
}
