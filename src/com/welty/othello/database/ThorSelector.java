package com.welty.othello.database;

import com.welty.othello.gdk.COsGame;
import com.welty.othello.thor.DatabaseData;
import com.welty.othello.thor.GuiProgressTracker;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Select games from the Thor database
 */
public class ThorSelector {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGui();
            }
        });
    }

    private static void createAndShowGui() {
        DatabaseData db = new DatabaseData();
        db.loadFromDirectory(new File("/home/chris/dist/nboard/db/ffo"), new GuiProgressTracker("games"));

        printGames("Championnat du Monde", db);
        printGames("Meijin", db);
    }

    /**
     * Select games where PC={place} from a database and write them, in GGF format, to /home/chris/Dropbox/othello/{place}.
     *
     * @param place place where games occurred
     * @param db    game source
     */
    private static void printGames(String place, DatabaseData db) {
        File dir = new File("/home/chris/Dropbox/othello");
        try {
            dir.mkdirs();
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(dir, place)));
            int n = db.NGames();
            int nGames = 0;
            for (int i = 0; i < n; i++) {
                COsGame game = db.GameFromIndex(i);
                if (game.sPlace.equals(place)) {
                    out.write(game.toString() + "\n");
                    nGames++;
                }
            }
            out.close();
            System.out.println(place + ": " + nGames + " games");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
