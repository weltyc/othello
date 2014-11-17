/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.thor;

import com.orbanova.common.misc.ListenerManager;
import com.welty.othello.c.CReader;
import com.welty.othello.gdk.*;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.welty.othello.core.Utils.Col;
import static com.welty.othello.core.Utils.Row;
import static com.welty.othello.thor.Thor.*;
import static com.welty.othello.thor.ThorOpeningMap.OpeningName;

/**
 * Database game storage.
 */
public class DatabaseData extends ListenerManager<DatabaseData.Listener> {
    /**
     * Thor games followed by converted GGF games
     */
    private ArrayList<ThorGameInternal> m_tgis = new ArrayList<>();
    /**
     * Player names for Thor database games
     */
    private ArrayList<String> m_players = new ArrayList<>();
    /**
     * Tournament names for Thor database games
     */
    private ArrayList<String> m_tournaments = new ArrayList<>();

    /**
     * Number of m_tgis that are Thor games. After this they are GGF games.
     */
    private int m_nThorGames;

    /**
     * GGF text of games loaded from GGF files
     */
    private final ArrayList<GgfGameText> m_ggfGames = new ArrayList<>();

    /**
     * @return true if the file ends with ".wtb", with any capitalization accepted
     */
    public static boolean isThorGamesFile(String fn) {
        return isFileType(fn, ".WTB");
    }

    /**
     * @return true if the file ends with ".trn", with any capitalization accepted
     */
    public static boolean isThorTournamentFile(String fn) {
        return isFileType(fn, ".TRN");
    }

    /**
     * @return true if the file ends with ".jou", with any capitalization accepted
     */
    public static boolean isThorPlayersFile(String fn) {
        return isFileType(fn, ".JOU");
    }

    /**
     * @return true if the file ends with ".ggf", with any capitalization accepted
     */
    public static boolean isGgfFile(String fn) {
        return isFileType(fn, ".GGF");
    }

    private static boolean isFileType(String fn, String suffix) {
        return fn.toUpperCase().endsWith(suffix);
    }

    /**
     * Remove all games from this Model
     */
    public void clearGames() {
        m_ggfGames.clear();
        m_tgis.clear();
        // Reclaim memory
        m_ggfGames.trimToSize();
        m_tgis.trimToSize();

        fireDatabaseChanged();
    }

    String getGgfText(int iGame) {
        return m_ggfGames.get(iGame - m_nThorGames).getText();
    }

    /**
     * @return the text (e.g. "Welty Chris") given the game (item) and field (e.g. 0=black player name)
     */
    public String GameItemText(int item, int field) {
        int n;

        final ThorGameInternal game = m_tgis.get(item);

        if (item < m_nThorGames) {
            // Thor game

            switch (field) {
                case 0:
                    return playerFromPlayerNumber(game.iBlackPlayer);
                case 1:
                    return playerFromPlayerNumber(game.iWhitePlayer);
                case 2:
                    n = game.year;
                    break;
                case 3:
                    return tournamentFromTournamentNumber(game.iTournament);
                case 4:
                    n = game.nBlackDiscs * 2 - 64;
                    break;
                case 5:
                    return OpeningName(game.openingCode);
                default:
                    return "";
            }
        } else {
            // GGF game
            final GgfGameText text = m_ggfGames.get(item - m_nThorGames);
            switch (field) {
                case 0:
                    return text.PB();
                case 1:
                    return text.PW();
                case 2:
                    n = game.year;
                    break;
                case 3:
                    return text.PC();
                case 4:
                    n = game.nBlackDiscs * 2 - 64;
                    break;
                case 5:
                    return OpeningName(game.openingCode);
                default:
                    return "";
            }
        }

        return Integer.toString(n);
    }

    /**
     * @return result of the game, #black discs - #white discs, for Thor games only
     */
    int GameResult(int iGame) {
        return m_tgis.get(iGame).nBlackDiscs * 2 - 64;
    }

    int NPlayers() {
        return m_players.size();
    }

    int NTournaments() {
        return m_tournaments.size();
    }

    /**
     * @return the total number of games loaded (both Thor and GGF)
     */
    public int NGames() {
        return m_tgis.size();
    }

    /**
     * @param iGame index of the game
     * @return a game in GGS/os format.
     */
    public COsGame GameFromIndex(int iGame) {
        COsGame game = new COsGame();

        if (iGame < m_nThorGames) {
            final ThorGameInternal tg = m_tgis.get(iGame);

            game.setToDefaultStartPosition(OsClock.DEFAULT, OsClock.DEFAULT);
            game.setBlackPlayer(playerFromPlayerNumber(tg.iBlackPlayer), 0);
            game.setWhitePlayer(playerFromPlayerNumber(tg.iWhitePlayer), 0);

            game.sPlace = tournamentFromTournamentNumber(tg.iTournament);
            for (int i = 0; i < 60 && tg.moves[i] >= 0; i++) {
                final int sq = tg.moves[i];
                OsMoveListItem mli = new OsMoveListItem(new OsMove(Row(sq), Col(sq)));

                // illegal moves end the game. Yes, the Thor database has some.
                if (!game.pos.board.isMoveLegal(mli.move)) {
                    break;
                }
                game.append(mli);

                if (!game.pos.board.hasLegalMove() && !game.pos.board.isGameOver()) {
                    game.append(OsMoveListItem.PASS);
                }
            }
            if (!game.pos.board.isGameOver()) {
                final OsResult osResult = new OsResult(OsResult.TStatus.kTimeout, tg.nBlackDiscs * 2 - 64);
                game.SetResult(osResult);
            }
        } else {
            game = new COsGame(new CReader(getGgfText(iGame)));
        }

        return game;
    }

