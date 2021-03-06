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

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CBinaryReader;
import com.welty.othello.core.CBitBoard;
import com.welty.othello.core.CMove;
import com.welty.othello.core.CMoves;
import com.welty.othello.core.CQPosition;
import com.welty.othello.gdk.COsBoard;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TObjectProcedure;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.welty.othello.core.Utils.*;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 20, 2009
 * Time: 9:40:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class Thor {


    /**
     * @return the Ntest move representation from Thor's move representation
     *         <p/>
     *         returns -2 if the tmv is not a valid move.
     */
    static byte SquareFromThorMove(byte tmv) {
        if (tmv != 0) {

            int col = tmv % 10;
            int row = (tmv - col) / 10;
            col--;
            row--;

            Require.eq(col & 7, "true col", col);
            Require.eq(row & 7, "true row", row);
            return (byte) Square(row, col);
        } else
            return -2;
    }

    //static class ThorString {
//	char name[nStringSize];
//};

//typedef ThorString<20> ThorPlayer;
//typedef ThorString<26> ThorTournament;


    /**
     * Load a thor 8x8 games database
     *
     * @param fn      Filename
     * @param tracker tracks progress in loading the database
     * @return list of games from the file
     * @throws IllegalArgumentException if file doesn't exist
     */
    public static ArrayList<ThorGameInternal> ThorLoadGames(final String fn, @NotNull IndeterminateProgressTracker tracker) {
        final CBinaryReader is = new CBinaryReader(fn);

        final ArrayList<ThorGameInternal> tgs = new ArrayList<>();
        // read header section
        final ThorHeader header = new ThorHeader(is);

        // check header for consistency
        if ((header.boardSize != 0 && header.boardSize != 8) ||
                header.n2 != 0 || header.fSolitaire || header.nPerfectPlay > 60
                || header.crDay > 31 || header.crMonth > 12)
            throw new IllegalArgumentException("This is not a thor games file : " + fn);

        // read games
        while (is.available() != 0) {
            ThorGameInternal tg = new ThorGameInternal(is, header.year);
            tgs.add(tg);
            tracker.increment();
        }
        tracker.update(); // #L6. If nInvalid!=0 the tracker would otherwise display an incorrect number of games.

        // check number of games vs header
        int nGames = tgs.size();
        if (nGames != header.n1) {
            throw new IllegalStateException("Wrong number of games in Thor games database " + fn);
        }

        return tgs;
    }

    /**
     * Load a thor players database or tournament database
     *
     * @param fn          filename
     * @param nStringSize 20 for players database, 26 for tournaments database
     * @return list of all players (or tournaments)
     * @throws IllegalArgumentException if there's an error reading the file
     */
    private static ArrayList<String> ThorLoadStrings(final String fn, int nStringSize) {
        final CBinaryReader is = new CBinaryReader(fn);

        ThorHeader header = new ThorHeader(is);

        // check header for consistency
        // board size should be 0 but Kostas's GGFToWthor converter sets it to 124, so ignore it.
        //if (header.boardSize!=0 || header.nPerfectPlay!=0 ||
        if (header.n1 != 0 || header.fSolitaire
                || header.crDay > 31 || header.crMonth > 12)
            throw new IllegalArgumentException("This is not a thor file : " + fn);

        // read players
        final ArrayList<String> ss = new ArrayList<>();
        while (is.available() != 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nStringSize; i++) {
                final char c = (char) is.readByte();
                if (c!=0) {
                    sb.append(c);
                }
            }
            ss.add(sb.toString());
        }

        // check number of games vs header
        int nss = ss.size();
        if (nss != header.n2)
            throw new IllegalArgumentException("Wrong number of strings in Thor database " + fn);


        return ss;
    }

    public static ArrayList<String> ThorLoadPlayers(final String fn) {
        return ThorLoadStrings(fn, 20);
    }

    public static ArrayList<String> ThorLoadTournaments(final String fn) {
        return ThorLoadStrings(fn, 26);
    }

    /**
     * Calc all reflections of the given bitboard
     *
     * @param bb Bitboard
     * @return all 8 reflections of the bitboard
     */
    static CBitBoard[] GetReflections(final CBitBoard bb) {
        CBitBoard[] reflections = new CBitBoard[8];
        for (int i = 0; i < 8; i++) {
            reflections[i] = bb.Symmetry(i);
        }
        return reflections;
    }

    /**
     * @param bb          [in] Bitboard of the position
     * @param reflections [in] all 8 reflections of the position we may want to match
     * @return if a position matches, index of the matching reflection. Otherwise -1.
     */
    private static int MatchesReflections(final CBitBoard bb, final CBitBoard reflections[]) {
        for (int reflection = 0; reflection < 8; reflection++) {
            if (bb.equals(reflections[reflection])) {
                return reflection;
            }
        }
        return -1;
    }

    /**
     * @return true if the given empties matches the empties of any of the 8 reflections
     */
    private static boolean EmptiesMatch(final long empty, final CBitBoard reflections[]) {
        for (int reflection = 0; reflection < 8; reflection++) {
            if (empty == reflections[reflection].empty)
                return true;
        }
        return false;
    }

    /**
     * @return true if the bitboard block has the given square set
     */
    static boolean GetBit(long bbBlock, int square) {
        return ((bbBlock >> square) & 1) != 0;
    }

    /**
     * @param gameMoves   Game moves, in ntest movelist format
     * @param reflections all 8 reflections of the position we may want to match
     * @return iReflection if a position matches, index of the matching reflection. Otherwise -1.
     */
    static int GameMatches(final byte[] gameMoves, CBitBoard reflections[]) {
        CQPosition posGame = new CQPosition();
        final int nEmpty = reflections[0].NEmpty();

        // Quick-and-dirty check:
        // If the empty squares don't match, the position doesn't match.
        // It's very fast to calculate the empty squares.
        long empty;
        empty = posGame.BitBoard().empty;
        for (int i = 0; i < 60 - nEmpty; i++) {
            final int sq = gameMoves[i];
            if (sq < 0)
                return -1;
            else
                empty &= ~(1L << sq);
        }
        if (!EmptiesMatch(empty, reflections))
            return -1;

        // full check, that pos actually is equal
        for (int i = 0; i < 60 - nEmpty; i++) {
            CMoves moves = new CMoves();
            posGame.CalcMovesAndPass(moves);
            final int sq = gameMoves[i];
            if (sq < 0)
                return -1;

            // don't continue if the move is illegal
            if (!GetBit(moves.All(), sq))
                return -1;

            posGame.MakeMove(new CMove((byte) sq));
        }
        int iReflection = MatchesReflections(posGame.BitBoard(), reflections);
        if (iReflection < 0 && posGame.Mobility(true) == 0) {
            posGame.Pass();
            iReflection = MatchesReflections(posGame.BitBoard(), reflections);
        }
        return iReflection;
    }

    /**
     * Find games that have a position matching posMatch, and output the list of indices as index.
     *
     * @param games    vector of thor games to check
     * @param posMatch position to match
     * @return vector of indices of matching games, and vector of reflection indices that make the games match
     */
    static MatchingPositions ThorFindMatchingPositions(final ArrayList<ThorGameInternal> games, final COsBoard posMatch) {
        CBitBoard reflections[] = GetReflections(new CQPosition(posMatch).BitBoard());
        final MatchingPositions result = new MatchingPositions();

        for (int i = 0; i < games.size(); i++) {
            final int iReflection = GameMatches(games.get(i).moves, reflections);
            if (iReflection >= 0) {
                result.index.add(i);
                result.iReflections.add(iReflection);
            }
        }
        return result;
    }

    public static class MatchingPositions {
        // index vector of indices of matching games
        public final TIntArrayList index = new TIntArrayList();
        // iReflections vector of reflection indices that make the games match. Useful for ThorFindNextMoves().
        public final TIntArrayList iReflections = new TIntArrayList();
    }

    /**
     * Transform a move via reflection.
     * <p/>
     * This function really INVERTS the reflection, to go from the reflected move to the original move
     * passes (and any move code<0) are returned unchanged
     *
     * @param move        move from the reflected position
     * @param iReflection reflection index that takes the original position to the reflected position
     * @return the move from the original position.
     */
    public static int MoveFromIReflection(int move, int iReflection) {
        if (move < 0)
            return move;

        if ((iReflection & 4) != 0)
            move = Square(Col(move), Row(move));
        if ((iReflection & 2) != 0)
            move ^= 7;
        if ((iReflection & 1) != 0)
            //noinspection OctalInteger
            move ^= 070;
        return move;
    }

    /**
     * Summarize statistics of games played from the current position, by move.
     *
     * @param games        list of all games, including those not matching pos.
     * @param pos          current board position
     * @param index        list of games that contain a position matching pos. These are given as an index into games.
     * @param iReflections list of reflection indices for each game in index. For each game, iReflections[i]
     *                     is the reflection that maps a move in the game to a move in pos
     * @return summary data for the various moves from a position
     */
    static ThorSummary ThorSummarize(final ArrayList<ThorGameInternal> games, final COsBoard pos, final TIntArrayList index, final TIntArrayList iReflections) {
        ThorSummary summary = new ThorSummary();

        final boolean fMustPass = !pos.hasLegalMove() && !pos.isGameOver();

        final int iMove = 60 - pos.nEmpty();
        for (int i = 0; i < index.size(); i++) {
            final ThorGameInternal game = games.get(index.get(i));

            // find move for pos
            int mvReflected = game.moves[iMove];
            if (mvReflected != -2) {
                int mv = fMustPass ? -1 : MoveFromIReflection(mvReflected, iReflections.get(i));
                ThorSummaryData data = summary.get(mv);
                if (data == null) {
                    data = new ThorSummaryData();
                    summary.put(mv, data);
                }

                // game result: +1 for win, 0 for draw, -1 for loss
                if (game.nBlackDiscs != 32) {
                    if (game.nBlackDiscs > 32)
                        data.nBlackWins++;
                    else
                        data.nWhiteWins++;
                }
                data.nPlayed++;
            }
        }

        /// calc average score
        summary.forEachValue(new TObjectProcedure<ThorSummaryData>() {

            public boolean execute(ThorSummaryData tsd) {
                tsd.CalcScore(pos.fBlackMove);
                tsd.CalcFrequency(index.size());
                return true;
            }
        });

        return summary;
    }
}