    public Thor.MatchingPositions findMatchingPositions(COsBoard pos) {
        return ThorFindMatchingPositions(m_tgis, pos);
    }

    /**
     * Summarize statistics of games played from the current position, by move.
     *
     * @param pos          current board position
     * @param index        list of games that contain a position matching pos. These are given as an index into games.
     * @param iReflections list of reflection indices for each game in index. For each game, iReflections[i]
     *                     is the reflection that maps a move in the game to a move in pos
     * @return summary data for the various moves from a position
     */
    public ThorSummary summarize(COsBoard pos, TIntArrayList index, TIntArrayList iReflections) {
        return ThorSummarize(m_tgis, pos, index, iReflections);
    }

    String playerFromPlayerNumber(char iPlayer) {
        if (iPlayer >= NPlayers())
            return "???";
        else
            return m_players.get(iPlayer);
    }

    String tournamentFromTournamentNumber(char iTournament) {
        if (iTournament >= NTournaments())
            return "???";
        else
            return m_tournaments.get(iTournament);
    }

    /**
     * Set this database to contain the given Thor games.
     * <p/>
     * Previously existing Thor games are discarded. Previously existing GGF games are retained.
     * <p/>
     * fireDatabaseChanged() is called once this is done.
     *
     * @param games list of Thor games.
     */
    public void setThorGames(ArrayList<ThorGameInternal> games) {
        // copy database data for games into m_ggfTgis for faster searching
        m_tgis = games;
        m_nThorGames = games.size();
        for (final GgfGameText game : m_ggfGames) {
            final int nBlackSquares = (new CReader(game.RE()).readInt(0) / 2) + 32;
            final String dt = game.DT();
            int year = new CReader(dt).readInt(0);
            if (year > 100000) {
                // early GGF games have a bug where the DT field is given in seconds since 1970-01-01 instead of
                // the standard format. In this case, translate
                final GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis((long) year * 1000);
                year = cal.get(Calendar.YEAR);
            }
            ThorGameInternal tgi = new ThorGameInternal(nBlackSquares, game.Moves(), game.m_openingCode, year);
            m_tgis.add(tgi);
        }

        fireDatabaseChanged();
    }

    /**
     * Adds GGF games to this database.
     * <p/>
     * You can get the GgfGameText by, for example,
     * <p/>
     * <code>GgfGameText.Load(new File(it), tracker);</code>
     *
     * @param ggfGameTexts GGF games, in text format.
     */
    public void addGgfGames(ArrayList<GgfGameText> ggfGameTexts) {
        m_ggfGames.addAll(ggfGameTexts);

        fireDatabaseChanged();
    }

    /**
     * Loads Thor players file, tournament file, and all games files from the given directory.
     * <p/>
     * This will not look in subdirectories. It clears any existing games/players/tournaments from this database.
     *
     * @param thorDirectory directory to load from
     */
    public void loadFromThorDirectory(File thorDirectory) {
        try (final GuiProgressTracker tracker = new GuiProgressTracker("games loaded")) {
            loadFromDirectory(thorDirectory, tracker);
        }
    }

    /**
     * Loads Thor players file, tournament file, and all games files from the given directory.
     * <p/>
     * This will not look in subdirectories. It clears any existing games/players/tournaments from this database.
     *
     * @param thorDirectory directory to load from
     */
    public void loadFromDirectory(File thorDirectory, IndeterminateProgressTracker tracker) {
        final File[] files = thorDirectory.listFiles();
        if (files == null) {
            throw new RuntimeException(thorDirectory + " is not a directory");
        }
        clearGames();

        ArrayList<ThorGameInternal> games = new ArrayList<>();
        for (File file : files) {
            final String fn = file.toString();
            if (isThorGamesFile(fn)) {
                // games file
                games.addAll(Thor.ThorLoadGames(fn, tracker));
            } else if (isThorTournamentFile(fn)) {
                setTournaments(ThorLoadTournaments(fn));
            } else if (isThorPlayersFile(fn)) {
                setPlayers(Thor.ThorLoadPlayers(fn));
            } else if (isGgfFile(fn)) {
                final ArrayList<GgfGameText> ggfGameTexts = GgfGameText.Load(file, tracker);
                addGgfGames(ggfGameTexts);
            }
        }
        setThorGames(games);
    }

    public void setPlayers(ArrayList<String> strings) {
        m_players = strings;

        fireDatabaseChanged();
    }

    public void setTournaments(ArrayList<String> strings) {
        m_tournaments = strings;

        fireDatabaseChanged();
    }

    /**
     * @param nOpenings number of possible openings ; must be > the highest opening number in this database.
     * @return the number of times each opening occurs in this database.
     */
    public int[] getOpeningCounts(int nOpenings) {
        final int[] counts = new int[nOpenings];
        for (ThorGameInternal it : m_tgis) {
            counts[it.openingCode]++;
        }
        return counts;
    }

    public int getGameYear(int iGame) {
        return m_tgis.get(iGame).year;
    }

    /**
     * Notify all listeners that the contents of the database have changed
     */
    private void fireDatabaseChanged() {
        for (Listener listener : getListeners()) {
            listener.databaseChanged();
        }
    }

    public interface Listener {
        /**
         * Notify the listener that the contents of the database have changed
         */
        void databaseChanged();
    }
}
